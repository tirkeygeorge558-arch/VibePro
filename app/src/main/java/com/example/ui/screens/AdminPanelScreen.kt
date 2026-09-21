package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NobleRank
import com.example.data.model.PartyRoomEntity
import com.example.data.model.SongEntity
import com.example.data.model.UserEntity
import com.example.data.model.VipLevel
import com.example.ui.components.ExclusiveIdTag
import com.example.ui.components.LevelBadge
import com.example.ui.components.NobleBadge
import com.example.ui.components.VipBadge

@Composable
fun AdminPanelScreen(
    songs: List<SongEntity>,
    users: List<UserEntity>,
    rooms: List<PartyRoomEntity>,
    onClose: () -> Unit,
    onUploadSong: (
        title: String,
        artist: String,
        genre: String,
        durationSec: Int,
        lrcLyrics: String,
        bpm: Int,
        difficulty: String,
        coverEmoji: String
    ) -> Unit,
    onDeleteSong: (Long) -> Unit,
    onModifyUser: (
        userId: Long,
        newLevel: Int,
        newVipTier: Int,
        newNobleTitle: String,
        newExclusiveId: String?,
        isAdmin: Boolean
    ) -> Unit,
    onAdjustRoomExp: (roomId: Long, newExp: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var adminTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🎵 Song Upload (LRC)", "👤 User & VIP / Noble", "🪐 Room EXP")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0619))
            .padding(16.dp)
    ) {
        // Admin Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "👑 Hidden Admin Control Panel",
                    color = Color(0xFFFFD700),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "First User Admin Privilege • Songs & Prestige",
                    color = Color(0xFFAFA2D1),
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Admin",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = adminTab,
            containerColor = Color(0xFF190F2C),
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[adminTab]),
                    color = Color(0xFFFF007F)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = adminTab == index,
                    onClick = { adminTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (adminTab == index) Color(0xFFFFE600) else Color(0xFF8678A3),
                            fontWeight = if (adminTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (adminTab) {
            0 -> AdminSongUploadTab(
                songs = songs,
                onUploadSong = onUploadSong,
                onDeleteSong = onDeleteSong
            )
            1 -> AdminUserManagementTab(
                users = users,
                onModifyUser = onModifyUser
            )
            2 -> AdminRoomExpTab(
                rooms = rooms,
                onAdjustRoomExp = onAdjustRoomExp
            )
        }
    }
}

/**
 * Tab 1: Song Upload with Timestamped Lyrics (LRC)
 */
@Composable
fun AdminSongUploadTab(
    songs: List<SongEntity>,
    onUploadSong: (
        title: String,
        artist: String,
        genre: String,
        durationSec: Int,
        lrcLyrics: String,
        bpm: Int,
        difficulty: String,
        coverEmoji: String
    ) -> Unit,
    onDeleteSong: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Pop Hits") }
    var durationSec by remember { mutableIntStateOf(120) }
    var bpm by remember { mutableIntStateOf(120) }
    var difficulty by remember { mutableStateOf("Medium") }
    var coverEmoji by remember { mutableStateOf("🎤") }
    var lrcLyrics by remember {
        mutableStateOf(
            """
                [00:02.00] Step up to the microphone
                [00:06.00] In this live singing room you're not alone
                [00:10.50] Hit the high note, make the crowd go wild
                [00:15.00] StarMaker shining on every singing child!
            """.trimIndent()
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF19102B))
                    .border(1.dp, Color(0xFF38235C), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Upload Karaoke Song + Timestamps (LRC)",
                        color = Color(0xFFFFD700),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Song Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = artist,
                        onValueChange = { artist = it },
                        label = { Text("Artist Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Genre") },
                            modifier = Modifier.weight(1f),
                            colors = outlinedColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = coverEmoji,
                            onValueChange = { coverEmoji = it },
                            label = { Text("Emoji") },
                            modifier = Modifier.width(70.dp),
                            colors = outlinedColors(),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = durationSec.toString(),
                            onValueChange = { durationSec = it.toIntOrNull() ?: 120 },
                            label = { Text("Duration (sec)") },
                            modifier = Modifier.weight(1f),
                            colors = outlinedColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = bpm.toString(),
                            onValueChange = { bpm = it.toIntOrNull() ?: 120 },
                            label = { Text("BPM (Synth Tempo)") },
                            modifier = Modifier.weight(1f),
                            colors = outlinedColors(),
                            singleLine = true
                        )
                    }

                    // Timestamped Lyrics Area
                    Text(
                        text = "Synchronized Lyrics with Timestamps (Format: [mm:ss.xx] line)",
                        color = Color(0xFFB4A7D4),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = lrcLyrics,
                        onValueChange = { lrcLyrics = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        colors = outlinedColors()
                    )

                    Button(
                        onClick = {
                            if (title.isNotBlank() && artist.isNotBlank()) {
                                onUploadSong(
                                    title,
                                    artist,
                                    genre,
                                    durationSec,
                                    lrcLyrics,
                                    bpm,
                                    difficulty,
                                    coverEmoji
                                )
                                title = ""
                                artist = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Upload, contentDescription = "Upload", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Song & Timestamped Lyrics", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // List of Existing Songs
        item {
            Text(
                text = "Catalog Songs (${songs.size})",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(songs) { song ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1B112E))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(song.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${song.artist} • ${song.genre} • ${song.durationSec}s", color = Color(0xFFAFA2D1), fontSize = 11.sp)
                    }

                    IconButton(onClick = { onDeleteSong(song.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252))
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: User Management (Level 1-100, Exclusive ID for Lv 50+, VIP 1-5, Noble)
 */
@Composable
fun AdminUserManagementTab(
    users: List<UserEntity>,
    onModifyUser: (
        userId: Long,
        newLevel: Int,
        newVipTier: Int,
        newNobleTitle: String,
        newExclusiveId: String?,
        isAdmin: Boolean
    ) -> Unit
) {
    var selectedUserId by remember { mutableStateOf(users.firstOrNull()?.id ?: 1L) }
    val user = users.find { it.id == selectedUserId } ?: users.firstOrNull()

    var levelSlider by remember(user) { mutableFloatStateOf((user?.level ?: 1).toFloat()) }
    var vipTier by remember(user) { mutableIntStateOf(user?.vipTier ?: 0) }
    var nobleTitle by remember(user) { mutableStateOf(user?.nobleTitle ?: "None") }
    var exclusiveId by remember(user) { mutableStateOf(user?.exclusiveId ?: "") }
    var isAdmin by remember(user) { mutableStateOf(user?.isAdmin ?: false) }

    val nobleOptions = listOf("None", "Knight", "Baron", "Viscount", "Count", "Marquis", "Duke", "King", "Emperor")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Select User to Manage",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                users.forEach { u ->
                    val isSelected = u.id == selectedUserId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFFF007F) else Color(0xFF22173B))
                            .clickable { selectedUserId = u.id }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${u.username} (Lv.${u.level})",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        if (user != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF19102B))
                        .border(1.dp, Color(0xFF3B2561), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Edit Singer: ${user.username}",
                            color = Color(0xFFFFD700),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        // Level Slider (Strictly 1 to 100)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Level (Only 1 to 100 Max):", color = Color.White, fontSize = 12.sp)
                            Text("Lv. ${levelSlider.toInt()}", color = Color(0xFFFFE600), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Slider(
                            value = levelSlider,
                            onValueChange = { levelSlider = it },
                            valueRange = 1f..100f,
                            steps = 98,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFF007F),
                                activeTrackColor = Color(0xFFFF007F)
                            )
                        )

                        // Quick level buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { levelSlider = 25f },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1C52))
                            ) {
                                Text("Lv. 25", fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    levelSlider = 50f
                                    if (exclusiveId.isBlank()) exclusiveId = "★VIP-${(1000..9999).random()}★"
                                },
                                modifier = Modifier.weight(1.3f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A3C08))
                            ) {
                                Text("Lv. 50 (Unlock ID)", fontSize = 10.sp, color = Color(0xFFFFD700))
                            }
                            Button(
                                onClick = { levelSlider = 100f },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
                            ) {
                                Text("Lv. 100 Max", fontSize = 10.sp)
                            }
                        }

                        // VIP Tier Selection (0 to 5)
                        Text("VIP Tier (0 - 5): VIP $vipTier", color = Color.White, fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            (0..5).forEach { tier ->
                                val isSelected = vipTier == tier
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Color(0xFFFF007F) else Color(0xFF261942))
                                        .clickable { vipTier = tier }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (tier == 0) "None" else "V$tier",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Noble Title Selection
                        Text("Noble Title: $nobleTitle", color = Color.White, fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("None", "Knight", "Baron", "Duke", "King", "Emperor").forEach { title ->
                                val isSelected = nobleTitle.equals(title, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Color(0xFFFFD700) else Color(0xFF261942))
                                        .clickable { nobleTitle = title }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = title.take(4),
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Exclusive ID (Granted from Level 50)
                        Column {
                            Text(
                                text = "Exclusive ID (Level 50+ Privilege)",
                                color = if (levelSlider >= 50) Color(0xFFFFD700) else Color(0xFF867BA6),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (levelSlider < 50) {
                                Text(
                                    text = "⚠️ User level is below 50. Increase level to >= 50 to activate exclusive ID.",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 10.sp
                                )
                            }
                            OutlinedTextField(
                                value = exclusiveId,
                                onValueChange = { exclusiveId = it },
                                placeholder = { Text("e.g. ★STAR-KING★") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = outlinedColors(),
                                singleLine = true
                            )
                        }

                        // Admin Privilege Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Admin Privileges", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Can access hidden admin panel", color = Color(0xFF867BA6), fontSize = 10.sp)
                            }
                            Switch(
                                checked = isAdmin,
                                onCheckedChange = { isAdmin = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF007F))
                            )
                        }

                        // Save Button
                        Button(
                            onClick = {
                                onModifyUser(
                                    user.id,
                                    levelSlider.toInt(),
                                    vipTier,
                                    nobleTitle,
                                    if (exclusiveId.isNotBlank()) exclusiveId else null,
                                    isAdmin
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                        ) {
                            Text("Apply Changes to User", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: Room EXP & Room Level Adjuster
 */
@Composable
fun AdminRoomExpTab(
    rooms: List<PartyRoomEntity>,
    onAdjustRoomExp: (roomId: Long, newExp: Long) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Live Rooms EXP Controller",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Each 600 EXP levels up the party room. Level up rooms to unlock themes!",
                color = Color(0xFFAFA2D1),
                fontSize = 11.sp
            )
        }

        items(rooms) { room ->
            var expText by remember { mutableStateOf(room.roomExp.toString()) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1B112F))
                    .border(1.dp, Color(0xFF3A245C), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(room.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Room Lv.${room.roomLevel}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = expText,
                            onValueChange = { expText = it },
                            label = { Text("Room EXP") },
                            modifier = Modifier.weight(1f),
                            colors = outlinedColors(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val expVal = expText.toLongOrNull() ?: 0L
                                onAdjustRoomExp(room.id, expVal)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
                        ) {
                            Text("Set EXP", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    // Quick buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onAdjustRoomExp(room.id, room.roomExp + 1000) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C1E4A))
                        ) {
                            Text("+1000 EXP", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onAdjustRoomExp(room.id, room.roomExp + 5000) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C1E4A))
                        ) {
                            Text("+5000 EXP", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF130922),
    unfocusedContainerColor = Color(0xFF130922),
    focusedBorderColor = Color(0xFFFF007F),
    unfocusedBorderColor = Color(0xFF352054),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
