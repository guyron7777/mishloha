package com.guyron.mishloha.presentation.ui.trending

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.guyron.mishloha.presentation.ui.components.*
import com.guyron.mishloha.presentation.viewmodels.TrendingRepositoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletTrendingScreen(
    onRepositoryClick: (Repository) -> Unit,
    onNavigateToFavorites: () -> Unit,
    selectedRepository: Repository?,
    onRepositorySelected: (Repository) -> Unit,
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
        TopAppBar(
            title = { Text("Trending Repositories") },
            actions = {
                IconButton(onClick = onNavigateToFavorites) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorites"
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
                        TabletSearchResultsList(
                            repositories = searchResults,
                            onRepositoryClick = { repo ->
                                onRepositorySelected(repo)
                                onRepositoryClick(repo)
                            },
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            selectedRepository = selectedRepository
                        )
                    }
                }

                else -> {
                    TabletTrendingRepositoriesList(
                        repositories = repositories,
                        onRepositoryClick = { repo ->
                            onRepositorySelected(repo)
                            onRepositoryClick(repo)
                        },
                        onFavoriteClick = { viewModel.toggleFavorite(it) },
                        selectedRepository = selectedRepository
                    )
                }
            }
        }
    }
}

@Composable
private fun TabletTrendingRepositoriesList(
    repositories: LazyPagingItems<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onFavoriteClick: (Repository) -> Unit,
    selectedRepository: Repository?
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
                TabletRepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onFavoriteClick = onFavoriteClick,
                    isSelected = selectedRepository?.id == repository.id
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
                            error = (loadState.refresh as LoadState.Error).error.message
                                ?: "Unknown error",
                            onRetry = { retry() },
                            onDismiss = { }
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    item {
                        ErrorContent(
                            error = (loadState.append as LoadState.Error).error.message
                                ?: "Unknown error",
                            onRetry = { retry() },
                            onDismiss = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabletSearchResultsList(
    repositories: List<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onFavoriteClick: (Repository) -> Unit,
    selectedRepository: Repository?
) {
    if (repositories.isEmpty()) {
        EmptyContent(
            title = "No repositories found",
            message = "Try adjusting your search criteria"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(repositories) { repository ->
                TabletRepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onFavoriteClick = onFavoriteClick,
                    isSelected = selectedRepository?.id == repository.id
                )
            }
        }
    }
}

@Composable
private fun TabletRepositoryItem(
    repository: Repository,
    onItemClick: (Repository) -> Unit,
    onFavoriteClick: (Repository) -> Unit,
    isSelected: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        RepositoryItem(
            repository = repository,
            onItemClick = onItemClick,
            onFavoriteClick = onFavoriteClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
