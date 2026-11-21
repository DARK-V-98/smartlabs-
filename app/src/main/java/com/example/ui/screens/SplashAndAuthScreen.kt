package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.ui.auth.attemptAutoSignIn
import com.example.ui.auth.onGoogleSignInClicked
import com.example.ui.theme.AccentMint
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.OceanMid
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RoyalBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "splash_pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulse_scale"
    )

    LaunchedEffect(Unit) {
        delay(1200L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(OceanDeep, OceanMid, RoyalBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0x28FFFFFF),
                border = BorderStroke(1.5.dp, Color(0x66FFFFFF)),
                modifier = Modifier
                    .size(96.dp)
                    .scale(pulse)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "SmartLabs",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Text(
                text = "SmartLabs",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
            Text(
                text = "AI-Powered PTE & IELTS Mastery",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFDCE8FF),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AuthAndOnboardingScreen(
    onboardingSeen: Boolean,
    onOnboardingComplete: () -> Unit,
    onAuthSuccess: () -> Unit,
    onEnterDirectPreview: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var slideIndex by remember { mutableIntStateOf(if (onboardingSeen) 3 else 0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = onAuthSuccess,
            onUnauthenticated = {},
            scope = scope
        )
    }

    val slides = listOf(
        Triple(
            "Instant AI Scoring",
            "Get real-time Gemini PTE (0–90) & IELTS Band (0–9) breakdowns for Speaking, Writing, and Mock Exams.",
            "AI Coach"
        ),
        Triple(
            "Full Timed Mock Labs",
            "Practice authentic PTE & Cambridge 11–21 IELTS papers with offline sync even on low signal.",
            "Exam Sim"
        ),
        Triple(
            "1-on-1 Lecturers & Vault",
            "Book Sri Lanka's top examiners in 15-minute slots, secured with Google OAuth 2.0 & Biometrics.",
            "Live & Secure"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_smartlabs_hero),
            contentDescription = "SmartLabs Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x99061539),
                            Color(0xD90B2B6B),
                            Color(0xF5061539)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0x2EFFFFFF),
                    border = BorderStroke(1.dp, Color(0x4DFFFFFF))
                ) {
                    Text(
                        text = "SMARTLABS.LK",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
                if (slideIndex < 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = {
                                onOnboardingComplete()
                                onEnterDirectPreview()
                            },
                            modifier = Modifier.testTag("top_direct_preview_button")
                        ) {
                            Text("Preview App", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(onClick = {
                            onOnboardingComplete()
                            slideIndex = 3
                        }) {
                            Text("Skip", color = Color(0xFFDCE8FF))
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
            ) {
                AnimatedContent(targetState = slideIndex, label = "onboarding_slide") { idx ->
                    if (idx < 3) {
                        val (title, body, chip) = slides[idx]
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = Color(0x2BFFFFFF),
                            border = BorderStroke(1.dp, Color(0x4DFFFFFF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(26.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryBlue.copy(alpha = 0.85f)
                                ) {
                                    Text(
                                        text = chip,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = body,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFFE2ECFF)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        repeat(3) { dot ->
                                            Box(
                                                modifier = Modifier
                                                    .size(width = if (dot == idx) 22.dp else 8.dp, height = 8.dp)
                                                    .background(
                                                        if (dot == idx) Color.White else Color(0x55FFFFFF),
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }
                                    Surface(
                                        onClick = {
                                            if (idx == 2) onOnboardingComplete()
                                            slideIndex = idx + 1
                                        },
                                        shape = CircleShape,
                                        color = Color.White,
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                                contentDescription = "Next",
                                                tint = OceanDeep
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Sign-in Glass Card
                        Surface(
                            shape = RoundedCornerShape(30.dp),
                            color = Color(0x2EFFFFFF),
                            border = BorderStroke(1.dp, Color(0x4DFFFFFF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(26.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = AccentMint.copy(alpha = 0.22f),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.VerifiedUser,
                                            contentDescription = "OAuth 2.0 Protected",
                                            tint = AccentMint,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Welcome to SmartLabs",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Sign in with Google OAuth 2.0 to sync credits, AI scorecards, and offline mocks across devices.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFDCE8FF),
                                    textAlign = TextAlign.Center
                                )

                                if (errorMsg != null) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0x33EF4444),
                                        border = BorderStroke(1.dp, Color(0x88EF4444)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = errorMsg!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        isLoading = true
                                        errorMsg = null
                                        onGoogleSignInClicked(
                                            context = context,
                                            credentialManager = credentialManager,
                                            onAuthSuccess = {
                                                isLoading = false
                                                onAuthSuccess()
                                            },
                                            onAuthError = { msg ->
                                                isLoading = false
                                                errorMsg = msg
                                            },
                                            scope = scope,
                                            onAuthCancelled = {
                                                isLoading = false
                                            }
                                        )
                                    },
                                    enabled = !isLoading,
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = OceanDeep
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .minimumInteractiveComponentSize()
                                        .testTag("google_sign_in_button")
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = OceanDeep,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Continue with Google",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                androidx.compose.material3.OutlinedButton(
                                    onClick = {
                                        onOnboardingComplete()
                                        onEnterDirectPreview()
                                    },
                                    enabled = !isLoading,
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color(0x88FFFFFF)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color(0x24FFFFFF),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .minimumInteractiveComponentSize()
                                        .testTag("direct_preview_button")
                                ) {
                                    Text(
                                        text = "Enter App Directly (Preview)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFB3C7E6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Protected by App Check & Android Keystore",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFB3C7E6)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BiometricLockScreen(
    userEmail: String,
    errorText: String?,
    onRequestUnlock: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(OceanDeep, OceanMid, RoyalBlue)))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = Color(0x26FFFFFF),
            border = BorderStroke(1.dp, Color(0x4DFFFFFF)),
            modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryBlue.copy(alpha = 0.4f),
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f)),
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Fingerprint,
                            contentDescription = "Biometric Vault",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
                Text(
                    text = "SmartLabs Vault Locked",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Signed in as $userEmail\nAuthenticate with biometrics or device PIN to access your personal study records.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDCE8FF),
                    textAlign = TextAlign.Center
                )
                if (errorText != null) {
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFB4AB),
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = onRequestUnlock,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = OceanDeep),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("biometric_unlock_button")
                ) {
                    Text("Unlock with Biometrics", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onSignOutClick) {
                    Text("Switch Account / Sign Out", color = Color(0xFFDCE8FF))
                }
            }
        }
    }
}
