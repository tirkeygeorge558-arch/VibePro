package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.NobleRank
import com.example.data.model.UserEntity
import com.example.data.model.VipLevel
import com.example.ui.components.ExclusiveIdTag
import com.example.ui.components.LevelBadge
import com.example.ui.components.NobleBadge
import com.example.ui.components.VipBadge

@Composable
fun RankingVipScreen(
    leaderboard: List<UserEntity>,
    exclusiveUsers: List<UserEntity>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🏆 Level Leaderboard", "★ Exclusive IDs (Lv.50+)", "💎 VIP Club", "👑 Noble Palace")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F081D))
            .padding(16.dp)
    ) {
        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF19112E),
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFFFF007F)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) Color(0xFFFFE600) else Color(0xFF9E92BC),
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> LeaderboardTab(leaderboard = leaderboard)
            1 -> ExclusiveIdsTab(exclusiveUsers = exclusiveUsers)
            2 -> VipClubTab()
            3 -> NoblePalaceTab()
        }
    }
}

@Composable
fun LeaderboardTab(leaderboard: List<UserEntity>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(leaderboard) { index, user ->
            val rank = index + 1
            val rankColor = when (rank) {
                1 -> Color(0xFFFFD700)
                2 -> Color(0xFFE0E0E0)
                3 -> Color(0xFFCD7F32)
                else -> Color(0xFF8D7FA8)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1B122F))
                    .border(1.dp, Color(0xFF332356), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#$rank",
                            color = rankColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.width(36.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(user.avatarColor.toULong().toLong())),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.username.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.username,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                LevelBadge(level = user.level)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (user.level >= 50 && !user.exclusiveId.isNullOrBlank()) {
                                    ExclusiveIdTag(exclusiveId = user.exclusiveId, level = user.level)
                                } else {
                                    Text(
                                        text = "ID: ${user.standardId}",
                                        color = Color(0xFF867BA6),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${user.currentExp} EXP",
                            color = Color(0xFFFFD700),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${user.songsSung} songs",
                            color = Color(0xFFAFA2D1),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExclusiveIdsTab(exclusiveUsers: List<UserEntity>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF5A3C08), Color(0xFF28183B))))
                .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★ LEVEL 50+ EXCLUSIVE PRIVILEGE", color = Color(0xFFFFD700), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reach Level 50 (up to Level 100 max) to permanently unlock custom Golden Exclusive IDs that sparkle across all party rooms!",
                    color = Color(0xFFF3E7C4),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (exclusiveUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No singers reached Level 50 yet. Sing to reach Level 50 and claim your exclusive ID!",
                    color = Color(0xFF8D7FA8),
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(exclusiveUsers) { user ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B122F))
                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(user.avatarColor.toULong().toLong())),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = user.username.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    ExclusiveIdTag(exclusiveId = user.exclusiveId, level = user.level)
                                }
                            }
                            LevelBadge(level = user.level)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VipClubTab() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(VipLevel.entries.filter { it.tier > 0 }) { vip ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1D1432))
                    .border(1.dp, Color(vip.color).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = vip.badgeIcon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = vip.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "+${vip.expBonusPercent}% Singing & Room EXP Multiplier", color = Color(0xFF00F5D4), fontSize = 11.sp)
                            Text(text = "Exclusive VIP chat badge & room entrance glow", color = Color(0xFFAFA2D1), fontSize = 10.sp)
                        }
                    }
                    VipBadge(vipTier = vip.tier)
                }
            }
        }
    }
}

@Composable
fun NoblePalaceTab() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(NobleRank.entries.filter { it != NobleRank.NONE }) { noble ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1C1330))
                    .border(1.dp, Color(noble.color).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = noble.crown, fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = noble.title, color = Color(noble.color), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text(text = "Noble Mount: ${noble.mountName}", color = Color(0xFFFFD700), fontSize = 11.sp)
                            Text(text = "Room-wide fanfare announcement upon arrival", color = Color(0xFFAFA2D1), fontSize = 10.sp)
                        }
                    }
                    NobleBadge(nobleTitle = noble.title)
                }
            }
        }
    }
}
