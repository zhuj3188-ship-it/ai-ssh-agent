package com.aissh.agent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aissh.agent.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, connectionState: String = "connected") {
    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Ivory)
            Spacer(Modifier.width(8.dp))
            val color = when (connectionState) { "connected" -> EmeraldPrimary; "connecting" -> WarningAmber; else -> DangerRed }
            Box(Modifier.size(8.dp).background(color, shape = CircleShape))
        }
    }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark))
}
