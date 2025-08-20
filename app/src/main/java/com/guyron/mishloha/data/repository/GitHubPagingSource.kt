package com.guyron.mishloha.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guyron.mishloha.data.remote.GitHubApiService
import com.guyron.mishloha.data.remote.dto.RepositoryDto
import com.guyron.mishloha.domain.models.TimeFrame
import com.guyron.mishloha.data.mapper.toDomain
import com.guyron.mishloha.data.utils.QueryUtils
import com.guyron.mishloha.domain.models.Repository

class GitHubPagingSource(
    private val apiService: GitHubApiService,
    private val timeFrame: TimeFrame,
    private val cache: MutableMap<Long, Repository>
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

            val query = QueryUtils.buildTimeFrameQuery(timeFrame)
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

            response.items.forEach { dto ->
                val repository = dto.toDomain()
                cache[repository.id] = repository
            }

            LoadResult.Page(
                data = response.items,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


}
