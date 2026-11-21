package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val examGoal: String = "PTE",
    val targetScore: Double = 79.0,
    val examDateLabel: String? = "In 23 days",
    val weeklyGoalSessions: Long = 12L,
    val sessionsThisWeek: Long = 5L,
    val streakDays: Long = 6L,
    val role: String = "student",
    val lecturerViewEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val creditsSpeaking: Long = 8L,
    val creditsSst: Long = 5L,
    val creditsSwt: Long = 5L,
    val creditsEssay: Long = 4L,
    val creditsIeltsEssay: Long = 3L,
    val creditsMock: Long = 2L,
    val creditsIeltsMock: Long = 2L,
    val creditsUniversal: Long = 10L,
    @com.google.firebase.firestore.ServerTimestamp val createdAt: Timestamp? = null,
    @com.google.firebase.firestore.ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "userId" to userId,
            "displayName" to displayName.take(80),
            "email" to email.take(120),
            "phone" to phone?.take(30)?.takeIf { it.isNotBlank() },
            "photoUrl" to photoUrl?.take(500)?.takeIf { it.isNotBlank() },
            "examGoal" to if (examGoal == "IELTS") "IELTS" else "PTE",
            "targetScore" to targetScore.coerceIn(4.0, 90.0),
            "examDateLabel" to examDateLabel?.take(40)?.takeIf { it.isNotBlank() },
            "weeklyGoalSessions" to weeklyGoalSessions.coerceIn(1L, 50L),
            "sessionsThisWeek" to sessionsThisWeek.coerceIn(0L, 200L),
            "streakDays" to streakDays.coerceIn(0L, 3650L),
            "role" to "student",
            "lecturerViewEnabled" to lecturerViewEnabled,
            "biometricEnabled" to biometricEnabled,
            "creditsSpeaking" to creditsSpeaking.coerceIn(0L, 100000L),
            "creditsSst" to creditsSst.coerceIn(0L, 100000L),
            "creditsSwt" to creditsSwt.coerceIn(0L, 100000L),
            "creditsEssay" to creditsEssay.coerceIn(0L, 100000L),
            "creditsIeltsEssay" to creditsIeltsEssay.coerceIn(0L, 100000L),
            "creditsMock" to creditsMock.coerceIn(0L, 100000L),
            "creditsIeltsMock" to creditsIeltsMock.coerceIn(0L, 100000L),
            "creditsUniversal" to creditsUniversal.coerceIn(0L, 100000L),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@Keep
@IgnoreExtraProperties
data class PracticeAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val examType: String = "PTE",
    val skill: String = "Speaking",
    val taskTitle: String = "",
    val overallScore: Double = 0.0,
    val maxScore: Double = 90.0,
    val contentScore: Double = 0.0,
    val fluencyScore: Double = 0.0,
    val pronunciationScore: Double = 0.0,
    val grammarScore: Double = 0.0,
    val userResponse: String = "",
    val aiFeedback: String = "",
    val modelAnswer: String = "",
    val durationSeconds: Long = 0L,
    @com.google.firebase.firestore.ServerTimestamp val createdAt: Timestamp? = null,
    @com.google.firebase.firestore.ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "examType" to if (examType == "IELTS") "IELTS" else "PTE",
        "skill" to skill,
        "taskTitle" to taskTitle.take(120).ifBlank { "Practice Task" },
        "overallScore" to overallScore.coerceIn(0.0, 90.0),
        "maxScore" to maxScore.coerceIn(9.0, 90.0),
        "contentScore" to contentScore.coerceIn(0.0, 90.0),
        "fluencyScore" to fluencyScore.coerceIn(0.0, 90.0),
        "pronunciationScore" to pronunciationScore.coerceIn(0.0, 90.0),
        "grammarScore" to grammarScore.coerceIn(0.0, 90.0),
        "userResponse" to userResponse.take(10000),
        "aiFeedback" to aiFeedback.take(5000),
        "modelAnswer" to modelAnswer.take(5000),
        "durationSeconds" to durationSeconds.coerceIn(0L, 14400L),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class BookingRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val bookingType: String = "ONE_TO_ONE",
    val title: String = "",
    val lecturerName: String = "",
    val dateLabel: String = "",
    val timeSlot: String = "",
    val mode: String = "Online",
    val status: String = "confirmed",
    val priceLkr: Long = 0L,
    val zoomUrl: String = "",
    @com.google.firebase.firestore.ServerTimestamp val createdAt: Timestamp? = null,
    @com.google.firebase.firestore.ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "bookingType" to bookingType,
        "title" to title.take(140).ifBlank { "SmartLabs Session" },
        "lecturerName" to lecturerName.take(80).ifBlank { "SmartLabs Faculty" },
        "dateLabel" to dateLabel.take(40).ifBlank { "Upcoming" },
        "timeSlot" to timeSlot.take(40).ifBlank { "19:00 IST" },
        "mode" to if (mode == "Physical") "Physical" else "Online",
        "status" to status,
        "priceLkr" to priceLkr.coerceIn(0L, 1000000L),
        "zoomUrl" to zoomUrl.take(300),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class TutorMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val courseContext: String = "PTE Academic",
    val sender: String = "user",
    val messageText: String = "",
    @com.google.firebase.firestore.ServerTimestamp val createdAt: Timestamp? = null,
    @com.google.firebase.firestore.ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "courseContext" to courseContext.take(80).ifBlank { "General Prep" },
        "sender" to if (sender == "model") "model" else "user",
        "messageText" to messageText.take(8000).ifBlank { "..." },
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@Keep
@IgnoreExtraProperties
data class SupportTicket(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val category: String = "Support",
    val status: String = "open",
    val latestMessage: String = "",
    @com.google.firebase.firestore.ServerTimestamp val createdAt: Timestamp? = null,
    @com.google.firebase.firestore.ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(uid: String): Map<String, Any> = mapOf(
        "userId" to uid,
        "subject" to subject.take(140).ifBlank { "Support Inquiry" },
        "category" to category,
        "status" to "open",
        "latestMessage" to latestMessage.take(2000).ifBlank { "Details submitted" },
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

// Curriculum Catalog & UI Models

enum class ExamType(val label: String) {
    PTE("PTE"),
    IELTS("IELTS")
}

enum class SkillCategory(val label: String) {
    ALL("All"),
    SPEAKING("Speaking"),
    WRITING("Writing"),
    READING("Reading"),
    LISTENING("Listening")
}

enum class TrainerInteractionMode {
    SPEAKING_MIC,
    WRITING_TEXT,
    READING_DROPDOWN,
    READING_DRAG_BLANKS,
    MCQ_SINGLE,
    MCQ_MULTIPLE,
    REORDER_PARAGRAPHS,
    LISTENING_AUDIO
}

enum class CreditPoolType(val label: String, val fieldName: String) {
    SPEAKING("Speaking", "creditsSpeaking"),
    SST("SST", "creditsSst"),
    SWT("SWT", "creditsSwt"),
    ESSAY("Essay", "creditsEssay"),
    IELTS_ESSAY("IELTS Essay", "creditsIeltsEssay"),
    MOCK("Mock Test", "creditsMock"),
    IELTS_MOCK("IELTS Mock", "creditsIeltsMock"),
    UNIVERSAL("Universal", "creditsUniversal")
}

data class PracticeTaskItem(
    val id: String,
    val examType: ExamType,
    val skill: SkillCategory,
    val title: String,
    val subtitle: String,
    val questionCount: Int,
    val creditCost: Int,
    val creditPool: CreditPoolType,
    val aiScored: Boolean,
    val mode: TrainerInteractionMode,
    val timeLimitSeconds: Int,
    val prepSeconds: Int = 0,
    val promptText: String,
    val passageOrTranscript: String = "",
    val options: List<String> = emptyList(),
    val correctIndices: Set<Int> = emptySet(),
    val wordBank: List<String> = emptyList(),
    val reorderItems: List<String> = emptyList(),
    val modelAnswerHint: String = ""
)

data class MockTestItem(
    val id: String,
    val examType: ExamType,
    val title: String,
    val subtitle: String,
    val durationMinutes: Int,
    val sections: List<String>,
    val creditCost: Int,
    val creditPool: CreditPoolType,
    val priceLkr: Int,
    val unlocked: Boolean,
    val isLevelTest: Boolean = false
)

data class CourseItem(
    val id: String,
    val title: String,
    val lecturer: String,
    val examType: ExamType,
    val schedule: String,
    val priceLkr: Int,
    val priceUsd: Int? = null,
    val syllabus: List<String>,
    val rating: Double,
    val enrolledCount: Int
)

data class RecordedPackageItem(
    val id: String,
    val title: String,
    val lecturer: String,
    val durationHours: String,
    val lessonsCount: Int,
    val examType: ExamType,
    val lessons: List<String>
)

data class LecturerProfile(
    val id: String,
    val name: String,
    val title: String,
    val subjects: List<String>,
    val ratePerHourLkr: Int,
    val supportsOnline: Boolean,
    val supportsPhysical: Boolean,
    val rating: Double,
    val bio: String
)

data class EventItem(
    val id: String,
    val type: String,
    val title: String,
    val host: String,
    val dateTimeLabel: String,
    val seatsLeft: Int,
    val priceLkr: Int
)

data class ResourceItem(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val examType: ExamType
)

data class CreditBundle(
    val id: String,
    val name: String,
    val pool: CreditPoolType,
    val quantity: Long,
    val priceLkr: Int,
    val badge: String
)
