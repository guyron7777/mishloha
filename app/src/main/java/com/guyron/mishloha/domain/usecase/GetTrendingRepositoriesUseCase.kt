package com.guyron.mishloha.domain.usecase

import androidx.paging.PagingData
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.models.TimeFrame
import com.guyron.mishloha.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrendingRepositoriesUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    operator fun invoke(timeFrame: TimeFrame): Flow<PagingData<Repository>> {
        return gitHubRepository.getTrendingRepositories(timeFrame)
    }
}
