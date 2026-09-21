package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdminSecretDialog
import com.example.ui.components.ExclusiveIdClaimDialog
import com.example.ui.components.GiftBottomSheet
import com.example.ui.components.ScoreCelebrationDialog
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.KaraokeScreen
import com.example.ui.screens.PartyRoomsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RankingVipScreen
import com.example.ui.screens.SingingStageScreen
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val iconEmoji: String) {
    SING("Sing", "🎤"),
    ROOMS("Live Rooms", "🪐"),
    PRESTIGE("Prestige", "🏆"),
    PROFILE("Profile", "👤")
}

@Composable
fun MainAppScreen(
    viewModel: StarMakerViewModel,
    onRequestRecordAudioPermission: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val songs by viewModel.allSongs.collectAsState()
    val rooms by viewModel.allRooms.collectAsState()
    val recordings by viewModel.userRecordings.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val exclusiveUsers by viewModel.exclusiveUsers.collectAsState()

    val showAdminPanel by viewModel.showAdminPanel.collectAsState()
    val activeSingingSong by viewModel.activeSong.collectAsState()
    val isSingingStageOpen by viewModel.isSingingStageOpen.collectAsState()
    val activePartyRoom by viewModel.activeRoom.collectAsState()
    val roomSeats by viewModel.roomSeats.collectAsState()
    val roomMessages by viewModel.roomMessages.collectAsState()

    val showAdminSecretDialog by viewModel.showAdminSecretDialog.collectAsState()
    val showExclusiveIdDialog by viewModel.showExclusiveIdDialog.collectAsState()
    val showScoreResult by viewModel.showScoreResult.collectAsState()
    val showGiftSheet by viewModel.showGiftSheet.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()

    val isFirstUser = allUsers.isNotEmpty() && currentUser?.id == allUsers.first().id

    var selectedTab by remember { mutableStateOf(MainTab.SING) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Top logo click counter for secret admin access
    var logoTapCount by remember { mutableIntStateOf(0) }

    // Display ViewModel snackbar toasts
    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackBar()
        }
    }

    // Request audio permission when opening singing stage
    LaunchedEffect(isSingingStageOpen) {
        if (isSingingStageOpen) {
            onRequestRecordAudioPermission()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0C0717))) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF140A26))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            logoTapCount++
                            if (logoTapCount >= 3) {
                                logoTapCount = 0
                                viewModel.openAdminPanelOrAuthenticate()
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFF007F), Color(0xFFFFD700))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("★", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "StarMaker",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SING LIVE",
                                    color = Color(0xFFFF007F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "Max Level 100 • Lv.50 Exclusive ID",
                                color = Color(0xFF9F92C2),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        currentUser?.let { user ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF261842))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙 ${user.coins}", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            if (user.isAdmin) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF007F))
                                        .clickable { viewModel.openAdminPanelOrAuthenticate() }
                                        .padding(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "Admin Panel",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom Navigation
                NavigationBar(
                    containerColor = Color(0xFF130A24),
                    tonalElevation = 8.dp
                ) {
                    MainTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Text(text = tab.iconEmoji, fontSize = 20.sp)
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color(0xFFFF007F),
                                unselectedIconColor = Color(0xFF7A6D99),
                                unselectedTextColor = Color(0xFF7A6D99),
                                indicatorColor = Color(0xFFFF007F).copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    MainTab.SING -> KaraokeScreen(
                        songs = songs,
                        onSingSong = { song -> viewModel.startSinging(song) },
                        onAdminSearchTrigger = { code ->
                            viewModel.claimAdminWithCode(code)
                        }
                    )

                    MainTab.ROOMS -> PartyRoomsScreen(
                        rooms = rooms,
                        activeRoom = activePartyRoom,
                        roomSeats = roomSeats,
                        roomMessages = roomMessages,
                        currentUser = currentUser,
                        songs = songs,
                        onEnterRoom = { room -> viewModel.enterPartyRoom(room) },
                        onLeaveRoom = { viewModel.leavePartyRoom() },
                        onTakeSeat = { seatIndex -> viewModel.takeSeat(seatIndex) },
                        onLeaveSeat = { seatIndex -> viewModel.leaveSeat(seatIndex) },
                        onSendMessage = { msg -> viewModel.sendRoomMessage(msg) },
                        onOpenGiftSheet = { viewModel.openGiftSheet() },
                        onSingSongInRoom = { song -> viewModel.startSinging(song) },
                        onPlayCheerEffect = { cheerText -> viewModel.sendSystemAnnouncement(cheerText) }
                    )

                    MainTab.PRESTIGE -> RankingVipScreen(
                        leaderboard = leaderboard,
                        exclusiveUsers = exclusiveUsers
                    )

                    MainTab.PROFILE -> ProfileScreen(
                        currentUser = currentUser,
                        allUsers = allUsers,
                        recordings = recordings,
                        onSwitchUser = { user -> viewModel.switchUser(user) },
                        onOpenExclusiveIdDialog = { viewModel.openExclusiveIdDialog() },
                        onTriggerAdminPanel = { viewModel.openAdminPanelOrAuthenticate() }
                    )
                }
            }
        }

        // Full Screen Karaoke Singing Stage Overlay
        if (isSingingStageOpen && activeSingingSong != null) {
            SingingStageScreen(
                song = activeSingingSong!!,
                karaokeEngine = viewModel.karaokeEngine,
                onClose = { viewModel.closeSingingStage() },
                onFinish = { viewModel.finishSinging() }
            )
        }

        // Full Screen Admin Panel Overlay
        if (showAdminPanel) {
            AdminPanelScreen(
                songs = songs,
                users = allUsers,
                rooms = rooms,
                onClose = { viewModel.closeAdminPanel() },
                onUploadSong = { title, artist, genre, durationSec, lrcLyrics, bpm, difficulty, coverEmoji ->
                    viewModel.adminUploadSong(
                        title, artist, genre, durationSec, lrcLyrics, bpm, difficulty, coverEmoji
                    )
                },
                onDeleteSong = { songId -> viewModel.adminDeleteSong(songId) },
                onModifyUser = { userId, newLevel, newVipTier, newNobleTitle, newExclusiveId, isAdmin ->
                    viewModel.adminModifyUser(
                        userId, newLevel, newVipTier, newNobleTitle, newExclusiveId, isAdmin
                    )
                },
                onAdjustRoomExp = { roomId, newExp ->
                    viewModel.adminAdjustRoomExp(roomId, newExp)
                }
            )
        }

        // Gift Tray Bottom Sheet
        if (showGiftSheet) {
            GiftBottomSheet(
                userCoins = currentUser?.coins ?: 0L,
                onSendGift = { giftId ->
                    viewModel.sendGift(giftId)
                },
                onDismiss = { viewModel.closeGiftSheet() }
            )
        }

        // Secret Admin Authentication Dialog
        if (showAdminSecretDialog) {
            AdminSecretDialog(
                isFirstUser = isFirstUser,
                onClaimWithCode = { code ->
                    viewModel.claimAdminWithCode(code)
                },
                onDismiss = { viewModel.closeAdminSecretDialog() }
            )
        }

        // Exclusive ID Setup Dialog (Lv 50+)
        if (showExclusiveIdDialog) {
            ExclusiveIdClaimDialog(
                currentExclusiveId = currentUser?.exclusiveId,
                userLevel = currentUser?.level ?: 1,
                onSubmit = { newId ->
                    viewModel.submitExclusiveId(newId)
                },
                onDismiss = { viewModel.closeExclusiveIdDialog() }
            )
        }

        // Score Celebration Dialog
        showScoreResult?.let { result ->
            ScoreCelebrationDialog(
                result = result,
                onDismiss = { viewModel.closeScoreResult() }
            )
        }
    }
}
