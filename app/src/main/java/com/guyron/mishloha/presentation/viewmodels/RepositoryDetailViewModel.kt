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
import android.content.Context
import com.guyron.mishloha.R
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val getRepositoryByIdUseCase: GetRepositoryByIdUseCase,
    private val getRepositoryByIdFromServerUseCase: GetRepositoryByIdFromServerUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    @ApplicationContext private val context: Context
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
                            error = context.getString(R.string.repository_not_found),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = context.getString(R.string.failed_to_load_repository, e.message ?: ""),
                    isLoading = false
                )
            }
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
                
                _uiState.value = _uiState.value.copy(
                    repository = _uiState.value.repository?.copy(
                        isFavorite = !repository.isFavorite
                    )
                )
            } catch (e: Exception) {
                val errorMessage = if (repository.isFavorite) {
                    context.getString(R.string.failed_to_remove_from_favorites, e.message ?: "")
                } else {
                    context.getString(R.string.failed_to_add_to_favorites, e.message ?: "")
                }
                _uiState.value = _uiState.value.copy(error = errorMessage)
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
