package com.aissh.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(val serverUrl: String = "", val darkMode: Boolean = true, val language: String = "zh",
    val provider: String = "anthropic", val proxyEnabled: Boolean = false,
    val proxyHost: String = "", val proxyPort: String = "", val proxyType: String = "http")

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repo: SettingsRepository) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    init {
        viewModelScope.launch { repo.serverUrl.collect { v -> _state.update { it.copy(serverUrl = v) } } }
        viewModelScope.launch { repo.darkMode.collect { v -> _state.update { it.copy(darkMode = v) } } }
        viewModelScope.launch { repo.language.collect { v -> _state.update { it.copy(language = v) } } }
        viewModelScope.launch { repo.provider.collect { v -> _state.update { it.copy(provider = v) } } }
        viewModelScope.launch { repo.proxyEnabled.collect { v -> _state.update { it.copy(proxyEnabled = v) } } }
    }
    fun setServerUrl(url: String) = viewModelScope.launch { repo.setServerUrl(url) }
    fun setDarkMode(on: Boolean) = viewModelScope.launch { repo.setDarkMode(on) }
    fun setLanguage(lang: String) = viewModelScope.launch { repo.setLanguage(lang) }
    fun setProvider(p: String) = viewModelScope.launch { repo.setProvider(p) }
    fun setProxy(enabled: Boolean, host: String, port: String, type: String) = viewModelScope.launch { repo.setProxy(enabled, host, port, type) }
}
