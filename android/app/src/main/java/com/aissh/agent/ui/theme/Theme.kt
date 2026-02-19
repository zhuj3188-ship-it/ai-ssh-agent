package com.aissh.agent.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue, onPrimary = DarkText, primaryContainer = AccentBlueDark,
    secondary = AccentGreen, onSecondary = DarkBg,
    background = DarkBg, onBackground = DarkText,
    surface = DarkSurface, onSurface = DarkText,
    surfaceVariant = DarkCard, onSurfaceVariant = DarkTextSec,
    error = AccentRed, outline = DarkBorder,
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue, onPrimary = LightBg, primaryContainer = AccentBlueDark,
    secondary = AccentGreen, onSecondary = LightBg,
    background = LightBg, onBackground = LightText,
    surface = LightSurface, onSurface = LightText,
    surfaceVariant = LightCard, onSurfaceVariant = LightTextSec,
    error = AccentRed, outline = LightBorder,
)

@Composable
fun AiSshTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
