package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KaraokeLyricLine

/**
 * Synchronized Karaoke Lyrics View with live timestamp tracking & progress animation.
 */
@Composable
fun LiveLyricsView(
    lyrics: List<KaraokeLyricLine>,
    activeLineIndex: Int,
    activeLineProgress: Float,
    currentPositionMs: Long,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Automatically auto-scroll to center the active line
    LaunchedEffect(activeLineIndex) {
        if (activeLineIndex >= 0 && activeLineIndex < lyrics.size) {
            val targetIndex = (activeLineIndex - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF160E29).copy(alpha = 0.85f))
            .border(1.dp, Color(0xFF332057), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        if (lyrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♪ Instrumental backing track playing ♪",
                    color = Color(0xFFA093C4),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeLineIndex
                    val isPast = index < activeLineIndex

                    val minutes = line.timestampMs / 60000
                    val seconds = (line.timestampMs % 60000) / 1000
                    val timestampLabel = String.format("%02d:%02d", minutes, seconds)

                    val rowBackgroundModifier = if (isActive) {
                        Modifier.background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFF2A85).copy(alpha = 0.25f),
                                    Color(0xFF7928CA).copy(alpha = 0.35f)
                                )
                            )
                        )
                    } else {
                        Modifier
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .then(rowBackgroundModifier)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = line.text,
                                color = when {
                                    isActive -> Color(0xFFFFE600) // Glowing Gold for active line
                                    isPast -> Color(0xFF7E729C) // Dimmed for sung lines
                                    else -> Color(0xFFE2DCF7) // Crisp white for upcoming lines
                                },
                                fontSize = if (isActive) 17.sp else 14.sp,
                                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = timestampLabel,
                                color = if (isActive) Color(0xFFFF2A85) else Color(0xFF5D5477),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Syllable/phrase progress bar along the active line
                        if (isActive) {
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { activeLineProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color(0xFFFF007F),
                                trackColor = Color(0x33FF007F)
                            )
                        }
                    }
                }
            }
        }
    }
}
