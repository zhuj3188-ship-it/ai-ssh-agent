package com.aissh.agent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aissh.agent.data.remote.QuotaItem
import com.aissh.agent.ui.theme.*

@Composable
fun QuotaCard(q: QuotaItem) {
    val shape = RoundedCornerShape(14.dp)
    Column(Modifier.fillMaxWidth().clip(shape)
        .background(Brush.linearGradient(listOf(CardDark, SurfaceDark)))
        .border(1.dp, CyanPrimary.copy(alpha = 0.2f), shape).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(q.provider, color = CyanPrimary, fontSize = 13.sp)
            Text("$${String.format("%.4f", q.cost_usd)}", color = MutedGray, fontSize = 12.sp)
        }
        Text(q.model, color = Ivory, fontSize = 15.sp)
        Text("输入: ${q.tokens_in}  输出: ${q.tokens_out}", color = MutedGray, fontSize = 12.sp)
    }
}
