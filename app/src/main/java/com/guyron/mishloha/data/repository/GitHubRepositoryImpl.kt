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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService,
) : GitHubRepository {

    override fun getTrendingRepositories(timeFrame: TimeFrame): Flow<PagingData<Repository>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                GitHubPagingSource(apiService, timeFrame)
            }
        ).flow.map { pagingData ->
            pagingData.map { dto ->
                dto.toDomain()
            }
        }
    }

    override suspend fun searchRepositories(query: String, timeFrame: TimeFrame): List<Repository> {
        val searchQuery = "$query ${buildTimeFrameQuery(timeFrame)}"
        val response = apiService.searchRepositories(query = searchQuery)
        
        return response.items.map { dto ->
            dto.toDomain()
        }
    }

    private fun buildTimeFrameQuery(timeFrame: TimeFrame): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        val endDate = dateFormat.format(calendar.time)
        
        when (timeFrame) {
            TimeFrame.DAY -> calendar.add(Calendar.DAY_OF_MONTH, -1)
            TimeFrame.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
            TimeFrame.MONTH -> calendar.add(Calendar.MONTH, -1)
        }
        
        val startDate = dateFormat.format(calendar.time)
        
        return "created:$startDate..$endDate"
    }

    override suspend fun getRepositoryById(repositoryId: Long): Repository? {
        return try {
            null
        } catch (_: Exception) {
            null
        }
    }
}
