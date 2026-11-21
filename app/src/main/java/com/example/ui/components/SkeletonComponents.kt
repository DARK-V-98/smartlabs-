package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalSmartLabsColors

@Composable
fun rememberShimmerBrush(): Brush {
    val colors = LocalSmartLabsColors.current
    val transition = rememberInfiniteTransition(label = "skeleton_shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )
    return Brush.linearGradient(
        colors = listOf(colors.shimmerBase, colors.shimmerHighlight, colors.shimmerBase),
        start = Offset(offset - 350f, 0f),
        end = Offset(offset, 320f)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun HomeSkeletonLoader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Low-signal subtle caption
        Text(
            text = "Syncing your study workspace…",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Greeting + avatar skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBox(modifier = Modifier.width(170.dp).height(26.dp), shape = RoundedCornerShape(10.dp))
                ShimmerBox(modifier = Modifier.width(120.dp).height(16.dp), shape = RoundedCornerShape(8.dp))
            }
            ShimmerBox(modifier = Modifier.size(48.dp), shape = CircleShape)
        }

        // Search pill skeleton
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(54.dp), shape = CircleShape)

        // Credits strip skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) {
                ShimmerBox(modifier = Modifier.width(96.dp).height(36.dp), shape = CircleShape)
            }
        }

        // Large translucent hero card skeleton
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(210.dp), shape = RoundedCornerShape(26.dp))

        // Weekly ring + schedule skeletons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(modifier = Modifier.weight(1f).height(132.dp), shape = RoundedCornerShape(22.dp))
            ShimmerBox(modifier = Modifier.weight(1f).height(132.dp), shape = RoundedCornerShape(22.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(88.dp), shape = RoundedCornerShape(20.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(88.dp), shape = RoundedCornerShape(20.dp))
    }
}

@Composable
fun CardListSkeletonLoader(count: Int = 4, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        repeat(count) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                shape = RoundedCornerShape(22.dp)
            )
        }
    }
}
