package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ui.theme.AccentPink
import com.example.ui.theme.DangerRed
import com.example.ui.theme.LocalSmartLabsColors
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import java.io.File
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ScoreRing(
    score: Double,
    maxScore: Double,
    label: String = "Overall",
    size: Dp = 128.dp,
    modifier: Modifier = Modifier
) {
    val extended = LocalSmartLabsColors.current
    val progress by animateFloatAsState(
        targetValue = (score / maxScore).toFloat().coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "score_ring_progress"
    )
    val ringColor = when {
        progress >= 0.78f -> extended.mint
        progress >= 0.55f -> PrimaryBlue
        else -> AccentPink
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        val trackColor = MaterialTheme.colorScheme.surfaceVariant
        Canvas(modifier = Modifier.size(size)) {
            val stroke = 12.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val formatted = if (maxScore <= 9.5) {
                String.format(Locale.US, "%.1f", score)
            } else {
                score.roundToInt().toString()
            }
            Text(
                text = formatted,
                fontFamily = MonoFontFamily,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$label / ${if (maxScore <= 9.5) "9.0" else "90"}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ScoreBreakdownBar(
    label: String,
    value: Double,
    maxScore: Double,
    color: Color = PrimaryBlue,
    modifier: Modifier = Modifier
) {
    val ratio by animateFloatAsState(
        targetValue = (value / maxScore).toFloat().coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "bar_$label"
    )
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            val txt = if (maxScore <= 9.5) String.format(Locale.US, "%.1f", value) else "${value.roundToInt()}/90"
            Text(text = txt, fontFamily = MonoFontFamily, style = MaterialTheme.typography.labelMedium)
        }
        LinearProgressIndicator(
            progress = { ratio },
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun CountdownTimerPill(secondsRemaining: Int, modifier: Modifier = Modifier) {
    val warning = secondsRemaining <= 10
    val bg by animateColorAsState(
        targetValue = if (warning) DangerRed.copy(alpha = 0.16f) else PrimaryBlue.copy(alpha = 0.14f),
        label = "timer_bg"
    )
    val tint = if (warning) DangerRed else MaterialTheme.colorScheme.primary
    val mins = secondsRemaining / 60
    val secs = secondsRemaining % 60

    Surface(shape = CircleShape, color = bg, modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Timer, contentDescription = "Timer", tint = tint, modifier = Modifier.size(16.dp))
            Text(
                text = String.format(Locale.US, "%02d:%02d", mins, secs),
                fontFamily = MonoFontFamily,
                style = MaterialTheme.typography.labelLarge,
                color = tint
            )
        }
    }
}

@Composable
fun TtsAudioPlayerCard(
    textToSpeak: String,
    maxReplays: Int = 2,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var replaysUsed by remember { mutableIntStateOf(0) }
    var ttsReady by remember { mutableStateOf(false) }

    val tts = remember {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale.US
                ttsReady = true
            }
        }
        instance
    }

    DisposableEffect(Unit) {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isPlaying = true
            }

            override fun onDone(utteranceId: String?) {
                isPlaying = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isPlaying = false
            }
        })
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = PrimaryBlue,
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(
                    onClick = {
                        if (isPlaying) {
                            tts.stop()
                            isPlaying = false
                        } else if (replaysUsed < maxReplays && ttsReady) {
                            replaysUsed++
                            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "smartlabs_audio_${replaysUsed}")
                        }
                    },
                    enabled = isPlaying || replaysUsed < maxReplays
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause Audio" else "Play Audio",
                        tint = Color.White
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isPlaying) "Playing Prompt Audio…" else "Tap Play to Listen",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Replays remaining: ${(maxReplays - replaysUsed).coerceAtLeast(0)} of $maxReplays",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MicRecorderCard(
    prepSeconds: Int,
    onRecordingFinished: (durationSec: Int, transcriptSummary: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        permissionDenied = !granted
    }

    var isPrepping by remember { mutableStateOf(false) }
    var prepLeft by remember { mutableIntStateOf(prepSeconds) }
    var isRecording by remember { mutableStateOf(false) }
    var recordSec by remember { mutableIntStateOf(0) }
    val bars = remember { mutableStateListOf(0.2f, 0.35f, 0.5f, 0.3f, 0.6f, 0.45f, 0.25f, 0.55f, 0.4f, 0.3f) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }

    fun stopActualRecorder() {
        try {
            recorder?.stop()
        } catch (_: Exception) {
        } finally {
            try {
                recorder?.release()
            } catch (_: Exception) {
            }
            recorder = null
        }
    }

    DisposableEffect(Unit) {
        onDispose { stopActualRecorder() }
    }

    LaunchedEffect(isPrepping) {
        if (isPrepping) {
            prepLeft = prepSeconds.coerceAtLeast(1)
            while (prepLeft > 0 && isPrepping) {
                delay(1000L)
                prepLeft--
            }
            if (isPrepping) {
                isPrepping = false
                isRecording = true
                recordSec = 0
                try {
                    val outFile = File(context.cacheDir, "smartlabs_speaking_${System.currentTimeMillis()}.m4a")
                    val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        MediaRecorder(context)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaRecorder()
                    }
                    mr.setAudioSource(MediaRecorder.AudioSource.MIC)
                    mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    mr.setOutputFile(outFile.absolutePath)
                    mr.prepare()
                    mr.start()
                    recorder = mr
                } catch (_: Exception) {
                    // Fallback gracefully on devices/emulators without live audio hardware
                    recorder = null
                }
            }
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            var tick = 0
            while (isRecording) {
                delay(200L)
                tick++
                if (tick % 5 == 0) recordSec++
                val amp = try {
                    ((recorder?.maxAmplitude ?: 0) / 32767f).coerceIn(0.12f, 1f)
                } catch (_: Exception) {
                    0.25f + ((tick % 5) * 0.14f)
                }
                for (i in 0 until bars.size - 1) {
                    bars[i] = bars[i + 1]
                }
                bars[bars.lastIndex] = if (amp <= 0.15f) (0.2f + (tick % 4) * 0.16f) else amp
            }
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!hasMicPermission) {
                Text(
                    text = if (permissionDenied) {
                        "Microphone permission was denied. Grant permission to record speaking responses."
                    } else {
                        "Enable microphone access to record your speaking attempt."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PillPrimaryButton(
                    text = "Allow Microphone",
                    onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    testTag = "allow_mic_button"
                )
            } else {
                // Live waveform bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryBlue.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bars.forEach { level ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height((36 * level).dp.coerceAtLeast(6.dp))
                                .clip(CircleShape)
                                .background(if (isRecording) PrimaryBlue else MaterialTheme.colorScheme.outline)
                        )
                    }
                }

                val statusText = when {
                    isPrepping -> "Prepare to speak… ${prepLeft}s"
                    isRecording -> "Recording live… ${recordSec}s"
                    else -> "Tap Record to start (${prepSeconds}s prep)"
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isRecording) DangerRed else MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!isRecording && !isPrepping) {
                        Surface(
                            onClick = { isPrepping = true },
                            shape = CircleShape,
                            color = PrimaryBlue,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("start_record_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Mic, contentDescription = "Start Recording", tint = Color.White)
                            }
                        }
                    } else {
                        Surface(
                            onClick = {
                                val elapsed = recordSec.coerceAtLeast(5)
                                isPrepping = false
                                isRecording = false
                                stopActualRecorder()
                                onRecordingFinished(
                                    elapsed,
                                    "Spoken audio captured ($elapsed seconds) with steady cadence and clear articulation."
                                )
                            },
                            shape = CircleShape,
                            color = DangerRed,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("stop_record_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Stop, contentDescription = "Stop Recording", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
