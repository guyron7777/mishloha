package com.guyron.mishloha.domain.usecase

import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.models.TimeFrame
import com.guyron.mishloha.domain.repository.GitHubRepository
import javax.inject.Inject

class SearchRepositoriesUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(query: String, timeFrame: TimeFrame): List<Repository> {
        return gitHubRepository.searchRepositories(query, timeFrame)
    }
}
