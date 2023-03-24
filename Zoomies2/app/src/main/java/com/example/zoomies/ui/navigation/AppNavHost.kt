package com.example.zoomies.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zoomies.SplashScreenView
import com.example.zoomies.database.AppDatabase
import com.example.zoomies.ui.components.AnimalCreationView
import com.example.zoomies.ui.components.AnimalDetailsView
import com.example.zoomies.ui.components.AnimalsView
import com.example.zoomies.ui.components.FiltersView
import com.example.zoomies.ui.view_model.AnimalsViewModel
import com.example.zoomies.ui.components.LoginView
import com.example.zoomies.ui.view_model.LoginViewModel
import com.example.zoomies.ui.components.UserCreationView
import com.example.zoomies.ui.components.UserDetailsView
import com.example.zoomies.ui.components.UsersView
import com.example.zoomies.ui.view_model.UsersViewModel
import com.example.zoomies.ui.theme.ZoomiesTheme

@Composable
fun AppNavHost(
    database: AppDatabase,
    navHostController: NavHostController = rememberNavController()
) {
    NavHost(navController = navHostController, startDestination = Route.SPLASH_SCREEN.toString()) {
        composable(Route.SPLASH_SCREEN.toString()) {
            ZoomiesTheme {
                SplashScreenView(
                    onTimeLimitReached = {
                        navHostController.navigate(Route.ANIMALS.toString())
                    }
                )
            }
        }
        composable(Route.USERS.toString()) {
            ZoomiesTheme {
                UsersView(
                    navigateToAnimals = {
                        navHostController.navigate(Route.ANIMALS.toString())
                    },
                    navigateToUserCreation = {
                        navHostController.navigate(Route.USER_CREATION.toString())
                    },
                    navigateToUserDetails = {
                        navHostController.navigate(Route.USER_DETAILS.toString())
                    },
                    navigateToLogin = {
                        navHostController.navigate(Route.LOGIN.toString())
                    },
                    viewModel = UsersViewModel(database)
                )
            }
        }
        composable(Route.USER_CREATION.toString()) {
            ZoomiesTheme {
                UserCreationView(
                    onScreenClose = {
                        navHostController.navigateUp()
                    },
                    navigateToLogin = {
                        navHostController.navigate(Route.LOGIN.toString())
                    },
                    viewModel = UsersViewModel(database)
                )
            }
        }
        composable(Route.USER_DETAILS.toString()) {
            ZoomiesTheme {
                UserDetailsView(
                    onScreenClose = {
                        navHostController.navigateUp()
                    },
                    navigateToLogin = {
                        navHostController.navigate(Route.LOGIN.toString())
                    },
                    viewModel = UsersViewModel(database)
                )
            }
        }
        composable(Route.ANIMALS.toString()) {
            ZoomiesTheme {
                AnimalsView(
                    navigateToAnimalCreation = {
                        navHostController.navigate(Route.ANIMAL_CREATION.toString())
                    },
                    navigateToAnimalDetails = {
                        navHostController.navigate(Route.ANIMAL_DETAILS.toString())
                    },
                    navigateToLogin = {
                        navHostController.navigate(Route.LOGIN.toString())
                    },
                    navigateToUsers = {
                        navHostController.navigate(Route.USERS.toString())
                    },
                    navigateToFilters = {
                        navHostController.navigate(Route.FILTERS.toString())
                    },
                    viewModel = AnimalsViewModel(database)
                )
            }
        }
        composable(Route.ANIMAL_DETAILS.toString()) {
            ZoomiesTheme {
                AnimalDetailsView(
                    onScreenClose = {
                        navHostController.navigateUp()
                    },
                    navigateToLogin = {
                        navHostController.navigate(Route.LOGIN.toString())
                    },
                    viewModel = AnimalsViewModel(database)
                )
            }
        }
        composable(Route.ANIMAL_CREATION.toString()) {
            ZoomiesTheme {
                AnimalCreationView(
                    onScreenClose = {
                        navHostController.navigateUp()
                    },
                    navigateToLogin = {
                        navHostController.navigate(Route.LOGIN.toString())
                    },
                    viewModel = AnimalsViewModel(database)
                )
            }
        }
        composable(Route.LOGIN.toString()) {
            ZoomiesTheme {
                LoginView(
                    onScreenClose = {
                        navHostController.navigate(Route.ANIMALS.toString())
                    },
                    viewModel = LoginViewModel(database)
                )
            }
        }
        composable(Route.FILTERS.toString()) {
            ZoomiesTheme {
                FiltersView(
                    onScreenClose = {
                        navHostController.navigateUp()
                    },
                    viewModel = AnimalsViewModel(database)
                )
            }
        }
    }
}