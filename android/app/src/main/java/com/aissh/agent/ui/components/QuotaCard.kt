package com.aissh.agent.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aissh.agent.data.remote.QuotaItem

@Composable
fun QuotaCard(q: QuotaItem) {
    val cs = MaterialTheme.colorScheme
    Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(q.provider, color = cs.primary, fontSize = 13.sp)
                Text("$${String.format("%.4f", q.cost_usd)}", color = cs.onSurfaceVariant, fontSize = 12.sp)
            }
            Text(q.model, color = cs.onSurface, fontSize = 15.sp)
            Text("In: ${q.tokens_in}  Out: ${q.tokens_out}", color = cs.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}
