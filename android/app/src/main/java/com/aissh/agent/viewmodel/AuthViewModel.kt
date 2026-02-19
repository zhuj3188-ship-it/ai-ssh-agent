package com.aissh.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(val isLoading: Boolean = false, val isLoggedIn: Boolean = false, val username: String = "", val error: String? = null)

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()
    init {
        viewModelScope.launch { repo.isLoggedIn.collect { v -> _state.update { it.copy(isLoggedIn = v) } } }
        viewModelScope.launch { repo.username.collect { v -> _state.update { it.copy(username = v) } } }
    }
    fun login(user: String, pass: String) { viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        repo.login(user, pass).onSuccess { _state.update { it.copy(isLoading = false) } }
            .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
    }}
    fun logout() { viewModelScope.launch { repo.logout() } }
}
