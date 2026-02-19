package com.aissh.agent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.aissh.agent.ui.theme.CardDark
import com.aissh.agent.ui.theme.CyanPrimary
import com.aissh.agent.ui.theme.SurfaceDark

@Composable
fun GlassmorphicCard(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(modifier = modifier.clip(shape)
        .background(Brush.linearGradient(listOf(CardDark.copy(alpha = 0.8f), SurfaceDark.copy(alpha = 0.6f))))
        .border(1.dp, CyanPrimary.copy(alpha = 0.15f), shape).padding(16.dp), content = content)
}
