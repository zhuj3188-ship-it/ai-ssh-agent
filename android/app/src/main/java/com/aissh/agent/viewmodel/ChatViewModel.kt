package com.aissh.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.data.local.entity.MessageEntity
import com.aissh.agent.data.repository.ChatRepository
import com.aissh.agent.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatState(
    val messages: List<MessageEntity> = emptyList(),
    val sessions: List<String> = emptyList(),
    val currentSession: String = "default",
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastProvider: String = "",
    val lastModel: String = "",
    val lastCost: Double = 0.0,
    val currentProvider: String = "anthropic"
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepository,
    private val settingsRepo: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    init {
        loadSession("default")
        viewModelScope.launch {
            chatRepo.getAllSessions().collect { sessions ->
                _state.update { it.copy(sessions = sessions) }
            }
        }
        viewModelScope.launch {
            settingsRepo.provider.collect { p ->
                _state.update { it.copy(currentProvider = p) }
            }
        }
    }

    fun loadSession(sessionId: String) {
        _state.update { it.copy(currentSession = sessionId) }
        viewModelScope.launch {
            chatRepo.getMessages(sessionId).collect { msgs ->
                _state.update { it.copy(messages = msgs) }
            }
        }
    }

    fun newSession() {
        val id = "chat_" + System.currentTimeMillis()
        loadSession(id)
    }

    fun setProvider(p: String) {
        _state.update { it.copy(currentProvider = p, lastProvider = p) }
        viewModelScope.launch { settingsRepo.setProvider(p) }
    }

    fun send(message: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = chatRepo.sendMessage(message, _state.value.currentProvider, _state.value.currentSession)
                _state.update { it.copy(isLoading = false,
                    lastProvider = result.provider, lastModel = result.model, lastCost = result.cost) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearCurrent() {
        viewModelScope.launch { chatRepo.clearSession(_state.value.currentSession) }
    }

    fun clearAll() {
        viewModelScope.launch { chatRepo.clearAll() }
    }
}
