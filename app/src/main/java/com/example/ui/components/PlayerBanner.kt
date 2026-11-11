package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Piece
import com.example.model.PieceType
import com.example.model.PlayerColor
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.MoroccanEmerald
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanRed

@Composable
fun PlayerBanner(
    playerColor: PlayerColor,
    playerName: String,
    isActive: Boolean,
    pieceCount: Int,
    capturedCount: Int,
    queensCount: Int,
    isAi: Boolean = false,
    isAiThinking: Boolean = false,
    timerText: String = "",
    modifier: Modifier = Modifier
) {
    val isLight = playerColor == PlayerColor.LIGHT
    val activeBorderColor = if (isActive) MoroccanGold else Color.Transparent

    val backgroundGradient = if (isActive) {
        Brush.horizontalGradient(
            listOf(
                CedarWoodDark.copy(alpha = 0.95f),
                if (isLight) MoroccanEmerald.copy(alpha = 0.35f) else MoroccanRed.copy(alpha = 0.35f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                CedarWoodDark.copy(alpha = 0.65f),
                CedarWoodDark.copy(alpha = 0.65f)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundGradient)
            .border(if (isActive) 2.dp else 1.dp, activeBorderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player Avatar & Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Miniature Piece representation
                PieceView(
                    piece = Piece(id = 0, player = playerColor, type = PieceType.MAN),
                    isSelected = false,
                    size = 32.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = playerName,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp,
                            color = if (isActive) MoroccanGoldLight else Color.White
                        )

                        if (isAi) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "IA",
                                tint = MoroccanGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "$pieceCount pièces",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        if (queensCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Dames",
                                tint = MoroccanGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "$queensCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoroccanGoldLight
                            )
                        }
                    }
                }
            }

            // Stats / Timer / Thinking Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(
                    visible = isAiThinking,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MoroccanGold.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MoroccanGoldLight,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoroccanGoldLight
                        )
                    }
                }

                if (!isAiThinking && capturedCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MoroccanRed.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+$capturedCount",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (timerText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timerText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MoroccanGoldLight
                    )
                }
            }
        }
    }
}
