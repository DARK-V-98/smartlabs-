package com.example.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiRepository
import com.example.data.AiScoreEvaluation
import com.example.data.BookingRecord
import com.example.data.CreditPoolType
import com.example.data.ExamType
import com.example.data.FirestoreRepository
import com.example.data.PracticeAttempt
import com.example.data.PracticeTaskItem
import com.example.data.SkillCategory
import com.example.data.SmartLabsCatalog
import com.example.data.SupportTicket
import com.example.data.TutorMessage
import com.example.data.UserPreferencesRepository
import com.example.data.UserProfile
import com.example.ui.theme.ThemePreference
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "SmartLabsVM"
private const val STOP_TIMEOUT_MS = 5000L

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiScoringState {
    data object Idle : AiScoringState
    data object Scoring : AiScoringState
    data class Scored(val evaluation: AiScoreEvaluation) : AiScoringState
    data class Error(val message: String) : AiScoringState
}

data class SearchFilterState(
    val query: String = "",
    val examFilter: ExamType? = null,
    val skillFilter: SkillCategory = SkillCategory.ALL,
    val aiScoredOnly: Boolean = false,
    val freeOnly: Boolean = false
)

class SmartLabsViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val aiRepository: AiRepository,
    private val prefsRepository: UserPreferencesRepository,
    private val currentUserId: String?,
    appContext: Context
) : ViewModel() {

    val isPreviewMode: Boolean = currentUserId.isNullOrBlank()
    private val effectiveUid: String = currentUserId?.takeIf { it.isNotBlank() } ?: "preview_guest"

    private val _previewProfile = MutableStateFlow<UserProfile?>(
        UserProfile(
            userId = "preview_guest",
            displayName = "Guest Student",
            email = "preview@smartlabs.lk",
            examGoal = "PTE",
            targetScore = 79.0,
            examDateLabel = "In 23 days",
            weeklyGoalSessions = 12L,
            sessionsThisWeek = 4L,
            streakDays = 5L,
            role = "student",
            lecturerViewEnabled = false,
            biometricEnabled = false,
            creditsSpeaking = 8L,
            creditsSst = 5L,
            creditsSwt = 5L,
            creditsEssay = 4L,
            creditsIeltsEssay = 3L,
            creditsMock = 2L,
            creditsIeltsMock = 2L,
            creditsUniversal = 10L
        )
    )

    private val _previewAttempts = MutableStateFlow(
        listOf(
            PracticeAttempt(
                id = "preview_a1",
                userId = "preview_guest",
                examType = "PTE",
                skill = "Speaking",
                taskTitle = "Read Aloud",
                overallScore = 81.0,
                maxScore = 90.0,
                contentScore = 82.0,
                fluencyScore = 80.0,
                pronunciationScore = 81.0,
                grammarScore = 84.0,
                userResponse = "Climate resilience requires urban green corridors and permeable pavements.",
                aiFeedback = "Strong natural pacing and clear word stress across multi-syllable academic terms.",
                modelAnswer = "Read smoothly in natural thought groups without rushing punctuation.",
                durationSeconds = 38L
            ),
            PracticeAttempt(
                id = "preview_a2",
                userId = "preview_guest",
                examType = "IELTS",
                skill = "Writing",
                taskTitle = "Writing Task 2",
                overallScore = 7.5,
                maxScore = 9.0,
                contentScore = 7.5,
                fluencyScore = 7.5,
                pronunciationScore = 7.5,
                grammarScore = 7.5,
                userResponse = "Remote education expands regional access while physical labs preserve collaboration.",
                aiFeedback = "Well-developed thesis and balanced body paragraphs; vary complex clauses slightly more.",
                modelAnswer = "Aim for 270+ words with clear topic sentences and hedged academic conclusions.",
                durationSeconds = 420L
            )
        )
    )

    private val _previewBookings = MutableStateFlow(
        listOf(
            BookingRecord(
                id = "preview_b1",
                userId = "preview_guest",
                bookingType = "ONE_TO_ONE",
                title = "1-on-1 Pronunciation & Read Aloud Clinic",
                lecturerName = "Dr. Kasun Perera",
                dateLabel = "Tomorrow",
                timeSlot = "19:00 IST",
                mode = "Online",
                status = "confirmed",
                priceLkr = 3500L,
                zoomUrl = "https://zoom.us/j/smartlabs-preview"
            )
        )
    )

    private val _previewTutorMessages = MutableStateFlow<List<TutorMessage>>(emptyList())
    private val _previewTickets = MutableStateFlow<List<SupportTicket>>(emptyList())

    val isOnline: StateFlow<Boolean> = observeNetworkConnectivity(appContext)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), true)

    val localPreferences = prefsRepository.preferences
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            com.example.data.LocalAppPreferences()
        )

    val profileState: StateFlow<UiState<UserProfile?>> = if (isPreviewMode) {
        _previewProfile
            .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    } else {
        firestoreRepository.observeProfile(effectiveUid)
            .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing profile", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to sync profile"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    }

    val attemptsState: StateFlow<UiState<List<PracticeAttempt>>> = if (isPreviewMode) {
        _previewAttempts
            .map<List<PracticeAttempt>, UiState<List<PracticeAttempt>>> { UiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    } else {
        firestoreRepository.observeAttempts(effectiveUid)
            .map<List<PracticeAttempt>, UiState<List<PracticeAttempt>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing attempts", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load practice attempts"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    }

    val bookingsState: StateFlow<UiState<List<BookingRecord>>> = if (isPreviewMode) {
        _previewBookings
            .map<List<BookingRecord>, UiState<List<BookingRecord>>> { UiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    } else {
        firestoreRepository.observeBookings(effectiveUid)
            .map<List<BookingRecord>, UiState<List<BookingRecord>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing bookings", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load bookings"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    }

    val tutorMessagesState: StateFlow<UiState<List<TutorMessage>>> = if (isPreviewMode) {
        _previewTutorMessages
            .map<List<TutorMessage>, UiState<List<TutorMessage>>> { UiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    } else {
        firestoreRepository.observeTutorMessages(effectiveUid)
            .map<List<TutorMessage>, UiState<List<TutorMessage>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing tutor messages", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load AI Tutor history"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    }

    val ticketsState: StateFlow<UiState<List<SupportTicket>>> = if (isPreviewMode) {
        _previewTickets
            .map<List<SupportTicket>, UiState<List<SupportTicket>>> { UiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    } else {
        firestoreRepository.observeSupportTickets(effectiveUid)
            .map<List<SupportTicket>, UiState<List<SupportTicket>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing tickets", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load support tickets"))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)
    }

    private val _searchFilter = MutableStateFlow(SearchFilterState())
    val searchFilter: StateFlow<SearchFilterState> = _searchFilter.asStateFlow()

    val filteredTasks: StateFlow<List<PracticeTaskItem>> = _searchFilter.map { filter ->
        SmartLabsCatalog.practiceTasks.filter { task ->
            val matchesQuery = filter.query.isBlank() ||
                task.title.contains(filter.query, ignoreCase = true) ||
                task.subtitle.contains(filter.query, ignoreCase = true) ||
                task.skill.label.contains(filter.query, ignoreCase = true)
            val matchesExam = filter.examFilter == null || task.examType == filter.examFilter
            val matchesSkill = filter.skillFilter == SkillCategory.ALL || task.skill == filter.skillFilter
            val matchesAi = !filter.aiScoredOnly || task.aiScored
            val matchesFree = !filter.freeOnly || task.creditCost == 0
            matchesQuery && matchesExam && matchesSkill && matchesAi && matchesFree
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), SmartLabsCatalog.practiceTasks)

    private val _aiScoringState = MutableStateFlow<AiScoringState>(AiScoringState.Idle)
    val aiScoringState: StateFlow<AiScoringState> = _aiScoringState.asStateFlow()

    private val _tutorStreamingText = MutableStateFlow<String?>(null)
    val tutorStreamingText: StateFlow<String?> = _tutorStreamingText.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isInitialSkeletonActive = MutableStateFlow(true)
    val isSkeletonVisible: StateFlow<Boolean> = combine(
        _isInitialSkeletonActive,
        profileState,
        localPreferences
    ) { bootSkeleton, prof, prefs ->
        bootSkeleton || prof is UiState.Loading || prefs.simulateLowSignalSkeleton
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), true)

    init {
        viewModelScope.launch {
            if (!isPreviewMode) {
                Firebase.auth.currentUser?.let { user ->
                    firestoreRepository.ensureProfileInitialized(user)
                }
            }
            // Brief smooth skeleton shimmer transition on cold start / low signal
            delay(550L)
            _isInitialSkeletonActive.value = false
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchFilter.value = _searchFilter.value.copy(query = query)
    }

    fun updateExamFilter(examType: ExamType?) {
        _searchFilter.value = _searchFilter.value.copy(examFilter = examType)
    }

    fun updateSkillFilter(skill: SkillCategory) {
        _searchFilter.value = _searchFilter.value.copy(skillFilter = skill)
    }

    fun updateAdvancedFilters(aiScoredOnly: Boolean, freeOnly: Boolean) {
        _searchFilter.value = _searchFilter.value.copy(
            aiScoredOnly = aiScoredOnly,
            freeOnly = freeOnly
        )
    }

    fun resetFilters() {
        _searchFilter.value = SearchFilterState()
    }

    fun setThemePreference(theme: ThemePreference) {
        viewModelScope.launch { prefsRepository.setThemePreference(theme) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setBiometricLockEnabled(enabled)
            if (isPreviewMode) {
                _previewProfile.value = _previewProfile.value?.copy(biometricEnabled = enabled)
                _statusMessage.value = if (enabled) "Biometric vault protection enabled" else "Biometric lock disabled"
                return@launch
            }
            val result = firestoreRepository.setBiometricEnabled(enabled)
            if (result.isFailure) {
                _statusMessage.value = "Biometric saved locally; will sync when online."
            } else {
                _statusMessage.value = if (enabled) "Biometric vault protection enabled" else "Biometric lock disabled"
            }
        }
    }

    fun setLecturerViewEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (isPreviewMode) {
                _previewProfile.value = _previewProfile.value?.copy(lecturerViewEnabled = enabled)
                return@launch
            }
            val res = firestoreRepository.setLecturerViewEnabled(enabled)
            if (res.isFailure) {
                _statusMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to switch mode"
            }
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setRemindersEnabled(enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setSoundEffectsEnabled(enabled) }
    }

    fun setLowSignalSkeletonPreview(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setSimulateLowSignalSkeleton(enabled) }
    }

    fun updateGoal(
        displayName: String,
        examGoal: String,
        targetScore: Double,
        examDateLabel: String,
        weeklyGoalSessions: Long
    ) {
        viewModelScope.launch {
            if (isPreviewMode) {
                _previewProfile.value = _previewProfile.value?.copy(
                    displayName = displayName.trim().take(80).ifBlank { "Guest Student" },
                    examGoal = if (examGoal == "IELTS") "IELTS" else "PTE",
                    targetScore = targetScore.coerceIn(4.0, 90.0),
                    examDateLabel = examDateLabel.trim().take(40).ifBlank { "In 23 days" },
                    weeklyGoalSessions = weeklyGoalSessions.coerceIn(1L, 50L)
                )
                _statusMessage.value = "Study plan updated locally (Preview)"
                return@launch
            }
            val res = firestoreRepository.updateProfileGoal(
                displayName = displayName,
                examGoal = examGoal,
                targetScore = targetScore,
                examDateLabel = examDateLabel,
                weeklyGoalSessions = weeklyGoalSessions
            )
            _statusMessage.value = if (res.isSuccess) {
                "Study plan synced across devices"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Unable to save goal"
            }
        }
    }

    fun resetAiScoringState() {
        _aiScoringState.value = AiScoringState.Idle
    }

    fun submitPracticeAttempt(
        task: PracticeTaskItem,
        userResponse: String,
        objectiveScoreRatio: Double? = null,
        durationSeconds: Int = 60
    ) {
        viewModelScope.launch {
            val profile = (profileState.value as? UiState.Success)?.data
            val poolBalance = if (profile != null) getPoolBalance(profile, task.creditPool) else 10L
            val universalBalance = profile?.creditsUniversal ?: 0L

            val poolToCharge: CreditPoolType? = when {
                task.creditCost <= 0 -> null
                poolBalance >= task.creditCost -> task.creditPool
                universalBalance >= task.creditCost -> CreditPoolType.UNIVERSAL
                else -> {
                    _aiScoringState.value = AiScoringState.Error(
                        "Needs ${task.creditCost} ${task.creditPool.label} credit. Top up in Credits."
                    )
                    return@launch
                }
            }

            if (task.aiScored) {
                _aiScoringState.value = AiScoringState.Scoring
                val aiRes = aiRepository.evaluatePracticeResponse(task, userResponse)
                aiRes.fold(
                    onSuccess = { eval ->
                        val attempt = PracticeAttempt(
                            id = if (isPreviewMode) "preview_${System.currentTimeMillis()}" else "",
                            userId = effectiveUid,
                            examType = task.examType.label,
                            skill = task.skill.label,
                            taskTitle = task.title,
                            overallScore = eval.overallScore,
                            maxScore = eval.maxScore,
                            contentScore = eval.contentScore,
                            fluencyScore = eval.fluencyScore,
                            pronunciationScore = eval.pronunciationScore,
                            grammarScore = eval.grammarScore,
                            userResponse = userResponse,
                            aiFeedback = eval.feedback,
                            modelAnswer = eval.modelAnswer,
                            durationSeconds = durationSeconds.toLong()
                        )
                        recordAttemptAndDeductCredit(poolToCharge, task.creditCost, attempt)
                        _aiScoringState.value = AiScoringState.Scored(eval)
                    },
                    onFailure = { err ->
                        _aiScoringState.value = AiScoringState.Error(
                            err.localizedMessage ?: "AI scoring failed. Please retry."
                        )
                    }
                )
            } else {
                val maxScale = if (task.examType == ExamType.IELTS) 9.0 else 90.0
                val ratio = (objectiveScoreRatio ?: 1.0).coerceIn(0.0, 1.0)
                val earned = (maxScale * (0.4 + 0.6 * ratio)).coerceAtMost(maxScale)
                val eval = AiScoreEvaluation(
                    overallScore = earned,
                    maxScore = maxScale,
                    contentScore = earned,
                    fluencyScore = earned,
                    pronunciationScore = earned,
                    grammarScore = earned,
                    feedback = if (ratio >= 0.8) {
                        "Accurate selection! Keep drilling timed passages to lock in consistency."
                    } else {
                        "Review key collocations and discourse markers in the model hint below."
                    },
                    modelAnswer = task.modelAnswerHint
                )
                val attempt = PracticeAttempt(
                    id = if (isPreviewMode) "preview_${System.currentTimeMillis()}" else "",
                    userId = effectiveUid,
                    examType = task.examType.label,
                    skill = task.skill.label,
                    taskTitle = task.title,
                    overallScore = eval.overallScore,
                    maxScore = eval.maxScore,
                    contentScore = eval.contentScore,
                    fluencyScore = eval.fluencyScore,
                    pronunciationScore = eval.pronunciationScore,
                    grammarScore = eval.grammarScore,
                    userResponse = userResponse,
                    aiFeedback = eval.feedback,
                    modelAnswer = eval.modelAnswer,
                    durationSeconds = durationSeconds.toLong()
                )
                recordAttemptAndDeductCredit(poolToCharge, task.creditCost, attempt)
                _aiScoringState.value = AiScoringState.Scored(eval)
            }
        }
    }

    private suspend fun recordAttemptAndDeductCredit(
        pool: CreditPoolType?,
        cost: Int,
        attempt: PracticeAttempt
    ): Result<Unit> {
        if (!isPreviewMode) {
            return firestoreRepository.consumeCreditAndRecordAttempt(pool, cost, attempt)
        }
        _previewAttempts.value = listOf(attempt) + _previewAttempts.value
        _previewProfile.value = _previewProfile.value?.let { current ->
            val delta = -cost.toLong()
            val updated = adjustPool(current, pool, delta)
            updated.copy(sessionsThisWeek = updated.sessionsThisWeek + 1L)
        }
        return Result.success(Unit)
    }

    private fun adjustPool(profile: UserProfile, pool: CreditPoolType?, delta: Long): UserProfile {
        if (pool == null || delta == 0L) return profile
        return when (pool) {
            CreditPoolType.SPEAKING -> profile.copy(creditsSpeaking = (profile.creditsSpeaking + delta).coerceAtLeast(0L))
            CreditPoolType.SST -> profile.copy(creditsSst = (profile.creditsSst + delta).coerceAtLeast(0L))
            CreditPoolType.SWT -> profile.copy(creditsSwt = (profile.creditsSwt + delta).coerceAtLeast(0L))
            CreditPoolType.ESSAY -> profile.copy(creditsEssay = (profile.creditsEssay + delta).coerceAtLeast(0L))
            CreditPoolType.IELTS_ESSAY -> profile.copy(creditsIeltsEssay = (profile.creditsIeltsEssay + delta).coerceAtLeast(0L))
            CreditPoolType.MOCK -> profile.copy(creditsMock = (profile.creditsMock + delta).coerceAtLeast(0L))
            CreditPoolType.IELTS_MOCK -> profile.copy(creditsIeltsMock = (profile.creditsIeltsMock + delta).coerceAtLeast(0L))
            CreditPoolType.UNIVERSAL -> profile.copy(creditsUniversal = (profile.creditsUniversal + delta).coerceAtLeast(0L))
        }
    }

    fun recordMockResult(
        examType: ExamType,
        mockTitle: String,
        score: Double,
        durationSeconds: Int,
        feedback: String
    ) {
        viewModelScope.launch {
            val maxScale = if (examType == ExamType.IELTS) 9.0 else 90.0
            val attempt = PracticeAttempt(
                id = if (isPreviewMode) "preview_mock_${System.currentTimeMillis()}" else "",
                userId = effectiveUid,
                examType = examType.label,
                skill = "Mock",
                taskTitle = mockTitle,
                overallScore = score.coerceIn(0.0, maxScale),
                maxScore = maxScale,
                contentScore = score,
                fluencyScore = (score - 1.0).coerceAtLeast(0.0),
                pronunciationScore = score,
                grammarScore = score,
                userResponse = "Full timed mock exam completed",
                aiFeedback = feedback,
                modelAnswer = "Review section-wise timing and weak question banks in Practice.",
                durationSeconds = durationSeconds.toLong()
            )
            val res = recordAttemptAndDeductCredit(null, 0, attempt)
            _statusMessage.value = if (res.isSuccess) {
                if (isPreviewMode) "Mock result saved locally (Preview)" else "Mock result saved & synced"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Saved offline — pending sync"
            }
        }
    }

    fun sendTutorMessage(courseContext: String, question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            val history = (tutorMessagesState.value as? UiState.Success)?.data ?: emptyList()
            val userMsg = TutorMessage(
                id = if (isPreviewMode) "u_${System.currentTimeMillis()}" else "",
                userId = effectiveUid,
                courseContext = courseContext,
                sender = "user",
                messageText = question.trim()
            )
            if (isPreviewMode) {
                _previewTutorMessages.value = _previewTutorMessages.value + userMsg
            } else {
                firestoreRepository.saveTutorMessage(userMsg)
            }
            _tutorStreamingText.value = ""
            try {
                var finalAnswer = ""
                aiRepository.streamTutorReply(courseContext, history, question.trim())
                    .collect { partial ->
                        finalAnswer = partial
                        _tutorStreamingText.value = partial
                    }
                _tutorStreamingText.value = null
                if (finalAnswer.isNotBlank()) {
                    val modelMsg = TutorMessage(
                        id = if (isPreviewMode) "m_${System.currentTimeMillis()}" else "",
                        userId = effectiveUid,
                        courseContext = courseContext,
                        sender = "model",
                        messageText = finalAnswer
                    )
                    if (isPreviewMode) {
                        _previewTutorMessages.value = _previewTutorMessages.value + modelMsg
                    } else {
                        firestoreRepository.saveTutorMessage(modelMsg)
                    }
                }
            } catch (e: CancellationException) {
                _tutorStreamingText.value = null
                throw e
            } catch (e: Exception) {
                _tutorStreamingText.value = null
                _statusMessage.value = e.localizedMessage ?: "AI Tutor failed to respond"
            }
        }
    }

    fun createBooking(
        bookingType: String,
        title: String,
        lecturerName: String,
        dateLabel: String,
        timeSlot: String,
        mode: String,
        priceLkr: Int
    ) {
        viewModelScope.launch {
            val record = BookingRecord(
                id = if (isPreviewMode) "bk_${System.currentTimeMillis()}" else "",
                userId = effectiveUid,
                bookingType = bookingType,
                title = title,
                lecturerName = lecturerName,
                dateLabel = dateLabel,
                timeSlot = timeSlot,
                mode = mode,
                status = "confirmed",
                priceLkr = priceLkr.toLong(),
                zoomUrl = "https://zoom.us/j/smartlabs-${effectiveUid.take(6)}"
            )
            if (isPreviewMode) {
                _previewBookings.value = listOf(record) + _previewBookings.value
                _statusMessage.value = "Booking confirmed locally (Preview)!"
                return@launch
            }
            val res = firestoreRepository.createBooking(record)
            _statusMessage.value = if (res.isSuccess) {
                "Booking confirmed! Added to Schedule."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Booking saved offline"
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            if (isPreviewMode) {
                _previewBookings.value = _previewBookings.value.map {
                    if (it.id == bookingId) it.copy(status = "cancelled") else it
                }
                _statusMessage.value = "Booking cancelled"
                return@launch
            }
            val res = firestoreRepository.cancelBooking(bookingId)
            _statusMessage.value = if (res.isSuccess) "Booking cancelled" else "Could not cancel booking"
        }
    }

    fun purchaseCredits(pool: CreditPoolType, quantity: Long) {
        viewModelScope.launch {
            if (isPreviewMode) {
                _previewProfile.value = _previewProfile.value?.let { adjustPool(it, pool, quantity) }
                _statusMessage.value = "+$quantity ${pool.label} credits added (Preview)!"
                return@launch
            }
            val res = firestoreRepository.addCredits(pool, quantity)
            _statusMessage.value = if (res.isSuccess) {
                "+$quantity ${pool.label} credits added!"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Purchase failed"
            }
        }
    }

    fun submitSupportTicket(subject: String, category: String, message: String) {
        viewModelScope.launch {
            if (isPreviewMode) {
                val tk = SupportTicket(
                    id = "tk_${System.currentTimeMillis()}",
                    userId = effectiveUid,
                    subject = subject,
                    category = category,
                    status = "open",
                    latestMessage = message
                )
                _previewTickets.value = listOf(tk) + _previewTickets.value
                _statusMessage.value = "$category request saved (Preview)"
                return@launch
            }
            val res = firestoreRepository.createSupportTicket(subject, category, message)
            _statusMessage.value = if (res.isSuccess) {
                "$category request submitted"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to submit ticket"
            }
        }
    }

    fun deleteAccountAndSignOut(onDeleted: () -> Unit) {
        viewModelScope.launch {
            if (isPreviewMode) {
                onDeleted()
                return@launch
            }
            val res = firestoreRepository.deleteAccountData()
            if (res.isSuccess) {
                onDeleted()
            } else {
                _statusMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to delete account data"
            }
        }
    }

    fun getPoolBalance(profile: UserProfile, pool: CreditPoolType): Long = when (pool) {
        CreditPoolType.SPEAKING -> profile.creditsSpeaking
        CreditPoolType.SST -> profile.creditsSst
        CreditPoolType.SWT -> profile.creditsSwt
        CreditPoolType.ESSAY -> profile.creditsEssay
        CreditPoolType.IELTS_ESSAY -> profile.creditsIeltsEssay
        CreditPoolType.MOCK -> profile.creditsMock
        CreditPoolType.IELTS_MOCK -> profile.creditsIeltsMock
        CreditPoolType.UNIVERSAL -> profile.creditsUniversal
    }

    private fun observeNetworkConnectivity(context: Context): Flow<Boolean> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        fun checkNow(): Boolean {
            val active = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(active) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        trySend(checkNow())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(checkNow())
            }
        }
        val req = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        try {
            cm.registerNetworkCallback(req, callback)
        } catch (_: Exception) {
            trySend(true)
        }
        awaitClose {
            try {
                cm.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
        }
    }
}
