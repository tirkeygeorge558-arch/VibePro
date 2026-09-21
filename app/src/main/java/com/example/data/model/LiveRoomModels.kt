package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Saved karaoke recording performance
 */
@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val songId: Long,
    val songTitle: String,
    val songArtist: String,
    val score: Int, // 0 - 10000
    val grade: String, // SSS, SS, S, A, B
    val expEarned: Long,
    val durationSec: Int,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * StarMaker Gift definition
 */
data class GiftItem(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val coinCost: Long,
    val expReward: Long,
    val roomExpReward: Long,
    val isVipOnly: Boolean = false,
    val effectDescription: String
)

object GiftCatalog {
    val gifts = listOf(
        GiftItem("mic", "Golden Mic", "🎤", 10, 25, 50, false, "Microphone sparkles on stage!"),
        GiftItem("rose", "Velvet Rose", "🌹", 20, 50, 100, false, "Shower of red rose petals!"),
        GiftItem("fireworks", "Stage Fireworks", "🎆", 50, 150, 300, false, "Spectacular stage fireworks explosion!"),
        GiftItem("crown", "Diamond Crown", "👑", 100, 350, 700, true, "Imperial crown shines over the singer!"),
        GiftItem("rocket", "Space Rocket", "🚀", 250, 900, 1800, true, "Rocket launch room-wide banner!"),
        GiftItem("galaxy", "Galaxy Supernova", "🪐", 500, 2000, 4000, true, "Cosmic galaxy animation fills the screen!")
    )
}

/**
 * Chat and event message in the live room
 */
data class LiveRoomMessage(
    val id: String,
    val senderName: String,
    val senderLevel: Int,
    val exclusiveId: String? = null,
    val vipTier: Int = 0,
    val nobleTitle: String = "None",
    val text: String,
    val isSystem: Boolean = false,
    val isGift: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
