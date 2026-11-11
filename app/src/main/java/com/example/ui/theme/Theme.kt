package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MoroccanGold,
    onPrimary = CedarWoodDark,
    primaryContainer = MoroccanEmeraldDark,
    onPrimaryContainer = MoroccanGoldLight,
    secondary = MoroccanEmeraldAccent,
    onSecondary = Color.White,
    secondaryContainer = MoroccanEmerald,
    onSecondaryContainer = MoroccanSand,
    tertiary = MoroccanRedLight,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = MoroccanSand,
    surface = DarkSurface,
    onSurface = MoroccanSand,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MoroccanSandDark,
    outline = MoroccanGold.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = MoroccanEmerald,
    onPrimary = Color.White,
    primaryContainer = MoroccanEmeraldLight,
    onPrimaryContainer = MoroccanIvory,
    secondary = MoroccanGold,
    onSecondary = CedarWoodDark,
    secondaryContainer = MoroccanGoldLight,
    onSecondaryContainer = CedarWoodMedium,
    tertiary = MoroccanRed,
    onTertiary = Color.White,
    background = MoroccanSand,
    onBackground = CedarWoodDark,
    surface = MoroccanIvory,
    onSurface = CedarWoodDark,
    surfaceVariant = MoroccanSandDark,
    onSurfaceVariant = CedarWoodMedium,
    outline = MoroccanEmerald.copy(alpha = 0.4f)
)

@Composable
fun DamaMarocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
