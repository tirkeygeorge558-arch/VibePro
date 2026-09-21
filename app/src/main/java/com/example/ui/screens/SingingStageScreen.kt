package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.KaraokeEngine
import com.example.data.model.SongEntity
import com.example.ui.components.AudioVisualizerView
import com.example.ui.components.LiveLyricsView

@Composable
fun SingingStageScreen(
    song: SongEntity,
    karaokeEngine: KaraokeEngine,
    onClose: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying by karaokeEngine.isPlaying.collectAsState()
    val currentPositionMs by karaokeEngine.currentPositionMs.collectAsState()
    val durationMs by karaokeEngine.durationMs.collectAsState()
    val lyrics by karaokeEngine.parsedLyrics.collectAsState()
    val activeLyricIndex by karaokeEngine.activeLyricIndex.collectAsState()
    val activeLineProgress by karaokeEngine.activeLineProgress.collectAsState()
    val volumeLevel by karaokeEngine.micVolumeLevel.collectAsState()
    val liveScore by karaokeEngine.liveScore.collectAsState()
    val comboCount by karaokeEngine.comboCount.collectAsState()
    val accuracyRating by karaokeEngine.accuracyRating.collectAsState()

    val currentSeconds = (currentPositionMs / 1000).toInt()
    val durationSeconds = (durationMs / 1000).toInt()
    val timeFormatted = String.format("%02d:%02d / %02d:%02d",
        currentSeconds / 60, currentSeconds % 60,
        durationSeconds / 60, durationSeconds % 60
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF19092E),
                        Color(0xFF0F061F),
                        Color(0xFF05020A)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Stage Header
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Stage",
                            tint = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = song.title,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${song.artist} • ${song.difficulty}",
                            color = Color(0xFFB5A7D8),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onFinish,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            text = "Finish",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Score Board & Combo Streak
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF22143B))
                        .border(1.dp, Color(0xFF3F2766), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KARAOKE SCORE",
                                color = Color(0xFFAFA2D1),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$liveScore pts",
                                color = Color(0xFFFFE600),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = accuracyRating,
                                color = Color(0xFFFF007F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            if (comboCount > 0) {
                                Text(
                                    text = "🔥 COMBO x$comboCount",
                                    color = Color(0xFF00F5D4),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Current Grade
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFFF007F), Color(0xFF7928CA)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = karaokeEngine.computeGrade(liveScore),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Live Audio Waveform Visualizer
                Text(
                    text = "LIVE AUDIO SPECTRUM & MIC",
                    color = Color(0xFF867BA6),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                AudioVisualizerView(
                    volumeLevel = volumeLevel,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Middle: Synchronized Lyrics Screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "SYNCHRONIZED KARAOKE LYRICS (LRC TIMESTAMPS)",
                    color = Color(0xFFFFD700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LiveLyricsView(
                    lyrics = lyrics,
                    activeLineIndex = activeLyricIndex,
                    activeLineProgress = activeLineProgress,
                    currentPositionMs = currentPositionMs,
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Stage Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B1130))
                    .padding(14.dp)
            ) {
                // Seek Bar & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeFormatted,
                        color = Color(0xFFB5A7D8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Real Backing Synth: Active",
                        color = Color(0xFF00F5D4),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = currentPositionMs.toFloat(),
                    onValueChange = { karaokeEngine.seekTo(it.toLong()) },
                    valueRange = 0f..durationMs.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF007F),
                        activeTrackColor = Color(0xFFFF007F),
                        inactiveTrackColor = Color(0xFF382658)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Play / Pause / Replay Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { karaokeEngine.seekTo(0L) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E1C4E))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Restart",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    IconButton(
                        onClick = {
                            if (isPlaying) karaokeEngine.pause() else karaokeEngine.play()
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFF007F), Color(0xFF7928CA))
                                )
                            )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
