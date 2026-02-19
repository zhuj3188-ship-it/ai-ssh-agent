package com.aissh.agent.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary, onPrimary = Ivory, primaryContainer = CyanDim,
    secondary = EmeraldPrimary, onSecondary = Obsidian,
    background = Obsidian, onBackground = Ivory,
    surface = SurfaceDark, onSurface = Ivory,
    surfaceVariant = CardDark, onSurfaceVariant = SoftWhite,
    error = DangerRed, outline = MutedGray,
)

private val LightColorScheme = lightColorScheme(
    primary = CyanPrimary, onPrimary = LightBg, primaryContainer = CyanDim,
    secondary = EmeraldPrimary, onSecondary = LightBg,
    background = LightBg, onBackground = LightText,
    surface = LightSurface, onSurface = LightText,
    surfaceVariant = LightCard, onSurfaceVariant = LightTextSec,
    error = DangerRed, outline = LightTextSec,
)

@Composable
fun AiSshTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
