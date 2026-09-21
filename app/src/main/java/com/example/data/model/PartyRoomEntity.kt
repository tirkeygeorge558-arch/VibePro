package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Live Singing Party Room entity.
 * - Supports 8 microphone stage seats
 * - Tracks room EXP and room Level (levels up through singing & gifts)
 */
@Entity(tableName = "party_rooms")
data class PartyRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val hostUserId: Long,
    val hostUsername: String,
    val category: String = "Karaoke Club", // Karaoke Club, Pop Stage, Duet Battle, Late Night Chill
    val roomExp: Long = 0,
    val roomLevel: Int = 1, // 1 to 50
    val onlineCount: Int = 12,
    val micSeatsJson: String = "[]", // Serialized or comma-separated occupant names
    val currentSongId: Long? = null,
    val announcement: String = "Welcome to the party! Mic open for singers 🎤🎶",
    val isLive: Boolean = true,
    val roomTheme: String = "NeonClub", // NeonClub, Cyberpunk, GoldGala, Galaxy
    val totalGiftsValue: Long = 0
) {
    /**
     * Calculates room level progression:
     * e.g., 500 exp per level
     */
    val nextLevelExp: Long
        get() = (roomLevel * 600L)

    val levelProgress: Float
        get() {
            val expInLevel = roomExp % 600L
            return (expInLevel.toFloat() / 600f).coerceIn(0f, 1f)
        }
}

/**
 * Seat model on the 8-mic stage
 */
data class MicSeat(
    val seatIndex: Int, // 0 = Host, 1..7 = Guest Mics
    val occupantName: String? = null,
    val occupantLevel: Int = 1,
    val isSinging: Boolean = false,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false
)
