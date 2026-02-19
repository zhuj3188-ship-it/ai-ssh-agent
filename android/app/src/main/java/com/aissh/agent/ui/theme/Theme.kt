package com.aissh.agent.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary, onPrimary = Obsidian, primaryContainer = CyanDim,
    secondary = EmeraldPrimary, onSecondary = Obsidian,
    background = Obsidian, onBackground = Ivory,
    surface = SurfaceDark, onSurface = Ivory,
    surfaceVariant = CardDark, onSurfaceVariant = SoftWhite,
    error = DangerRed, outline = MutedGray,
)

@Composable
fun AiSshTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, shapes = Shapes, content = content)
}
