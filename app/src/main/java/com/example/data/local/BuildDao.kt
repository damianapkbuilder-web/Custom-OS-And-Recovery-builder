package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildDao {
    @Query("SELECT * FROM build_history ORDER BY timestamp DESC")
    fun getAllBuilds(): Flow<List<BuildEntity>>

    @Query("SELECT * FROM build_history WHERE id = :id LIMIT 1")
    fun getBuildById(id: Long): Flow<BuildEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuild(build: BuildEntity): Long

    @Delete
    suspend fun deleteBuild(build: BuildEntity)

    @Query("DELETE FROM build_history WHERE id = :id")
    suspend fun deleteBuildById(id: Long)

    @Query("SELECT * FROM offline_sync_queue ORDER BY queuedTimestamp ASC")
    fun getAllSyncQueue(): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM offline_sync_queue WHERE isSynced = 0 ORDER BY queuedTimestamp ASC")
    fun getPendingSyncItems(): Flow<List<SyncQueueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(item: SyncQueueEntity): Long

    @Update
    suspend fun updateSyncItem(item: SyncQueueEntity)

    @Delete
    suspend fun deleteSyncItem(item: SyncQueueEntity)

    @Query("DELETE FROM offline_sync_queue WHERE isSynced = 1")
    suspend fun clearCompletedSyncItems()
}
