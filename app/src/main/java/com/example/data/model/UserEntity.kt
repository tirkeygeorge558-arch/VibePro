package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity representing a StarMaker singer.
 * - Maximum level: 100
 * - Level 50+ unlocks Exclusive ID (custom vanity/golden ID)
 * - First registered user automatically receives isAdmin = true
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val standardId: String, // e.g. "SM-104928"
    val exclusiveId: String? = null, // e.g. "★STAR-KING★" - unlocked at Level 50+
    val avatarColor: Long = 0xFFFF2A85,
    val level: Int = 1, // 1 to 100
    val currentExp: Long = 0,
    val coins: Long = 1000,
    val diamonds: Long = 100,
    val vipTier: Int = 0, // 0 = None, 1..5 = VIP 1 to VIP 5
    val nobleTitle: String = "None", // Knight, Baron, Viscount, Count, Marquis, Duke, King, Emperor
    val isAdmin: Boolean = false, // Automatically true for first user in DB!
    val bio: String = "Singing my heart out on StarMaker 🎤✨",
    val songsSung: Int = 0,
    val totalScore: Long = 0,
    val followers: Int = 128,
    val following: Int = 45,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Checks if this user qualifies for Exclusive ID (Level >= 50).
     */
    val hasExclusiveIdAccess: Boolean
        get() = level >= 50

    /**
     * Display ID shown on profile and in rooms:
     * Shows Exclusive ID if available and level >= 50, otherwise standard ID.
     */
    val displayId: String
        get() = if (level >= 50 && !exclusiveId.isNullOrBlank()) exclusiveId else standardId
}

/**
 * VIP Tier definition with perks and styling
 */
enum class VipLevel(
    val tier: Int,
    val title: String,
    val color: Long,
    val expBonusPercent: Int,
    val badgeIcon: String
) {
    NONE(0, "Regular", 0xFF9E9E9E, 0, "👤"),
    VIP_1(1, "Bronze VIP", 0xFFCD7F32, 20, "🥉"),
    VIP_2(2, "Silver VIP", 0xFFC0C0C0, 40, "🥈"),
    VIP_3(3, "Gold VIP", 0xFFFFD700, 60, "🥇"),
    VIP_4(4, "Platinum VIP", 0xFF00E5FF, 80, "💎"),
    VIP_5(5, "Star Diamond VIP", 0xFFFF1493, 100, "👑");

    companion object {
        fun fromTier(tier: Int): VipLevel = entries.find { it.tier == tier } ?: NONE
    }
}

/**
 * StarMaker Noble System
 */
enum class NobleRank(
    val title: String,
    val order: Int,
    val color: Long,
    val crown: String,
    val mountName: String
) {
    NONE("None", 0, 0xFF888888, "", "None"),
    KNIGHT("Knight", 1, 0xFF4CAF50, "⚔️", "War Horse"),
    BARON("Baron", 2, 0xFF2196F3, "🛡️", "Silver Pegasus"),
    VISCOUNT("Viscount", 3, 0xFF9C27B0, "🗡️", "Night Panther"),
    COUNT("Count", 4, 0xFFE91E63, "⚜️", "Golden Griffin"),
    MARQUIS("Marquis", 5, 0xFFFF9800, "👑", "Frost Phoenix"),
    DUKE("Duke", 6, 0xFF00BCD4, "💎", "Celestial Dragon"),
    KING("King", 7, 0xFFFFD700, "🌟", "Solar Chariot"),
    EMPEROR("Emperor", 8, 0xFFFF0055, "⚡", "Cosmic Throne");

    companion object {
        fun fromTitle(title: String): NobleRank = entries.find { it.title.equals(title, ignoreCase = true) } ?: NONE
    }
}
