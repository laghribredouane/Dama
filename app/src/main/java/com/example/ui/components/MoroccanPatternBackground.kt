package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.MoroccanGold

@Composable
fun MoroccanPatternBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.05f,
    starSpacing: Float = 90f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val stroke = Stroke(width = 1.2f)
        val color = MoroccanGold.copy(alpha = alpha)

        var x = 0f
        while (x < width + starSpacing) {
            var y = 0f
            while (y < height + starSpacing) {
                // Draw decorative 8-pointed star lattice
                val center = Offset(x, y)
                val starR = starSpacing * 0.32f

                val path = Path()
                for (i in 0 until 16) {
                    val r = if (i % 2 == 0) starR else starR * 0.5f
                    val angle = (i * Math.PI / 8).toFloat()
                    val px = center.x + r * kotlin.math.cos(angle)
                    val py = center.y + r * kotlin.math.sin(angle)
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                drawPath(path = path, color = color, style = stroke)

                // Diagonal connecting diamond lines
                drawLine(
                    color = color,
                    start = Offset(x - starSpacing / 2f, y),
                    end = Offset(x, y - starSpacing / 2f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = color,
                    start = Offset(x, y - starSpacing / 2f),
                    end = Offset(x + starSpacing / 2f, y),
                    strokeWidth = 1f
                )

                y += starSpacing
            }
            x += starSpacing
        }
    }
}
