package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CedarWoodDark
import com.example.ui.theme.CedarWoodMedium
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGoldLight

@Composable
fun GameControls(
    canUndo: Boolean,
    isPaused: Boolean,
    soundEnabled: Boolean,
    onUndoClicked: () -> Unit,
    onHintClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onRestartClicked: () -> Unit,
    onSoundToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MoroccanGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(CedarWoodDark, CedarWoodMedium, CedarWoodDark)
                    )
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo
            IconButton(
                onClick = onUndoClicked,
                enabled = canUndo,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("undo_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Annuler le coup",
                    tint = if (canUndo) MoroccanGoldLight else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Hint
            IconButton(
                onClick = onHintClicked,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("hint_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Conseil de coup",
                    tint = MoroccanGoldLight,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Pause / Resume
            IconButton(
                onClick = onPauseClicked,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("pause_button")
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Reprendre" else "Pause",
                    tint = MoroccanGoldLight,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Restart / New Game
            IconButton(
                onClick = onRestartClicked,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Recommencer la partie",
                    tint = MoroccanGoldLight,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Sound Toggle
            IconButton(
                onClick = onSoundToggle,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("sound_toggle_button")
            ) {
                Icon(
                    imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                    contentDescription = "Son",
                    tint = if (soundEnabled) MoroccanGoldLight else Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
