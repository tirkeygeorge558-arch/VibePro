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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.RecordingEntity
import com.example.data.model.UserEntity
import com.example.ui.components.ExclusiveIdTag
import com.example.ui.components.LevelBadge
import com.example.ui.components.NobleBadge
import com.example.ui.components.VipBadge

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    recordings: List<RecordingEntity>,
    onSwitchUser: (UserEntity) -> Unit,
    onOpenExclusiveIdDialog: () -> Unit,
    onTriggerAdminPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secretTapCount by remember { mutableIntStateOf(0) }

    if (currentUser == null) {
        Box(
            modifier = modifier.fillMaxSize().background(Color(0xFF0F081D)),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading profile...", color = Color.White)
        }
        return
    }

    val expForCurrentLvl = (currentUser.level.toLong() * currentUser.level.toLong() * 100L)
    val expForNextLvl = ((currentUser.level + 1).coerceAtMost(100).toLong() * (currentUser.level + 1).coerceAtMost(100).toLong() * 100L)
    val levelProgress = if (currentUser.level >= 100) 1f else {
        val range = (expForNextLvl - expForCurrentLvl).coerceAtLeast(1L)
        val currentWithinLvl = (currentUser.currentExp - expForCurrentLvl).coerceAtLeast(0L)
        (currentWithinLvl.toFloat() / range.toFloat()).coerceIn(0f, 1f)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F081D))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF2E1752),
                                Color(0xFF1B0E33)
                            )
                        )
                    )
                    .border(1.dp, Color(0xFF4C2A85), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFFF007F), Color(0xFF7928CA))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.username.take(1),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = currentUser.username,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LevelBadge(level = currentUser.level)
                                    if (currentUser.vipTier > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VipBadge(vipTier = currentUser.vipTier)
                                    }
                                    if (currentUser.nobleTitle != "None") {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        NobleBadge(nobleTitle = currentUser.nobleTitle)
                                    }
                                }
                            }
                        }

                        if (currentUser.isAdmin) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFF007F).copy(alpha = 0.2f))
                                    .border(1.dp, Color(0xFFFF007F), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("ADMIN", color = Color(0xFFFF007F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser.bio,
                        color = Color(0xFFC0B3E3),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Coins & Diamonds Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF130A24))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪙 ${currentUser.coins}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Coins", color = Color(0xFF867BA6), fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💎 ${currentUser.diamonds}", color = Color(0xFF00F5D4), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Diamonds", color = Color(0xFF867BA6), fontSize = 10.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎤 ${currentUser.songsSung}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Songs Sung", color = Color(0xFF867BA6), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Level 1-100 Progression
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF19112E))
                    .border(1.dp, Color(0xFF332057), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Level Progression (Max Lv. 100)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lv.${currentUser.level} / 100",
                            color = Color(0xFFFFD700),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { levelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFFF007F),
                        trackColor = Color(0x33FF007F)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Current EXP: ${currentUser.currentExp} pts • Next Lv at $expForNextLvl pts",
                        color = Color(0xFFAFA2D1),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Exclusive ID Section (Unlocked at Level 50+)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (currentUser.level >= 50) Brush.horizontalGradient(
                            listOf(Color(0xFF4A3405), Color(0xFF26143C))
                        ) else Brush.horizontalGradient(
                            listOf(Color(0xFF1E1633), Color(0xFF150D26))
                        )
                    )
                    .border(
                        1.dp,
                        if (currentUser.level >= 50) Color(0xFFFFD700) else Color(0xFF38275A),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "★ Exclusive ID (Lv. 50+ Privilege)",
                                color = if (currentUser.level >= 50) Color(0xFFFFD700) else Color(0xFF867BA6),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (currentUser.level >= 50) {
                            Button(
                                onClick = onOpenExclusiveIdDialog,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Edit ID", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (currentUser.level >= 50) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ExclusiveIdTag(exclusiveId = currentUser.exclusiveId, level = currentUser.level)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exclusive ID unlocked! Displays with golden shine in all rooms.",
                                color = Color(0xFFEADBBE),
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF867BA6), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reach Level 50 to unlock your custom Golden Exclusive ID! (${currentUser.level}/50)",
                                color = Color(0xFF867BA6),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Switch Active User (Testing multiple accounts & 1st user admin)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF19112E))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Switch Account (Test 1st User Admin vs Standard)",
                        color = Color(0xFFB5A7D8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allUsers.forEach { user ->
                            val isSelected = user.id == currentUser.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFFFF007F) else Color(0xFF281A45))
                                    .clickable { onSwitchUser(user) }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = user.username,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = if (user.isAdmin) "Admin (1st User)" else "Lv.${user.level}",
                                        color = if (isSelected) Color.White else Color(0xFFFFD700),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Admin Access Button (if admin) or Secret Trigger
        item {
            if (currentUser.isAdmin) {
                Button(
                    onClick = onTriggerAdminPanel,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF007F)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Hidden Admin Panel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Saved Karaoke Recordings
        item {
            Text(
                text = "🎵 My Recordings (${recordings.size})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (recordings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF160E28)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recordings yet. Pick a song and sing your heart out!", color = Color(0xFF867BA6), fontSize = 12.sp)
                }
            }
        } else {
            items(recordings) { rec ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B122F))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(rec.songTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${rec.songArtist} • Score: ${rec.score} pts", color = Color(0xFFAFA2D1), fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF007F))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(rec.grade, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Secret Admin Trigger at bottom: 5 taps on version label
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        secretTapCount++
                        if (secretTapCount >= 5) {
                            secretTapCount = 0
                            onTriggerAdminPanel()
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "StarMaker Sing Live • v1.0.0",
                        color = Color(0xFF53496C),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "(Hidden Admin: Tap 5 times or enter *#9999# in chat)",
                        color = Color(0xFF382F4E),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
