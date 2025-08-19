package com.guyron.mishloha.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.guyron.mishloha.presentation.ui.detail.RepositoryDetailScreen
import com.guyron.mishloha.presentation.ui.favorites.FavoritesScreen
import com.guyron.mishloha.presentation.ui.trending.TrendingRepositoriesScreen


sealed class Screen(val route: String) {
    object TrendingRepositories : Screen("trending_repositories")
    object Favorites : Screen("favorites")
    object RepositoryDetail : Screen("repository_detail/{repositoryId}")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.TrendingRepositories.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.TrendingRepositories.route) {
            TrendingRepositoriesScreen(
                onRepositoryClick = { repository ->
                    navController.navigate("repository_detail/${repository.id}")
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onRepositoryClick = { repository ->
                    navController.navigate("repository_detail/${repository.id}")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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
