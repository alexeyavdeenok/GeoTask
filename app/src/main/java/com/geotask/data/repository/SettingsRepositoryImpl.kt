package com.geotask.data.repository

import com.geotask.data.local.dao.SettingsDao
import com.geotask.domain.model.Settings
import com.geotask.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override suspend fun save(settings: Settings) {
        settingsDao.insert(settings)
    }

    override fun getSettings(): Flow<Settings> {
        return settingsDao.getSettings()
            .map { it ?: Settings() }   // если ещё нет записи — возвращаем дефолтную
    }

    override suspend fun getSettingsSync(): Settings {
        return settingsDao.getSettingsSync() ?: Settings()
    }

    override suspend fun update(settings: Settings) {
        settingsDao.update(settings)
    }
}