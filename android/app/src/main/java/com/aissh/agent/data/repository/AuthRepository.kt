package com.aissh.agent.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aissh.agent.data.remote.ApiService
import com.aissh.agent.data.remote.AuthInterceptor
import com.aissh.agent.data.remote.LoginRequest
import com.aissh.agent.data.remote.RegisterRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val api: ApiService, private val dataStore: DataStore<Preferences>) {
    companion object { val USERNAME_KEY = stringPreferencesKey("username") }
    val isLoggedIn = dataStore.data.map { it[AuthInterceptor.TOKEN_KEY] != null }
    val username = dataStore.data.map { it[USERNAME_KEY] ?: "" }

    suspend fun login(user: String, pass: String): Result<String> = runCatching {
        val resp = api.login(LoginRequest(user, pass))
        dataStore.edit { it[AuthInterceptor.TOKEN_KEY] = resp.token; it[USERNAME_KEY] = resp.user.username }
        resp.user.username
    }
    suspend fun register(user: String, email: String, pass: String): Result<String> = runCatching {
        val resp = api.register(RegisterRequest(user, email, pass))
        dataStore.edit { it[AuthInterceptor.TOKEN_KEY] = resp.token; it[USERNAME_KEY] = resp.user.username }
        resp.user.username
    }
    suspend fun logout() { dataStore.edit { it.remove(AuthInterceptor.TOKEN_KEY); it.remove(USERNAME_KEY) } }
    suspend fun getToken(): String? = dataStore.data.map { it[AuthInterceptor.TOKEN_KEY] }.first()
}
