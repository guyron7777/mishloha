package com.guyron.mishloha.presentation.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.presentation.ui.components.AppTopBar
import com.guyron.mishloha.presentation.ui.components.ErrorContent
import com.guyron.mishloha.presentation.ui.components.LoadingContent
import com.guyron.mishloha.presentation.ui.components.EmptyContent
import com.guyron.mishloha.presentation.ui.components.RepositoryItem
import com.guyron.mishloha.presentation.ui.components.SearchBar
import com.guyron.mishloha.presentation.viewmodels.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onRepositoryClick: (Repository) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Favorite Repositories",
            onNavigateBack = onNavigateBack
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }

                searchQuery.isNotBlank() -> {
                    if (isSearching) {
                        LoadingContent()
                    } else {
                        SearchResultsList(
                            repositories = searchResults,
                            onRepositoryClick = onRepositoryClick,
                            onRemoveFromFavorites = { viewModel.removeFromFavorites(it) }
                        )
                    }
                }

                else -> {
                    FavoritesList(
                        repositories = uiState.favorites,
                        onRepositoryClick = onRepositoryClick,
                        onRemoveFromFavorites = { viewModel.removeFromFavorites(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesList(
    repositories: List<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onRemoveFromFavorites: (Repository) -> Unit
) {
    if (repositories.isEmpty()) {
        EmptyContent(
            title = "No favorite repositories yet",
            message = "Add repositories to your favorites to see them here"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(repositories) { repository ->
                RepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onFavoriteClick = onRemoveFromFavorites,
                    showAvatar = true,
                    showStats = true
                )
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    repositories: List<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onRemoveFromFavorites: (Repository) -> Unit
) {
    if (repositories.isEmpty()) {
        EmptyContent(
            title = "No favorites found",
            message = "Try adjusting your search criteria"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(repositories) { repository ->
                RepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onFavoriteClick = onRemoveFromFavorites,
                    showAvatar = true,
                    showStats = true
                )
            }
        }
    }
}




