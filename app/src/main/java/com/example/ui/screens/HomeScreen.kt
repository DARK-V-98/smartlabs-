package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BookingRecord
import com.example.data.CreditPoolType
import com.example.data.ExamType
import com.example.data.PracticeAttempt
import com.example.data.PracticeTaskItem
import com.example.data.SmartLabsCatalog
import com.example.data.UserProfile
import com.example.ui.components.ConnectivitySyncBanner
import com.example.ui.components.GlassCard
import com.example.ui.components.HomeSkeletonLoader
import com.example.ui.components.RoundSearchAndFilterBar
import com.example.ui.components.TranslucentBlueHeroCard
import com.example.ui.theme.AccentPink
import com.example.ui.theme.LocalSmartLabsColors
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RoyalBlue

@Composable
fun HomeScreen(
    isSkeletonVisible: Boolean,
    isOnline: Boolean,
    profile: UserProfile?,
    attempts: List<PracticeAttempt>,
    bookings: List<BookingRecord>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchFilterClick: () -> Unit,
    onOpenCredits: () -> Unit,
    onContinuePractice: (PracticeTaskItem) -> Unit,
    onQuickPracticeExam: (ExamType) -> Unit,
    onOpenTests: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenOneToOne: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(targetState = isSkeletonVisible, label = "home_skeleton_crossfade") { loading ->
        if (loading) {
            HomeSkeletonLoader(modifier = modifier)
        } else {
            val extended = LocalSmartLabsColors.current
            val firstName = profile?.displayName?.split(" ")?.firstOrNull()?.ifBlank { "Student" } ?: "Student"
            val lastTask = SmartLabsCatalog.practiceTasks.firstOrNull {
                it.title == attempts.firstOrNull()?.taskTitle
            } ?: SmartLabsCatalog.practiceTasks.first()

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 860.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 108.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Greeting + Avatar + Sync Banner
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Hello, $firstName 👋",
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${profile?.examGoal ?: "PTE"} ${profile?.examDateLabel ?: "In 23 days"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    ConnectivitySyncBanner(isOnline = isOnline)
                                }
                            }

                            Surface(
                                onClick = onOpenProfile,
                                shape = CircleShape,
                                color = PrimaryBlue.copy(alpha = 0.16f),
                                border = BorderStroke(2.dp, PrimaryBlue.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .size(50.dp)
                                    .testTag("home_avatar_button")
                            ) {
                                if (!profile?.photoUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = profile?.photoUrl,
                                        contentDescription = "Profile Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = firstName.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Global Search & Filter Pill
                    item {
                        RoundSearchAndFilterBar(
                            query = searchQuery,
                            onQueryChange = onSearchChange,
                            onFilterClick = onSearchFilterClick,
                            hasActiveFilters = false
                        )
                    }

                    // All 8 Credit Pools Strip
                    item {
                        val pools = listOf(
                            CreditPoolType.SPEAKING to (profile?.creditsSpeaking ?: 8L),
                            CreditPoolType.SST to (profile?.creditsSst ?: 5L),
                            CreditPoolType.SWT to (profile?.creditsSwt ?: 5L),
                            CreditPoolType.ESSAY to (profile?.creditsEssay ?: 4L),
                            CreditPoolType.IELTS_ESSAY to (profile?.creditsIeltsEssay ?: 3L),
                            CreditPoolType.MOCK to (profile?.creditsMock ?: 2L),
                            CreditPoolType.IELTS_MOCK to (profile?.creditsIeltsMock ?: 2L),
                            CreditPoolType.UNIVERSAL to (profile?.creditsUniversal ?: 10L)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(pools) { (pool, balance) ->
                                Surface(
                                    onClick = onOpenCredits,
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = pool.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = balance.toString(),
                                            fontFamily = MonoFontFamily,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Continue Practising Translucent Blue Hero Card
                    item {
                        TranslucentBlueHeroCard(
                            drawableRes = R.drawable.img_smartlabs_hero,
                            badgeText = "CONTINUE PRACTISING · ${lastTask.examType.label}",
                            title = lastTask.title,
                            subtitle = lastTask.subtitle,
                            ctaLabel = "Resume AI Trainer",
                            onClick = { onContinuePractice(lastTask) },
                            testTag = "continue_practice_hero"
                        )
                    }

                    // Weekly Goal Ring + Exam Readiness Card
                    item {
                        val goal = (profile?.weeklyGoalSessions ?: 12L).coerceAtLeast(1L)
                        val done = (profile?.sessionsThisWeek ?: 4L).coerceAtLeast(0L)
                        val progress = (done.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GlassCard(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(58.dp)) {
                                        val track = MaterialTheme.colorScheme.surfaceVariant
                                        Canvas(modifier = Modifier.size(58.dp)) {
                                            val stroke = 7.dp.toPx()
                                            drawArc(track, -90f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round))
                                            drawArc(extended.mint, -90f, 360f * progress, false, style = Stroke(stroke, cap = StrokeCap.Round))
                                        }
                                        Text(
                                            text = "${(progress * 100).toInt()}%",
                                            fontFamily = MonoFontFamily,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    Column {
                                        Text("Weekly Goal", style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            text = "$done / $goal sessions",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            GlassCard(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = AccentPink.copy(alpha = 0.16f),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = "Streak", tint = AccentPink)
                                        }
                                    }
                                    Column {
                                        Text("${profile?.streakDays ?: 5}d Streak", style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            text = "Target ${profile?.targetScore?.let { if (it <= 9.5) it.toString() else it.toInt().toString() } ?: "79"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Quick Actions Grid
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                QuickActionTile("Practice PTE", Icons.Rounded.Mic, PrimaryBlue, Modifier.weight(1f)) {
                                    onQuickPracticeExam(ExamType.PTE)
                                }
                                QuickActionTile("IELTS Lab", Icons.Rounded.School, RoyalBlue, Modifier.weight(1f)) {
                                    onQuickPracticeExam(ExamType.IELTS)
                                }
                                QuickActionTile("Mock Test", Icons.Rounded.Timer, extended.mint, Modifier.weight(1f)) {
                                    onOpenTests()
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                QuickActionTile("AI Tutor", Icons.Rounded.AutoAwesome, PrimaryBlue, Modifier.weight(1f)) {
                                    onOpenTutor()
                                }
                                QuickActionTile("Book 1-on-1", Icons.Rounded.PersonAdd, AccentPink, Modifier.weight(1f)) {
                                    onOpenOneToOne()
                                }
                                QuickActionTile("Recordings", Icons.Rounded.PlayCircle, RoyalBlue, Modifier.weight(1f)) {
                                    onOpenOneToOne()
                                }
                            }
                        }
                    }

                    // Today / Upcoming Schedule
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Today & Upcoming", style = MaterialTheme.typography.titleMedium)
                            val activeBookings = bookings.filter { it.status != "cancelled" }
                            if (activeBookings.isEmpty()) {
                                GlassCard(onClick = onOpenOneToOne) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(shape = CircleShape, color = PrimaryBlue.copy(alpha = 0.14f), modifier = Modifier.size(44.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = PrimaryBlue)
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Live PTE Speaking Clinic · 7:30 PM IST", style = MaterialTheme.typography.titleSmall)
                                            Text(
                                                "Tap to view 1-on-1 slots & live webinars",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                activeBookings.take(3).forEach { bk ->
                                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(bk.title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text(
                                                    "${bk.lecturerName} · ${bk.dateLabel} · ${bk.timeSlot}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Surface(shape = CircleShape, color = extended.mint.copy(alpha = 0.16f)) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(Icons.Rounded.VideoCall, contentDescription = "Join Zoom", tint = extended.mint, modifier = Modifier.size(16.dp))
                                                    Text("Zoom", style = MaterialTheme.typography.labelMedium, color = extended.mint)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Seasonal Offer Banner
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Brush.horizontalGradient(listOf(OceanDeep, RoyalBlue, PrimaryBlue)))
                                .clickable { onOpenCredits() }
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("2026 Exam Season Bundle", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                    Text(
                                        "Save 20% on 3 Full PTE Mocks + Unlimited AI Tutor coaching",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFDCE8FF)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(shape = CircleShape, color = Color.White) {
                                    Text(
                                        "LKR 6,900",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = OceanDeep,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
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

@Composable
private fun QuickActionTile(
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = CircleShape,
                color = tint.copy(alpha = 0.14f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
