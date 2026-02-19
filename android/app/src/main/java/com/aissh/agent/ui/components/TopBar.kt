package com.aissh.agent.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTopBar(title: String) {
    val cs = MaterialTheme.colorScheme
    Surface(color = cs.surface, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(title, fontSize = 18.sp, color = cs.onSurface)
        }
    }
}
