package com.aissh.agent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.R
import com.aissh.agent.data.remote.ApiService
import com.aissh.agent.data.remote.QuotaItem
import com.aissh.agent.ui.components.*
import com.aissh.agent.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotaViewModel @Inject constructor(private val api: ApiService) : ViewModel() {
    private val _quotas = MutableStateFlow<List<QuotaItem>>(emptyList())
    val quotas = _quotas.asStateFlow()
    init { viewModelScope.launch { try { _quotas.value = api.getQuota().quotas } catch (_: Exception) {} } }
}

@Composable
fun QuotaScreen(vm: QuotaViewModel = hiltViewModel()) {
    val quotas by vm.quotas.collectAsState()
    Column(Modifier.fillMaxSize().background(Obsidian)) {
        AppTopBar(title = stringResource(R.string.quota))
        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(quotas) { q -> QuotaCard(q) }
        }
    }
}
