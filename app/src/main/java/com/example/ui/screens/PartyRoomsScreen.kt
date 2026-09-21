package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveRoomMessage
import com.example.data.model.MicSeat
import com.example.data.model.PartyRoomEntity
import com.example.data.model.SongEntity
import com.example.data.model.UserEntity
import com.example.ui.components.ExclusiveIdTag
import com.example.ui.components.LevelBadge
import com.example.ui.components.NobleBadge
import com.example.ui.components.VipBadge

/**
 * Party Rooms List or Live Stage Screen
 */
@Composable
fun PartyRoomsScreen(
    rooms: List<PartyRoomEntity>,
    activeRoom: PartyRoomEntity?,
    roomSeats: List<MicSeat>,
    roomMessages: List<LiveRoomMessage>,
    currentUser: UserEntity?,
    songs: List<SongEntity>,
    onEnterRoom: (PartyRoomEntity) -> Unit,
    onLeaveRoom: () -> Unit,
    onTakeSeat: (Int) -> Unit,
    onLeaveSeat: (Int) -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenGiftSheet: () -> Unit,
    onSingSongInRoom: (SongEntity) -> Unit,
    onPlayCheerEffect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeRoom != null) {
        // Active Live Room Stage
        ActiveLiveRoomStage(
            room = activeRoom,
            seats = roomSeats,
            messages = roomMessages,
            currentUser = currentUser,
            songs = songs,
            onLeaveRoom = onLeaveRoom,
            onTakeSeat = onTakeSeat,
            onLeaveSeat = onLeaveSeat,
            onSendMessage = onSendMessage,
            onOpenGiftSheet = onOpenGiftSheet,
            onSingSongInRoom = onSingSongInRoom,
            onPlayCheerEffect = onPlayCheerEffect,
            modifier = modifier
        )
    } else {
        // Rooms Explorer list
        RoomsExplorerView(
            rooms = rooms,
            onEnterRoom = onEnterRoom,
            modifier = modifier
        )
    }
}

/**
 * Rooms Catalog / Explorer
 */
@Composable
fun RoomsExplorerView(
    rooms: List<PartyRoomEntity>,
    onEnterRoom: (PartyRoomEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F081D))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🪐 Live Singing Rooms",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Join 8-Mic karaoke party rooms and boost room EXP!",
                    color = Color(0xFFAFA2D1),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(rooms) { room ->
                PartyRoomCard(room = room, onClick = { onEnterRoom(room) })
            }
        }
    }
}

@Composable
fun PartyRoomCard(
    room: PartyRoomEntity,
    onClick: () -> Unit
) {
    val themeGradient = when (room.roomTheme) {
        "GoldGala" -> listOf(Color(0xFF4A3405), Color(0xFF231438))
        "Cyberpunk" -> listOf(Color(0xFF0A2E38), Color(0xFF220A38))
        else -> listOf(Color(0xFF281347), Color(0xFF14092B))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(themeGradient))
            .border(1.dp, Color(0xFF4B346E), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF007F))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Room Lv.${room.roomLevel}",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Online",
                        tint = Color(0xFF00F5D4),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.onlineCount} online",
                        color = Color(0xFF00F5D4),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = room.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Host: ${room.hostUsername} • ${room.category}",
                color = Color(0xFFB8AAD9),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Room EXP Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Room EXP: ${room.roomExp} pts",
                        color = Color(0xFFA093C4),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Next Lv: ${room.roomExp % 600}/600",
                        color = Color(0xFFFF9E00),
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { room.levelProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFFFFD700),
                    trackColor = Color(0x33FFD700)
                )
            }
        }
    }
}

/**
 * Inside the Live Party Room Stage
 */
@Composable
fun ActiveLiveRoomStage(
    room: PartyRoomEntity,
    seats: List<MicSeat>,
    messages: List<LiveRoomMessage>,
    currentUser: UserEntity?,
    songs: List<SongEntity>,
    onLeaveRoom: () -> Unit,
    onTakeSeat: (Int) -> Unit,
    onLeaveSeat: (Int) -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenGiftSheet: () -> Unit,
    onSingSongInRoom: (SongEntity) -> Unit,
    onPlayCheerEffect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var chatInput by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()
    var showSongPicker by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            chatListState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0717))
            .padding(12.dp)
    ) {
        // Room Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onLeaveRoom) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Leave Room",
                    tint = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = room.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Room Lv.${room.roomLevel}",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${room.roomExp} EXP",
                        color = Color(0xFF00F5D4),
                        fontSize = 10.sp
                    )
                }
            }

            // Gift Button
            Button(
                onClick = onOpenGiftSheet,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("🎁 Gift", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Room EXP Progress line
        LinearProgressIndicator(
            progress = { room.levelProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFFFFD700),
            trackColor = Color(0x33FFD700)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 8-Mic Stage Grid
        Text(
            text = "🎤 8-Mic Live Stage",
            color = Color(0xFFD6CEF0),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF170F28))
                .border(1.dp, Color(0xFF332057), RoundedCornerShape(14.dp))
                .padding(8.dp)
        ) {
            items(seats) { seat ->
                MicSeatItem(
                    seat = seat,
                    isCurrentUser = seat.occupantName == currentUser?.username,
                    onTakeSeat = { onTakeSeat(seat.seatIndex) },
                    onLeaveSeat = { onLeaveSeat(seat.seatIndex) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cheering Soundboard & Song Launcher Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showSongPicker = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7928CA)),
                modifier = Modifier.weight(1.3f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Sing", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sing On Stage", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Cheering SFX Buttons
            CheerSfxButton("👏", "Clap") { onPlayCheerEffect("👏 Applause echoes across the room!") }
            CheerSfxButton("🎉", "Cheer") { onPlayCheerEffect("🎉 The audience goes wild cheering!") }
            CheerSfxButton("💖", "Heart") { onPlayCheerEffect("💖 Shower of love hearts for the singer!") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Live Room Chat & Events feed
        LazyColumn(
            state = chatListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF130B22))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { msg ->
                RoomChatMessageItem(msg = msg)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Chat in room or type *#9999#...", color = Color(0xFF756A92), fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1C1333),
                    unfocusedContainerColor = Color(0xFF1C1333),
                    focusedBorderColor = Color(0xFFFF007F),
                    unfocusedBorderColor = Color(0xFF332555),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (chatInput.isNotBlank()) {
                        onSendMessage(chatInput)
                        chatInput = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF007F))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // Modal Song Picker to sing inside room
    if (showSongPicker) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSongPicker = false },
            containerColor = Color(0xFF1D1435),
            title = {
                Text(
                    text = "Select Song to Sing on Stage",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(songs) { song ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF2A1C4C))
                                .clickable {
                                    showSongPicker = false
                                    onSingSongInRoom(song)
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = song.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${song.artist} • ${song.genre}",
                                        color = Color(0xFFA597C9),
                                        fontSize = 11.sp
                                    )
                                }
                                Text("🎤 Sing", color = Color(0xFFFF007F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSongPicker = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A3A69))
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun MicSeatItem(
    seat: MicSeat,
    isCurrentUser: Boolean,
    onTakeSeat: () -> Unit,
    onLeaveSeat: () -> Unit
) {
    val isOccupied = seat.occupantName != null
    val isHost = seat.seatIndex == 0

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseBorder by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_width"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isOccupied) Color(0xFF261842) else Color(0xFF1A1030))
            .border(
                width = if (seat.isSinging) pulseBorder.dp else 1.dp,
                color = when {
                    seat.isSinging -> Color(0xFFFF007F)
                    isHost -> Color(0xFFFFD700)
                    isOccupied -> Color(0xFF4C3870)
                    else -> Color(0xFF2F214D)
                },
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                if (isOccupied) {
                    if (isCurrentUser) onLeaveSeat()
                } else {
                    onTakeSeat()
                }
            }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    if (isOccupied) Brush.linearGradient(
                        listOf(Color(0xFFFF007F), Color(0xFF7928CA))
                    ) else Brush.linearGradient(listOf(Color(0xFF2C1F48), Color(0xFF2C1F48)))
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isOccupied) {
                Text(
                    text = seat.occupantName?.take(1) ?: "U",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Take Mic",
                    tint = Color(0xFF8675AC),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = if (isOccupied) (seat.occupantName ?: "") else "Mic ${seat.seatIndex + 1}",
            color = if (isOccupied) Color.White else Color(0xFF8E7FA8),
            fontSize = 10.sp,
            fontWeight = if (isOccupied) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )

        if (isOccupied) {
            Text(
                text = if (isCurrentUser) "Leave" else (if (isHost) "👑 Host" else "Lv.${seat.occupantLevel}"),
                color = if (isCurrentUser) Color(0xFFFF5252) else Color(0xFFFFD700),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CheerSfxButton(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF22163B))
            .border(1.dp, Color(0xFF3C2963), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(3.dp))
            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun RoomChatMessageItem(msg: LiveRoomMessage) {
    if (msg.isSystem) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C194D).copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = msg.text,
                color = Color(0xFFFFD700),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else if (msg.isGift) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFFF007F).copy(alpha = 0.25f), Color(0xFF7928CA).copy(alpha = 0.25f))))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "🎁 ${msg.senderName} ${msg.text}",
                color = Color(0xFFFF80BF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            LevelBadge(level = msg.senderLevel)
            Spacer(modifier = Modifier.width(4.dp))
            if (msg.vipTier > 0) {
                VipBadge(vipTier = msg.vipTier)
                Spacer(modifier = Modifier.width(4.dp))
            }
            if (msg.nobleTitle != "None") {
                NobleBadge(nobleTitle = msg.nobleTitle)
                Spacer(modifier = Modifier.width(4.dp))
            }
            if (!msg.exclusiveId.isNullOrBlank()) {
                ExclusiveIdTag(exclusiveId = msg.exclusiveId, level = msg.senderLevel)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "${msg.senderName}: ",
                color = Color(0xFFB5A7D8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = msg.text,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}
