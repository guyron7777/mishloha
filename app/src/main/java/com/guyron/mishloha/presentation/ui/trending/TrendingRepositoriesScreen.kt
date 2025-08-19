package com.guyron.mishloha.presentation.ui.trending

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.presentation.ui.components.AppTopBar
import com.guyron.mishloha.presentation.ui.components.ErrorContent
import com.guyron.mishloha.presentation.ui.components.LoadingContent
import com.guyron.mishloha.presentation.ui.components.LoadingItem
import com.guyron.mishloha.presentation.ui.components.ScreenContent
import com.guyron.mishloha.presentation.ui.components.SearchResultsList
import com.guyron.mishloha.presentation.ui.components.RepositoryItem
import com.guyron.mishloha.presentation.ui.components.SearchBar
import com.guyron.mishloha.presentation.ui.components.TimeFrameSelector
import com.guyron.mishloha.presentation.viewmodels.TrendingRepositoriesViewModel
import androidx.compose.ui.res.stringResource
import com.guyron.mishloha.R
import com.guyron.mishloha.data.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingRepositoriesScreen(
    onRepositoryClick: (Repository) -> Unit,
    onNavigateToFavorites: () -> Unit,
    viewModel: TrendingRepositoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val repositories = viewModel.repositories.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = stringResource(R.string.trending_repositories),
            actions = {
                IconButton(onClick = onNavigateToFavorites) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.favorites_icon)
                    )
                }
            }
        )
        TimeFrameSelector(
            selectedTimeFrame = selectedTimeFrame,
            onTimeFrameSelected = { viewModel.selectTimeFrame(it) }
        )
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )
        ScreenContent(
            isLoading = uiState.isLoading,
            error = uiState.error,
            onRetry = { viewModel.selectTimeFrame(selectedTimeFrame) },
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
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            emptyTitle = stringResource(R.string.no_results_found),
                            emptyMessage = stringResource(R.string.try_different_search)
                        )
                    }
                }
                else -> {
                    TrendingRepositoriesList(
                        repositories = repositories,
                        onRepositoryClick = onRepositoryClick,
                        onFavoriteClick = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendingRepositoriesList(
    repositories: LazyPagingItems<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onFavoriteClick: (Repository) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            count = repositories.itemCount,
            key = repositories.itemKey { it.id }
        ) { index ->
            repositories[index]?.let { repository ->
                RepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }

        repositories.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        LoadingItem()
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        LoadingItem()
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    item {
                        ErrorContent(
                            error = (loadState.refresh as LoadState.Error).error.message ?: Constants.DEFAULT_ERROR_MESSAGE,
                            onRetry = { retry() },
                            onDismiss = { }
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    item {
                        ErrorContent(
                            error = (loadState.append as LoadState.Error).error.message ?: Constants.DEFAULT_ERROR_MESSAGE,
                            onRetry = { retry() },
                            onDismiss = { }
                        )
                    }
                }
            }
        }
    }
}




