package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aissh.agent.R
import com.aissh.agent.ui.components.*
import com.aissh.agent.ui.theme.*
import com.aissh.agent.viewmodel.ChatViewModel

@Composable
fun ChatScreen(vm: ChatViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages.size) { if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1) }

    Column(Modifier.fillMaxSize().background(Obsidian)) {
        AppTopBar(title = stringResource(R.string.chat))
        if (state.lastProvider.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().background(SurfaceDark).padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${state.lastProvider} / ${state.lastModel}", color = MutedGray, fontSize = 11.sp)
                Text("$${String.format("%.4f", state.lastCost)}", color = EmeraldPrimary, fontSize = 11.sp)
            }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), state = listState, contentPadding = PaddingValues(vertical = 8.dp)) {
            if (state.messages.isEmpty()) { item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text(stringResource(R.string.no_messages), color = MutedGray) } } }
            items(state.messages, key = { it.id }) { msg -> MessageBubble(content = msg.content, isUser = msg.role == "user") }
            if (state.isLoading) { item { MessageShimmer() } }
        }
        state.error?.let { Text(it, color = DangerRed, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp)) }
        Row(Modifier.fillMaxWidth().background(SurfaceDark).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = input, onValueChange = { input = it },
                placeholder = { Text(stringResource(R.string.type_message), color = MutedGray) },
                modifier = Modifier.weight(1f), maxLines = 4, shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, unfocusedBorderColor = MutedGray.copy(alpha = 0.3f), focusedTextColor = Ivory, unfocusedTextColor = SoftWhite))
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { if (input.isNotBlank()) { vm.send(input.trim()); input = "" } }, enabled = input.isNotBlank() && !state.isLoading) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.send), tint = if (input.isNotBlank()) CyanPrimary else MutedGray)
            }
        }
    }
}
