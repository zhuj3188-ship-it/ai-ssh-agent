package com.aissh.agent.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aissh.agent.ui.theme.CardDark
import com.aissh.agent.ui.theme.SurfaceDark

@Composable
fun ShimmerBlock(width: Dp = 200.dp, height: Dp = 14.dp) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(initialValue = -500f, targetValue = 1500f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)), label = "offset")
    Box(Modifier.size(width, height).clip(RoundedCornerShape(6.dp)).background(
        Brush.linearGradient(listOf(CardDark, SurfaceDark.copy(alpha = 0.6f), CardDark),
            start = Offset(offset, 0f), end = Offset(offset + 400f, 0f))))
}

@Composable
fun MessageShimmer() {
    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ShimmerBlock(260.dp); ShimmerBlock(200.dp); ShimmerBlock(140.dp)
    }
}
