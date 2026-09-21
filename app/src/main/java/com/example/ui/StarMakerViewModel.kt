package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.KaraokeEngine
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.firebase.FirestoreService
import com.example.data.model.GiftCatalog
import com.example.data.model.LiveRoomMessage
import com.example.data.model.MicSeat
import com.example.data.model.PartyRoomEntity
import com.example.data.model.RecordingEntity
import com.example.data.model.SongEntity
import com.example.data.model.UserEntity
import com.example.data.model.VipLevel
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SingingSessionResult(
    val songTitle: String,
    val artist: String,
    val score: Int,
    val grade: String,
    val expEarned: Long,
    val coinsEarned: Long
)

class StarMakerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    val karaokeEngine = KaraokeEngine(viewModelScope)
    private var activeRoomListener: ListenerRegistration? = null

    init {
        FirestoreService.initialize(application)
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AppRepository(
            database.userDao(),
            database.songDao(),
            database.partyRoomDao(),
            database.recordingDao()
        )
    }

    // Streams from Repository
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<UserEntity>> = repository.leaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exclusiveUsers: StateFlow<List<UserEntity>> = repository.exclusiveUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSongs: StateFlow<List<SongEntity>> = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRooms: StateFlow<List<PartyRoomEntity>> = repository.allRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Current User
    private val _currentUserId = MutableStateFlow<Long?>(null)
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // User Recordings
    private val _userRecordings = MutableStateFlow<List<RecordingEntity>>(emptyList())
    val userRecordings: StateFlow<List<RecordingEntity>> = _userRecordings.asStateFlow()

    // Active Live Party Room
    private val _activeRoom = MutableStateFlow<PartyRoomEntity?>(null)
    val activeRoom: StateFlow<PartyRoomEntity?> = _activeRoom.asStateFlow()

    private val _roomMessages = MutableStateFlow<List<LiveRoomMessage>>(emptyList())
    val roomMessages: StateFlow<List<LiveRoomMessage>> = _roomMessages.asStateFlow()

    private val _roomSeats = MutableStateFlow<List<MicSeat>>(emptyList())
    val roomSeats: StateFlow<List<MicSeat>> = _roomSeats.asStateFlow()

    // Active Singing Stage State
    private val _activeSong = MutableStateFlow<SongEntity?>(null)
    val activeSong: StateFlow<SongEntity?> = _activeSong.asStateFlow()

    private val _isSingingStageOpen = MutableStateFlow(false)
    val isSingingStageOpen: StateFlow<Boolean> = _isSingingStageOpen.asStateFlow()

    private val _showScoreResult = MutableStateFlow<SingingSessionResult?>(null)
    val showScoreResult: StateFlow<SingingSessionResult?> = _showScoreResult.asStateFlow()

    // Admin Panel State
    private val _showAdminPanel = MutableStateFlow(false)
    val showAdminPanel: StateFlow<Boolean> = _showAdminPanel.asStateFlow()

    private val _showAdminSecretDialog = MutableStateFlow(false)
    val showAdminSecretDialog: StateFlow<Boolean> = _showAdminSecretDialog.asStateFlow()

    // Gift Tray & Exclusive ID Modals
    private val _showGiftSheet = MutableStateFlow(false)
    val showGiftSheet: StateFlow<Boolean> = _showGiftSheet.asStateFlow()

    private val _showExclusiveIdDialog = MutableStateFlow(false)
    val showExclusiveIdDialog: StateFlow<Boolean> = _showExclusiveIdDialog.asStateFlow()

    // Status snackbar / banner notifications
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    init {
        // Observe users and initialize first user if not chosen; sync to Firestore
        viewModelScope.launch {
            allUsers.collect { users ->
                if (users.isNotEmpty()) {
                    users.forEach { FirestoreService.saveUserProfile(it) }
                    val currentId = _currentUserId.value
                    if (currentId == null) {
                        // Default to the first user (Admin)
                        val first = users.first()
                        _currentUserId.value = first.id
                        _currentUser.value = first
                        refreshRecordings(first.id)
                    } else {
                        val refreshed = users.find { it.id == currentId } ?: users.first()
                        _currentUser.value = refreshed
                    }
                }
            }
        }

        // Observe and sync party rooms to Firestore
        viewModelScope.launch {
            allRooms.collect { rooms ->
                rooms.forEach { FirestoreService.savePartyRoom(it) }
            }
        }
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun showToast(msg: String) {
        _snackBarMessage.value = msg
    }

    fun switchUser(user: UserEntity) {
        _currentUserId.value = user.id
        _currentUser.value = user
        refreshRecordings(user.id)
        showToast("Switched user to ${user.username}")
    }

    private fun refreshRecordings(userId: Long) {
        viewModelScope.launch {
            repository.getUserRecordings(userId).collect { list ->
                _userRecordings.value = list
            }
        }
    }

    // Party Room Management
    fun enterPartyRoom(room: PartyRoomEntity) {
        _activeRoom.value = room

        // Initialize 8 mic stage seats
        val seats = (0..7).map { idx ->
            if (idx == 0) {
                MicSeat(0, room.hostUsername, 45, isSinging = true)
            } else {
                MicSeat(idx, null, 1)
            }
        }
        _roomSeats.value = seats

        // Initial room welcome messages
        _roomMessages.value = listOf(
            LiveRoomMessage(
                id = "sys-1",
                senderName = "StarMaker Bot",
                senderLevel = 100,
                text = "🎉 Welcome to ${room.title}! Level up room EXP by singing and gifting.",
                isSystem = true
            ),
            LiveRoomMessage(
                id = "sys-2",
                senderName = room.hostUsername,
                senderLevel = 45,
                text = "Welcome everyone! Grab an open mic seat to sing 🎤"
            )
        )

        // Noble / VIP entrance announcement if current user has noble or high VIP
        val user = _currentUser.value
        if (user != null && (user.nobleTitle != "None" || user.vipTier >= 3)) {
            val announcement = if (user.nobleTitle != "None") {
                "👑 Noble ${user.nobleTitle} [${user.username}] entered with glorious majesty!"
            } else {
                "💎 VIP ${user.vipTier} Star [${user.username}] entered the room in shimmering light!"
            }
            sendSystemAnnouncement(announcement)
        }

        // Real-time Firestore Room updates
        activeRoomListener?.remove()
        activeRoomListener = FirestoreService.listenToPartyRoom(room.id) { level, exp, online ->
            _activeRoom.value?.let { current ->
                if (current.id == room.id && (current.roomLevel != level || current.roomExp != exp)) {
                    _activeRoom.value = current.copy(roomLevel = level, roomExp = exp, onlineCount = online)
                }
            }
        }
    }

    fun leavePartyRoom() {
        activeRoomListener?.remove()
        activeRoomListener = null
        _activeRoom.value = null
        _roomMessages.value = emptyList()
        _roomSeats.value = emptyList()
        karaokeEngine.stop()
    }

    fun takeSeat(seatIndex: Int) {
        val user = _currentUser.value ?: return
        val seats = _roomSeats.value.toMutableList()
        if (seatIndex in seats.indices && seats[seatIndex].occupantName == null) {
            seats[seatIndex] = MicSeat(
                seatIndex = seatIndex,
                occupantName = user.username,
                occupantLevel = user.level,
                isSinging = false
            )
            _roomSeats.value = seats
            sendSystemAnnouncement("🎤 ${user.username} took Mic Seat #${seatIndex + 1}!")
        }
    }

    fun leaveSeat(seatIndex: Int) {
        val seats = _roomSeats.value.toMutableList()
        if (seatIndex in seats.indices) {
            val name = seats[seatIndex].occupantName ?: "User"
            seats[seatIndex] = MicSeat(seatIndex, null, 1)
            _roomSeats.value = seats
            sendSystemAnnouncement("👋 $name left Mic Seat #${seatIndex + 1}")
        }
    }

    fun sendRoomMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        // Check for secret hidden admin code in chat!
        if (trimmed == "*#9999#" || trimmed == "/admin") {
            openAdminPanelOrAuthenticate()
            return
        }

        val user = _currentUser.value ?: return
        val msg = LiveRoomMessage(
            id = System.currentTimeMillis().toString(),
            senderName = user.username,
            senderLevel = user.level,
            exclusiveId = user.exclusiveId,
            vipTier = user.vipTier,
            nobleTitle = user.nobleTitle,
            text = trimmed
        )
        _roomMessages.value = _roomMessages.value + msg

        // Add 5 User EXP and 10 Room EXP for active participation
        viewModelScope.launch {
            repository.addExpToUser(user.id, 5)
            _activeRoom.value?.let { room ->
                repository.addRoomExp(room.id, 10)
            }
        }
    }

    fun sendSystemAnnouncement(text: String) {
        val msg = LiveRoomMessage(
            id = System.currentTimeMillis().toString(),
            senderName = "Room Notice",
            senderLevel = 100,
            text = text,
            isSystem = true
        )
        _roomMessages.value = _roomMessages.value + msg
    }

    fun openGiftSheet() {
        _showGiftSheet.value = true
    }

    fun closeGiftSheet() {
        _showGiftSheet.value = false
    }

    fun sendGift(giftId: String) {
        val user = _currentUser.value ?: return
        val room = _activeRoom.value ?: return
        val gift = GiftCatalog.gifts.find { it.id == giftId } ?: return

        viewModelScope.launch {
            val success = repository.sendGift(user.id, room.id, giftId)
            if (success) {
                closeGiftSheet()
                _currentUser.value?.let { FirestoreService.saveUserProfile(it) }
                _activeRoom.value?.let { FirestoreService.updateRoomExp(it.id, it.roomLevel, it.roomExp) }
                val giftMsg = LiveRoomMessage(
                    id = System.currentTimeMillis().toString(),
                    senderName = user.username,
                    senderLevel = user.level,
                    exclusiveId = user.exclusiveId,
                    vipTier = user.vipTier,
                    nobleTitle = user.nobleTitle,
                    text = "sent ${gift.iconEmoji} ${gift.name}! (+${gift.expReward} EXP, +${gift.roomExpReward} Room EXP)",
                    isGift = true
                )
                _roomMessages.value = _roomMessages.value + giftMsg
                showToast("Sent ${gift.name}! +${gift.expReward} EXP earned 🌟")
            } else {
                showToast("Not enough coins to send ${gift.name}!")
            }
        }
    }

    // Karaoke & Live Singing
    fun startSinging(song: SongEntity, insideRoomId: Long? = null) {
        _activeSong.value = song
        karaokeEngine.loadSong(
            lrcContent = song.lrcLyrics,
            durationSec = song.durationSec,
            bpm = song.bpm,
            baseKey = song.baseKeyNote
        )
        _isSingingStageOpen.value = true
        karaokeEngine.play()
    }

    fun closeSingingStage() {
        karaokeEngine.stop()
        _isSingingStageOpen.value = false
        _activeSong.value = null
    }

    fun finishSinging() {
        val song = _activeSong.value ?: return
        val user = _currentUser.value ?: return
        val finalScore = karaokeEngine.liveScore.value
        val grade = karaokeEngine.computeGrade(finalScore)
        val roomId = _activeRoom.value?.id

        karaokeEngine.stop()
        _isSingingStageOpen.value = false

        viewModelScope.launch {
            val baseExp = (finalScore / 10).toLong().coerceAtLeast(60L)
            repository.finishSingingSession(
                userId = user.id,
                songId = song.id,
                score = finalScore,
                grade = grade,
                roomId = roomId
            )

            val sessionResult = SingingSessionResult(
                songTitle = song.title,
                artist = song.artist,
                score = finalScore,
                grade = grade,
                expEarned = baseExp,
                coinsEarned = (finalScore / 20).toLong()
            )
            _showScoreResult.value = sessionResult

            // Broadcast celebration in active room if applicable
            if (roomId != null) {
                sendSystemAnnouncement(
                    "🏆 [${user.username}] completed singing '${song.title}' with grade $grade ($finalScore pts)! Party room received Room EXP!"
                )
                _activeRoom.value?.let { curRoom ->
                    FirestoreService.updateRoomExp(curRoom.id, curRoom.roomLevel, curRoom.roomExp)
                }
            }

            _currentUser.value?.let { updatedUser ->
                FirestoreService.updateUserLevel(
                    userId = updatedUser.id,
                    level = updatedUser.level,
                    currentExp = updatedUser.currentExp,
                    exclusiveId = updatedUser.exclusiveId
                )
            }
        }
    }

    fun closeScoreResult() {
        _showScoreResult.value = null
    }

    // Exclusive ID Dialog
    fun openExclusiveIdDialog() {
        _showExclusiveIdDialog.value = true
    }

    fun closeExclusiveIdDialog() {
        _showExclusiveIdDialog.value = false
    }

    fun submitExclusiveId(customId: String) {
        val user = _currentUser.value ?: return
        if (user.level < 50) {
            showToast("Exclusive ID requires Level 50 or higher! Current: Lvl ${user.level}")
            return
        }

        viewModelScope.launch {
            val success = repository.setExclusiveId(user.id, customId)
            if (success) {
                closeExclusiveIdDialog()
                FirestoreService.updateUserLevel(user.id, user.level, user.currentExp, customId)
                showToast("Exclusive ID updated to: ★$customId★ 🌟")
            } else {
                showToast("Failed to set exclusive ID. Level 50+ required.")
            }
        }
    }

    // Admin Panel & First User Privilege
    fun openAdminPanelOrAuthenticate() {
        val user = _currentUser.value
        if (user != null && user.isAdmin) {
            _showAdminPanel.value = true
        } else {
            // Secret authentication dialog for first user / secret passcode
            _showAdminSecretDialog.value = true
        }
    }

    fun closeAdminPanel() {
        _showAdminPanel.value = false
    }

    fun closeAdminSecretDialog() {
        _showAdminSecretDialog.value = false
    }

    /**
     * Unlock / claim Admin privileges:
     * First user automatically gets Admin, or user enters secret pass code 'starmaker777' or claims first user key.
     */
    fun claimAdminWithCode(code: String) {
        val user = _currentUser.value ?: return
        val users = allUsers.value

        // Check if user is the first user in DB OR code matches secret
        val isFirstUserInDb = users.isNotEmpty() && users.first().id == user.id
        val codeMatches = code.trim() == "starmaker777" || code.trim() == "*#9999#" || code.trim().lowercase() == "admin"

        if (isFirstUserInDb || codeMatches) {
            viewModelScope.launch {
                repository.adminUpdateUser(
                    userId = user.id,
                    newLevel = user.level,
                    newVipTier = user.vipTier,
                    newNobleTitle = user.nobleTitle,
                    newExclusiveId = user.exclusiveId,
                    isAdmin = true
                )
                closeAdminSecretDialog()
                _showAdminPanel.value = true
                showToast("Admin privileges activated for ${user.username}! 👑")
            }
        } else {
            showToast("Incorrect code. Only the 1st user or valid key can access Admin.")
        }
    }

    // Admin Actions
    fun adminUploadSong(
        title: String,
        artist: String,
        genre: String,
        durationSec: Int,
        lrcLyrics: String,
        bpm: Int,
        difficulty: String,
        coverEmoji: String
    ) {
        viewModelScope.launch {
            repository.adminUploadSong(
                title = title,
                artist = artist,
                genre = genre,
                durationSec = durationSec,
                lrcLyrics = lrcLyrics,
                bpm = bpm,
                difficulty = difficulty,
                coverEmoji = coverEmoji
            )
            showToast("Song '$title' uploaded with timestamped lyrics! 🎶")
        }
    }

    fun adminDeleteSong(songId: Long) {
        viewModelScope.launch {
            repository.adminDeleteSong(songId)
            showToast("Song removed from catalog.")
        }
    }

    fun adminModifyUser(
        userId: Long,
        newLevel: Int,
        newVipTier: Int,
        newNobleTitle: String,
        newExclusiveId: String?,
        isAdmin: Boolean
    ) {
        viewModelScope.launch {
            repository.adminUpdateUser(
                userId = userId,
                newLevel = newLevel,
                newVipTier = newVipTier,
                newNobleTitle = newNobleTitle,
                newExclusiveId = newExclusiveId,
                isAdmin = isAdmin
            )
            FirestoreService.updateUserLevel(
                userId = userId,
                level = newLevel,
                currentExp = repository.expRequiredForLevel(newLevel),
                exclusiveId = newExclusiveId
            )
            showToast("User #$userId updated (Level $newLevel, VIP $newVipTier, Noble $newNobleTitle)")
        }
    }

    fun adminAdjustRoomExp(roomId: Long, newExp: Long) {
        viewModelScope.launch {
            repository.adminSetRoomExp(roomId, newExp)
            FirestoreService.updateRoomExp(
                roomId = roomId,
                roomLevel = (newExp / 600).toInt() + 1,
                roomExp = newExp
            )
            showToast("Room EXP set to $newExp.")
        }
    }
}
