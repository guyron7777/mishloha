package com.guyron.mishloha.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guyron.mishloha.data.remote.GitHubApiService
import com.guyron.mishloha.data.remote.dto.RepositoryDto
import com.guyron.mishloha.domain.models.TimeFrame
import java.text.SimpleDateFormat
import java.util.*

class GitHubPagingSource(
    private val apiService: GitHubApiService,
    private val timeFrame: TimeFrame
) : PagingSource<Int, RepositoryDto>() {

    override fun getRefreshKey(state: PagingState<Int, RepositoryDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepositoryDto> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val query = buildSearchQuery(timeFrame)
            val response = apiService.searchRepositories(
                query = query,
                page = page,
                perPage = pageSize
            )

            val nextKey = if (response.items.isEmpty()) {
                null
            } else {
                page + 1
            }

            val prevKey = if (page == 1) null else page - 1

            LoadResult.Page(
                data = response.items,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun buildSearchQuery(timeFrame: TimeFrame): String {
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
}
