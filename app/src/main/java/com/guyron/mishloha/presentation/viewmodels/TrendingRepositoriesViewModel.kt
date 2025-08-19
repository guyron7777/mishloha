package com.guyron.mishloha.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.models.TimeFrame
import com.guyron.mishloha.domain.usecase.AddToFavoritesUseCase
import com.guyron.mishloha.domain.usecase.GetTrendingRepositoriesUseCase
import com.guyron.mishloha.domain.usecase.IsFavoriteUseCase
import com.guyron.mishloha.domain.usecase.RemoveFromFavoritesUseCase
import com.guyron.mishloha.domain.usecase.SearchRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingRepositoriesViewModel @Inject constructor(
    private val getTrendingRepositoriesUseCase: GetTrendingRepositoriesUseCase,
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
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

    private val _repositories = MutableStateFlow<PagingData<Repository>>(PagingData.empty())
    val repositories: Flow<PagingData<Repository>> = _repositories.asStateFlow()

    init {
        loadTrendingRepositories()
    }

    fun selectTimeFrame(timeFrame: TimeFrame) {
        _selectedTimeFrame.value = timeFrame
        loadTrendingRepositories()
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
                loadTrendingRepositories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update favorite: ${e.message}"
                )
            }
        }
    }

    private fun loadTrendingRepositories() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                getTrendingRepositoriesUseCase(_selectedTimeFrame.value)
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _repositories.value = pagingData
                        _uiState.value = _uiState.value.copy(
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load repositories: ${e.message}"
                )
            }
        }
    }

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
