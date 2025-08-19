package com.guyron.mishloha.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.guyron.mishloha.domain.models.Owner
import com.guyron.mishloha.domain.models.Repository
import com.guyron.mishloha.presentation.ui.detail.RepositoryDetailScreen
import com.guyron.mishloha.presentation.ui.trending.TrendingRepositoriesScreen
import java.util.Date
import kotlin.text.get

sealed class Screen(val route: String) {
    object TrendingRepositories : Screen("trending_repositories")
    object Favorites : Screen("favorites")
    object RepositoryDetail : Screen("repository_detail/{repoKey}")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.TrendingRepositories.route
) {
    val selectedRepositories = remember { mutableStateMapOf<String, Repository>() }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.TrendingRepositories.route) {
            TrendingRepositoriesScreen(
                onRepositoryClick = { repository ->

                    val repoKey = "selected_repo"
                    selectedRepositories[repoKey] = repository
                    navController.navigate("repository_detail/$repoKey")
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }


        composable(
            route = Screen.RepositoryDetail.route,
            arguments = listOf(
                navArgument("repository") {
                    type = NavType.ParcelableType(Repository::class.java)
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val repoKey = backStackEntry.arguments?.getString("repoKey") ?: ""
            val repository = selectedRepositories[repoKey]
            if (repository != null) {
                RepositoryDetailScreen(
                    repository = repository,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onToggleFavorite = { repo ->
                    }
                )
            } else {
                val errorRepository = Repository(
                    id = 0L,
                    name = "No Repository Data",
                    fullName = "error/no_data",
                    description = "No repository data was passed to this screen",
                    owner = Owner(
                        id = 0,
                        login = "no_data",
                        avatarUrl = null
                    ),
                    stargazersCount = 0,
                    language = null,
                    forksCount = 0,
                    createdAt = Date(),
                    htmlUrl = "https://github.com",
                    isFavorite = false
                )

                RepositoryDetailScreen(
                    repository = errorRepository,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onToggleFavorite = { repo ->
                    }
                )
            }
        }
    }
}
