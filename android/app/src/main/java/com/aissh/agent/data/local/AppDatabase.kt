package com.aissh.agent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aissh.agent.data.local.dao.MessageDao
import com.aissh.agent.data.local.dao.ServerDao
import com.aissh.agent.data.local.entity.MessageEntity
import com.aissh.agent.data.local.entity.ServerEntity

@Database(entities = [MessageEntity::class, ServerEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun serverDao(): ServerDao
}
