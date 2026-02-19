package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aissh.agent.R
import com.aissh.agent.viewmodel.AuthViewModel

@Composable
fun LoginScreen(vm: AuthViewModel) {
    val state by vm.state.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme

    Box(Modifier.fillMaxSize().background(cs.background), contentAlignment = Alignment.Center) {
        Card(Modifier.width(340.dp), shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant)) {
            Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Box(Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(cs.primary),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = cs.onPrimary, modifier = Modifier.size(28.dp))
                }
                Text("AI SSH Agent", fontSize = 22.sp, color = cs.onSurface)

                OutlinedTextField(value = username, onValueChange = { username = it },
                    label = { Text(stringResource(R.string.username)) }, singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = cs.primary, focusedTextColor = cs.onSurface, unfocusedTextColor = cs.onSurfaceVariant))

                OutlinedTextField(value = password, onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) }, singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = { IconButton(onClick = { showPw = !showPw }) {
                        Icon(if (showPw) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null) } },
                    visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = cs.primary, focusedTextColor = cs.onSurface, unfocusedTextColor = cs.onSurfaceVariant))

                state.error?.let { Text(it, color = cs.error, fontSize = 13.sp) }

                Button(onClick = { vm.login(username, password) },
                    enabled = !state.isLoading && username.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cs.primary),
                    shape = RoundedCornerShape(14.dp)) {
                    if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = cs.onPrimary)
                    else Text(stringResource(R.string.login), fontSize = 16.sp)
                }
            }
        }
    }
}
