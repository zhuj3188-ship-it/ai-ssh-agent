package com.aissh.agent.data.local.dao

import androidx.room.*
import com.aissh.agent.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE sessionId = :sid ORDER BY timestamp ASC")
    fun getBySession(sid: String): Flow<List<MessageEntity>>

    @Query("SELECT DISTINCT sessionId FROM messages ORDER BY MAX(timestamp) DESC")
    fun getAllSessions(): Flow<List<String>>

    @Query("SELECT * FROM messages WHERE sessionId = :sid ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessage(sid: String): MessageEntity?

    @Query("SELECT COUNT(*) FROM messages WHERE sessionId = :sid")
    suspend fun getMessageCount(sid: String): Int

    @Insert suspend fun insert(msg: MessageEntity)
    @Query("DELETE FROM messages WHERE sessionId = :sid") suspend fun clearSession(sid: String)
    @Query("DELETE FROM messages") suspend fun clearAll()
}
