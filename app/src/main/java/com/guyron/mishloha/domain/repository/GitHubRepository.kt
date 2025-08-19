package com.guyron.mishloha.domain.repository

import androidx.paging.PagingData
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.models.TimeFrame

import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    fun getTrendingRepositories(timeFrame: TimeFrame): Flow<PagingData<Repository>>
    suspend fun searchRepositories(query: String, timeFrame: TimeFrame): List<Repository>
}
