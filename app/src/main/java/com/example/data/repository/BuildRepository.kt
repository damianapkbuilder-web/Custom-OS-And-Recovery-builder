package com.example.data.repository

import com.example.data.local.BuildDao
import com.example.data.local.BuildEntity
import com.example.data.local.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

class BuildRepository(private val buildDao: BuildDao) {
    val allBuilds: Flow<List<BuildEntity>> = buildDao.getAllBuilds()
    val syncQueue: Flow<List<SyncQueueEntity>> = buildDao.getAllSyncQueue()
    val pendingSyncQueue: Flow<List<SyncQueueEntity>> = buildDao.getPendingSyncItems()

    suspend fun insertBuild(build: BuildEntity): Long = buildDao.insertBuild(build)

    suspend fun deleteBuild(build: BuildEntity) = buildDao.deleteBuild(build)

    suspend fun deleteBuildById(id: Long) = buildDao.deleteBuildById(id)

    suspend fun enqueueSync(syncKey: String, syncType: String, payload: String): Long {
        val item = SyncQueueEntity(
            syncKey = syncKey,
            syncType = syncType,
            payload = payload,
            queuedTimestamp = System.currentTimeMillis()
        )
        return buildDao.insertSyncItem(item)
    }

    suspend fun updateSyncItem(item: SyncQueueEntity) = buildDao.updateSyncItem(item)

    suspend fun clearCompletedSyncItems() = buildDao.clearCompletedSyncItems()
}
