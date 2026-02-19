package com.aissh.agent.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aissh.agent.ui.theme.*

@Composable
fun MessageBubble(content: String, isUser: Boolean, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(visible = visible,
        enter = slideInHorizontally(initialOffsetX = { if (isUser) it else -it },
            animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f)) + fadeIn(tween(300))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
            val shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp, bottomEnd = if (isUser) 4.dp else 18.dp)
            val bg = if (isUser) Brush.linearGradient(listOf(CyanDim, CyanPrimary.copy(alpha = 0.3f)))
                     else Brush.linearGradient(listOf(CardDark, SurfaceDark))
            Box(Modifier.widthIn(max = 300.dp).clip(shape).background(bg)
                .border(1.dp, CyanPrimary.copy(alpha = if (isUser) 0.4f else 0.15f), shape).padding(12.dp)) {
                Text(text = content, color = if (isUser) Ivory else SoftWhite, fontSize = 15.sp, lineHeight = 22.sp)
            }
        }
    }
}
