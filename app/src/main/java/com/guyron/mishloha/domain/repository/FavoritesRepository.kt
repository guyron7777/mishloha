package com.guyron.mishloha.domain.repository

import com.guyron.mishloha.domain.models.Repository
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteRepositories(): Flow<List<Repository>>
    suspend fun addToFavorites(repository: Repository)
    suspend fun removeFromFavorites(repositoryId: Long)
    suspend fun isFavorite(repositoryId: Long): Boolean
    suspend fun searchFavorites(query: String): List<Repository>
    suspend fun getRepositoryById(repositoryId: Long): Repository?
}
