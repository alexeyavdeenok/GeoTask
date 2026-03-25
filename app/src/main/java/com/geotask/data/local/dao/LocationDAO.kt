package com.geotask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geotask.domain.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location): Long

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: Long): Location?

    @Query("SELECT * FROM locations")
    fun getAll(): Flow<List<Location>>

    @Query("SELECT * FROM locations")
    suspend fun getAllSync(): List<Location>

    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getCount(): Int
}