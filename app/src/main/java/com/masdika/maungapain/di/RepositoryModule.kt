package com.masdika.maungapain.di

import com.masdika.maungapain.data.repository.TaskRepositoryImpl
import com.masdika.maungapain.data.repository.`interface`.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}