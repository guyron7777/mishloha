package com.guyron.mishloha.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.models.TimeFrame
import com.guyron.mishloha.domain.usecase.AddToFavoritesUseCase
import com.guyron.mishloha.domain.usecase.DecorateWithFavoritesUseCase
import com.guyron.mishloha.domain.usecase.GetTrendingRepositoriesUseCase
import com.guyron.mishloha.domain.usecase.GetFavoriteRepositoriesUseCase
import com.guyron.mishloha.domain.usecase.RemoveFromFavoritesUseCase
import com.guyron.mishloha.domain.usecase.SearchRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingRepositoriesViewModel @Inject constructor(
    private val getTrendingRepositoriesUseCase: GetTrendingRepositoriesUseCase,
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val getFavoriteRepositoriesUseCase: GetFavoriteRepositoriesUseCase,
    private val decorateWithFavoritesUseCase: DecorateWithFavoritesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingRepositoriesUiState())
    val uiState: StateFlow<TrendingRepositoriesUiState> = _uiState.asStateFlow()

    private val _selectedTimeFrame = MutableStateFlow(TimeFrame.DAY)
    val selectedTimeFrame: StateFlow<TimeFrame> = _selectedTimeFrame.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Repository>>(emptyList())
    val searchResults: StateFlow<List<Repository>> = _searchResults.asStateFlow()

    private val favoriteIds: StateFlow<Set<Long>> = getFavoriteRepositoriesUseCase()
        .map { repos -> repos.map { it.id }.toSet() }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    @OptIn(ExperimentalCoroutinesApi::class)
    val repositories: Flow<PagingData<Repository>> = selectedTimeFrame
        .flatMapLatest { timeFrame ->
            getTrendingRepositoriesUseCase(timeFrame)
                .cachedIn(viewModelScope)
        }
        .let { pagingFlow ->
            decorateWithFavoritesUseCase(pagingFlow, favoriteIds)
        }

    init { }

    fun selectTimeFrame(timeFrame: TimeFrame) {
        _selectedTimeFrame.value = timeFrame
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _isSearching.value = false
            _searchResults.value = emptyList()
        } else {
            performSearch(query)
        }
    }

    fun toggleFavorite(repository: Repository) {
        viewModelScope.launch {
            try {
                if (repository.isFavorite) {
                    removeFromFavoritesUseCase(repository.id)
                } else {
                    addToFavoritesUseCase(repository)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update favorite: ${e.message}"
                )
            }
        }
    }

    private fun loadTrendingRepositories() { }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            try {
                _isSearching.value = true
                val results = searchRepositoriesUseCase(query, _selectedTimeFrame.value)
                _searchResults.value = results
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Search failed: ${e.message}"
                )
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class TrendingRepositoriesUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
