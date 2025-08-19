package com.guyron.mishloha.di

import android.content.Context
import com.guyron.mishloha.data.local.AppDatabase
import com.guyron.mishloha.data.local.dao.RepositoryDao
import com.guyron.mishloha.data.remote.GitHubApiService
import com.guyron.mishloha.data.repository.FavoritesRepositoryImpl
import com.guyron.mishloha.data.repository.GitHubRepositoryImpl
import com.guyron.mishloha.domain.repository.FavoritesRepository
import com.guyron.mishloha.domain.repository.GitHubRepository
import com.guyron.mishloha.domain.usecase.DecorateListWithFavoritesUseCase
import com.guyron.mishloha.domain.usecase.DecorateWithFavoritesUseCase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(retrofit: Retrofit): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRepositoryDao(database: AppDatabase): RepositoryDao {
        return database.repositoryDao()
    }

    @Provides
    @Singleton
    fun provideGitHubRepository(
        apiService: GitHubApiService,
        repositoryDao: RepositoryDao
    ): GitHubRepository {
        return GitHubRepositoryImpl(apiService, repositoryDao)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(
        repositoryDao: RepositoryDao
    ): FavoritesRepository {
        return FavoritesRepositoryImpl(repositoryDao)
    }

    @Provides
    @Singleton
    fun provideDecorateWithFavoritesUseCase(): DecorateWithFavoritesUseCase = DecorateWithFavoritesUseCase()

    @Provides
    @Singleton
    fun provideDecorateListWithFavoritesUseCase(): DecorateListWithFavoritesUseCase = DecorateListWithFavoritesUseCase()
}
