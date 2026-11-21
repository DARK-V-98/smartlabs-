package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.ExamType
import com.example.data.MockTestItem
import com.example.data.PracticeAttempt
import com.example.data.SmartLabsCatalog
import com.example.ui.components.CardListSkeletonLoader
import com.example.ui.components.CountdownTimerPill
import com.example.ui.components.GlassCard
import com.example.ui.components.PillPrimaryButton
import com.example.ui.components.PillSecondaryButton
import com.example.ui.components.ScoreBreakdownBar
import com.example.ui.components.ScoreRing
import com.example.ui.components.SegmentedExamSwitch
import com.example.ui.theme.LocalSmartLabsColors
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

@Composable
fun TestsScreen(
    isSkeletonVisible: Boolean,
    attempts: List<PracticeAttempt>,
    onStartMock: (MockTestItem) -> Unit,
    onOpenCredits: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExam by remember { mutableStateOf(ExamType.PTE) }
    var selectedResult by remember(attempts) { mutableStateOf(attempts.firstOrNull()) }
    val mocks = remember(selectedExam) {
        SmartLabsCatalog.mockTests.filter { it.examType == selectedExam || it.isLevelTest }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 840.dp
        if (isSkeletonVisible) {
            CardListSkeletonLoader(count = 5, modifier = Modifier.widthIn(max = 980.dp))
        } else if (isWide) {
            // Tablet List-Detail Layout (> 840dp)
            Row(
                modifier = Modifier
                    .widthIn(max = 1120.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 108.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SegmentedExamSwitch(selected = selectedExam, onSelect = { selectedExam = it })
                    }
                    item { Text("Mock Tests & Placement", style = MaterialTheme.typography.titleMedium) }
                    items(mocks, key = { it.id }) { mock ->
                        MockTestCard(mock = mock, onStart = { onStartMock(mock) }, onUnlock = onOpenCredits)
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recent Scorecards", style = MaterialTheme.typography.titleMedium)
                    }
                    items(attempts.take(10), key = { it.id.ifBlank { it.taskTitle + it.durationSeconds } }) { attempt ->
                        AttemptSummaryRow(
                            attempt = attempt,
                            selected = attempt == selectedResult,
                            onClick = { selectedResult = attempt }
                        )
                    }
                }

                // Detail Pane on wide tablets
                Box(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
                    ResultDetailCard(attempt = selectedResult)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 108.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    SegmentedExamSwitch(selected = selectedExam, onSelect = { selectedExam = it })
                }
                item {
                    Text("Mock Tests & Benchmarks", style = MaterialTheme.typography.titleMedium)
                }
                items(mocks, key = { it.id }) { mock ->
                    MockTestCard(mock = mock, onStart = { onStartMock(mock) }, onUnlock = onOpenCredits)
                }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Past Results (${attempts.size})", style = MaterialTheme.typography.titleMedium)
                }
                if (attempts.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No test results recorded yet. Complete a practice task or mock exam to see your AI scorecard.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(attempts.take(8), key = { it.id.ifBlank { it.taskTitle + it.durationSeconds } }) { attempt ->
                        AttemptSummaryRow(
                            attempt = attempt,
                            selected = attempt == selectedResult,
                            onClick = { selectedResult = attempt }
                        )
                    }
                    if (selectedResult != null) {
                        item { ResultDetailCard(attempt = selectedResult) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MockTestCard(
    mock: MockTestItem,
    onStart: () -> Unit,
    onUnlock: () -> Unit
) {
    val extended = LocalSmartLabsColors.current
    GlassCard(modifier = Modifier.fillMaxWidth().testTag("mock_card_${mock.id}")) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (mock.isLevelTest) extended.mint.copy(alpha = 0.16f) else PrimaryBlue.copy(alpha = 0.14f)
            ) {
                Text(
                    text = if (mock.isLevelTest) "Placement Test" else "${mock.examType.label} · ${mock.durationMinutes}m",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (mock.isLevelTest) extended.mint else PrimaryBlue,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Text(
                text = if (mock.priceLkr == 0) "Free" else "LKR ${mock.priceLkr}",
                fontFamily = MonoFontFamily,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(mock.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(mock.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mock.sections.joinToString(" · "),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (mock.unlocked) {
                PillPrimaryButton(text = "Start", onClick = onStart, testTag = "start_mock_${mock.id}")
            } else {
                PillSecondaryButton(text = "Unlock (${mock.creditCost} cr)", onClick = onUnlock)
            }
        }
    }
}

@Composable
private fun AttemptSummaryRow(
    attempt: PracticeAttempt,
    selected: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(attempt.taskTitle, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${attempt.examType} · ${attempt.skill} · ${attempt.durationSeconds}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = CircleShape,
                color = if (selected) PrimaryBlue else PrimaryBlue.copy(alpha = 0.14f)
            ) {
                Text(
                    text = if (attempt.maxScore <= 9.5) String.format("%.1f", attempt.overallScore) else "${attempt.overallScore.toInt()}/90",
                    fontFamily = MonoFontFamily,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else PrimaryBlue,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ResultDetailCard(attempt: PracticeAttempt?) {
    val extended = LocalSmartLabsColors.current
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        if (attempt == null) {
            Text("Select a scorecard to inspect breakdown", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Tap any past test or practice attempt to review sub-skill analytics and Gemini examiner feedback.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(attempt.taskTitle, style = MaterialTheme.typography.titleLarge)
                ScoreRing(score = attempt.overallScore, maxScore = attempt.maxScore)
                ScoreBreakdownBar("Content", attempt.contentScore, attempt.maxScore, PrimaryBlue)
                ScoreBreakdownBar("Fluency", attempt.fluencyScore, attempt.maxScore, extended.mint)
                ScoreBreakdownBar("Grammar", attempt.grammarScore, attempt.maxScore, extended.amber)
                Text(
                    text = attempt.aiFeedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FullScreenMockRunner(
    mock: MockTestItem,
    onFinishMock: (score: Double, durationSeconds: Int, feedback: String) -> Unit,
    onExitWithoutSaving: () -> Unit
) {
    var showLeaveDialog by remember { mutableStateOf(false) }
    var sectionIndex by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(mock.durationMinutes * 60) }
    var selectedOption by remember(sectionIndex) { mutableIntStateOf(-1) }
    var essayText by remember(sectionIndex) { mutableStateOf("") }
    var correctCount by remember { mutableIntStateOf(0) }

    BackHandler { showLeaveDialog = true }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }

    val currentSection = mock.sections.getOrElse(sectionIndex) { "Summary" }
    val progress = (sectionIndex + 1).toFloat() / mock.sections.size.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { showLeaveDialog = true }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Leave Test")
                        }
                        Column {
                            Text(mock.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Section ${sectionIndex + 1}/${mock.sections.size}: $currentSection",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    CountdownTimerPill(secondsRemaining = secondsLeft)
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(OceanDeep, PrimaryBlue)))
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("$currentSection Module Prompt", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text(
                        when (currentSection) {
                            "Writing" -> "Write a clear academic response arguing whether AI tutors complement human teachers in higher education (100+ words recommended)."
                            "Reading" -> "Passage: Geothermal district heating systems circulate closed-loop fluid underground, cutting winter building emissions by 62% across Nordic municipalities."
                            else -> "Listen & evaluate: Identify the primary policy benefit described in the Nordic infrastructure briefing."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2ECFF)
                    )
                }
            }

            if (currentSection == "Writing") {
                OutlinedTextField(
                    value = essayText,
                    onValueChange = { essayText = it },
                    label = { Text("Exam Response") },
                    minLines = 6,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                val options = listOf(
                    "Geothermal closed-loop heating cuts winter emissions by 62%.",
                    "Geothermal plants only operate during summer months.",
                    "Nordic cities abandoned underground fluid loops."
                )
                options.forEachIndexed { idx, opt ->
                    val chosen = selectedOption == idx
                    Surface(
                        onClick = { selectedOption = idx },
                        shape = RoundedCornerShape(16.dp),
                        color = if (chosen) PrimaryBlue.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (chosen) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = chosen, onClick = { selectedOption = idx })
                            Text(opt, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                PillPrimaryButton(
                    text = if (sectionIndex < mock.sections.lastIndex) "Next Section" else "Submit Mock Exam",
                    onClick = {
                        if (selectedOption == 0 || essayText.length > 30) correctCount++
                        if (sectionIndex < mock.sections.lastIndex) {
                            sectionIndex++
                        } else {
                            val elapsed = ((mock.durationMinutes * 60) - secondsLeft).coerceAtLeast(30)
                            val ratio = (correctCount.toDouble() / mock.sections.size.coerceAtLeast(1)).coerceIn(0.4, 1.0)
                            val finalScore = if (mock.examType == ExamType.IELTS) {
                                ((5.5 + 3.0 * ratio) * 2).toInt() / 2.0
                            } else {
                                (54.0 + 30.0 * ratio)
                            }
                            onFinishMock(
                                finalScore,
                                elapsed,
                                "Completed ${mock.sections.size} sections of ${mock.title}. Strong pacing across timed segments."
                            )
                        }
                    },
                    testTag = "mock_next_submit_button"
                )
            }
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Leave test?") },
            text = { Text("Your progress in this timed mock exam will be lost.") },
            confirmButton = {
                TextButton(onClick = {
                    showLeaveDialog = false
                    onExitWithoutSaving()
                }) {
                    Text("Leave Test", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Continue Exam")
                }
            }
        )
    }
}
