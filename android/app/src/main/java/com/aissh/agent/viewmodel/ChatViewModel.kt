package com.aissh.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.data.local.entity.MessageEntity
import com.aissh.agent.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatState(val messages: List<MessageEntity> = emptyList(), val isLoading: Boolean = false,
    val error: String? = null, val lastProvider: String = "", val lastModel: String = "", val lastCost: Double = 0.0)

@HiltViewModel
class ChatViewModel @Inject constructor(private val repo: ChatRepository) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()
    private val sessionId = "default"
    init { viewModelScope.launch { repo.getMessages(sessionId).collect { msgs -> _state.update { it.copy(messages = msgs) } } } }

    fun send(text: String, provider: String? = null) { if (text.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try { val resp = repo.sendMessage(text, sessionId, provider)
                _state.update { it.copy(isLoading = false, lastProvider = resp.provider, lastModel = resp.model, lastCost = resp.cost) }
            } catch (e: Exception) { _state.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
    fun clearChat() { viewModelScope.launch { repo.clearSession(sessionId) } }
}
