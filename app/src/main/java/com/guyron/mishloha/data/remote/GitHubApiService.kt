package com.guyron.mishloha.data.remote

import com.guyron.mishloha.data.remote.dto.RepositoryDto
import com.guyron.mishloha.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchResponseDto

    @GET("trending")
    suspend fun getTrendingRepositories(
        @Query("since") since: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<RepositoryDto>
}
