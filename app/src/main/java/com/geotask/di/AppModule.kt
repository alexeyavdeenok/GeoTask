package com.geotask.di

import android.content.Context
import com.geotask.data.local.AppDatabase
import com.geotask.data.local.dao.LocationDao
import com.geotask.data.local.dao.SettingsDao
import com.geotask.data.local.dao.TaskDao
import com.geotask.data.local.dao.SecureLocationDao
import com.geotask.data.local.dao.SecureSettingsDao
import com.geotask.data.local.dao.SecureTaskDao
import com.geotask.data.repository.LocationRepositoryImpl
import com.geotask.data.repository.SettingsRepositoryImpl
import com.geotask.data.repository.LocalTaskRepository
import com.geotask.data.security.EncryptionManager
import com.geotask.domain.repository.LocationRepository
import com.geotask.domain.repository.SettingsRepository
import com.geotask.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ==================== Database ====================
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context)

    // ==================== Encryption ====================
    @Provides
    @Singleton
    fun provideEncryptionManager(): EncryptionManager =
        EncryptionManager()

    // ==================== DAOs (with Security Layer) ====================
    @Provides
    @Singleton
    fun provideTaskDao(
        db: AppDatabase,
        encryptionManager: EncryptionManager
    ): TaskDao = SecureTaskDao(db.taskDao(), encryptionManager)

    @Provides
    @Singleton
    fun provideLocationDao(
        db: AppDatabase,
        encryptionManager: EncryptionManager
    ): LocationDao = SecureLocationDao(db.locationDao(), encryptionManager)

    @Provides
    @Singleton
    fun provideSettingsDao(
        db: AppDatabase,
        encryptionManager: EncryptionManager
    ): SettingsDao = SecureSettingsDao(db.settingsDao(), encryptionManager)

    // ==================== Repositories ====================
    @Provides
    @Singleton
    fun provideTaskRepository(dao: TaskDao): TaskRepository =
        LocalTaskRepository(dao)

    @Provides
    @Singleton
    fun provideLocationRepository(dao: LocationDao): LocationRepository =
        LocationRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideSettingsRepository(dao: SettingsDao): SettingsRepository =
        SettingsRepositoryImpl(dao)
}