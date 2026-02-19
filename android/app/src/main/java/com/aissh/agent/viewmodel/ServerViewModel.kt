package com.aissh.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.data.local.entity.ServerEntity
import com.aissh.agent.data.repository.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServerState(val servers: List<ServerEntity> = emptyList(), val isLoading: Boolean = false)

@HiltViewModel
class ServerViewModel @Inject constructor(private val repo: ServerRepository) : ViewModel() {
    private val _state = MutableStateFlow(ServerState())
    val state = _state.asStateFlow()
    init { viewModelScope.launch { repo.servers.collect { list -> _state.update { it.copy(servers = list) } } }; refresh() }
    fun refresh() { viewModelScope.launch { _state.update { it.copy(isLoading = true) }; try { repo.refresh() } catch (_: Exception) {}; _state.update { it.copy(isLoading = false) } } }
    fun testConnection(name: String) { viewModelScope.launch { try { repo.testConnection(name) } catch (_: Exception) {} } }
}
