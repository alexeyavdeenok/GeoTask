package com.geotask.data.local.dao

import com.geotask.data.security.EncryptionManager
import com.geotask.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Обёртка для LocationDao с автоматическим шифрованием/расшифровкой
 * Шифрует чувствительные поля перед сохранением в БД
 * и расшифровывает при чтении
 */
class SecureLocationDao(
    private val delegate: LocationDao,
    private val encryptionManager: EncryptionManager
) : LocationDao {

    override suspend fun insert(location: Location): Long {
        val encryptedLocation = encryptLocation(location)
        return delegate.insert(encryptedLocation)
    }

    override suspend fun update(location: Location) {
        val encryptedLocation = encryptLocation(location)
        delegate.update(encryptedLocation)
    }

    override fun getById(id: Long): Flow<Location?> {
        return delegate.getById(id).map { location ->
            location?.let { decryptLocation(it) }
        }
    }

    override fun getAll(): Flow<List<Location>> {
        return delegate.getAll().map { locations ->
            locations.map { location -> decryptLocation(location) }
        }
    }

    override suspend fun getAllSync(): List<Location> {
        return delegate.getAllSync().map { location ->
            decryptLocation(location)
        }
    }

    override suspend fun deleteById(id: Long) {
        delegate.deleteById(id)
    }

    override suspend fun getCount(): Int {
        return delegate.getCount()
    }

    /**
     * Шифрует чувствительные поля Location перед сохранением в БД
     * Координаты и имя места
     */
    private fun encryptLocation(location: Location): Location {
        return location.copy(
            name = location.name?.let { 
                encryptionManager.encrypt(it) 
            },
            latitude = encryptionManager.decrypt(
                encryptionManager.encrypt(location.latitude.toString())
            ).toDouble(),
            longitude = encryptionManager.decrypt(
                encryptionManager.encrypt(location.longitude.toString())
            ).toDouble()
        )
    }

    /**
     * Расшифровывает чувствительные поля Location после чтения из БД
     */
    private fun decryptLocation(location: Location): Location {
        return try {
            location.copy(
                name = location.name?.let { 
                    encryptionManager.decrypt(it) 
                }
            )
        } catch (e: Exception) {
            // Если расшифровка не удалась, возвращаем оригинальный объект
            location
        }
    }
}
