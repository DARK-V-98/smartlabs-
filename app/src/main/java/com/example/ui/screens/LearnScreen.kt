package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BookingRecord
import com.example.data.CourseItem
import com.example.data.ExamType
import com.example.data.LecturerProfile
import com.example.data.RecordedPackageItem
import com.example.data.SmartLabsCatalog
import com.example.data.TutorMessage
import com.example.ui.components.CardListSkeletonLoader
import com.example.ui.components.GlassCard
import com.example.ui.components.PillPrimaryButton
import com.example.ui.components.PillSecondaryButton
import com.example.ui.theme.LocalSmartLabsColors
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RoyalBlue

enum class LearnSubSection(val label: String) {
    COURSES("Courses"),
    ONE_TO_ONE("1-on-1"),
    AI_TUTOR("AI Tutor"),
    RECORDED("Recorded"),
    EVENTS("Webinars"),
    RESOURCES("Resources")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LearnScreen(
    isSkeletonVisible: Boolean,
    initialSection: LearnSubSection,
    userEmail: String,
    bookings: List<BookingRecord>,
    tutorMessages: List<TutorMessage>,
    tutorStreamingText: String?,
    onCreateBooking: (bookingType: String, title: String, lecturer: String, date: String, slot: String, mode: String, priceLkr: Int) -> Unit,
    onCancelBooking: (String) -> Unit,
    onSendTutorMessage: (courseContext: String, question: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSection by remember(initialSection) { mutableStateOf(initialSection) }
    var selectedLecturerForBooking by remember { mutableStateOf<LecturerProfile?>(null) }
    var selectedRecordedPkg by remember { mutableStateOf<RecordedPackageItem?>(null) }
    var selectedCourseForEnroll by remember { mutableStateOf<CourseItem?>(null) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 920.dp)
                .fillMaxSize()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LearnSubSection.entries) { sec ->
                    FilterChip(
                        selected = activeSection == sec,
                        onClick = { activeSection = sec },
                        label = { Text(sec.label) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("learn_tab_${sec.name.lowercase()}")
                    )
                }
            }

            if (isSkeletonVisible) {
                CardListSkeletonLoader(count = 4)
            } else {
                when (activeSection) {
                    LearnSubSection.COURSES -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 108.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(SmartLabsCatalog.courses, key = { it.id }) { course ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(132.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = if (course.examType == ExamType.PTE) R.drawable.img_pte_studio else R.drawable.img_ielts_studio
                                            ),
                                            contentDescription = course.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC061539))))
                                        )
                                        Row(
                                            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(shape = CircleShape, color = PrimaryBlue) {
                                                Text(
                                                    course.examType.label,
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Text(
                                                "★ ${course.rating} (${course.enrolledCount} enrolled)",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(course.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${course.lecturer} · ${course.schedule}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    course.syllabus.forEach { topic ->
                                        Text("• $topic", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val priceText = buildString {
                                            append("LKR ${course.priceLkr}")
                                            if (course.priceUsd != null) append(" / USD ${course.priceUsd}")
                                        }
                                        Text(
                                            text = priceText,
                                            fontFamily = MonoFontFamily,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = PrimaryBlue
                                        )
                                        PillPrimaryButton(
                                            text = "Enroll via PayHere",
                                            onClick = { selectedCourseForEnroll = course }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    LearnSubSection.ONE_TO_ONE -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 108.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = PrimaryBlue.copy(alpha = 0.12f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Rounded.Schedule, contentDescription = null, tint = PrimaryBlue)
                                        Text(
                                            "15-minute step booking grid · A 15-minute break is kept between sessions.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            items(SmartLabsCatalog.lecturers, key = { it.id }) { lec ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(lec.name, style = MaterialTheme.typography.titleMedium)
                                            Text(lec.title, style = MaterialTheme.typography.bodySmall, color = PrimaryBlue)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                            Text(" ${lec.rating}", fontFamily = MonoFontFamily, style = MaterialTheme.typography.labelLarge)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(lec.bio, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Subjects: ${lec.subjects.joinToString(" · ")}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "LKR ${lec.ratePerHourLkr} / hr",
                                            fontFamily = MonoFontFamily,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        PillPrimaryButton(
                                            text = "Book Slot",
                                            onClick = { selectedLecturerForBooking = lec },
                                            testTag = "book_lecturer_${lec.id}"
                                        )
                                    }
                                }
                            }

                            if (bookings.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("My Bookings", style = MaterialTheme.typography.titleMedium)
                                }
                                items(bookings, key = { it.id.ifBlank { it.title + it.timeSlot } }) { bk ->
                                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(bk.title, style = MaterialTheme.typography.titleSmall)
                                                Text(
                                                    "${bk.lecturerName} · ${bk.dateLabel} · ${bk.timeSlot} (${bk.mode})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Surface(
                                                shape = CircleShape,
                                                color = if (bk.status == "cancelled") MaterialTheme.colorScheme.error.copy(alpha = 0.14f) else PrimaryBlue.copy(alpha = 0.14f)
                                            ) {
                                                Text(
                                                    bk.status.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (bk.status == "cancelled") MaterialTheme.colorScheme.error else PrimaryBlue,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        if (bk.status == "confirmed") {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = LocalSmartLabsColors.current.mint.copy(alpha = 0.16f)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(Icons.Rounded.VideoCall, contentDescription = null, tint = LocalSmartLabsColors.current.mint, modifier = Modifier.size(16.dp))
                                                        Text("Join Zoom Ready", style = MaterialTheme.typography.labelMedium, color = LocalSmartLabsColors.current.mint)
                                                    }
                                                }
                                                PillSecondaryButton(
                                                    text = "Cancel",
                                                    onClick = { if (bk.id.isNotBlank()) onCancelBooking(bk.id) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LearnSubSection.AI_TUTOR -> {
                        var selectedCourseCtx by remember { mutableStateOf("PTE 79+ Mastery") }
                        var draftQuestion by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                                .padding(horizontal = 16.dp)
                        ) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf("PTE 79+ Mastery", "IELTS Band 8.0", "Grammar & Essay")) { ctx ->
                                    FilterChip(
                                        selected = selectedCourseCtx == ctx,
                                        onClick = { selectedCourseCtx = ctx },
                                        label = { Text(ctx) },
                                        shape = CircleShape
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (tutorMessages.isEmpty() && tutorStreamingText == null) {
                                    item {
                                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = PrimaryBlue)
                                                Text(
                                                    "Ask Gemini AI Tutor about $selectedCourseCtx templates, speaking pacing, or Band 8+ essay structures.",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                                items(tutorMessages, key = { it.id.ifBlank { it.messageText.hashCode() } }) { msg ->
                                    val isUser = msg.sender == "user"
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (isUser) PrimaryBlue else MaterialTheme.colorScheme.surface,
                                            border = if (isUser) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                                            modifier = Modifier.widthIn(max = 320.dp)
                                        ) {
                                            Text(
                                                text = msg.messageText,
                                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(14.dp)
                                            )
                                        }
                                    }
                                }
                                if (tutorStreamingText != null) {
                                    item {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f)),
                                            modifier = Modifier.widthIn(max = 320.dp)
                                        ) {
                                            Text(
                                                text = tutorStreamingText.ifBlank { "Thinking…" },
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 96.dp, top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = draftQuestion,
                                    onValueChange = { draftQuestion = it },
                                    placeholder = { Text("Ask SmartLabs AI Tutor…") },
                                    shape = CircleShape,
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("ai_tutor_input")
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryBlue,
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (draftQuestion.isNotBlank()) {
                                                onSendTutorMessage(selectedCourseCtx, draftQuestion)
                                                draftQuestion = ""
                                            }
                                        },
                                        enabled = tutorStreamingText == null,
                                        modifier = Modifier.testTag("ai_tutor_send_button")
                                    ) {
                                        Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    LearnSubSection.RECORDED -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 108.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(SmartLabsCatalog.recordedPackages, key = { it.id }) { pkg ->
                                GlassCard(onClick = { selectedRecordedPkg = pkg }, modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(pkg.title, style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "${pkg.lecturer} · ${pkg.lessonsCount} lessons · ${pkg.durationHours}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Icon(Icons.Rounded.PlayCircle, contentDescription = "Watch", tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                                    }
                                }
                            }
                        }
                    }

                    LearnSubSection.EVENTS -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 108.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(SmartLabsCatalog.events, key = { it.id }) { ev ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text("${ev.type} · ${ev.dateTimeLabel}", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(ev.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Host: ${ev.host} · ${ev.seatsLeft} seats left", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    PillPrimaryButton(
                                        text = if (ev.priceLkr == 0) "Register Free" else "Register (LKR ${ev.priceLkr})",
                                        onClick = {
                                            onCreateBooking(
                                                ev.type.uppercase(),
                                                ev.title,
                                                ev.host,
                                                ev.dateTimeLabel,
                                                "Live Stream",
                                                "Online",
                                                ev.priceLkr
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    LearnSubSection.RESOURCES -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 108.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(SmartLabsCatalog.resources, key = { it.id }) { res ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text("${res.examType.label} · ${res.category}", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(res.title, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(res.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // One-to-One 15-Minute Step Booking Sheet
    selectedLecturerForBooking?.let { lec ->
        var mode by remember { mutableStateOf("Online") }
        var hours by remember { mutableIntStateOf(1) }
        var selectedDate by remember { mutableStateOf("Tomorrow") }
        var selectedSlot by remember { mutableStateOf(SmartLabsCatalog.fifteenMinuteTimeSlots.first()) }
        ModalBottomSheet(onDismissRequest = { selectedLecturerForBooking = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Book ${lec.name}", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Online", "Physical").filter {
                        (it == "Online" && lec.supportsOnline) || (it == "Physical" && lec.supportsPhysical)
                    }.forEach { m ->
                        FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(m) }, shape = CircleShape)
                    }
                    listOf(1, 2, 3).forEach { h ->
                        FilterChip(selected = hours == h, onClick = { hours = h }, label = { Text("${h}h") }, shape = CircleShape)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Tomorrow", "Mon, Nov 23", "Wed, Nov 25").forEach { d ->
                        FilterChip(selected = selectedDate == d, onClick = { selectedDate = d }, label = { Text(d) }, shape = CircleShape)
                    }
                }
                Text("Select Start Time (15-min steps, 15-min break kept):", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmartLabsCatalog.fifteenMinuteTimeSlots.take(8).forEach { slot ->
                        FilterChip(selected = selectedSlot == slot, onClick = { selectedSlot = slot }, label = { Text(slot) }, shape = CircleShape)
                    }
                }
                PillPrimaryButton(
                    text = "Confirm & Pay LKR ${lec.ratePerHourLkr * hours}",
                    onClick = {
                        onCreateBooking(
                            "ONE_TO_ONE",
                            "1-on-1 Coaching (${hours}h)",
                            lec.name,
                            selectedDate,
                            selectedSlot,
                            mode,
                            lec.ratePerHourLkr * hours
                        )
                        selectedLecturerForBooking = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "confirm_one_to_one_button"
                )
            }
        }
    }

    // Course Enroll PayHere Sheet
    selectedCourseForEnroll?.let { course ->
        ModalBottomSheet(onDismissRequest = { selectedCourseForEnroll = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Enroll in ${course.title}", style = MaterialTheme.typography.titleLarge)
                Text("Lecturer: ${course.lecturer}", style = MaterialTheme.typography.bodyMedium)
                Text("Schedule: ${course.schedule}", style = MaterialTheme.typography.bodyMedium)
                PillPrimaryButton(
                    text = "Confirm Enrollment · LKR ${course.priceLkr}",
                    onClick = {
                        onCreateBooking("COURSE", course.title, course.lecturer, course.schedule, "Cohort Live", "Online", course.priceLkr)
                        selectedCourseForEnroll = null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Secure Watermarked Recorded Video Player Sheet
    selectedRecordedPkg?.let { pkg ->
        ModalBottomSheet(onDismissRequest = { selectedRecordedPkg = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(pkg.title, style = MaterialTheme.typography.titleLarge)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(OceanDeep, RoyalBlue)))
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircle,
                        contentDescription = "Playing Lesson",
                        tint = Color.White,
                        modifier = Modifier.size(52.dp).align(Alignment.Center)
                    )
                    // Dynamic student email watermark + no download guard
                    Row(
                        modifier = Modifier.align(Alignment.BottomStart),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xCCFFFFFF), modifier = Modifier.size(14.dp))
                        Text(
                            text = "Protected Stream · $userEmail",
                            fontFamily = MonoFontFamily,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xCCFFFFFF)
                        )
                    }
                }
                pkg.lessons.forEach { lesson ->
                    Text(lesson, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
