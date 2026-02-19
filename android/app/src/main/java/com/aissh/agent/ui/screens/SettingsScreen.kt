package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aissh.agent.R
import com.aissh.agent.ui.components.*
import com.aissh.agent.ui.theme.*
import com.aissh.agent.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(vm: SettingsViewModel = hiltViewModel(), onLogout: () -> Unit = {}) {
    val state by vm.state.collectAsState()
    Column(Modifier.fillMaxSize().background(Obsidian).verticalScroll(rememberScrollState())) {
        AppTopBar(title = stringResource(R.string.settings))
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassmorphicCard(Modifier.fillMaxWidth()) { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.server_url), color = CyanPrimary, fontSize = 14.sp)
                OutlinedTextField(value = state.serverUrl, onValueChange = { vm.setServerUrl(it) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, focusedTextColor = Ivory, unfocusedTextColor = SoftWhite))
            }}
            GlassmorphicCard(Modifier.fillMaxWidth()) { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI Provider", color = CyanPrimary, fontSize = 14.sp)
                listOf("anthropic", "openai", "gemini", "deepseek", "zhipu", "nvidia", "ollama").forEach { p ->
                    Row(Modifier.fillMaxWidth()) {
                        RadioButton(selected = state.provider == p, onClick = { vm.setProvider(p) },
                            colors = RadioButtonDefaults.colors(selectedColor = CyanPrimary, unselectedColor = MutedGray))
                        Text(p, color = if (state.provider == p) CyanPrimary else SoftWhite, modifier = Modifier.padding(start = 4.dp, top = 12.dp))
                    }
                }
            }}
            GlassmorphicCard(Modifier.fillMaxWidth()) { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.language), color = CyanPrimary, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("zh" to "中文", "en" to "English").forEach { (code, label) ->
                        FilterChip(selected = state.language == code, onClick = { vm.setLanguage(code) }, label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanPrimary.copy(alpha = 0.2f), selectedLabelColor = CyanPrimary))
                    }
                }
            }}
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed), shape = RoundedCornerShape(12.dp)) {
                Text(stringResource(R.string.logout), color = Ivory, fontSize = 16.sp)
            }
        }
    }
}
