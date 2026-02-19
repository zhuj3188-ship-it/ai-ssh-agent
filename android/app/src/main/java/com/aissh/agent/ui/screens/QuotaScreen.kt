package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.R
import com.aissh.agent.data.remote.ApiService
import com.aissh.agent.data.remote.QuotaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuotaState(val quotas: List<QuotaItem> = emptyList(), val isLoading: Boolean = true, val error: String? = null)

@HiltViewModel
class QuotaViewModel @Inject constructor(private val api: ApiService) : ViewModel() {
    private val _state = MutableStateFlow(QuotaState())
    val state = _state.asStateFlow()
    init { refresh() }
    fun refresh() { viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try { _state.update { it.copy(quotas = api.getQuota().quotas, isLoading = false) } }
        catch (e: Exception) { _state.update { it.copy(isLoading = false, error = e.message) } }
    }}
}

@Composable
fun QuotaScreen(vm: QuotaViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val cs = MaterialTheme.colorScheme
    val colors = listOf(Color(0xFF6C5CE7), Color(0xFF00B894), Color(0xFFE17055), Color(0xFF0984E3), Color(0xFFFDCB6E), Color(0xFFE84393))
    val totalTokens = state.quotas.sumOf { it.tokens_in + it.tokens_out }
    val totalCost = state.quotas.sumOf { it.cost_usd }

    Column(Modifier.fillMaxSize().background(cs.background)) {
        Surface(color = cs.surface, shadowElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.quota), fontSize = 18.sp, color = cs.onSurface)
                IconButton(onClick = { vm.refresh() }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Refresh, null, tint = cs.primary, modifier = Modifier.size(20.dp))
                }
            }
        }
        if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = cs.primary)
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Token, null, tint = colors[0], modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(R.string.total_tokens), fontSize = 11.sp, color = cs.onSurfaceVariant)
                            Text(String.format("%,d", totalTokens), fontSize = 20.sp, color = cs.onSurface)
                        }
                    }
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Icon(Icons.Default.AttachMoney, null, tint = colors[1], modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(R.string.total_cost), fontSize = 11.sp, color = cs.onSurfaceVariant)
                            Text("$${String.format("%.4f", totalCost)}", fontSize = 20.sp, color = cs.onSurface)
                        }
                    }
                }
            }
            itemsIndexed(state.quotas) { i, q ->
                val c = colors[i % colors.size]
                Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.size(10.dp).clip(CircleShape).background(c))
                                Text(q.provider, fontSize = 15.sp, color = cs.onSurface)
                            }
                            Surface(color = c.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)) {
                                Text(q.model, fontSize = 10.sp, color = c, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.tokens_in), fontSize = 10.sp, color = cs.onSurfaceVariant)
                                Text(String.format("%,d", q.tokens_in), fontSize = 14.sp, color = cs.onSurface)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.tokens_out), fontSize = 10.sp, color = cs.onSurfaceVariant)
                                Text(String.format("%,d", q.tokens_out), fontSize = 14.sp, color = cs.onSurface)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.cost), fontSize = 10.sp, color = cs.onSurfaceVariant)
                                Text("$${String.format("%.4f", q.cost_usd)}", fontSize = 14.sp, color = cs.secondary)
                            }
                        }
                    }
                }
            }
            if (state.quotas.isEmpty() && !state.isLoading) {
                item {
                    Column(Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DataUsage, null, tint = cs.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.no_quota), color = cs.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
