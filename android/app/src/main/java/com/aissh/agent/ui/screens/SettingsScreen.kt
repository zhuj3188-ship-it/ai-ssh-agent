package com.aissh.agent.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val providers = listOf("anthropic", "openai", "gemini", "deepseek", "zhipu", "nvidia", "ollama")

    Column(Modifier.fillMaxSize().background(cs.background).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(stringResource(R.string.settings), fontSize = 22.sp, color = cs.onBackground,
            modifier = Modifier.padding(bottom = 4.dp))

        // AI Provider Keys
        Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Key, null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.ai_provider), color = cs.onSurface, fontSize = 15.sp)
                }
                providers.forEach { p ->
                    val keyState = state.providerKeys[p]
                    var showKey by remember(p) { mutableStateOf(false) }
                    val keyVal = keyState?.key ?: ""
                    val isValid = keyState?.isValid ?: false
                    val isValidating = keyState?.isValidating ?: false

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(
                                when { isValid -> Color(0xFF10B981); keyVal.isNotBlank() -> Color(0xFFF59E0B); else -> cs.outline }))
                            Text(p.replaceFirstChar { it.uppercase() }, fontSize = 13.sp, color = cs.onSurface)
                            Spacer(Modifier.weight(1f))
                            if (isValid) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = keyVal,
                                onValueChange = { vm.setApiKey(p, it) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                placeholder = { Text(stringResource(R.string.api_key_hint, p), fontSize = 11.sp) },
                                visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { showKey = !showKey }, modifier = Modifier.size(24.dp)) {
                                        Icon(if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            null, modifier = Modifier.size(14.dp), tint = cs.onSurfaceVariant)
                                    }
                                },
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = cs.primary,
                                    unfocusedBorderColor = cs.outline.copy(alpha = 0.3f),
                                    focusedTextColor = cs.onSurface,
                                    unfocusedTextColor = cs.onSurfaceVariant)
                            )
                            FilledTonalButton(
                                onClick = { vm.validateKey(p) },
                                enabled = keyVal.isNotBlank() && !isValidating,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isValidating) {
                                    CircularProgressIndicator(Modifier.size(14.dp), color = cs.primary, strokeWidth = 2.dp)
                                } else {
                                    Text(stringResource(R.string.validate), fontSize = 11.sp)
                                }
                            }
                        }
                        keyState?.error?.let {
                            Text(it, color = cs.error, fontSize = 10.sp, maxLines = 1)
                        }
                    }
                    if (p != providers.last()) HorizontalDivider(color = cs.outline.copy(alpha = 0.1f))
                }
            }
        }

        // Proxy
        Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.VpnKey, null, tint = cs.primary, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.proxy), color = cs.onSurface, fontSize = 14.sp)
                    }
                    Switch(checked = state.proxyEnabled,
                        onCheckedChange = { vm.setProxy(it, state.proxyHost, state.proxyPort) },
                        colors = SwitchDefaults.colors(checkedTrackColor = cs.primary))
                }
                AnimatedVisibility(visible = state.proxyEnabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = state.proxyHost,
                            onValueChange = { vm.setProxy(state.proxyEnabled, it, state.proxyPort) },
                            label = { Text(stringResource(R.string.proxy_host), fontSize = 12.sp) },
                            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = cs.primary))
                        OutlinedTextField(value = state.proxyPort,
                            onValueChange = { vm.setProxy(state.proxyEnabled, state.proxyHost, it) },
                            label = { Text(stringResource(R.string.proxy_port), fontSize = 12.sp) },
                            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = cs.primary))
                    }
                }
            }
        }

        // Theme + Language
        Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(if (state.darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            null, tint = cs.primary, modifier = Modifier.size(20.dp))
                        Text(stringResource(R.string.dark_mode), color = cs.onSurface, fontSize = 14.sp)
                    }
                    Switch(checked = state.darkMode, onCheckedChange = { vm.setDarkMode(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = cs.primary, checkedThumbColor = cs.onPrimary))
                }
                HorizontalDivider(color = cs.outline.copy(alpha = 0.2f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Language, null, tint = cs.primary, modifier = Modifier.size(20.dp))
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
            colors = ButtonDefaults.buttonColors(containerColor = cs.error),
            shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.logout), fontSize = 15.sp)
        }
    }
}
