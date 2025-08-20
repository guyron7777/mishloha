package com.guyron.mishloha.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map

import com.guyron.mishloha.data.mapper.toDomain
import com.guyron.mishloha.data.remote.GitHubApiService
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.models.TimeFrame
import com.guyron.mishloha.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.guyron.mishloha.data.Constants
import com.guyron.mishloha.data.utils.QueryUtils

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService,
) : GitHubRepository {
    private val trendingRepositoriesCache = mutableMapOf<Long, Repository>()

    override fun getTrendingRepositories(timeFrame: TimeFrame): Flow<PagingData<Repository>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = Constants.PREFETCH_DISTANCE
            ),
            pagingSourceFactory = {
                GitHubPagingSource(apiService, timeFrame, trendingRepositoriesCache)
            }
        ).flow.map { pagingData ->
            pagingData.map { dto ->
                dto.toDomain()
            }
        }
    }

    override suspend fun searchRepositories(query: String, timeFrame: TimeFrame): List<Repository> {
        val searchQuery = "$query ${QueryUtils.buildTimeFrameQuery(timeFrame)}"
        val response = apiService.searchRepositories(query = searchQuery)
        
        return response.items.map { dto ->
            dto.toDomain()
        }
    }

    override suspend fun getRepositoryById(repositoryId: Long): Repository? {
        return try {
            trendingRepositoriesCache[repositoryId]
        } catch (_: Exception) {
            null
        }
    }
}
