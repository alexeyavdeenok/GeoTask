package com.geotask.domain.repository

import com.geotask.domain.model.Location
import kotlinx.coroutines.flow.Flow
interface LocationRepository {
    suspend fun insert(location: Location): Long
    suspend fun update(location: Location) // Новый метод
    fun getById(id: Long): Flow<Location?> // Изменено на Flow
    fun getAll(): Flow<List<Location>>
    suspend fun deleteById(id: Long)
}