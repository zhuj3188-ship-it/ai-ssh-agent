package com.aissh.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.data.local.entity.MessageEntity
import com.aissh.agent.data.repository.ChatRepository
import com.aissh.agent.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    val currentProvider: String = "anthropic",
    val validProviders: Set<String> = emptySet()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepository,
    private val settingsRepo: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()
    private var messageJob: Job? = null

    private val allProviders = listOf("anthropic", "openai", "gemini", "deepseek", "zhipu", "nvidia", "ollama")

    init {
        loadSession("default")
        viewModelScope.launch {
            chatRepo.getAllSessions().collect { s -> _state.update { it.copy(sessions = s) } }
        }
        viewModelScope.launch {
            settingsRepo.provider.collect { p -> _state.update { it.copy(currentProvider = p) } }
        }
        allProviders.forEach { p ->
            viewModelScope.launch {
                settingsRepo.apiValidFlow(p).collect { valid ->
                    _state.update { st ->
                        val set = st.validProviders.toMutableSet()
                        if (valid) set.add(p) else set.remove(p)
                        st.copy(validProviders = set)
                    }
                }
            }
        }
    }

    fun loadSession(sessionId: String) {
        _state.update { it.copy(currentSession = sessionId) }
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            chatRepo.getMessages(sessionId).collect { msgs -> _state.update { it.copy(messages = msgs) } }
        }
    }

    fun newSession() { loadSession("chat_" + System.currentTimeMillis()) }

    fun setProvider(p: String) {
        _state.update { it.copy(currentProvider = p, lastProvider = p) }
        viewModelScope.launch { settingsRepo.setProvider(p) }
    }

    fun send(message: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val r = chatRepo.sendMessage(message, _state.value.currentProvider, _state.value.currentSession)
                _state.update { it.copy(isLoading = false, lastProvider = r.provider, lastModel = r.model, lastCost = r.cost) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearCurrent() { viewModelScope.launch { chatRepo.clearSession(_state.value.currentSession) } }
    fun clearAll() { viewModelScope.launch { chatRepo.clearAll() } }
}
