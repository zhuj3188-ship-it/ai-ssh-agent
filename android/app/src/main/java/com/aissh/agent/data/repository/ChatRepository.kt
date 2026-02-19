package com.aissh.agent.data.repository

import com.aissh.agent.data.local.dao.MessageDao
import com.aissh.agent.data.local.entity.MessageEntity
import com.aissh.agent.data.remote.ApiService
import com.aissh.agent.data.remote.ChatRequest
import com.aissh.agent.data.remote.ChatResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(private val api: ApiService, private val messageDao: MessageDao) {
    fun getMessages(sessionId: String): Flow<List<MessageEntity>> = messageDao.getMessages(sessionId)
    fun getAllSessions(): Flow<List<String>> = messageDao.getAllSessions()

    suspend fun sendMessage(text: String, provider: String? = null, sessionId: String = "default"): ChatResponse {
        messageDao.insert(MessageEntity(sessionId = sessionId, role = "user", content = text))
        val resp = api.chat(ChatRequest(message = text, provider = provider, session_id = sessionId))
        messageDao.insert(MessageEntity(sessionId = sessionId, role = "assistant", content = resp.reply,
            provider = resp.provider, model = resp.model, tokensUsed = resp.tokens_in + resp.tokens_out))
        return resp
    }
    suspend fun clearSession(sessionId: String) = messageDao.deleteSession(sessionId)
    suspend fun clearAll() = messageDao.clearAll()
}
