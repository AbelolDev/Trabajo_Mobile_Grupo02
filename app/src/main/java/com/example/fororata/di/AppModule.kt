package com.example.fororata.di

import com.example.fororata.api.RetrofitInstance
import com.example.fororata.api.service.*
import com.example.fororata.api.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePublicationApiService(): PublicationApiService {
        return RetrofitInstance.publicationApiService
    }

    @Provides
    @Singleton
    fun provideCommentaryApiService(): CommentaryApiService {
        return RetrofitInstance.commentaryApiService
    }

    @Provides
    @Singleton
    fun provideUserApiService(): UserApiService {
        return RetrofitInstance.userApiService
    }

    @Provides
    @Singleton
    fun provideUserRepository(userApiService: UserApiService): UserRepository {
        return UserRepository(userApiService)
    }
}