package com.aissh.agent.data.local.dao

import androidx.room.*
import com.aissh.agent.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE sessionId = :sid ORDER BY timestamp ASC")
    fun getBySession(sid: String): Flow<List<MessageEntity>>
    @Insert suspend fun insert(msg: MessageEntity)
    @Query("DELETE FROM messages WHERE sessionId = :sid") suspend fun clearSession(sid: String)
    @Query("DELETE FROM messages") suspend fun clearAll()
}
