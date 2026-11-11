package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Board
import com.example.model.BoardTheme
import com.example.model.Move
import com.example.model.Position
import com.example.ui.theme.BoardClassicDark
import com.example.ui.theme.BoardClassicLight
import com.example.ui.theme.BoardMarbleDark
import com.example.ui.theme.BoardMarbleLight
import com.example.ui.theme.BoardWoodDark
import com.example.ui.theme.BoardWoodLight
import com.example.ui.theme.BoardZelligeDark
import com.example.ui.theme.BoardZelligeLight
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.CedarWoodMedium
import com.example.ui.theme.MoroccanBrass
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanRed

@Composable
fun DamaBoardView(
    board: Board,
    selectedPosition: Position?,
    validMoves: List<Move>,
    theme: BoardTheme,
    onSquareClicked: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    val (lightSquareColor, darkSquareColor) = when (theme) {
        BoardTheme.WOOD -> Pair(BoardWoodLight, BoardWoodDark)
        BoardTheme.ZELLIGE -> Pair(BoardZelligeLight, BoardZelligeDark)
        BoardTheme.MARBLE -> Pair(BoardMarbleLight, BoardMarbleDark)
        BoardTheme.DESERT -> Pair(BoardClassicLight, BoardClassicDark)
    }

    val validDestinations = remember(validMoves) {
        validMoves.associateBy { it.to }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "board_target_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(CedarWoodDark, CedarWoodMedium, CedarWoodDark)
                )
            )
            .border(4.dp, MoroccanBrass, RoundedCornerShape(16.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = board.size
        val squareSize = maxWidth / boardSize

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, MoroccanGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        ) {
            for (row in 0 until boardSize) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    for (col in 0 until boardSize) {
                        val pos = Position(row, col)
                        val isPlayable = pos.isPlayableSquare()
                        val squareColor = if (isPlayable) darkSquareColor else lightSquareColor
                        val piece = board[pos]
                        val isSelected = selectedPosition == pos
                        val moveForSquare = validDestinations[pos]
                        val isTarget = moveForSquare != null
                        val isCaptureTarget = moveForSquare?.isCapture == true

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(squareColor)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onSquareClicked(pos)
                                }
                                .testTag("square_${row}_${col}"),
                            contentAlignment = Alignment.Center
                        ) {
                            // Coordinate labels on edges (a-h, 1-8)
                            if (col == 0) {
                                Text(
                                    text = "${boardSize - row}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlayable) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.35f),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 2.dp, top = 1.dp)
                                )
                            }
                            if (row == boardSize - 1) {
                                val colLetter = ('A' + col).toString()
                                Text(
                                    text = colLetter,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlayable) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.35f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 2.dp, bottom = 1.dp)
                                )
                            }

                            // Selection highlight
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MoroccanGold.copy(alpha = 0.3f))
                                        .border(2.dp, MoroccanGoldLight)
                                )
                            }

                            // Render Piece
                            if (piece != null) {
                                PieceView(
                                    piece = piece,
                                    isSelected = isSelected,
                                    size = squareSize * 0.82f
                                )
                            }

                            // Target move indicator
                            if (isTarget) {
                                Canvas(modifier = Modifier.size(squareSize * 0.55f)) {
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val radius = (size.minDimension / 2f) * pulseScale

                                    if (isCaptureTarget) {
                                        // Red pulsing ring for capture
                                        drawCircle(
                                            color = MoroccanRed,
                                            radius = radius,
                                            center = center,
                                            style = Stroke(width = 3.5f)
                                        )
                                        drawCircle(
                                            color = MoroccanRed.copy(alpha = 0.45f),
                                            radius = radius * 0.5f,
                                            center = center
                                        )
                                    } else {
                                        // Golden indicator dot for normal move
                                        drawCircle(
                                            color = MoroccanGoldLight,
                                            radius = radius * 0.45f,
                                            center = center
                                        )
                                        drawCircle(
                                            color = MoroccanGold.copy(alpha = 0.6f),
                                            radius = radius,
                                            center = center,
                                            style = Stroke(width = 2f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
