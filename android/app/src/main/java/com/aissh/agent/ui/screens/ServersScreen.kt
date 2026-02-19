package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aissh.agent.R
import com.aissh.agent.ui.components.*
import com.aissh.agent.ui.theme.*
import com.aissh.agent.viewmodel.ServerViewModel

@Composable
fun ServersScreen(vm: ServerViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    Column(Modifier.fillMaxSize().background(Obsidian)) {
        AppTopBar(title = stringResource(R.string.servers))
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { vm.refresh() }) { Icon(Icons.Default.Refresh, null, tint = CyanPrimary) }
        }
        if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = CyanPrimary)
        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.servers, key = { it.name }) { server ->
                Box(Modifier.clickable { vm.testConnection(server.name) }) { ServerCard(server = server, onTest = { vm.testConnection(server.name) }) }
            }
        }
    }
}
