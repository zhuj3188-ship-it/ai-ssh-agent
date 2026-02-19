package com.aissh.agent.data.remote

import retrofit2.http.*

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val user: UserInfo)
data class UserInfo(val id: Int, val username: String, val role: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
data class ChatRequest(val message: String, val provider: String? = null, val session_id: String = "default")
data class ChatResponse(val reply: String, val tokens_in: Int = 0, val tokens_out: Int = 0,
    val cost: Double = 0.0, val provider: String = "", val model: String = "")
data class ServerInfo(val name: String, val host: String, val port: Int, val username: String)
data class ServersResponse(val servers: List<ServerInfo>)
data class ConnectionTest(val server: String, val connected: Boolean, val latency_ms: Int, val error: String?)
data class QuotaItem(val provider: String, val model: String, val tokens_in: Long, val tokens_out: Long, val cost_usd: Double)
data class QuotaResponse(val quotas: List<QuotaItem>)
data class ValidateKeyRequest(val provider: String, val api_key: String, val base_url: String? = null)
data class ValidateKeyResponse(val valid: Boolean, val error: String? = null, val model: String? = null, val provider: String? = null)
data class ProvidersListResponse(val available: List<String>, val all: List<String>, val default: String)

interface ApiService {
    @POST("api/auth/login") suspend fun login(@Body body: LoginRequest): LoginResponse
    @POST("api/auth/register") suspend fun register(@Body body: RegisterRequest): LoginResponse
    @POST("api/chat") suspend fun chat(@Body body: ChatRequest): ChatResponse
    @GET("api/servers") suspend fun listServers(): ServersResponse
    @POST("api/servers/{name}/test") suspend fun testServer(@Path("name") name: String): ConnectionTest
    @GET("api/quota") suspend fun getQuota(): QuotaResponse
    @POST("api/providers/validate") suspend fun validateKey(@Body body: ValidateKeyRequest): ValidateKeyResponse
    @GET("api/providers/list") suspend fun listProviders(): ProvidersListResponse
}
