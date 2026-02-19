package com.aissh.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aissh.agent.data.remote.ApiService
import com.aissh.agent.data.remote.ValidateKeyRequest
import com.aissh.agent.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderKeyState(
    val key: String = "",
    val isValid: Boolean = false,
    val isValidating: Boolean = false,
    val error: String? = null
)

data class SettingsState(
    val darkMode: Boolean = true,
    val language: String = "zh",
    val provider: String = "anthropic",
    val proxyEnabled: Boolean = false,
    val proxyHost: String = "",
    val proxyPort: String = "",
    val providerKeys: Map<String, ProviderKeyState> = emptyMap()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    private val api: ApiService
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val providers = listOf("anthropic", "openai", "gemini", "deepseek", "zhipu", "nvidia", "ollama")

    init {
        viewModelScope.launch { repo.darkMode.collect { v -> _state.update { it.copy(darkMode = v) } } }
        viewModelScope.launch { repo.language.collect { v -> _state.update { it.copy(language = v) } } }
        viewModelScope.launch { repo.provider.collect { v -> _state.update { it.copy(provider = v) } } }
        viewModelScope.launch { repo.proxyEnabled.collect { v -> _state.update { it.copy(proxyEnabled = v) } } }
        viewModelScope.launch { repo.proxyHost.collect { v -> _state.update { it.copy(proxyHost = v) } } }
        viewModelScope.launch { repo.proxyPort.collect { v -> _state.update { it.copy(proxyPort = v) } } }
        providers.forEach { p ->
            viewModelScope.launch {
                repo.apiKeyFlow(p).collect { key ->
                    _state.update { st ->
                        val keys = st.providerKeys.toMutableMap()
                        keys[p] = (keys[p] ?: ProviderKeyState()).copy(key = key)
                        st.copy(providerKeys = keys)
                    }
                }
            }
            viewModelScope.launch {
                repo.apiValidFlow(p).collect { valid ->
                    _state.update { st ->
                        val keys = st.providerKeys.toMutableMap()
                        keys[p] = (keys[p] ?: ProviderKeyState()).copy(isValid = valid)
                        st.copy(providerKeys = keys)
                    }
                }
            }
        }
    }

    fun setDarkMode(on: Boolean) = viewModelScope.launch { repo.setDarkMode(on) }
    fun setLanguage(lang: String) = viewModelScope.launch { repo.setLanguage(lang) }
    fun setProvider(p: String) = viewModelScope.launch { repo.setProvider(p) }

    fun setApiKey(provider: String, key: String) {
        viewModelScope.launch {
            repo.setApiKey(provider, key)
            repo.setApiValid(provider, false)
        }
    }

    fun validateKey(provider: String) {
        val key = _state.value.providerKeys[provider]?.key ?: return
        if (key.isBlank()) return
        _state.update { st ->
            val keys = st.providerKeys.toMutableMap()
            keys[provider] = (keys[provider] ?: ProviderKeyState()).copy(isValidating = true, error = null)
            st.copy(providerKeys = keys)
        }
        viewModelScope.launch {
            try {
                val resp = api.validateKey(ValidateKeyRequest(provider = provider, api_key = key))
                repo.setApiValid(provider, resp.valid)
                _state.update { st ->
                    val keys = st.providerKeys.toMutableMap()
                    keys[provider] = (keys[provider] ?: ProviderKeyState()).copy(
                        isValidating = false, isValid = resp.valid, error = if (!resp.valid) resp.error else null)
                    st.copy(providerKeys = keys)
                }
            } catch (e: Exception) {
                _state.update { st ->
                    val keys = st.providerKeys.toMutableMap()
                    keys[provider] = (keys[provider] ?: ProviderKeyState()).copy(isValidating = false, isValid = false, error = e.message)
                    st.copy(providerKeys = keys)
                }
            }
        }
    }

    fun setProxy(enabled: Boolean, host: String, port: String) = viewModelScope.launch { repo.setProxy(enabled, host, port) }
}
