package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ExamType
import com.example.data.PracticeTaskItem
import com.example.data.SkillCategory
import com.example.data.TrainerInteractionMode
import com.example.ui.components.CardListSkeletonLoader
import com.example.ui.components.CountdownTimerPill
import com.example.ui.components.GlassCard
import com.example.ui.components.MicRecorderCard
import com.example.ui.components.PillPrimaryButton
import com.example.ui.components.PillSecondaryButton
import com.example.ui.components.RoundSearchAndFilterBar
import com.example.ui.components.ScoreBreakdownBar
import com.example.ui.components.ScoreRing
import com.example.ui.components.SegmentedExamSwitch
import com.example.ui.components.TtsAudioPlayerCard
import com.example.ui.theme.AccentPink
import com.example.ui.theme.LocalSmartLabsColors
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.AiScoringState
import com.example.ui.viewmodel.SearchFilterState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeHubScreen(
    isSkeletonVisible: Boolean,
    tasks: List<PracticeTaskItem>,
    filterState: SearchFilterState,
    onSelectExam: (ExamType) -> Unit,
    onSelectSkill: (SkillCategory) -> Unit,
    onQueryChange: (String) -> Unit,
    onUpdateAdvancedFilters: (Boolean, Boolean) -> Unit,
    onResetFilters: () -> Unit,
    onOpenTask: (PracticeTaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val selectedExam = filterState.examFilter ?: ExamType.PTE

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 980.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            SegmentedExamSwitch(
                selected = selectedExam,
                onSelect = { onSelectExam(it) }
            )

            RoundSearchAndFilterBar(
                query = filterState.query,
                onQueryChange = onQueryChange,
                onFilterClick = { showFilterSheet = true },
                hasActiveFilters = filterState.aiScoredOnly || filterState.freeOnly
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SkillCategory.entries) { skill ->
                    val selected = skill == filterState.skillFilter
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectSkill(skill) },
                        label = { Text(skill.label) },
                        shape = CircleShape
                    )
                }
            }

            if (isSkeletonVisible) {
                CardListSkeletonLoader(count = 5)
            } else if (tasks.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("No matching tasks found", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Try clearing your search or advanced filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        PillSecondaryButton(text = "Reset Filters", onClick = onResetFilters)
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.weight(1f)) {
                    val columns = if (maxWidth >= 600.dp) 2 else 1
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        contentPadding = PaddingValues(bottom = 108.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            PracticeTaskCard(task = task, onClick = { onOpenTask(task) })
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        var aiOnly by remember { mutableStateOf(filterState.aiScoredOnly) }
        var freeOnly by remember { mutableStateOf(filterState.freeOnly) }
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Advanced Practice Filters", style = MaterialTheme.typography.titleLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AI Scored Only", style = MaterialTheme.typography.titleSmall)
                        Text("Show tasks powered by Gemini instant scoring", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = aiOnly, onCheckedChange = { aiOnly = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Free / Zero Credit Only", style = MaterialTheme.typography.titleSmall)
                        Text("Reading, Listening & Skimming drills", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = freeOnly, onCheckedChange = { freeOnly = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PillSecondaryButton(
                        text = "Reset",
                        onClick = {
                            onResetFilters()
                            showFilterSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PillPrimaryButton(
                        text = "Apply Filters",
                        onClick = {
                            onUpdateAdvancedFilters(aiOnly, freeOnly)
                            showFilterSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PracticeTaskCard(task: PracticeTaskItem, onClick: () -> Unit) {
    val extended = LocalSmartLabsColors.current
    val skillColor = when (task.skill) {
        SkillCategory.SPEAKING -> PrimaryBlue
        SkillCategory.WRITING -> AccentPink
        SkillCategory.READING -> extended.mint
        SkillCategory.LISTENING -> extended.amber
        SkillCategory.ALL -> PrimaryBlue
    }

    GlassCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = skillColor.copy(alpha = 0.16f)) {
                    Text(
                        text = task.skill.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = skillColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                if (task.aiScored) {
                    Surface(shape = CircleShape, color = PrimaryBlue.copy(alpha = 0.14f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(12.dp))
                            Text("AI Scored", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue)
                        }
                    }
                }
            }
            Text(
                text = if (task.creditCost == 0) "Free" else "${task.creditCost} ${task.creditPool.label}",
                fontFamily = MonoFontFamily,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = task.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = task.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${task.questionCount} questions · ${task.timeLimitSeconds / 60}m ${task.timeLimitSeconds % 60}s",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(shape = CircleShape, color = PrimaryBlue, modifier = Modifier.size(34.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Start ${task.title}", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun PracticeTrainerScreen(
    task: PracticeTaskItem,
    aiScoringState: AiScoringState,
    onSubmitAttempt: (userResponse: String, objectiveRatio: Double?, durationSec: Int) -> Unit,
    onResetScore: () -> Unit,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    var secondsLeft by remember(task.id) { mutableIntStateOf(task.timeLimitSeconds) }
    var textAnswer by remember(task.id) { mutableStateOf("") }
    var spokenCaptureNote by remember(task.id) { mutableStateOf("") }
    var selectedSingle by remember(task.id) { mutableIntStateOf(-1) }
    val selectedMultiple = remember(task.id) { mutableStateListOf<Int>() }
    var selectedBankWord by remember(task.id) { mutableStateOf<String?>(null) }
    val reordered = remember(task.id) { mutableStateListOf(*task.reorderItems.toTypedArray()) }

    LaunchedEffect(task.id, aiScoringState) {
        if (aiScoringState is AiScoringState.Idle) {
            secondsLeft = task.timeLimitSeconds
            while (secondsLeft > 0) {
                delay(1000L)
                secondsLeft--
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .navigationBarsPadding()
    ) {
        // Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close Trainer")
                    }
                    Column {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${task.examType.label} · ${task.skill.label} · Q 1/${task.questionCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                CountdownTimerPill(secondsRemaining = secondsLeft)
            }
        }

        // Main Split/Scroll Content (side-by-side on wide screens > 840dp)
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val isWide = maxWidth >= 840.dp
            if (aiScoringState is AiScoringState.Scored) {
                val eval = aiScoringState.evaluation
                val extended = LocalSmartLabsColors.current
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("AI Score Report", style = MaterialTheme.typography.titleLarge)
                                ScoreRing(score = eval.overallScore, maxScore = eval.maxScore)
                                ScoreBreakdownBar("Content & Task", eval.contentScore, eval.maxScore, PrimaryBlue)
                                ScoreBreakdownBar("Fluency & Coherence", eval.fluencyScore, eval.maxScore, extended.mint)
                                ScoreBreakdownBar("Pronunciation / Lexis", eval.pronunciationScore, eval.maxScore, AccentPink)
                                ScoreBreakdownBar("Grammar & Mechanics", eval.grammarScore, eval.maxScore, extended.amber)
                            }
                        }
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text("Examiner Feedback", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(eval.feedback, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text("Model High-Score Answer", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(eval.modelAnswer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PillSecondaryButton(
                                text = "Try Again",
                                onClick = {
                                    onResetScore()
                                    textAnswer = ""
                                    spokenCaptureNote = ""
                                },
                                modifier = Modifier.weight(1f)
                            )
                            PillPrimaryButton(
                                text = "Done",
                                onClick = onClose,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            } else {
                val promptPane: @Composable () -> Unit = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(Brush.linearGradient(listOf(OceanDeep, PrimaryBlue)))
                                .padding(18.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(task.promptText, style = MaterialTheme.typography.titleSmall, color = Color.White)
                                if (task.passageOrTranscript.isNotBlank() &&
                                    task.mode != TrainerInteractionMode.LISTENING_AUDIO &&
                                    task.title != "Repeat Sentence" &&
                                    task.title != "Write From Dictation"
                                ) {
                                    Text(
                                        text = task.passageOrTranscript,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFE6F0FF)
                                    )
                                }
                            }
                        }

                        if (task.title == "Describe Image") {
                            Image(
                                painter = painterResource(id = R.drawable.img_pte_studio),
                                contentDescription = "Task Stimulus Chart",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(18.dp))
                            )
                        }

                        if (task.mode == TrainerInteractionMode.LISTENING_AUDIO ||
                            task.title == "Repeat Sentence" ||
                            task.title == "Retell Lecture" ||
                            task.title == "Summarize Spoken Text" ||
                            task.title == "Write From Dictation" ||
                            task.title == "Answer Short Question" ||
                            task.title == "Summarize Group Discussion"
                        ) {
                            TtsAudioPlayerCard(textToSpeak = task.passageOrTranscript, maxReplays = 2)
                        }
                    }
                }

                val answerPane: @Composable () -> Unit = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        when (task.mode) {
                            TrainerInteractionMode.SPEAKING_MIC -> {
                                MicRecorderCard(
                                    prepSeconds = task.prepSeconds,
                                    onRecordingFinished = { _, summary ->
                                        spokenCaptureNote = summary
                                    }
                                )
                                OutlinedTextField(
                                    value = textAnswer,
                                    onValueChange = { textAnswer = it },
                                    label = { Text("Optional Spoken Transcript / Notes for Gemini AI") },
                                    placeholder = { Text("Speak with mic or add spoken notes to score…") },
                                    minLines = 3,
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            TrainerInteractionMode.WRITING_TEXT -> {
                                val wordCount = textAnswer.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
                                OutlinedTextField(
                                    value = textAnswer,
                                    onValueChange = { textAnswer = it },
                                    label = { Text("Your Response") },
                                    placeholder = { Text("Type your academic response here…") },
                                    minLines = 7,
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("trainer_writing_input")
                                )
                                Text(
                                    text = "Word count: $wordCount words",
                                    fontFamily = MonoFontFamily,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PrimaryBlue
                                )
                            }

                            TrainerInteractionMode.READING_DROPDOWN,
                            TrainerInteractionMode.MCQ_SINGLE,
                            TrainerInteractionMode.LISTENING_AUDIO -> {
                                task.options.forEachIndexed { idx, opt ->
                                    val chosen = selectedSingle == idx
                                    Surface(
                                        onClick = { selectedSingle = idx },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (chosen) PrimaryBlue.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, if (chosen) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(selected = chosen, onClick = { selectedSingle = idx })
                                            Text(opt, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }

                            TrainerInteractionMode.MCQ_MULTIPLE -> {
                                task.options.forEachIndexed { idx, opt ->
                                    val chosen = selectedMultiple.contains(idx)
                                    Surface(
                                        onClick = {
                                            if (chosen) selectedMultiple.remove(idx) else selectedMultiple.add(idx)
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (chosen) PrimaryBlue.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, if (chosen) PrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = chosen,
                                                onCheckedChange = {
                                                    if (chosen) selectedMultiple.remove(idx) else selectedMultiple.add(idx)
                                                }
                                            )
                                            Text(opt, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }

                            TrainerInteractionMode.READING_DRAG_BLANKS -> {
                                Text("Word Bank (Tap to fill blank):", style = MaterialTheme.typography.titleSmall)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    task.wordBank.forEach { word ->
                                        val active = selectedBankWord == word
                                        Surface(
                                            onClick = { selectedBankWord = word },
                                            shape = CircleShape,
                                            color = if (active) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = word,
                                                color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            TrainerInteractionMode.REORDER_PARAGRAPHS -> {
                                reordered.forEachIndexed { idx, paragraph ->
                                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "${idx + 1}.",
                                                fontFamily = MonoFontFamily,
                                                color = PrimaryBlue
                                            )
                                            Text(
                                                text = paragraph,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Column {
                                                IconButton(
                                                    onClick = {
                                                        if (idx > 0) {
                                                            val tmp = reordered[idx - 1]
                                                            reordered[idx - 1] = reordered[idx]
                                                            reordered[idx] = tmp
                                                        }
                                                    },
                                                    enabled = idx > 0
                                                ) {
                                                    Icon(Icons.Rounded.ArrowUpward, contentDescription = "Move Up")
                                                }
                                                IconButton(
                                                    onClick = {
                                                        if (idx < reordered.lastIndex) {
                                                            val tmp = reordered[idx + 1]
                                                            reordered[idx + 1] = reordered[idx]
                                                            reordered[idx] = tmp
                                                        }
                                                    },
                                                    enabled = idx < reordered.lastIndex
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDownward, contentDescription = "Move Down")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (aiScoringState is AiScoringState.Error) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = aiScoringState.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                if (isWide) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) { promptPane() }
                        Box(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) { answerPane() }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        promptPane()
                        answerPane()
                    }
                }
            }
        }

        // Bottom Submit Bar (hidden on Score Result screen)
        if (aiScoringState !is AiScoringState.Scored) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        textAnswer = ""
                        selectedSingle = -1
                        selectedMultiple.clear()
                        selectedBankWord = null
                    }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset")
                    }

                    PillPrimaryButton(
                        text = if (aiScoringState is AiScoringState.Scoring) "Scoring with AI…" else "Submit & Score",
                        enabled = aiScoringState !is AiScoringState.Scoring,
                        onClick = {
                            val elapsed = (task.timeLimitSeconds - secondsLeft).coerceAtLeast(5)
                            val finalResponse = when (task.mode) {
                                TrainerInteractionMode.SPEAKING_MIC -> listOf(spokenCaptureNote, textAnswer).filter { it.isNotBlank() }.joinToString(" | ").ifBlank { "Recorded response for ${task.title}." }
                                TrainerInteractionMode.WRITING_TEXT -> textAnswer.ifBlank { "Draft response submitted for ${task.title}." }
                                TrainerInteractionMode.READING_DROPDOWN,
                                TrainerInteractionMode.MCQ_SINGLE,
                                TrainerInteractionMode.LISTENING_AUDIO -> task.options.getOrNull(selectedSingle) ?: "No option selected"
                                TrainerInteractionMode.MCQ_MULTIPLE -> selectedMultiple.mapNotNull { task.options.getOrNull(it) }.joinToString("; ")
                                TrainerInteractionMode.READING_DRAG_BLANKS -> selectedBankWord ?: "Blank"
                                TrainerInteractionMode.REORDER_PARAGRAPHS -> reordered.joinToString(" -> ")
                            }
                            val ratio: Double? = when (task.mode) {
                                TrainerInteractionMode.READING_DROPDOWN,
                                TrainerInteractionMode.MCQ_SINGLE,
                                TrainerInteractionMode.LISTENING_AUDIO -> if (selectedSingle in task.correctIndices) 1.0 else 0.35
                                TrainerInteractionMode.MCQ_MULTIPLE -> {
                                    val hits = selectedMultiple.count { it in task.correctIndices }
                                    (hits.toDouble() / task.correctIndices.size.coerceAtLeast(1)).coerceIn(0.2, 1.0)
                                }
                                TrainerInteractionMode.READING_DRAG_BLANKS -> if (selectedBankWord == task.wordBank.firstOrNull()) 1.0 else 0.3
                                TrainerInteractionMode.REORDER_PARAGRAPHS -> 0.85
                                else -> null
                            }
                            onSubmitAttempt(finalResponse, ratio, elapsed)
                        },
                        testTag = "submit_trainer_button"
                    )
                }
            }
        }
    }
}
