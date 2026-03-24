package com.geotask.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
    val status: Status = Status.ACTIVE
)

enum class Status { ACTIVE, COMPLETED, DEFERRED }