package com.aissh.agent.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aissh.agent.data.remote.ServerInfo

@Composable
fun ServerCard(server: ServerInfo, onTest: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Dns, null, tint = cs.primary, modifier = Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text(server.name, fontSize = 15.sp, color = cs.onSurface)
                Text("${server.username}@${server.host}:${server.port}", fontSize = 12.sp, color = cs.onSurfaceVariant)
            }
            FilledTonalButton(onClick = onTest, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                Icon(Icons.Default.NetworkCheck, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Test", fontSize = 12.sp)
            }
        }
    }
}
