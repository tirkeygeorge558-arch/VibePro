package com.example.data

import com.example.data.dao.PartyRoomDao
import com.example.data.dao.RecordingDao
import com.example.data.dao.SongDao
import com.example.data.dao.UserDao
import com.example.data.model.GiftCatalog
import com.example.data.model.GiftItem
import com.example.data.model.PartyRoomEntity
import com.example.data.model.RecordingEntity
import com.example.data.model.SongEntity
import com.example.data.model.UserEntity
import com.example.data.model.VipLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.min
import kotlin.math.sqrt

class AppRepository(
    private val userDao: UserDao,
    private val songDao: SongDao,
    private val partyRoomDao: PartyRoomDao,
    private val recordingDao: RecordingDao
) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val leaderboard: Flow<List<UserEntity>> = userDao.getLeaderboard()
    val exclusiveUsers: Flow<List<UserEntity>> = userDao.getExclusiveIdUsers()

    val allSongs: Flow<List<SongEntity>> = songDao.getAllSongs()
    val allRooms: Flow<List<PartyRoomEntity>> = partyRoomDao.getAllRooms()

    fun getUser(id: Long): Flow<UserEntity?> = userDao.getUserById(id)
    fun getRoom(id: Long): Flow<PartyRoomEntity?> = partyRoomDao.getRoomById(id)
    fun getSong(id: Long): Flow<SongEntity?> = songDao.getSongById(id)
    fun getUserRecordings(userId: Long): Flow<List<RecordingEntity>> = recordingDao.getRecordingsByUser(userId)

    suspend fun getFirstUser(): UserEntity? = userDao.getFirstUser()

    /**
     * Level calculation logic:
     * - Minimum level: 1
     * - Maximum level: strictly 100 ("level only 100 level")
     * - Level 50+ qualifies for Exclusive ID ("from 50 level get exclusive id")
     */
    fun calculateLevelFromExp(exp: Long): Int {
        if (exp <= 0) return 1
        // Level curve: EXP = Level^2 * 100. So Level = sqrt(exp / 100).
        val computedLevel = (sqrt(exp.toDouble() / 100.0)).toInt().coerceIn(1, 100)
        return computedLevel
    }

    fun expRequiredForLevel(level: Int): Long {
        val lvl = level.coerceIn(1, 100)
        return lvl.toLong() * lvl.toLong() * 100L
    }

    /**
     * Add EXP to a user, recalculating their level up to 100 max.
     * Automatically unlocks Exclusive ID if level reaches 50!
     */
    suspend fun addExpToUser(userId: Long, expToAdd: Long): UserEntity? {
        val user = userDao.getUserByIdOnce(userId) ?: return null

        // VIP EXP Multiplier bonus
        val vipBonusMultiplier = 1.0 + (VipLevel.fromTier(user.vipTier).expBonusPercent / 100.0)
        val finalExpGain = (expToAdd * vipBonusMultiplier).toLong()

        val newTotalExp = user.currentExp + finalExpGain
        val newLevel = calculateLevelFromExp(newTotalExp)

        // If newly reached 50 and doesn't have an exclusive ID yet, auto-assign a golden Exclusive ID
        val updatedExclusiveId = if (newLevel >= 50 && user.exclusiveId.isNullOrBlank()) {
            "★GOLD-${(1000..9999).random()}★"
        } else if (newLevel < 50) {
            null
        } else {
            user.exclusiveId
        }

        val updatedUser = user.copy(
            currentExp = newTotalExp,
            level = newLevel,
            exclusiveId = updatedExclusiveId
        )
        userDao.updateUser(updatedUser)
        return updatedUser
    }

    /**
     * Update user profile with manual level (1-100), VIP tier, Noble, and Exclusive ID (Admin action)
     */
    suspend fun adminUpdateUser(
        userId: Long,
        newLevel: Int,
        newVipTier: Int,
        newNobleTitle: String,
        newExclusiveId: String?,
        isAdmin: Boolean
    ) {
        val user = userDao.getUserByIdOnce(userId) ?: return
        val clampedLevel = newLevel.coerceIn(1, 100)
        val finalExclusiveId = if (clampedLevel >= 50) {
            if (!newExclusiveId.isNullOrBlank()) newExclusiveId else (user.exclusiveId ?: "★VIP-${(1000..9999).random()}★")
        } else {
            null // Exclusive ID only allowed from level 50+
        }

        val updated = user.copy(
            level = clampedLevel,
            vipTier = newVipTier.coerceIn(0, 5),
            nobleTitle = newNobleTitle,
            exclusiveId = finalExclusiveId,
            isAdmin = isAdmin,
            currentExp = expRequiredForLevel(clampedLevel)
        )
        userDao.updateUser(updated)
    }

    /**
     * Set a custom exclusive ID for a level 50+ user
     */
    suspend fun setExclusiveId(userId: Long, customId: String): Boolean {
        val user = userDao.getUserByIdOnce(userId) ?: return false
        if (user.level < 50) return false // Level 50 requirement

        val sanitized = customId.trim()
        val finalId = if (sanitized.startsWith("★") && sanitized.endsWith("★")) sanitized else "★$sanitized★"
        userDao.updateUser(user.copy(exclusiveId = finalId))
        return true
    }

    /**
     * Admin Song Upload / Creation with Timestamped Lyrics
     */
    suspend fun adminUploadSong(
        title: String,
        artist: String,
        genre: String,
        durationSec: Int,
        lrcLyrics: String,
        bpm: Int,
        difficulty: String,
        coverEmoji: String
    ): Long {
        val song = SongEntity(
            title = title.trim(),
            artist = artist.trim(),
            genre = genre,
            durationSec = durationSec.coerceAtLeast(30),
            lrcLyrics = lrcLyrics.trim(),
            bpm = bpm.coerceIn(60, 200),
            difficulty = difficulty,
            coverEmoji = coverEmoji,
            uploadedByAdmin = true
        )
        return songDao.insertSong(song)
    }

    /**
     * Delete a song (Admin privilege)
     */
    suspend fun adminDeleteSong(songId: Long) {
        songDao.deleteSong(songId)
    }

    /**
     * Complete a singing session:
     * - Records the performance
     * - Grants User EXP
     * - Grants Room EXP if singing inside a party room
     * - Increments song play counter
     */
    suspend fun finishSingingSession(
        userId: Long,
        songId: Long,
        score: Int,
        grade: String,
        roomId: Long?
    ) {
        val song = songDao.getSongByIdOnce(songId) ?: return
        val user = userDao.getUserByIdOnce(userId) ?: return

        // Calculate EXP based on performance score
        val baseExp = (score / 10).toLong().coerceAtLeast(50L)
        addExpToUser(userId, baseExp)

        // Save recording
        recordingDao.insertRecording(
            RecordingEntity(
                userId = userId,
                songId = songId,
                songTitle = song.title,
                songArtist = song.artist,
                score = score,
                grade = grade,
                expEarned = baseExp,
                durationSec = song.durationSec
            )
        )

        // Increment times sung
        songDao.incrementTimesSung(songId)

        // Update user stats
        userDao.updateUser(
            user.copy(
                songsSung = user.songsSung + 1,
                totalScore = user.totalScore + score,
                coins = user.coins + (score / 20)
            )
        )

        // If in a party room, award Room EXP
        if (roomId != null) {
            val roomExpReward = (score / 8).toLong().coerceAtLeast(100L)
            partyRoomDao.addRoomExp(roomId, roomExpReward)
        }
    }

    /**
     * Send gift in a party room:
     * - Deducts coins from user
     * - Grants User EXP
     * - Grants Room EXP
     * - Updates room total gifts value
     */
    suspend fun sendGift(
        userId: Long,
        roomId: Long,
        giftId: String
    ): Boolean {
        val user = userDao.getUserByIdOnce(userId) ?: return false
        val room = partyRoomDao.getRoomByIdOnce(roomId) ?: return false
        val gift = GiftCatalog.gifts.find { it.id == giftId } ?: return false

        if (user.coins < gift.coinCost) {
            return false // Insufficient coins
        }

        // Deduct coins & add EXP to sender
        val updatedUser = user.copy(coins = user.coins - gift.coinCost)
        userDao.updateUser(updatedUser)
        addExpToUser(userId, gift.expReward)

        // Add Room EXP to party room
        partyRoomDao.addRoomExp(roomId, gift.roomExpReward)

        // Update room gifts value
        partyRoomDao.updateRoom(
            room.copy(
                totalGiftsValue = room.totalGiftsValue + gift.coinCost
            )
        )
        return true
    }

    suspend fun addRoomExp(roomId: Long, exp: Long) {
        partyRoomDao.addRoomExp(roomId, exp)
    }

    /**
     * Adjust party room EXP directly (Admin privilege)
     */
    suspend fun adminSetRoomExp(roomId: Long, newExp: Long) {
        val room = partyRoomDao.getRoomByIdOnce(roomId) ?: return
        val clampedExp = newExp.coerceAtLeast(0)
        val computedLevel = (clampedExp / 600).toInt() + 1
        partyRoomDao.updateRoom(room.copy(roomExp = clampedExp, roomLevel = computedLevel))
    }

    /**
     * Create a new party room
     */
    suspend fun createRoom(
        title: String,
        hostUserId: Long,
        hostUsername: String,
        category: String
    ): Long {
        val newRoom = PartyRoomEntity(
            title = title,
            hostUserId = hostUserId,
            hostUsername = hostUsername,
            category = category,
            roomExp = 0,
            roomLevel = 1,
            onlineCount = 1
        )
        return partyRoomDao.insertRoom(newRoom)
    }
}
