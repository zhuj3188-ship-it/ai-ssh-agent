package com.aissh.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String, val role: String, val content: String,
    val provider: String = "", val model: String = "",
    val tokensUsed: Int = 0, val timestamp: Long = System.currentTimeMillis()
)
