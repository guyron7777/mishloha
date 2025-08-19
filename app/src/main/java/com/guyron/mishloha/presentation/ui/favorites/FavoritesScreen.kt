package com.guyron.mishloha.presentation.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guyron.mishloha.domain.models.Repository
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
        TopAppBar(
            title = { Text("Favorite Repositories") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }
                searchQuery.isNotBlank() -> {
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
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
        EmptyFavoritesContent()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(repositories) { repository ->
                FavoriteRepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onRemoveFromFavorites = onRemoveFromFavorites
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No favorites found",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(repositories) { repository ->
                FavoriteRepositoryItem(
                    repository = repository,
                    onItemClick = onRepositoryClick,
                    onRemoveFromFavorites = onRemoveFromFavorites
                )
            }
        }
    }
}

@Composable
private fun FavoriteRepositoryItem(
    repository: Repository,
    onItemClick: (Repository) -> Unit,
    onRemoveFromFavorites: (Repository) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = repository.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                repository.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
            }
            IconButton(
                onClick = { onRemoveFromFavorites(repository) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoritesContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No favorite repositories yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add repositories to your favorites to see them here",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onDismiss) {
            Text("Dismiss")
        }
    }
}
