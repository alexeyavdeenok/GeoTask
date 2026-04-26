package com.geotask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.geotask.domain.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location): Long

    // НОВОЕ: Метод для обновления существующих данных (например, названия)
    @Update
    suspend fun update(location: Location)

    // ИСПРАВЛЕНО: Убран suspend, добавлен Flow.
    // Теперь база сама будет уведомлять ViewModel, если данные локации изменятся.
    @Query("SELECT * FROM locations WHERE id = :id")
    fun getById(id: Long): Flow<Location?>

    @Query("SELECT * FROM locations")
    fun getAll(): Flow<List<Location>>

    @Query("SELECT * FROM locations")
    suspend fun getAllSync(): List<Location>

    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getCount(): Int
}