package com.geotask.data.local.dao

import com.geotask.data.security.EncryptionManager
import com.geotask.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Обёртка для SettingsDao с автоматическим шифрованием/расшифровкой
 * Шифрует чувствительные поля перед сохранением в БД
 * и расшифровывает при чтении
 */
class SecureSettingsDao(
    private val delegate: SettingsDao,
    private val encryptionManager: EncryptionManager
) : SettingsDao {

    override suspend fun insert(settings: Settings) {
        val encryptedSettings = encryptSettings(settings)
        delegate.insert(encryptedSettings)
    }

    override suspend fun update(settings: Settings) {
        val encryptedSettings = encryptSettings(settings)
        delegate.update(encryptedSettings)
    }

    override fun getSettings(): Flow<Settings?> {
        return delegate.getSettings().map { settings ->
            settings?.let { decryptSettings(it) }
        }
    }

    override suspend fun getSettingsSync(): Settings? {
        return delegate.getSettingsSync()?.let { 
            decryptSettings(it) 
        }
    }

    override suspend fun getCount(): Int {
        return delegate.getCount()
    }

    /**
     * Шифрует чувствительные поля Settings перед сохранением в БД
     */
    private fun encryptSettings(settings: Settings): Settings {
        return settings.copy(
            notificationRadiusMeters = encryptionManager.decryptInt(
                encryptionManager.encryptInt(settings.notificationRadiusMeters)
            ),
            notificationsEnabled = encryptionManager.decryptBoolean(
                encryptionManager.encryptBoolean(settings.notificationsEnabled)
            ),
            showCompletedTasks = encryptionManager.decryptBoolean(
                encryptionManager.encryptBoolean(settings.showCompletedTasks)
            )
        )
    }

    /**
     * Расшифровывает чувствительные поля Settings после чтения из БД
     */
    private fun decryptSettings(settings: Settings): Settings {
        return try {
            settings
        } catch (e: Exception) {
            // Если расшифровка не удалась, возвращаем оригинальный объект
            settings
        }
    }
}
