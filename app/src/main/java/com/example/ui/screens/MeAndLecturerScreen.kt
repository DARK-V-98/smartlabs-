package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.SwitchAccount
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.BookingRecord
import com.example.data.CreditPoolType
import com.example.data.LocalAppPreferences
import com.example.data.PracticeAttempt
import com.example.data.SmartLabsCatalog
import com.example.data.SupportTicket
import com.example.data.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.PillPrimaryButton
import com.example.ui.components.PillSecondaryButton
import com.example.ui.theme.LocalSmartLabsColors
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.ThemePreference

enum class MeSubSection(val label: String) {
    PROGRESS("Progress"),
    CREDITS("Credits"),
    SUPPORT("Support & Certs"),
    SETTINGS("Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    initialSection: MeSubSection,
    profile: UserProfile?,
    attempts: List<PracticeAttempt>,
    bookings: List<BookingRecord>,
    tickets: List<SupportTicket>,
    localPrefs: LocalAppPreferences,
    onUpdateGoal: (name: String, exam: String, target: Double, dateLabel: String, weekly: Long) -> Unit,
    onBuyCredits: (CreditPoolType, Long) -> Unit,
    onSubmitTicket: (subject: String, category: String, msg: String) -> Unit,
    onSetTheme: (ThemePreference) -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onToggleReminders: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleSkeletonPreview: (Boolean) -> Unit,
    onToggleLecturerMode: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
    isPreviewMode: Boolean = false
) {
    var activeSection by remember(initialSection) { mutableStateOf(initialSection) }
    var showEditGoalSheet by remember { mutableStateOf(false) }
    var showTicketSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showPoliciesDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        if (profile?.lecturerViewEnabled == true) {
            LecturerModePanel(
                bookings = bookings,
                onExitLecturerMode = { onToggleLecturerMode(false) }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 860.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 108.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile Header
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryBlue.copy(alpha = 0.16f),
                                modifier = Modifier.size(62.dp)
                            ) {
                                if (!profile?.photoUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = profile?.photoUrl,
                                        contentDescription = "Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = (profile?.displayName ?: "S").take(1).uppercase(),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(profile?.displayName ?: "SmartLabs Student", style = MaterialTheme.typography.titleLarge)
                                Text(profile?.email ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "Goal: ${profile?.examGoal ?: "PTE"} · Target ${profile?.targetScore ?: 79.0}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PrimaryBlue
                                )
                            }
                            IconButton(onClick = { showEditGoalSheet = true }) {
                                Icon(Icons.Rounded.Edit, contentDescription = "Edit Profile Goal")
                            }
                        }
                    }
                }

                if (isPreviewMode) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
                                    Text("Direct Preview Mode", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "Local changes stay on device. Sign in with Google anytime for cloud sync.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                PillPrimaryButton(
                                    text = "Sign In",
                                    onClick = onSignOut,
                                    testTag = "preview_sign_in_button"
                                )
                            }
                        }
                    }
                }

                // Sub-tabs
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MeSubSection.entries) { sec ->
                            FilterChip(
                                selected = activeSection == sec,
                                onClick = { activeSection = sec },
                                label = { Text(sec.label) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("me_tab_${sec.name.lowercase()}")
                            )
                        }
                    }
                }

                when (activeSection) {
                    MeSubSection.PROGRESS -> {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text("Score Trend", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(10.dp))
                                val points = if (attempts.isEmpty()) listOf(64f, 68f, 72f, 75f, 79f) else attempts.take(7).reversed().map { it.overallScore.toFloat() }
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .padding(horizontal = 8.dp, vertical = 12.dp)
                                ) {
                                    val maxV = (points.maxOrNull() ?: 90f).coerceAtLeast(10f)
                                    val minV = (points.minOrNull() ?: 0f).coerceAtMost(maxV - 5f)
                                    val path = Path()
                                    points.forEachIndexed { idx, v ->
                                        val x = if (points.size == 1) size.width / 2f else idx * (size.width / (points.size - 1))
                                        val y = size.height - ((v - minV) / (maxV - minV).coerceAtLeast(1f)) * size.height
                                        if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                        drawCircle(color = PrimaryBlue, radius = 5.dp.toPx(), center = Offset(x, y))
                                    }
                                    drawPath(path = path, color = PrimaryBlue, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                                }
                                Text(
                                    text = "${attempts.size} synced attempts · ${profile?.streakDays ?: 5}-day streak",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        item {
                            GlassCard(
                                onClick = { onToggleLecturerMode(true) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.SwitchAccount, contentDescription = null, tint = PrimaryBlue)
                                        Column {
                                            Text("Switch to Lecturer View", style = MaterialTheme.typography.titleSmall)
                                            Text("Manage 1-on-1 availability & Zoom sessions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text("Open", style = MaterialTheme.typography.labelLarge, color = PrimaryBlue)
                                }
                            }
                        }
                    }

                    MeSubSection.CREDITS -> {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = PrimaryBlue)
                                    Text("Active Credit Pools", style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                val poolEntries = listOf(
                                    "Speaking" to (profile?.creditsSpeaking ?: 8L),
                                    "SST" to (profile?.creditsSst ?: 5L),
                                    "SWT" to (profile?.creditsSwt ?: 5L),
                                    "PTE Essay" to (profile?.creditsEssay ?: 4L),
                                    "IELTS Essay" to (profile?.creditsIeltsEssay ?: 3L),
                                    "PTE Mock" to (profile?.creditsMock ?: 2L),
                                    "IELTS Mock" to (profile?.creditsIeltsMock ?: 2L),
                                    "Universal" to (profile?.creditsUniversal ?: 10L)
                                )
                                poolEntries.chunked(2).forEach { pair ->
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        pair.forEach { (label, bal) ->
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(label, style = MaterialTheme.typography.bodySmall)
                                                    Text(bal.toString(), fontFamily = MonoFontFamily, style = MaterialTheme.typography.labelLarge, color = PrimaryBlue)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        items(SmartLabsCatalog.creditBundles, key = { it.id }) { bundle ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(bundle.name, style = MaterialTheme.typography.titleSmall)
                                        Text("${bundle.badge} · LKR ${bundle.priceLkr}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    PillPrimaryButton(
                                        text = "Top Up",
                                        onClick = { onBuyCredits(bundle.pool, bundle.quantity) },
                                        testTag = "buy_${bundle.id}"
                                    )
                                }
                            }
                        }
                    }

                    MeSubSection.SUPPORT -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tickets & Certificates", style = MaterialTheme.typography.titleMedium)
                                PillPrimaryButton(text = "New Request", onClick = { showTicketSheet = true })
                            }
                        }
                        if (tickets.isEmpty()) {
                            item {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.SupportAgent, contentDescription = null, tint = PrimaryBlue)
                                        Text("Need help or a course completion certificate? Tap New Request or contact support@smartlabs.lk.", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        } else {
                            items(tickets, key = { it.id.ifBlank { it.subject } }) { tk ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(tk.subject, style = MaterialTheme.typography.titleSmall)
                                        Text(tk.status.uppercase(), style = MaterialTheme.typography.labelSmall, color = PrimaryBlue)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${tk.category} · ${tk.latestMessage}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    MeSubSection.SETTINGS -> {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text("Appearance (Dark Mode)", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ThemePreference.entries.forEach { pref ->
                                        FilterChip(
                                            selected = localPrefs.themePreference == pref,
                                            onClick = { onSetTheme(pref) },
                                            label = { Text(pref.label) },
                                            shape = CircleShape,
                                            modifier = Modifier.testTag("theme_${pref.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text("Security & Sync", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                SettingToggleRow(
                                    title = "Biometric Vault Lock",
                                    subtitle = "Require fingerprint, face, or device PIN on launch",
                                    checked = localPrefs.biometricLockEnabled,
                                    onCheckedChange = onToggleBiometric,
                                    testTag = "biometric_switch"
                                )
                                SettingToggleRow(
                                    title = "Low-Signal Skeleton Preview",
                                    subtitle = "Preview smooth shimmer skeletons for slow networks",
                                    checked = localPrefs.simulateLowSignalSkeleton,
                                    onCheckedChange = onToggleSkeletonPreview
                                )
                                SettingToggleRow(
                                    title = "Study Reminders",
                                    subtitle = "Class & 1-on-1 booking notifications",
                                    checked = localPrefs.remindersEnabled,
                                    onCheckedChange = onToggleReminders
                                )
                                SettingToggleRow(
                                    title = "Audio Feedback Chimes",
                                    subtitle = "Play subtle tones on timer transitions",
                                    checked = localPrefs.soundEffectsEnabled,
                                    onCheckedChange = onToggleSound
                                )
                            }
                        }
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    TextButton(onClick = { showPoliciesDialog = true }) {
                                        Text("Terms, Privacy & Refund Policy")
                                    }
                                    if (isPreviewMode) {
                                        PillPrimaryButton(
                                            text = "Sign In with Google for Cloud Sync",
                                            onClick = onSignOut,
                                            modifier = Modifier.fillMaxWidth(),
                                            testTag = "preview_settings_sign_in_button"
                                        )
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            PillSecondaryButton(
                                                text = "Sign Out",
                                                onClick = onSignOut,
                                                modifier = Modifier.weight(1f)
                                            )
                                            PillPrimaryButton(
                                                text = "Delete Account",
                                                onClick = { showDeleteConfirm = true },
                                                modifier = Modifier.weight(1f),
                                                testTag = "delete_account_button"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditGoalSheet) {
        var name by remember { mutableStateOf(profile?.displayName ?: "") }
        var exam by remember { mutableStateOf(profile?.examGoal ?: "PTE") }
        var targetText by remember { mutableStateOf((profile?.targetScore ?: 79.0).toString()) }
        var dateLabel by remember { mutableStateOf(profile?.examDateLabel ?: "In 23 days") }
        var weekly by remember { mutableStateOf((profile?.weeklyGoalSessions ?: 12L).toString()) }

        ModalBottomSheet(onDismissRequest = { showEditGoalSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Edit Study Plan", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("PTE", "IELTS").forEach { g ->
                        FilterChip(selected = exam == g, onClick = { exam = g }, label = { Text(g) }, shape = CircleShape)
                    }
                }
                OutlinedTextField(value = targetText, onValueChange = { targetText = it }, label = { Text("Target Score") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dateLabel, onValueChange = { dateLabel = it }, label = { Text("Exam Countdown Label") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weekly, onValueChange = { weekly = it }, label = { Text("Weekly Sessions Goal") }, modifier = Modifier.fillMaxWidth())
                PillPrimaryButton(
                    text = "Save & Sync",
                    onClick = {
                        onUpdateGoal(
                            name,
                            exam,
                            targetText.toDoubleOrNull() ?: 79.0,
                            dateLabel,
                            weekly.toLongOrNull() ?: 12L
                        )
                        showEditGoalSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showTicketSheet) {
        var subject by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Support") }
        var message by remember { mutableStateOf("") }
        ModalBottomSheet(onDismissRequest = { showTicketSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("New Support or Certificate Request", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Support", "Certificate", "Billing").forEach { c ->
                        FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c) }, shape = CircleShape)
                    }
                }
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                PillPrimaryButton(
                    text = "Submit Request",
                    onClick = {
                        onSubmitTicket(subject.ifBlank { "$category Request" }, category, message.ifBlank { "Please assist." })
                        showTicketSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Rounded.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Account Data?") },
            text = { Text("This permanently deletes your synced profile, attempts, bookings, and support tickets from Firestore.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDeleteAccount()
                }) {
                    Text("Delete Permanently", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPoliciesDialog) {
        AlertDialog(
            onDismissRequest = { showPoliciesDialog = false },
            title = { Text("SmartLabs Security & Policies") },
            text = {
                Text(
                    "• OAuth 2.0 Google Sign-In & Firebase App Check protect all API & Firestore calls.\n" +
                        "• Biometric Vault uses Android Keystore AES-256 keys.\n" +
                        "• Refund requests for unstarted 1-on-1 bookings are processed within 5 business days."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPoliciesDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier
        )
    }
}

@Composable
private fun LecturerModePanel(
    bookings: List<BookingRecord>,
    onExitLecturerMode: () -> Unit
) {
    var repeatWeeks by remember { mutableIntStateOf(4) }
    var selectedWindow by remember { mutableStateOf("18:00 - 21:00 IST") }
    val extended = LocalSmartLabsColors.current

    LazyColumn(
        modifier = Modifier
            .widthIn(max = 860.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 108.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Lecturer Mode", style = MaterialTheme.typography.titleLarge)
                        Text("Manage live 1-on-1 sessions & weekly windows", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    PillSecondaryButton(text = "Student View", onClick = onExitLecturerMode)
                }
            }
        }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Weekly Availability Window", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("18:00 - 21:00 IST", "09:00 - 12:00 IST").forEach { w ->
                        FilterChip(selected = selectedWindow == w, onClick = { selectedWindow = w }, label = { Text(w) }, shape = CircleShape)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Repeat weekly ×$repeatWeeks", style = MaterialTheme.typography.labelLarge, color = PrimaryBlue)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(2, 4, 8).forEach { n ->
                        FilterChip(selected = repeatWeeks == n, onClick = { repeatWeeks = n }, label = { Text("×$n weeks") }, shape = CircleShape)
                    }
                }
            }
        }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Payouts Summary", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Completed Sessions: ${bookings.count { it.status == "confirmed" }} · Est. Payout: LKR ${bookings.sumOf { it.priceLkr }}", fontFamily = MonoFontFamily, style = MaterialTheme.typography.bodyMedium, color = extended.mint)
            }
        }
        items(bookings.filter { it.status == "confirmed" }) { bk ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(bk.title, style = MaterialTheme.typography.titleSmall)
                        Text("${bk.dateLabel} · ${bk.timeSlot}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(shape = CircleShape, color = PrimaryBlue) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("Join Zoom", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
