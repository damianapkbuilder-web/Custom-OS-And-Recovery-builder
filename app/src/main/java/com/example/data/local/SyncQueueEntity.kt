package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val syncKey: String,
    val syncType: String,
    val payload: String,
    val queuedTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val lastAttempt: Long = 0,
    val retryCount: Int = 0
)
