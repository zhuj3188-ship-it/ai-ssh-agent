package com.aissh.agent.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val PROVIDER = stringPreferencesKey("provider")
        val PROXY_ENABLED = booleanPreferencesKey("proxy_enabled")
        val PROXY_HOST = stringPreferencesKey("proxy_host")
        val PROXY_PORT = stringPreferencesKey("proxy_port")
        fun apiKeyKey(provider: String) = stringPreferencesKey("api_key_$provider")
        fun apiValidKey(provider: String) = booleanPreferencesKey("api_valid_$provider")
    }
    val darkMode = dataStore.data.map { it[DARK_MODE] ?: true }
    val language = dataStore.data.map { it[LANGUAGE] ?: "zh" }
    val provider = dataStore.data.map { it[PROVIDER] ?: "anthropic" }
    val proxyEnabled = dataStore.data.map { it[PROXY_ENABLED] ?: false }
    val proxyHost = dataStore.data.map { it[PROXY_HOST] ?: "" }
    val proxyPort = dataStore.data.map { it[PROXY_PORT] ?: "" }

    fun apiKeyFlow(provider: String) = dataStore.data.map { it[apiKeyKey(provider)] ?: "" }
    fun apiValidFlow(provider: String) = dataStore.data.map { it[apiValidKey(provider)] ?: false }

    suspend fun setDarkMode(on: Boolean) = dataStore.edit { it[DARK_MODE] = on }
    suspend fun setLanguage(lang: String) = dataStore.edit { it[LANGUAGE] = lang }
    suspend fun setProvider(p: String) = dataStore.edit { it[PROVIDER] = p }
    suspend fun setApiKey(provider: String, key: String) = dataStore.edit { it[apiKeyKey(provider)] = key }
    suspend fun setApiValid(provider: String, valid: Boolean) = dataStore.edit { it[apiValidKey(provider)] = valid }
    suspend fun setProxy(enabled: Boolean, host: String, port: String) {
        dataStore.edit { it[PROXY_ENABLED] = enabled; it[PROXY_HOST] = host; it[PROXY_PORT] = port }
    }
}
