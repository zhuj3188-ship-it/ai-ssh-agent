package com.aissh.agent.data.repository

import com.aissh.agent.data.local.dao.ServerDao
import com.aissh.agent.data.local.entity.ServerEntity
import com.aissh.agent.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepository @Inject constructor(private val api: ApiService, private val serverDao: ServerDao) {
    val servers: Flow<List<ServerEntity>> = serverDao.getAll()

    suspend fun refresh() {
        val resp = api.listServers()
        serverDao.upsertAll(resp.servers.map { ServerEntity(name = it.name, host = it.host, port = it.port, username = it.username) })
    }
    suspend fun testConnection(name: String): ServerEntity {
        val r = api.testServer(name)
        val e = ServerEntity(name = r.server, host = "", isConnected = r.connected, latencyMs = r.latency_ms, lastChecked = System.currentTimeMillis())
        serverDao.upsert(e)
        return e
    }
}
