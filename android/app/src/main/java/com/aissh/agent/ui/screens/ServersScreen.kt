package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aissh.agent.R
import com.aissh.agent.viewmodel.ServerViewModel

@Composable
fun ServersScreen(vm: ServerViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val cs = MaterialTheme.colorScheme

    Column(Modifier.fillMaxSize().background(cs.background)) {
        Surface(color = cs.surface, shadowElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.servers), fontSize = 18.sp, color = cs.onSurface)
                IconButton(onClick = { vm.refresh() }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Refresh, null, tint = cs.primary, modifier = Modifier.size(20.dp))
                }
            }
        }

        if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = cs.primary)

        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.servers, key = { it.name }) { server ->
                Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Dns, null, tint = cs.primary, modifier = Modifier.size(28.dp))
                        Column(Modifier.weight(1f)) {
                            Text(server.name, fontSize = 15.sp, color = cs.onSurface)
                            Text("${server.username}@${server.host}:${server.port}", fontSize = 12.sp, color = cs.onSurfaceVariant)
                        }
                        FilledTonalButton(onClick = { vm.testConnection(server.name) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                            Icon(Icons.Default.NetworkCheck, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.test), fontSize = 12.sp)
                        }
                    }
                }
            }
            if (state.servers.isEmpty() && !state.isLoading) {
                item {
                    Column(Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Dns, null, tint = cs.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.no_servers), color = cs.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
