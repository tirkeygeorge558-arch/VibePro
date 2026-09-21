package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.SingingSessionResult

/**
 * Secret Admin Authentication / 1st User Claim Dialog
 */
@Composable
fun AdminSecretDialog(
    isFirstUser: Boolean,
    onClaimWithCode: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var secretCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1D1433),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👑 Hidden Admin Panel", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isFirstUser) {
                        "You are the first registered user on this StarMaker app! You are entitled to instant Super Admin privileges."
                    } else {
                        "Enter the secret admin passcode or developer pass (*#9999# or starmaker777) to unlock the hidden admin panel."
                    },
                    color = Color(0xFFC4B8E3),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = secretCode,
                    onValueChange = { secretCode = it },
                    placeholder = { Text("Enter secret passcode...", color = Color(0xFF7B6E9C)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF120B22),
                        unfocusedContainerColor = Color(0xFF120B22),
                        focusedBorderColor = Color(0xFFFF007F),
                        unfocusedBorderColor = Color(0xFF3B275F),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onClaimWithCode(if (isFirstUser && secretCode.isBlank()) "starmaker777" else secretCode)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
            ) {
                Text(
                    text = if (isFirstUser && secretCode.isBlank()) "Claim 1st User Admin" else "Authenticate",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFAFA2D1))
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Custom Exclusive ID setup dialog (Available for Level 50+)
 */
@Composable
fun ExclusiveIdClaimDialog(
    currentExclusiveId: String?,
    userLevel: Int,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var vanityId by remember { mutableStateOf(currentExclusiveId?.replace("★", "") ?: "STAR-ACE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1333),
        title = {
            Text("★ Set Exclusive ID", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Congratulations on reaching Level $userLevel! You have unlocked the Exclusive ID privilege. Your custom ID will be framed in gold across all live rooms.",
                    color = Color(0xFFDCD2F5),
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = vanityId,
                    onValueChange = { vanityId = it.uppercase().take(12) },
                    placeholder = { Text("e.g. VIP-HERO-7") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF140B24),
                        unfocusedContainerColor = Color(0xFF140B24),
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF3F2A66),
                        focusedTextColor = Color(0xFFFFE600),
                        unfocusedTextColor = Color(0xFFFFE600)
                    ),
                    singleLine = true
                )

                Text(
                    text = "Preview: ★${vanityId.ifBlank { "CUSTOM-ID" }}★",
                    color = Color(0xFFFFD700),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(vanityId) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Save Exclusive ID", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

/**
 * Score Celebration Dialog after Karaoke Performance
 */
@Composable
fun ScoreCelebrationDialog(
    result: SingingSessionResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF190C2E),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎉 PERFORMANCE COMPLETED!", color = Color(0xFFFFD700), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(result.songTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Giant Grade Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF007F), Color(0xFFFFD700))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.grade,
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = "${result.score} Points",
                    color = Color(0xFFFFE600),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF261642))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+${result.expEarned}", color = Color(0xFF00F5D4), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("User EXP", color = Color(0xFFAFA2D1), fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+${result.coinsEarned}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Coins Reward", color = Color(0xFFAFA2D1), fontSize = 10.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
            ) {
                Text("Collect Rewards & Continue", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
