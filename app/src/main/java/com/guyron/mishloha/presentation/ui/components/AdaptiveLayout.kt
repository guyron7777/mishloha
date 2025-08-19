package com.guyron.mishloha.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guyron.mishloha.domain.models.Repository
import androidx.core.net.toUri
import android.content.Intent
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import java.text.SimpleDateFormat
import java.util.*
import com.guyron.mishloha.R

@Composable
fun AdaptiveLayout(
    windowSizeClass: WindowSizeClass,
    trendingContent: @Composable () -> Unit,
    detailContent: @Composable (Repository?) -> Unit,
    selectedRepository: Repository?,
    onRepositorySelected: (Repository) -> Unit
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    trendingContent()
                }
                TabletDivider()
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    detailContent(selectedRepository)
                }
            }
        }

        else -> {
            trendingContent()
        }
    }
}

@Composable
fun TabletDetailPanel(
    repository: Repository?,
    onNavigateBack: () -> Unit,
    onToggleFavorite: (Repository) -> Unit,
    modifier: Modifier = Modifier
) {
    if (repository != null) {
        TabletRepositoryDetailContent(
            repository = repository,
            onNavigateBack = onNavigateBack,
            onToggleFavorite = onToggleFavorite,
            modifier = modifier
        )
    } else {
        EmptyDetailPanel(modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabletRepositoryDetailContent(
    repository: Repository,
    onNavigateBack: () -> Unit,
    onToggleFavorite: (Repository) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Repository Details") },
            actions = {
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
                    contentDescription = "Avatar of ${repository.owner.login}",
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
                        text = "by ${repository.owner.login}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (repository.description?.isNotBlank() == true) {
                Text(
                    text = "Description",
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
                    label = "Stars"
                )
                StatItem(
                    icon = painterResource(id = R.drawable.ic_fork),
                    value = "${repository.forksCount}",
                    label = "Forks"
                )
                if (repository.language != null) {
                    StatItem(
                        icon = painterResource(id = R.drawable.ic_language),
                        value = repository.language,
                        label = "Language"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Created",
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
                    contentDescription = "Open in browser"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open on GitHub")
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

@Composable
private fun EmptyDetailPanel(
    modifier: Modifier = Modifier
) {
    TabletEmptyState(
        title = "Select a repository",
        message = "Choose a repository from the list to view its details",
        modifier = modifier
    )
}
