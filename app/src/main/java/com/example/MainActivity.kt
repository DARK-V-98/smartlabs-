package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.AiRepository
import com.example.data.BiometricSecurityManager
import com.example.data.ExamType
import com.example.data.FirestoreRepository
import com.example.data.MockTestItem
import com.example.data.PracticeTaskItem
import com.example.data.UserPreferencesRepository
import com.example.data.configureAppCheck
import com.example.ui.auth.authStateFlow
import com.example.ui.auth.signOut
import com.example.ui.components.FloatingPillBottomBar
import com.example.ui.components.MainTab
import com.example.ui.components.SmartLabsNavigationRail
import com.example.ui.components.SmartLabsTopBar
import com.example.ui.screens.AuthAndOnboardingScreen
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.FullScreenMockRunner
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.LearnSubSection
import com.example.ui.screens.MeScreen
import com.example.ui.screens.MeSubSection
import com.example.ui.screens.PracticeHubScreen
import com.example.ui.screens.PracticeTrainerScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TestsScreen
import com.example.ui.theme.SmartLabsTheme
import com.example.ui.theme.ThemePreference
import com.example.ui.viewmodel.SmartLabsViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before Compose UI or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            SmartLabsRootApp(launchIntent = intent)
        }
    }
}

@Composable
fun SmartLabsRootApp(launchIntent: Intent?) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val scope = rememberCoroutineScope()
    val prefsRepo = remember { UserPreferencesRepository(context.applicationContext) }
    val localPrefs by prefsRepo.preferences.collectAsStateWithLifecycle(
        initialValue = com.example.data.LocalAppPreferences()
    )

    var showSplash by remember { mutableStateOf(true) }
    var isDirectPreview by rememberSaveable { mutableStateOf(false) }
    val currentUser by Firebase.auth.authStateFlow().collectAsStateWithLifecycle(
        initialValue = Firebase.auth.currentUser
    )

    SmartLabsTheme(themePreference = localPrefs.themePreference) {
        if (showSplash) {
            SplashScreen(onFinished = { showSplash = false })
        } else {
            val user = currentUser
            if (user == null && !isDirectPreview) {
                AuthAndOnboardingScreen(
                    onboardingSeen = localPrefs.onboardingSeen,
                    onOnboardingComplete = { scope.launch { prefsRepo.setOnboardingSeen(true) } },
                    onAuthSuccess = { isDirectPreview = false },
                    onEnterDirectPreview = { isDirectPreview = true }
                )
            } else {
                val uid = user?.uid
                val scopeKey = uid ?: "preview_guest"
                var biometricUnlocked by remember(scopeKey, localPrefs.biometricLockEnabled) {
                    mutableStateOf(uid == null || !localPrefs.biometricLockEnabled)
                }
                var biometricError by remember { mutableStateOf<String?>(null) }

                if (uid != null && !biometricUnlocked && localPrefs.biometricLockEnabled) {
                    LaunchedEffect(uid) {
                        if (activity != null) {
                            BiometricSecurityManager.authenticate(
                                activity = activity,
                                onSuccess = { biometricUnlocked = true },
                                onError = { msg -> biometricError = msg }
                            )
                        }
                    }
                    BiometricLockScreen(
                        userEmail = user.email ?: "SmartLabs Account",
                        errorText = biometricError,
                        onRequestUnlock = {
                            if (activity != null) {
                                biometricError = null
                                BiometricSecurityManager.authenticate(
                                    activity = activity,
                                    onSuccess = { biometricUnlocked = true },
                                    onError = { msg -> biometricError = msg }
                                )
                            } else {
                                biometricUnlocked = true
                            }
                        },
                        onSignOutClick = {
                            val cm = androidx.credentials.CredentialManager.create(context)
                            signOut(cm, scope)
                        }
                    )
                } else {
                    // Key authenticated/preview UI and ViewModel cleanly
                    key(scopeKey) {
                        val vm: SmartLabsViewModel = viewModel(
                            key = scopeKey,
                            factory = viewModelFactory {
                                initializer {
                                    val app = checkNotNull(this[APPLICATION_KEY])
                                    SmartLabsViewModel(
                                        firestoreRepository = FirestoreRepository(app),
                                        aiRepository = AiRepository(),
                                        prefsRepository = UserPreferencesRepository(app),
                                        currentUserId = uid,
                                        appContext = app
                                    )
                                }
                            }
                        )
                        AuthenticatedSmartLabsApp(
                            viewModel = vm,
                            launchIntent = launchIntent,
                            activity = activity,
                            isPreviewMode = (uid == null),
                            onExitPreviewToLogin = { isDirectPreview = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthenticatedSmartLabsApp(
    viewModel: SmartLabsViewModel,
    launchIntent: Intent?,
    activity: FragmentActivity?,
    isPreviewMode: Boolean = false,
    onExitPreviewToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val attemptsState by viewModel.attemptsState.collectAsStateWithLifecycle()
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
    val tutorState by viewModel.tutorMessagesState.collectAsStateWithLifecycle()
    val ticketsState by viewModel.ticketsState.collectAsStateWithLifecycle()
    val localPrefs by viewModel.localPreferences.collectAsStateWithLifecycle()
    val isSkeletonVisible by viewModel.isSkeletonVisible.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val filterState by viewModel.searchFilter.collectAsStateWithLifecycle()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val aiScoringState by viewModel.aiScoringState.collectAsStateWithLifecycle()
    val tutorStreamingText by viewModel.tutorStreamingText.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val profile = (profileState as? UiState.Success)?.data
    val attempts = (attemptsState as? UiState.Success)?.data ?: emptyList()
    val bookings = (bookingsState as? UiState.Success)?.data ?: emptyList()
    val tutorMessages = (tutorState as? UiState.Success)?.data ?: emptyList()
    val tickets = (ticketsState as? UiState.Success)?.data ?: emptyList()

    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    var learnSubSection by remember { mutableStateOf(LearnSubSection.COURSES) }
    var meSubSection by remember { mutableStateOf(MeSubSection.PROGRESS) }
    var activeTrainerTask by remember { mutableStateOf<PracticeTaskItem?>(null) }
    var activeMockTest by remember { mutableStateOf<MockTestItem?>(null) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var lastBackPressMs by remember { mutableLongStateOf(0L) }

    // Handle smartlabs.lk deep links cleanly
    LaunchedEffect(launchIntent) {
        val path = launchIntent?.data?.path.orEmpty()
        when {
            path.contains("practice") -> selectedTab = MainTab.PRACTICE
            path.contains("mock") || path.contains("tests") -> selectedTab = MainTab.TESTS
            path.contains("tutor") -> {
                selectedTab = MainTab.LEARN
                learnSubSection = LearnSubSection.AI_TUTOR
            }
            path.contains("one-to-one") -> {
                selectedTab = MainTab.LEARN
                learnSubSection = LearnSubSection.ONE_TO_ONE
            }
            path.contains("credits") -> {
                selectedTab = MainTab.ME
                meSubSection = MeSubSection.CREDITS
            }
        }
    }

    LaunchedEffect(statusMessage) {
        val msg = statusMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearStatusMessage()
    }

    // Back button behavior per spec: non-Home tab returns to Home; Home shows "Press back again to exit"
    if (activeTrainerTask == null && activeMockTest == null) {
        BackHandler {
            if (selectedTab != MainTab.HOME) {
                selectedTab = MainTab.HOME
            } else {
                val now = System.currentTimeMillis()
                if (now - lastBackPressMs < 2000L) {
                    activity?.finish()
                } else {
                    lastBackPressMs = now
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Full-screen Practice Trainer & Mock Runner hide the tab bar
    val activeMock = activeMockTest
    val activeTask = activeTrainerTask
    if (activeMock != null) {
        FullScreenMockRunner(
            mock = activeMock,
            onFinishMock = { score, duration, feedback ->
                viewModel.recordMockResult(activeMock.examType, activeMock.title, score, duration, feedback)
                activeMockTest = null
                selectedTab = MainTab.TESTS
            },
            onExitWithoutSaving = { activeMockTest = null }
        )
        return
    }
    if (activeTask != null) {
        PracticeTrainerScreen(
            task = activeTask,
            aiScoringState = aiScoringState,
            onSubmitAttempt = { resp, ratio, sec ->
                viewModel.submitPracticeAttempt(activeTask, resp, ratio, sec)
            },
            onResetScore = { viewModel.resetAiScoringState() },
            onClose = {
                viewModel.resetAiScoringState()
                activeTrainerTask = null
            }
        )
        return
    }

    val totalCredits = (profile?.creditsSpeaking ?: 8L) +
        (profile?.creditsSst ?: 5L) +
        (profile?.creditsSwt ?: 5L) +
        (profile?.creditsEssay ?: 4L) +
        (profile?.creditsIeltsEssay ?: 3L) +
        (profile?.creditsMock ?: 2L) +
        (profile?.creditsIeltsMock ?: 2L) +
        (profile?.creditsUniversal ?: 10L)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useRail) {
                SmartLabsNavigationRail(
                    selectedTab = selectedTab,
                    onSelectTab = { selectedTab = it }
                )
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    SmartLabsTopBar(
                        title = when (selectedTab) {
                            MainTab.HOME -> "SmartLabs"
                            MainTab.PRACTICE -> "Practice Hub"
                            MainTab.TESTS -> "Mock Tests"
                            MainTab.LEARN -> "Learn & Bookings"
                            MainTab.ME -> "Profile & Vault"
                        },
                        isSubScreen = false,
                        totalCredits = totalCredits,
                        unreadNotifications = 2,
                        onBackClick = { selectedTab = MainTab.HOME },
                        onCreditsClick = {
                            selectedTab = MainTab.ME
                            meSubSection = MeSubSection.CREDITS
                        },
                        onNotificationsClick = { showNotificationsDialog = true },
                        onToggleTheme = {
                            val next = when (localPrefs.themePreference) {
                                ThemePreference.DARK -> ThemePreference.LIGHT
                                ThemePreference.LIGHT -> ThemePreference.DARK
                                ThemePreference.SYSTEM -> ThemePreference.DARK
                            }
                            viewModel.setThemePreference(next)
                        }
                    )
                },
                bottomBar = {
                    if (!useRail) {
                        FloatingPillBottomBar(
                            selectedTab = selectedTab,
                            onSelectTab = { selectedTab = it }
                        )
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
                    when (selectedTab) {
                        MainTab.HOME -> HomeScreen(
                            isSkeletonVisible = isSkeletonVisible,
                            isOnline = isOnline,
                            profile = profile,
                            attempts = attempts,
                            bookings = bookings,
                            searchQuery = filterState.query,
                            onSearchChange = {
                                viewModel.updateSearchQuery(it)
                                if (it.isNotBlank()) selectedTab = MainTab.PRACTICE
                            },
                            onSearchFilterClick = { selectedTab = MainTab.PRACTICE },
                            onOpenCredits = {
                                selectedTab = MainTab.ME
                                meSubSection = MeSubSection.CREDITS
                            },
                            onContinuePractice = { task ->
                                viewModel.resetAiScoringState()
                                activeTrainerTask = task
                            },
                            onQuickPracticeExam = { exam ->
                                viewModel.updateExamFilter(exam)
                                selectedTab = MainTab.PRACTICE
                            },
                            onOpenTests = { selectedTab = MainTab.TESTS },
                            onOpenTutor = {
                                learnSubSection = LearnSubSection.AI_TUTOR
                                selectedTab = MainTab.LEARN
                            },
                            onOpenOneToOne = {
                                learnSubSection = LearnSubSection.ONE_TO_ONE
                                selectedTab = MainTab.LEARN
                            },
                            onOpenProfile = {
                                meSubSection = MeSubSection.PROGRESS
                                selectedTab = MainTab.ME
                            }
                        )

                        MainTab.PRACTICE -> PracticeHubScreen(
                            isSkeletonVisible = isSkeletonVisible,
                            tasks = filteredTasks,
                            filterState = filterState,
                            onSelectExam = { viewModel.updateExamFilter(it) },
                            onSelectSkill = { viewModel.updateSkillFilter(it) },
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            onUpdateAdvancedFilters = { ai, free -> viewModel.updateAdvancedFilters(ai, free) },
                            onResetFilters = { viewModel.resetFilters() },
                            onOpenTask = { task ->
                                viewModel.resetAiScoringState()
                                activeTrainerTask = task
                            }
                        )

                        MainTab.TESTS -> TestsScreen(
                            isSkeletonVisible = isSkeletonVisible,
                            attempts = attempts,
                            onStartMock = { mock -> activeMockTest = mock },
                            onOpenCredits = {
                                selectedTab = MainTab.ME
                                meSubSection = MeSubSection.CREDITS
                            }
                        )

                        MainTab.LEARN -> LearnScreen(
                            isSkeletonVisible = isSkeletonVisible,
                            initialSection = learnSubSection,
                            userEmail = profile?.email ?: "student@smartlabs.lk",
                            bookings = bookings,
                            tutorMessages = tutorMessages,
                            tutorStreamingText = tutorStreamingText,
                            onCreateBooking = { type, title, lec, date, slot, mode, price ->
                                viewModel.createBooking(type, title, lec, date, slot, mode, price)
                            },
                            onCancelBooking = { id -> viewModel.cancelBooking(id) },
                            onSendTutorMessage = { ctx, q -> viewModel.sendTutorMessage(ctx, q) }
                        )

                        MainTab.ME -> MeScreen(
                            initialSection = meSubSection,
                            profile = profile,
                            attempts = attempts,
                            bookings = bookings,
                            tickets = tickets,
                            localPrefs = localPrefs,
                            onUpdateGoal = { name, exam, target, dateLbl, weekly ->
                                viewModel.updateGoal(name, exam, target, dateLbl, weekly)
                            },
                            onBuyCredits = { pool, qty -> viewModel.purchaseCredits(pool, qty) },
                            onSubmitTicket = { subj, cat, msg -> viewModel.submitSupportTicket(subj, cat, msg) },
                            onSetTheme = { viewModel.setThemePreference(it) },
                            onToggleBiometric = { wantEnable ->
                                if (wantEnable && activity != null) {
                                    BiometricSecurityManager.authenticate(
                                        activity = activity,
                                        title = "Enable Biometric Vault",
                                        subtitle = "Confirm fingerprint, face, or device PIN to enable lock",
                                        onSuccess = { viewModel.setBiometricEnabled(true) },
                                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                                    )
                                } else {
                                    viewModel.setBiometricEnabled(wantEnable)
                                }
                            },
                            onToggleReminders = { viewModel.setRemindersEnabled(it) },
                            onToggleSound = { viewModel.setSoundEnabled(it) },
                            onToggleSkeletonPreview = { viewModel.setLowSignalSkeletonPreview(it) },
                            onToggleLecturerMode = { viewModel.setLecturerViewEnabled(it) },
                            onSignOut = {
                                if (isPreviewMode) {
                                    onExitPreviewToLogin()
                                } else {
                                    val cm = CredentialManager.create(context)
                                    signOut(cm, scope)
                                }
                            },
                            onDeleteAccount = {
                                if (isPreviewMode) {
                                    onExitPreviewToLogin()
                                } else {
                                    val cm = CredentialManager.create(context)
                                    viewModel.deleteAccountAndSignOut {
                                        signOut(cm, scope)
                                    }
                                }
                            },
                            isPreviewMode = isPreviewMode
                        )
                    }
                }
            }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Notifications") },
            text = {
                Column {
                    Text("• Live PTE Speaking Clinic starts tomorrow at 7:30 PM IST.")
                    Text("• Cambridge 21 Reading & AI Essay prompts synced to your offline vault.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) { Text("Done") }
            }
        )
    }
}
