package com.example.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiRepository"

data class AiScoreEvaluation(
    val overallScore: Double,
    val maxScore: Double,
    val contentScore: Double,
    val fluencyScore: Double,
    val pronunciationScore: Double,
    val grammarScore: Double,
    val feedback: String,
    val modelAnswer: String
)

class AiRepository {

    private val scoringSchema = Schema.obj(
        properties = mapOf(
            "overallScore" to Schema.double("Overall score (0-90 for PTE, or 0.0-9.0 for IELTS)"),
            "contentScore" to Schema.double("Content & task response score"),
            "fluencyScore" to Schema.double("Coherence & oral/written fluency score"),
            "pronunciationScore" to Schema.double("Pronunciation or lexical resource score"),
            "grammarScore" to Schema.double("Grammar & spelling accuracy score"),
            "feedback" to Schema.string("Actionable 2-3 sentence examiner feedback"),
            "modelAnswer" to Schema.string("Concise model 90 / Band 9 exemplar response")
        )
    )

    private val scoringModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = scoringSchema
            }
        )
    }

    private val tutorModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    suspend fun evaluatePracticeResponse(
        task: PracticeTaskItem,
        userResponse: String
    ): Result<AiScoreEvaluation> {
        return try {
            val isIelts = task.examType == ExamType.IELTS
            val scaleMax = if (isIelts) 9.0 else 90.0
            val prompt = buildString {
                appendLine("You are an official ${task.examType.label} Academic examiner for SmartLabs.")
                appendLine("Task Type: ${task.title} (${task.skill.label})")
                appendLine("Prompt / Passage: ${task.promptText}")
                if (task.passageOrTranscript.isNotBlank()) {
                    appendLine("Reference Material: ${task.passageOrTranscript}")
                }
                appendLine("Student Submission: $userResponse")
                appendLine("Score strictly on a 0 to $scaleMax scale (IELTS in 0.5 band steps, PTE integers/decimals 10-90).")
                appendLine("Provide constructive examiner feedback and a concise high-scoring model answer.")
            }

            val response = withTimeout(60_000L) {
                scoringModel.generateContent(prompt)
            }
            val rawJson = response.text ?: throw IllegalStateException("AI returned an empty score payload.")
            val json = JSONObject(rawJson)

            val eval = AiScoreEvaluation(
                overallScore = json.optDouble("overallScore", if (isIelts) 7.0 else 72.0).coerceIn(0.0, scaleMax),
                maxScore = scaleMax,
                contentScore = json.optDouble("contentScore", if (isIelts) 7.0 else 72.0).coerceIn(0.0, scaleMax),
                fluencyScore = json.optDouble("fluencyScore", if (isIelts) 7.0 else 74.0).coerceIn(0.0, scaleMax),
                pronunciationScore = json.optDouble("pronunciationScore", if (isIelts) 7.0 else 71.0).coerceIn(0.0, scaleMax),
                grammarScore = json.optDouble("grammarScore", if (isIelts) 7.0 else 75.0).coerceIn(0.0, scaleMax),
                feedback = json.optString("feedback", "Solid academic structure. Refine lexical precision and cohesion."),
                modelAnswer = json.optString("modelAnswer", task.modelAnswerHint)
            )
            Result.success(eval)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI scoring timed out", e)
            Result.failure(Exception("AI scoring timed out after 60 seconds. Check signal and retry.", e))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI scoring failed", e)
            Result.failure(e)
        }
    }

    fun streamTutorReply(
        courseContext: String,
        history: List<TutorMessage>,
        userQuestion: String
    ): Flow<String> = flow {
        val recentHistory = history.takeLast(8).joinToString("\n") {
            "${if (it.sender == "model") "Tutor" else "Student"}: ${it.messageText}"
        }
        val prompt = buildString {
            appendLine("You are the SmartLabs AI Tutor specializing in $courseContext (PTE Academic & IELTS).")
            appendLine("Give crisp, practical, high-scoring exam coaching with concrete examples in 2-4 short paragraphs.")
            if (recentHistory.isNotBlank()) {
                appendLine("\nRecent conversation:\n$recentHistory")
            }
            appendLine("\nStudent question: $userQuestion")
        }

        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            tutorModel.generateContentStream(prompt).collect { chunk ->
                val textChunk = chunk.text.orEmpty()
                if (textChunk.isNotEmpty()) {
                    accumulated.append(textChunk)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("AI Tutor returned an empty response.")
        }
    }
}
