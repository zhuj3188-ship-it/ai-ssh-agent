package com.aissh.agent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aissh.agent.data.local.entity.ServerEntity
import com.aissh.agent.ui.theme.*

@Composable
fun ServerCard(server: ServerEntity, onTest: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(Modifier.fillMaxWidth().clip(shape)
        .background(Brush.linearGradient(listOf(CardDark, SurfaceDark)))
        .border(1.dp, CyanPrimary.copy(alpha = 0.2f), shape).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(if (server.isConnected) EmeraldPrimary else DangerRed))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(server.name, color = Ivory, fontSize = 16.sp)
            Text("${server.host}:${server.port}", color = MutedGray, fontSize = 12.sp)
            if (server.latencyMs > 0) Text("${server.latencyMs}ms", color = EmeraldPrimary, fontSize = 11.sp)
        }
    }
}
