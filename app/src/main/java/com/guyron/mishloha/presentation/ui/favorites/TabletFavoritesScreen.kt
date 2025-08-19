package com.guyron.mishloha.presentation.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.presentation.ui.components.*
import com.guyron.mishloha.presentation.viewmodels.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletFavoritesScreen(
    onRepositoryClick: (Repository) -> Unit,
    onNavigateBack: () -> Unit,
    selectedRepository: Repository?,
    onRepositorySelected: (Repository) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Favorite Repositories") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
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
                        TabletFavoritesSearchResultsList(
                            repositories = searchResults,
                            onRepositoryClick = { repo ->
                                onRepositorySelected(repo)
                                onRepositoryClick(repo)
                            },
                            onRemoveFromFavorites = { viewModel.removeFromFavorites(it) },
                            selectedRepository = selectedRepository
                        )
                    }
                }

                else -> {
                    TabletFavoritesList(
                        repositories = uiState.favorites,
                        onRepositoryClick = { repo ->
                            onRepositorySelected(repo)
                            onRepositoryClick(repo)
                        },
                        onRemoveFromFavorites = { viewModel.removeFromFavorites(it) },
                        selectedRepository = selectedRepository
                    )
                }
            }
        }
    }
}

@Composable
private fun TabletFavoritesList(
    repositories: List<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onRemoveFromFavorites: (Repository) -> Unit,
    selectedRepository: Repository?
) {
    if (repositories.isEmpty()) {
        EmptyContent(
            title = "No favorites yet",
            message = "Add repositories to your favorites to see them here"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(repositories) { repository ->
                TabletFavoriteRepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onRemoveFromFavorites = onRemoveFromFavorites,
                    isSelected = selectedRepository?.id == repository.id
                )
            }
        }
    }
}

@Composable
private fun TabletFavoritesSearchResultsList(
    repositories: List<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onRemoveFromFavorites: (Repository) -> Unit,
    selectedRepository: Repository?
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
                TabletFavoriteRepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onRemoveFromFavorites = onRemoveFromFavorites,
                    isSelected = selectedRepository?.id == repository.id
                )
            }
        }
    }
}

@Composable
private fun TabletFavoriteRepositoryItem(
    repository: Repository,
    onItemClick: (Repository) -> Unit,
    onRemoveFromFavorites: (Repository) -> Unit,
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
            onFavoriteClick = { onRemoveFromFavorites(it) },
            showAvatar = true,
            showStats = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
