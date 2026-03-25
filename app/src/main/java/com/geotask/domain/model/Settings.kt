package com.geotask.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1,                    // всегда будет только одна запись с id = 1

    val notificationRadiusMeters: Int = 500,
    val notificationsEnabled: Boolean = true,
    val showCompletedTasks: Boolean = false
)