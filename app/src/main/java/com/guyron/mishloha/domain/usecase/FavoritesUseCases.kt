package com.guyron.mishloha.domain.usecase

import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteRepositoriesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<Repository>> {
        return favoritesRepository.getFavoriteRepositories()
    }
}

class AddToFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(repository: Repository) {
        favoritesRepository.addToFavorites(repository)
    }
}

class RemoveFromFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(repositoryId: Long) {
        favoritesRepository.removeFromFavorites(repositoryId)
    }
}

class IsFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(repositoryId: Long): Boolean {
        return favoritesRepository.isFavorite(repositoryId)
    }
}

class SearchFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(query: String): List<Repository> {
        return favoritesRepository.searchFavorites(query)
    }
}
