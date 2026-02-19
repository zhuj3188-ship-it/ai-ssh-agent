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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aissh.agent.R
import com.aissh.agent.ui.components.*
import com.aissh.agent.viewmodel.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    var input by remember { mutableStateOf("") }
    var showProviders by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val cs = MaterialTheme.colorScheme

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1)
    }

    Column(Modifier.fillMaxSize().background(cs.background)) {
        // Top bar - compact
        Surface(color = cs.surface, shadowElevation = 2.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.chat), fontSize = 18.sp, color = cs.onSurface)
                // Provider selector
                Row(Modifier.clip(RoundedCornerShape(20.dp)).background(cs.primary.copy(alpha = 0.1f))
                    .clickable { showProviders = !showProviders }.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = cs.primary, modifier = Modifier.size(16.dp))
                    Text(state.lastProvider.ifEmpty { "anthropic" }, fontSize = 12.sp, color = cs.primary)
                    Icon(Icons.Default.ExpandMore, contentDescription = null, tint = cs.primary, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Provider dropdown
        AnimatedVisibility(visible = showProviders, enter = expandVertically(), exit = shrinkVertically()) {
            Surface(color = cs.surfaceVariant) {
                Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("anthropic", "openai", "gemini", "deepseek", "zhipu", "ollama").forEach { p ->
                        FilterChip(selected = (state.lastProvider.ifEmpty { "anthropic" }) == p,
                            onClick = { vm.setProvider(p); showProviders = false },
                            label = { Text(p, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cs.primary.copy(alpha = 0.15f), selectedLabelColor = cs.primary,
                                containerColor = cs.surface, labelColor = cs.onSurfaceVariant))
                    }
                }
            }
        }

        // Cost info - compact
        if (state.lastProvider.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().background(cs.surfaceVariant).padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${state.lastProvider}/${state.lastModel}", color = cs.onSurfaceVariant, fontSize = 11.sp)
                Text("$${String.format("%.4f", state.lastCost)}", color = cs.secondary, fontSize = 11.sp)
            }
        }

        // Messages
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = listState, contentPadding = PaddingValues(vertical = 4.dp)) {
            if (state.messages.isEmpty()) {
                item {
                    Column(Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = cs.primary.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                        Text(stringResource(R.string.no_messages), color = cs.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
            items(state.messages, key = { it.id }) { msg -> MessageBubble(content = msg.content, isUser = msg.role == "user") }
            if (state.isLoading) { item { MessageShimmer() } }
        }

        // Error
        state.error?.let {
            Text(it, color = cs.error, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))
        }

        // Input - compact
        Surface(color = cs.surface, shadowElevation = 4.dp) {
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.Bottom) {
                OutlinedTextField(value = input, onValueChange = { input = it },
                    placeholder = { Text(stringResource(R.string.type_message), color = cs.onSurfaceVariant, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f), maxLines = 3, shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary, unfocusedBorderColor = cs.outline.copy(alpha = 0.3f),
                        focusedTextColor = cs.onSurface, unfocusedTextColor = cs.onSurface,
                        focusedContainerColor = cs.surfaceVariant, unfocusedContainerColor = cs.surfaceVariant))
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { if (input.isNotBlank()) { vm.send(input.trim()); input = "" } },
                    enabled = input.isNotBlank() && !state.isLoading,
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(
                        if (input.isNotBlank()) cs.primary else cs.surfaceVariant)) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.send),
                        tint = if (input.isNotBlank()) cs.onPrimary else cs.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
