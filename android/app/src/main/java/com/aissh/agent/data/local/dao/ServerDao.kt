package com.aissh.agent.data.local.dao

import androidx.room.*
import com.aissh.agent.data.local.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY name ASC") fun getAll(): Flow<List<ServerEntity>>
    @Upsert suspend fun upsert(server: ServerEntity)
    @Upsert suspend fun upsertAll(servers: List<ServerEntity>)
    @Query("DELETE FROM servers") suspend fun clearAll()
}
