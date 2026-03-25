package com.geotask.domain.repository

import com.geotask.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun save(settings: Settings)
    fun getSettings(): Flow<Settings>
    suspend fun getSettingsSync(): Settings
    suspend fun update(settings: Settings)
}