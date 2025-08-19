package com.guyron.mishloha.data.repository

import com.guyron.mishloha.data.local.dao.RepositoryDao
import com.guyron.mishloha.data.mapper.toDomain
import com.guyron.mishloha.data.mapper.toEntity
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val repositoryDao: RepositoryDao
) : FavoritesRepository {

    override fun getFavoriteRepositories(): Flow<List<Repository>> {
        return repositoryDao.getFavoriteRepositories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addToFavorites(repository: Repository) {
        val entity = repository.toEntity()
        repositoryDao.insertRepository(entity)
        repositoryDao.addToFavorites(repository.id, Date())
    }

    override suspend fun removeFromFavorites(repositoryId: Long) {
        repositoryDao.removeFromFavorites(repositoryId)
    }

    override suspend fun isFavorite(repositoryId: Long): Boolean {
        return repositoryDao.isFavorite(repositoryId) ?: false
    }

    override suspend fun searchFavorites(query: String): List<Repository> {
        return repositoryDao.searchFavorites(query).map { it.toDomain() }
    }
}
