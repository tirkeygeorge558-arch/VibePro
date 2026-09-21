package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Song entity for StarMaker karaoke singing.
 * Contains synchronized timestamped lyrics (LRC format) and audio backing track data.
 */
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val genre: String, // Pop, Rock, RnB, Acoustic, EDM, Ballad
    val durationSec: Int = 180,
    val lrcLyrics: String, // Standard timestamped LRC: [00:04.20] Lyric line
    val bpm: Int = 120, // Beats per minute for synthesizer accompaniment
    val baseKeyNote: String = "C4", // Musical key
    val chordProgression: String = "C,G,Am,F", // Chord progression for synth playback
    val difficulty: String = "Medium", // Easy, Medium, Hard, Master
    val timesSung: Int = 0,
    val coverEmoji: String = "🎵",
    val uploadedByAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Parsed single line of synchronized karaoke lyrics with timing in milliseconds
 */
data class KaraokeLyricLine(
    val timestampMs: Long,
    val text: String,
    val durationMs: Long = 3500L
) {
    val endTimeMs: Long
        get() = timestampMs + durationMs
}
