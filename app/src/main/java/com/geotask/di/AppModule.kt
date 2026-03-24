package com.geotask.di

import android.content.Context
import com.geotask.data.local.AppDatabase
import com.geotask.data.local.TaskDao
import com.geotask.data.repository.LocalTaskRepository
import com.geotask.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun provideDatabase(@ApplicationContext context: Context) = AppDatabase.getDatabase(context)
    @Provides fun provideDao(db: AppDatabase): TaskDao = db.taskDao()
    @Provides @Singleton fun provideRepository(dao: TaskDao): TaskRepository = LocalTaskRepository(dao)
}