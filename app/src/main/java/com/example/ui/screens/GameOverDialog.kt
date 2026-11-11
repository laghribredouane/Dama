package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.i18n.DamaStrings
import com.example.model.AppLanguage
import com.example.model.GameStatus
import com.example.model.PlayerColor
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.CedarWoodMedium
import com.example.ui.theme.MoroccanEmerald
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanRed

@Composable
fun GameOverDialog(
    status: GameStatus,
    totalMoves: Int,
    durationSeconds: Int,
    language: AppLanguage,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    if (status is GameStatus.Active) return

    val isWin = status is GameStatus.Victory && status.winner == PlayerColor.LIGHT
    val isLoss = status is GameStatus.Victory && status.winner == PlayerColor.DARK
    val isDraw = status is GameStatus.Draw

    val title = when {
        isWin -> if (language == AppLanguage.ARABIC) "مبروك الفوز !" else "Victoire éclatante !"
        isLoss -> if (language == AppLanguage.ARABIC) "هزيمة !" else "Partie terminée !"
        else -> if (language == AppLanguage.ARABIC) "تعادل !" else "Match nul !"
    }

    val subtitle = when {
        isWin -> if (language == AppLanguage.ARABIC) "أداء تكتيكي مغربي ممتاز" else "Excellente maîtrise de la Dama marocaine"
        isLoss -> if (language == AppLanguage.ARABIC) "حظ أفضل في الجولة القادمة" else "Bonne tentative, réessayez une tactique différente"
        else -> if (language == AppLanguage.ARABIC) "مباراة متكافئة وقوية" else "Partie très équilibrée"
    }

    val iconColor = if (isWin) MoroccanGold else if (isLoss) MoroccanRed else MoroccanEmerald

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CedarWoodDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MoroccanGold, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(CedarWoodDark, CedarWoodMedium, CedarWoodDark)
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.2f))
                        .border(2.dp, iconColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophée",
                        tint = iconColor,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoroccanGoldLight,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                // Stats summary cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "النقلات" else "Coups",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$totalMoves",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoroccanGoldLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "المدة" else "Durée",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        val m = durationSeconds / 60
                        val s = durationSeconds % 60
                        Text(
                            text = "%02d:%02d".format(m, s),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoroccanGoldLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Replay and Home
                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoroccanEmerald,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("play_again_button")
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DamaStrings.playAgain(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onHome,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MoroccanGoldLight),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(MoroccanGold, MoroccanGoldLight))),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("game_over_home_button")
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.ARABIC) "القائمة الرئيسية" else "Menu Principal",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
