package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Neon dancing audio visualizer wave driven by live mic and music level.
 */
@Composable
fun AudioVisualizerView(
    volumeLevel: Float,
    barCount: Int = 28,
    modifier: Modifier = Modifier
) {
    val animatedVol = remember { Animatable(0f) }

    LaunchedEffect(volumeLevel) {
        animatedVol.animateTo(
            targetValue = volumeLevel,
            animationSpec = tween(durationMillis = 80)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = (width / barCount) * 0.65f
        val gap = (width - (barWidth * barCount)) / (barCount - 1).coerceAtLeast(1)

        val brush = Brush.verticalGradient(
            listOf(
                Color(0xFFFF007F), // Neon Pink top
                Color(0xFF7928CA), // Electric Purple mid
                Color(0xFF00F5D4)  // Cyan base
            )
        )

        for (i in 0 until barCount) {
            val normalizedIndex = i.toFloat() / barCount.toFloat()
            // Bell curve multiplier so middle bars peak higher
            val bellMultiplier = sin(normalizedIndex * Math.PI).toFloat()

            // Random slight flutter
            val flutter = ((sin((i * 13 + System.currentTimeMillis() / 70.0).toFloat()) + 1f) * 0.2f)
            val barHeight = ((animatedVol.value * 0.8f + flutter * 0.25f) * bellMultiplier * height)
                .coerceIn(4f, height)

            val left = i * (barWidth + gap)
            val top = (height - barHeight) / 2f

            drawRoundRect(
                brush = brush,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
