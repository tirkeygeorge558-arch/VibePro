package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NobleRank
import com.example.data.model.VipLevel

/**
 * Level badge (1 to 100 max) with color brackets:
 * - 1-19: Bronze / Slate
 * - 20-49: Purple / Magenta
 * - 50-79: Golden Exclusive Tier
 * - 80-99: Diamond Cyan
 * - 100: Cosmic Legend Star
 */
@Composable
fun LevelBadge(
    level: Int,
    modifier: Modifier = Modifier
) {
    val clampedLevel = level.coerceIn(1, 100)
    val (gradientColors, iconEmoji) = when {
        clampedLevel >= 100 -> Pair(
            listOf(Color(0xFFFF007F), Color(0xFFFFD700), Color(0xFF9D00FF)),
            "👑"
        )
        clampedLevel >= 80 -> Pair(
            listOf(Color(0xFF00F5D4), Color(0xFF00B4D8)),
            "💎"
        )
        clampedLevel >= 50 -> Pair(
            listOf(Color(0xFFFFD166), Color(0xFFFF9E00), Color(0xFFFF5400)),
            "★"
        )
        clampedLevel >= 20 -> Pair(
            listOf(Color(0xFFB5179E), Color(0xFF7209B7)),
            "🎵"
        )
        else -> Pair(
            listOf(Color(0xFF4A5568), Color(0xFF2D3748)),
            "🎤"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .padding(horizontal = 7.dp, vertical = 2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = iconEmoji,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "Lv.$clampedLevel",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Level 50+ Exclusive ID Badge or Locked State
 */
@Composable
fun ExclusiveIdTag(
    exclusiveId: String?,
    level: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "exclusive_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    if (level >= 50 && !exclusiveId.isNullOrBlank()) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFD700).copy(alpha = 0.25f),
                            Color(0xFFFF8C00).copy(alpha = 0.35f)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFD700).copy(alpha = glowAlpha),
                            Color(0xFFFF007F).copy(alpha = glowAlpha)
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Exclusive Star",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = exclusiveId,
                    color = Color(0xFFFFE600),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    } else {
        // Locked indicator for users below level 50
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF232035))
                .border(1.dp, Color(0xFF3F3B56), RoundedCornerShape(8.dp))
                .padding(horizontal = 7.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Exclusive ID (Lv.50)",
                    color = Color(0xFFB0B0B0),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * VIP Tier Badge (VIP 1 - 5)
 */
@Composable
fun VipBadge(
    vipTier: Int,
    modifier: Modifier = Modifier
) {
    if (vipTier <= 0) return
    val vip = VipLevel.fromTier(vipTier)
    val brush = when (vipTier) {
        5 -> Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFFD700)))
        4 -> Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF2979FF)))
        3 -> Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF8F00)))
        2 -> Brush.horizontalGradient(listOf(Color(0xFFE0E0E0), Color(0xFF9E9E9E)))
        else -> Brush.horizontalGradient(listOf(Color(0xFFCD7F32), Color(0xFF8D5524)))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(brush)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "VIP $vipTier",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Noble Title Badge (Knight, Duke, King, Emperor)
 */
@Composable
fun NobleBadge(
    nobleTitle: String,
    modifier: Modifier = Modifier
) {
    val noble = NobleRank.fromTitle(nobleTitle)
    if (noble == NobleRank.NONE) return

    val nobleColor = Color(noble.color)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(nobleColor.copy(alpha = 0.22f))
            .border(1.dp, nobleColor.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = noble.crown,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = noble.title,
                color = nobleColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
