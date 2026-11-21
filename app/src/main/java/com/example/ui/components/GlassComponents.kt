package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalSmartLabsColors
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RoyalBlue

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val extended = LocalSmartLabsColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && onClick != null) 0.985f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "card_press_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(),
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(24.dp),
        color = extended.cardElevated,
        tonalElevation = 2.dp,
        shadowElevation = if (extended.isDark) 0.dp else 4.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (extended.isDark) Color(0x3360A5FA) else Color(0xFFE2E8F0)
        )
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun TranslucentBlueHeroCard(
    drawableRes: Int,
    badgeText: String,
    title: String,
    subtitle: String,
    ctaLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "hero_card"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "hero_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(216.dp)
            .scale(scale)
            .clip(RoundedCornerShape(28.dp))
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = Color.White),
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Translucent oceanic blue overlay matching reference aesthetic
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x330A2463),
                            Color(0x990B2B6B),
                            Color(0xE6061539)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0x38FFFFFF),
                border = BorderStroke(1.dp, Color(0x55FFFFFF))
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFDCE8FF),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Frosted translucent CTA bar with circular white arrow button (inspired by reference)
                Surface(
                    shape = CircleShape,
                    color = Color(0x30FFFFFF),
                    border = BorderStroke(1.dp, Color(0x4DFFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ctaLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = ctaLabel,
                                    tint = OceanDeep,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PillPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PillSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RoundSearchAndFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean,
    placeholder: String = "Search tasks, mocks, courses…",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .weight(1f)
                .testTag("global_search_input")
        )

        val filterBg by animateColorAsState(
            targetValue = if (hasActiveFilters) PrimaryBlue else MaterialTheme.colorScheme.surface,
            label = "filter_bg"
        )
        val filterTint by animateColorAsState(
            targetValue = if (hasActiveFilters) Color.White else MaterialTheme.colorScheme.onSurface,
            label = "filter_tint"
        )
        Surface(
            shape = CircleShape,
            color = filterBg,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier
                .size(52.dp)
                .testTag("filter_button")
        ) {
            IconButton(onClick = onFilterClick, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = "Advanced Filters",
                    tint = filterTint
                )
            }
        }
    }
}

@Composable
fun CreditPillBadge(
    totalCredits: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = PrimaryBlue.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.35f)),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag("top_credits_pill")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Bolt,
                contentDescription = "Credits",
                tint = PrimaryBlue,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$totalCredits",
                fontFamily = MonoFontFamily,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ConnectivitySyncBanner(isOnline: Boolean, modifier: Modifier = Modifier) {
    val extended = LocalSmartLabsColors.current
    val bgColor = if (isOnline) extended.mint.copy(alpha = 0.14f) else extended.amber.copy(alpha = 0.18f)
    val contentColor = if (isOnline) extended.mint else extended.amber
    Surface(
        shape = CircleShape,
        color = bgColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Rounded.CloudDone else Icons.Rounded.CloudOff,
                contentDescription = if (isOnline) "Real-time cloud sync active" else "Offline cache active",
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (isOnline) "Cloud Synced" else "Offline Cache · Will Sync",
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}

@Composable
fun SegmentedExamSwitch(
    selected: com.example.data.ExamType,
    onSelect: (com.example.data.ExamType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            com.example.data.ExamType.entries.forEach { exam ->
                val active = exam == selected
                val bg by animateColorAsState(
                    targetValue = if (active) RoyalBlue else Color.Transparent,
                    label = "seg_bg"
                )
                val fg by animateColorAsState(
                    targetValue = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "seg_fg"
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(CircleShape)
                        .background(bg)
                        .testTag("segment_${exam.label.lowercase()}")
                        .clickable { onSelect(exam) }
                ) {
                    Text(
                        text = exam.label,
                        style = MaterialTheme.typography.titleSmall,
                        color = fg
                    )
                }
            }
        }
    }
}
