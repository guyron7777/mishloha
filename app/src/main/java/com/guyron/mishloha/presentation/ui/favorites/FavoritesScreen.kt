package com.guyron.mishloha.presentation.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.presentation.ui.components.AppTopBar
import com.guyron.mishloha.presentation.ui.components.LoadingContent
import com.guyron.mishloha.presentation.ui.components.EmptyContent
import com.guyron.mishloha.presentation.ui.components.ScreenContent
import com.guyron.mishloha.presentation.ui.components.SearchResultsList
import com.guyron.mishloha.presentation.ui.components.RepositoryList
import com.guyron.mishloha.presentation.ui.components.SearchBar
import com.guyron.mishloha.presentation.viewmodels.FavoritesViewModel
import androidx.compose.ui.res.stringResource
import com.guyron.mishloha.R

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
            title = stringResource(R.string.favorites),
            onNavigateBack = onNavigateBack
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )

        ScreenContent(
            isLoading = uiState.isLoading,
            error = uiState.error,
            onDismissError = { viewModel.clearError() }
        ) {
            when {
                searchQuery.isNotBlank() -> {
                    if (isSearching) {
                        LoadingContent()
                    } else {
                        SearchResultsList(
                            repositories = searchResults,
                            onRepositoryClick = onRepositoryClick,
                            onFavoriteClick = { viewModel.removeFromFavorites(it) },
                            emptyTitle = stringResource(R.string.no_results_found),
                            emptyMessage = stringResource(R.string.try_different_search),
                            showAvatar = true,
                            showStats = true
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
            title = stringResource(R.string.no_favorites_yet),
            message = stringResource(R.string.add_repositories_to_favorites)
        )
    } else {
        RepositoryList(
            repositories = repositories,
            onRepositoryClick = onRepositoryClick,
            onFavoriteClick = onRemoveFromFavorites,
            showAvatar = true,
            showStats = true
        )
    }
}






