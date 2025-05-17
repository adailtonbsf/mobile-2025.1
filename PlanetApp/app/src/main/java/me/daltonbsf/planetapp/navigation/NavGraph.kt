package me.daltonbsf.planetapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.daltonbsf.planetapp.models.planetList
import me.daltonbsf.planetapp.ui.components.BottomNavigationBar
import me.daltonbsf.planetapp.ui.screens.DetailsScreen
import me.daltonbsf.planetapp.ui.screens.FavoritesScreen
import me.daltonbsf.planetapp.ui.screens.HomeScreen

@ExperimentalMaterial3Api
@Composable
fun NavGraph(
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomBarScreen.Home.route) {
                HomeScreen(
                    onPlanetSelected = { planet ->
                        navController.navigate("details/${planet.id}")
                    },
                    onSettingsClick = onSettingsClick,
                    onHelpClick = onHelpClick
                )
            }
            composable(BottomBarScreen.Favorites.route) {
                FavoritesScreen(
                    onPlanetSelected = { planet ->
                        navController.navigate("details/${planet.id}")
                    },
                    onFavoriteToggle = { planet ->
                        planet.isFavorite = !planet.isFavorite
                    }
                )
            }
            composable("details/{planetId}") { backStackEntry ->
                val planetName = backStackEntry.arguments?.getString("planetName")
                val selectedPlanet = planetList.first { it.name == planetName }
                DetailsScreen(planet = selectedPlanet)
            }
        }
    }
}