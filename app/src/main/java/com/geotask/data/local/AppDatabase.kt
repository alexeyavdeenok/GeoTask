package com.geotask.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.geotask.data.local.dao.LocationDao
import com.geotask.data.local.dao.SettingsDao
import com.geotask.data.local.dao.TaskDao
import com.geotask.domain.model.Location
import com.geotask.domain.model.Settings
import com.geotask.domain.model.Task

@Database(
    entities = [
        Task::class,
        Location::class,
        Settings::class
    ],
    version = 1,                    // Пока версия 1
    exportSchema = false            // Для разработки можно false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun taskDao(): TaskDao
    abstract fun locationDao(): LocationDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "geotask_database"
                )
                    // Миграцию убрали, как ты просил
                    // .fallbackToDestructiveMigration() // можно добавить на время разработки
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}