package com.aissh.agent.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aissh.agent.R
import com.aissh.agent.ui.components.*
import com.aissh.agent.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(vm: ChatViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    var input by remember { mutableStateOf("") }
    var showProviders by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val cs = MaterialTheme.colorScheme

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1)
    }

    // History drawer
    if (showHistory) {
        ModalBottomSheet(onDismissRequest = { showHistory = false },
            containerColor = cs.surfaceVariant) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.chat_history), fontSize = 18.sp, color = cs.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { vm.newSession(); showHistory = false }) {
                            Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.new_chat), fontSize = 13.sp)
                        }
                        if (state.sessions.isNotEmpty()) {
                            IconButton(onClick = { vm.clearAll() }) {
                                Icon(Icons.Default.DeleteSweep, null, tint = cs.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (state.sessions.isEmpty()) {
                    Text(stringResource(R.string.no_history), color = cs.onSurfaceVariant, modifier = Modifier.padding(20.dp))
                }
                state.sessions.forEach { sid ->
                    val isActive = sid == state.currentSession
                    Surface(
                        onClick = { vm.loadSession(sid); showHistory = false },
                        color = if (isActive) cs.primary.copy(alpha = 0.12f) else cs.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.ChatBubbleOutline, null,
                                tint = if (isActive) cs.primary else cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            Column(Modifier.weight(1f)) {
                                Text(if (sid == "default") stringResource(R.string.default_chat) else sid.replace("chat_", "#"),
                                    fontSize = 14.sp, color = if (isActive) cs.primary else cs.onSurface,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (isActive) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(cs.primary))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    Column(Modifier.fillMaxSize().background(cs.background)) {
        // Top bar
        Surface(color = cs.surface, shadowElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showHistory = true }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Menu, null, tint = cs.onSurface, modifier = Modifier.size(20.dp))
                }
                Text(stringResource(R.string.chat), fontSize = 17.sp, color = cs.onSurface,
                    modifier = Modifier.weight(1f).padding(start = 4.dp))
                // Provider chip
                Surface(onClick = { showProviders = !showProviders },
                    color = cs.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.SmartToy, null, tint = cs.primary, modifier = Modifier.size(14.dp))
                        Text(state.currentProvider, fontSize = 11.sp, color = cs.primary)
                        Icon(if (showProviders) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null, tint = cs.primary, modifier = Modifier.size(14.dp))
                    }
                }
                IconButton(onClick = { vm.clearCurrent() }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.DeleteOutline, null, tint = cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Provider selector
        AnimatedVisibility(visible = showProviders, enter = expandVertically(), exit = shrinkVertically()) {
            Surface(color = cs.surfaceVariant, shadowElevation = 2.dp) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    listOf("anthropic", "openai", "gemini", "deepseek", "zhipu", "nvidia", "ollama").chunked(4).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { p ->
                                FilterChip(selected = state.currentProvider == p,
                                    onClick = { vm.setProvider(p); showProviders = false },
                                    label = { Text(p, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = cs.primary.copy(alpha = 0.15f), selectedLabelColor = cs.primary,
                                        containerColor = cs.surface, labelColor = cs.onSurfaceVariant))
                            }
                            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }

        // Cost bar
        if (state.lastModel.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().background(cs.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 14.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${state.lastProvider} / ${state.lastModel}", color = cs.onSurfaceVariant, fontSize = 10.sp)
                Text("$${String.format("%.4f", state.lastCost)}", color = cs.secondary, fontSize = 10.sp)
            }
        }

        // Messages
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = listState, contentPadding = PaddingValues(vertical = 4.dp)) {
            if (state.messages.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.SmartToy, null, tint = cs.primary.copy(alpha = 0.2f), modifier = Modifier.size(56.dp))
                        Text(stringResource(R.string.no_messages), color = cs.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
            items(state.messages, key = { it.id }) { msg -> MessageBubble(content = msg.content, isUser = msg.role == "user") }
            if (state.isLoading) { item { MessageShimmer() } }
        }

        // Error
        state.error?.let {
            Text(it, color = cs.error, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp), maxLines = 2)
        }

        // Input
        Surface(color = cs.surface, shadowElevation = 4.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.Bottom) {
                OutlinedTextField(value = input, onValueChange = { input = it },
                    placeholder = { Text(stringResource(R.string.type_message), fontSize = 14.sp) },
                    modifier = Modifier.weight(1f), maxLines = 3, shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary, unfocusedBorderColor = cs.outline.copy(alpha = 0.2f),
                        focusedTextColor = cs.onSurface, unfocusedTextColor = cs.onSurface,
                        cursorColor = cs.primary,
                        focusedContainerColor = cs.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = cs.surfaceVariant.copy(alpha = 0.3f)))
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { if (input.isNotBlank()) { vm.send(input.trim()); input = "" } },
                    enabled = input.isNotBlank() && !state.isLoading,
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(
                        if (input.isNotBlank()) cs.primary else cs.surfaceVariant)) {
                    Icon(Icons.AutoMirrored.Filled.Send, stringResource(R.string.send),
                        tint = if (input.isNotBlank()) cs.onPrimary else cs.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
