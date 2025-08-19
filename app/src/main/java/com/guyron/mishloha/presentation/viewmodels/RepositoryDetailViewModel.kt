package com.guyron.mishloha.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.domain.usecase.AddToFavoritesUseCase
import com.guyron.mishloha.domain.usecase.GetRepositoryByIdFromServerUseCase
import com.guyron.mishloha.domain.usecase.GetRepositoryByIdUseCase
import com.guyron.mishloha.domain.usecase.RemoveFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val getRepositoryByIdUseCase: GetRepositoryByIdUseCase,
    private val getRepositoryByIdFromServerUseCase: GetRepositoryByIdFromServerUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoryDetailUiState())
    val uiState: StateFlow<RepositoryDetailUiState> = _uiState.asStateFlow()

    fun loadRepository(repositoryId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val localRepository = getRepositoryByIdUseCase(repositoryId)
                
                if (localRepository != null) {
                    _uiState.value = _uiState.value.copy(
                        repository = localRepository,
                        isLoading = false,
                        dataSource = DataSource.LOCAL
                    )
                } else {
                    val serverRepository = getRepositoryByIdFromServerUseCase(repositoryId)
                    if (serverRepository != null) {
                        _uiState.value = _uiState.value.copy(
                            repository = serverRepository,
                            isLoading = false,
                            dataSource = DataSource.SERVER
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "Repository not found locally or on server",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load repository: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun toggleFavorite(repository: Repository) {
        viewModelScope.launch {
            try {
                if (repository.isFavorite) {
                    removeFromFavoritesUseCase(repository.id)
                } else {
                    addToFavoritesUseCase(repository)
                }
                
                _uiState.value = _uiState.value.copy(
                    repository = _uiState.value.repository?.copy(
                        isFavorite = !repository.isFavorite
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to ${if (repository.isFavorite) "remove from" else "add to"} favorites: ${e.message}"
                )
            }
        }
    }
}

data class RepositoryDetailUiState(
    val repository: Repository? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val dataSource: DataSource = DataSource.UNKNOWN
)

enum class DataSource {
    LOCAL,
    SERVER,
    UNKNOWN
}
