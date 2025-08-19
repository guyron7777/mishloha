package com.guyron.mishloha.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.guyron.mishloha.domain.models.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DecorateWithFavoritesUseCase @Inject constructor() {
    operator fun invoke(
        pagingDataFlow: Flow<PagingData<Repository>>,
        favoriteIdsFlow: Flow<Set<Long>>
    ): Flow<PagingData<Repository>> {
        return pagingDataFlow.combine(favoriteIdsFlow) { pagingData, favoriteIds ->
            pagingData.map { repo ->
                val isFav = favoriteIds.contains(repo.id)
                if (repo.isFavorite == isFav) repo else repo.copy(isFavorite = isFav)
            }
        }
    }
}

class DecorateListWithFavoritesUseCase @Inject constructor() {
    operator fun invoke(
        repositories: List<Repository>,
        favoriteIds: Set<Long>
    ): List<Repository> {
        if (favoriteIds.isEmpty()) return repositories
        return repositories.map { repo ->
            val isFav = favoriteIds.contains(repo.id)
            if (repo.isFavorite == isFav) repo else repo.copy(isFavorite = isFav)
        }
    }
}


