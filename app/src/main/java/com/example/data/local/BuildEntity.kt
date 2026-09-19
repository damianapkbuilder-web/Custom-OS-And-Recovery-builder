package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "build_history")
data class BuildEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val buildUuid: String,
    val timestamp: Long = System.currentTimeMillis(),
    val targetType: String,
    val softwareName: String,
    val softwareVersion: String,
    val deviceName: String,
    val deviceCodename: String,
    val targetArch: String,
    val archDowngradeLabel: String,
    val featuresSummary: String,
    val zipFileName: String,
    val zipFilePath: String,
    val fileSizeBytes: Long,
    val md5Checksum: String,
    val status: String,
    val buildLog: String
)
