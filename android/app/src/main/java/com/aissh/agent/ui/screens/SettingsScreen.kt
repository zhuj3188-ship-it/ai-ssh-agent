package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aissh.agent.R
import com.aissh.agent.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(vm: SettingsViewModel = hiltViewModel(), onLogout: () -> Unit = {}) {
    val state by vm.state.collectAsState()
    val cs = MaterialTheme.colorScheme
    var showKey by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(cs.background).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(stringResource(R.string.settings), fontSize = 22.sp, color = cs.onBackground,
            modifier = Modifier.padding(bottom = 4.dp))

        // Server URL
        Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Cloud, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.server_url), color = cs.onSurface, fontSize = 14.sp)
                }
                OutlinedTextField(value = state.serverUrl, onValueChange = { vm.setServerUrl(it) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = cs.primary, focusedTextColor = cs.onSurface, unfocusedTextColor = cs.onSurfaceVariant))
            }
        }

        // AI Provider
        Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.ai_provider), color = cs.onSurface, fontSize = 14.sp)
                }
                val providers = listOf("anthropic", "openai", "gemini", "deepseek", "zhipu", "nvidia", "ollama")
                providers.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach { p ->
                            FilterChip(selected = state.provider == p, onClick = { vm.setProvider(p) },
                                label = { Text(p, fontSize = 12.sp) }, modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = cs.primary.copy(alpha = 0.15f), selectedLabelColor = cs.primary,
                                    containerColor = cs.surface, labelColor = cs.onSurfaceVariant))
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        // API Key
        Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.api_key), color = cs.onSurface, fontSize = 14.sp)
                }
                OutlinedTextField(value = state.apiKeys, onValueChange = { vm.setApiKeys(it) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(stringResource(R.string.api_key_hint), fontSize = 13.sp) },
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = cs.onSurfaceVariant)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = cs.primary, focusedTextColor = cs.onSurface, unfocusedTextColor = cs.onSurfaceVariant))
            }
        }

        // Theme + Language
        Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(if (state.darkMode) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.dark_mode), color = cs.onSurface, fontSize = 14.sp)
                    }
                    Switch(checked = state.darkMode, onCheckedChange = { vm.setDarkMode(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = cs.primary, checkedThumbColor = cs.onPrimary))
                }
                Divider(color = cs.outline.copy(alpha = 0.2f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.language), color = cs.onSurface, fontSize = 14.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("zh" to "中文", "en" to "English").forEach { (code, label) ->
                        FilterChip(selected = state.language == code, onClick = { vm.setLanguage(code) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cs.primary.copy(alpha = 0.15f), selectedLabelColor = cs.primary,
                                containerColor = cs.surface, labelColor = cs.onSurfaceVariant))
                    }
                }
            }
        }

        // Logout
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.logout), fontSize = 15.sp)
        }
    }
}
