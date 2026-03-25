package com.geotask.data.repository

import com.geotask.data.local.dao.LocationDao
import com.geotask.domain.model.Location
import com.geotask.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {

    override suspend fun insert(location: Location): Long =
        locationDao.insert(location)

    override suspend fun getById(id: Long): Location? =
        locationDao.getById(id)

    override fun getAll(): Flow<List<Location>> =
        locationDao.getAll()

    override suspend fun getAllSync(): List<Location> =
        locationDao.getAllSync()

    override suspend fun deleteById(id: Long) =
        locationDao.deleteById(id)
}