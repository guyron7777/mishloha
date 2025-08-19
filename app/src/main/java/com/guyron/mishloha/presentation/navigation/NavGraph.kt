package com.guyron.mishloha.presentation.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.presentation.ui.components.AdaptiveLayout
import com.guyron.mishloha.presentation.ui.components.TabletDetailPanel
import com.guyron.mishloha.presentation.ui.detail.RepositoryDetailScreen
import com.guyron.mishloha.presentation.ui.favorites.FavoritesScreen
import com.guyron.mishloha.presentation.ui.favorites.TabletFavoritesScreen
import com.guyron.mishloha.presentation.ui.trending.TabletTrendingScreen
import com.guyron.mishloha.presentation.ui.trending.TrendingRepositoriesScreen


sealed class Screen(val route: String) {
    object TrendingRepositories : Screen("trending_repositories")
    object Favorites : Screen("favorites")
    object RepositoryDetail : Screen("repository_detail/{repositoryId}")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    startDestination: String = Screen.TrendingRepositories.route
) {
    var selectedRepository by remember { mutableStateOf<Repository?>(null) }
    
    val isTablet = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.TrendingRepositories.route) {
            if (isTablet) {
                AdaptiveLayout(
                    windowSizeClass = windowSizeClass,
                    trendingContent = {
                        TabletTrendingScreen(
                            onRepositoryClick = { repository ->
                                selectedRepository = repository
                            },
                            onNavigateToFavorites = {
                                navController.navigate(Screen.Favorites.route)
                            },
                            selectedRepository = selectedRepository,
                            onRepositorySelected = { repository ->
                                selectedRepository = repository
                            }
                        )
                    },
                    detailContent = { repository ->
                        TabletDetailPanel(
                            repository = repository,
                            onNavigateBack = { /* No back needed on tablet */ },
                            onToggleFavorite = { repo ->
                            }
                        )
                    },
                    selectedRepository = selectedRepository,
                    onRepositorySelected = { repository ->
                        selectedRepository = repository
                    }
                )
            } else {
                TrendingRepositoriesScreen(
                    onRepositoryClick = { repository ->
                        navController.navigate("repository_detail/${repository.id}")
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    }
                )
            }
        }

        composable(Screen.Favorites.route) {
            if (isTablet) {
                AdaptiveLayout(
                    windowSizeClass = windowSizeClass,
                    trendingContent = {
                        TabletFavoritesScreen(
                            onRepositoryClick = { repository ->
                                selectedRepository = repository
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            selectedRepository = selectedRepository,
                            onRepositorySelected = { repository ->
                                selectedRepository = repository
                            }
                        )
                    },
                    detailContent = { repository ->
                        TabletDetailPanel(
                            repository = repository,
                            onNavigateBack = { /* No back needed on tablet */ },
                            onToggleFavorite = { repo ->
                            }
                        )
                    },
                    selectedRepository = selectedRepository,
                    onRepositorySelected = { repository ->
                        selectedRepository = repository
                    }
                )
            } else {
                FavoritesScreen(
                    onRepositoryClick = { repository ->
                        navController.navigate("repository_detail/${repository.id}")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            route = Screen.RepositoryDetail.route,
            arguments = listOf(
                navArgument("repositoryId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val repositoryId = backStackEntry.arguments?.getLong("repositoryId") ?: 0L
            RepositoryDetailScreen(
                repositoryId = repositoryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
