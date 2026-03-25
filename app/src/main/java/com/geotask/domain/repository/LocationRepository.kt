package com.geotask.domain.repository

import com.geotask.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun insert(location: Location): Long
    suspend fun getById(id: Long): Location?
    fun getAll(): Flow<List<Location>>
    suspend fun getAllSync(): List<Location>
    suspend fun deleteById(id: Long)
}