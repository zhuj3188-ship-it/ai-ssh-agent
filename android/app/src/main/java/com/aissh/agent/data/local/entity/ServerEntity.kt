package com.aissh.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey val name: String, val host: String, val port: Int = 22,
    val username: String = "root", val isConnected: Boolean = false,
    val latencyMs: Int = 0, val lastChecked: Long = 0
)
