package com.guyron.mishloha.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.usecase.GetFavoriteRepositoriesUseCase
import com.guyron.mishloha.domain.usecase.RemoveFromFavoritesUseCase
import com.guyron.mishloha.domain.usecase.SearchFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import com.guyron.mishloha.R
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteRepositoriesUseCase: GetFavoriteRepositoriesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val searchFavoritesUseCase: SearchFavoritesUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Repository>>(emptyList())
    val searchResults: StateFlow<List<Repository>> = _searchResults.asStateFlow()

    init {
        loadFavorites()
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

    fun removeFromFavorites(repository: Repository) {
        viewModelScope.launch {
            try {
                removeFromFavoritesUseCase(repository.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = context.getString(R.string.failed_to_remove_from_favorites, e.message ?: "")
                )
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                getFavoriteRepositoriesUseCase()
                    .collect { favorites ->
                        _uiState.value = _uiState.value.copy(
                            favorites = favorites,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = context.getString(R.string.failed_to_load_favorites, e.message ?: "")
                )
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            try {
                _isSearching.value = true
                val results = searchFavoritesUseCase(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = context.getString(R.string.search_failed, e.message ?: "")
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

data class FavoritesUiState(
    val favorites: List<Repository> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
