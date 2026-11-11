package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.Piece
import com.example.model.PieceType
import com.example.model.PlayerColor
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.CedarWoodHighlight
import com.example.ui.theme.CedarWoodMedium
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight
import com.example.ui.theme.MoroccanIvory
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.MoroccanRedDark
import com.example.ui.theme.MoroccanRedLight
import com.example.ui.theme.MoroccanSand
import com.example.ui.theme.MoroccanSandDark

@Composable
fun PieceView(
    piece: Piece,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val isLight = piece.player == PlayerColor.LIGHT
    val isKing = piece.type == PieceType.KING

    val infiniteTransition = rememberInfiniteTransition(label = "piece_anim")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(3.dp)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2f) - 2f

            // 1. Selection aura / glow
            if (isSelected) {
                drawCircle(
                    color = MoroccanGold.copy(alpha = glowAlpha),
                    radius = radius + 4f,
                    center = center,
                    style = Stroke(width = 4f)
                )
            }

            // 2. Drop shadow for 3D depth
            drawCircle(
                color = Color.Black.copy(alpha = 0.45f),
                radius = radius,
                center = center + Offset(2f, 3f)
            )

            // 3. Piece body gradient (Cedar wood or Ivory/Sand)
            val bodyBrush = if (isLight) {
                Brush.radialGradient(
                    colors = listOf(MoroccanIvory, MoroccanSand, MoroccanSandDark),
                    center = center - Offset(radius * 0.25f, radius * 0.25f),
                    radius = radius * 1.2f
                )
            } else {
                Brush.radialGradient(
                    colors = listOf(MoroccanRedLight, MoroccanRed, MoroccanRedDark, CedarWoodDark),
                    center = center - Offset(radius * 0.25f, radius * 0.25f),
                    radius = radius * 1.2f
                )
            }
            drawCircle(brush = bodyBrush, radius = radius, center = center)

            // 4. Outer decorative beveled rim
            val rimColor = if (isLight) MoroccanSandDark else CedarWoodDark
            drawCircle(
                color = rimColor,
                radius = radius,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // 5. Inner concentric groove (traditional wooden draughts carving)
            val innerRingColor = if (isLight) MoroccanSandDark.copy(alpha = 0.7f) else CedarWoodHighlight.copy(alpha = 0.6f)
            drawCircle(
                color = innerRingColor,
                radius = radius * 0.65f,
                center = center,
                style = Stroke(width = 1.8f)
            )

            // 6. Center Moroccan Octagram (8-pointed star / خاتم سليمان)
            val starRadius = radius * 0.35f
            val starColor = if (isKing) {
                MoroccanGold
            } else if (isLight) {
                MoroccanSandDark.copy(alpha = 0.5f)
            } else {
                MoroccanGoldLight.copy(alpha = 0.4f)
            }

            // Draw Moroccan 8-pointed star
            val path = Path()
            val points = 16
            for (i in 0 until points) {
                val r = if (i % 2 == 0) starRadius else starRadius * 0.55f
                val angle = (i * Math.PI / 8).toFloat() - (Math.PI / 2).toFloat()
                val x = center.x + r * kotlin.math.cos(angle)
                val y = center.y + r * kotlin.math.sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path = path, color = starColor)

            // 7. Gold border ring for King
            if (isKing) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(MoroccanGoldLight, MoroccanGold, MoroccanGoldLight)
                    ),
                    radius = radius * 0.88f,
                    center = center,
                    style = Stroke(width = 3.5f)
                )
            }
        }

        // Royal crown badge on King piece
        if (isKing) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Dame",
                tint = MoroccanGoldLight,
                modifier = Modifier.size(size * 0.42f)
            )
        }
    }
}
