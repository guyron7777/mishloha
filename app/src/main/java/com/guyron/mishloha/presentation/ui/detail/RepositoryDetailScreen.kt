package com.guyron.mishloha.presentation.ui.detail

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.presentation.ui.components.ErrorContent
import com.guyron.mishloha.presentation.ui.components.LoadingContent
import com.guyron.mishloha.presentation.viewmodels.RepositoryDetailViewModel
import com.guyron.mishloha.R
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri
import androidx.compose.ui.res.stringResource
import com.guyron.mishloha.data.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailScreen(
    repositoryId: Long,
    onNavigateBack: () -> Unit,
    viewModel: RepositoryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(repositoryId) {
        viewModel.loadRepository(repositoryId)
    }

    when {
        uiState.isLoading -> {
            LoadingContent()
        }

        uiState.error != null -> {
            ErrorContent(
                error = uiState.error!!,
                onDismiss = onNavigateBack
            )
            return
        }

        uiState.repository != null -> {
            RepositoryDetailContent(
                repository = uiState.repository!!,
                onNavigateBack = onNavigateBack,
                onToggleFavorite = { repository ->
                    viewModel.toggleFavorite(repository)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepositoryDetailContent(
    repository: Repository,
    onNavigateBack: () -> Unit,
    onToggleFavorite: (Repository) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT, Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(stringResource(R.string.repository_details))
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { onToggleFavorite(repository) }
                ) {
                    Icon(
                        imageVector = if (repository.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (repository.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (repository.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(repository.owner.avatarUrl ?: "")
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.avatar_of, repository.owner.login),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = repository.fullName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.by, repository.owner.login),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (repository.description?.isNotBlank() == true) {
                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = repository.description,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = painterResource(id = R.drawable.ic_star),
                    value = "${repository.stargazersCount}",
                    label = stringResource(R.string.stars)
                )
                StatItem(
                    icon = painterResource(id = R.drawable.ic_fork),
                    value = "${repository.forksCount}",
                    label = stringResource(R.string.forks)
                )
                if (repository.language != null) {
                    StatItem(
                        icon = painterResource(id = R.drawable.ic_language),
                        value = repository.language,
                        label = stringResource(R.string.language)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.created),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dateFormat.format(repository.createdAt),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, repository.htmlUrl.toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = stringResource(R.string.open_in_browser)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.open_on_github))
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: Painter,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
