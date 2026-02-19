package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aissh.agent.R
import com.aissh.agent.ui.theme.*
import com.aissh.agent.viewmodel.AuthViewModel

@Composable
fun LoginScreen(vm: AuthViewModel) {
    val state by vm.state.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize().background(Obsidian), contentAlignment = Alignment.Center) {
        Column(Modifier.width(320.dp).clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(CardDark, SurfaceDark))).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("AI SSH Agent", color = CyanPrimary, fontSize = 24.sp)
            OutlinedTextField(value = username, onValueChange = { username = it },
                label = { Text(stringResource(R.string.username)) }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, unfocusedBorderColor = MutedGray, focusedTextColor = Ivory, unfocusedTextColor = SoftWhite))
            OutlinedTextField(value = password, onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) }, singleLine = true, visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, unfocusedBorderColor = MutedGray, focusedTextColor = Ivory, unfocusedTextColor = SoftWhite))
            state.error?.let { Text(it, color = DangerRed, fontSize = 13.sp) }
            Button(onClick = { vm.login(username, password) }, enabled = !state.isLoading && username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Obsidian),
                shape = RoundedCornerShape(12.dp)) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Obsidian)
                else Text(stringResource(R.string.login), fontSize = 16.sp)
            }
        }
    }
}
