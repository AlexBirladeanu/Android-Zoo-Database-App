package com.example.zoomies.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zoomies.SplashScreenView
import com.example.zoomies.model.Route
import com.example.zoomies.model.database.AppDatabase
import com.example.zoomies.model.observer.LanguageEventHandler
import com.example.zoomies.view_model.AnimalsViewModel
import com.example.zoomies.view_model.LoginViewModel
import com.example.zoomies.view_model.UsersViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AppNavHost(
    database: AppDatabase,
    navHostController: NavHostController = rememberNavController()
) {

    val languageEventHandler = LanguageEventHandler()
    NavHost(navController = navHostController, startDestination = Route.SPLASH_SCREEN.toString()) {
        composable(Route.SPLASH_SCREEN.toString()) {
            SplashScreenView(
                onTimeLimitReached = {
                    navHostController.navigate(Route.ANIMALS.toString())
                }
            )
        }
        composable(Route.USERS.toString()) {
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
                viewModel = UsersViewModel(languageEventHandler, database, refreshPage = {
                    navHostController.navigate(Route.USERS.toString())
                })
            )
        }
        composable(Route.USER_CREATION.toString()) {
            UserCreationView(
                onScreenClose = {
                    navHostController.navigateUp()
                },
                navigateToLogin = {
                    navHostController.navigate(Route.LOGIN.toString())
                },
                viewModel = UsersViewModel(languageEventHandler, database)
            )
        }
        composable(Route.USER_DETAILS.toString()) {
            UserDetailsView(
                onScreenClose = {
                    navHostController.navigateUp()
                },
                navigateToLogin = {
                    navHostController.navigate(Route.LOGIN.toString())
                },
                viewModel = UsersViewModel(languageEventHandler, database)
            )
        }
        composable(Route.ANIMALS.toString()) {
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
                viewModel = AnimalsViewModel(languageEventHandler, database, refreshPage = {
                    navHostController.navigate(Route.ANIMALS.toString())
                })
            )
        }
        composable(Route.ANIMAL_DETAILS.toString()) {
            AnimalDetailsView(
                onScreenClose = {
                    navHostController.navigateUp()
                },
                navigateToLogin = {
                    navHostController.navigate(Route.LOGIN.toString())
                },
                viewModel = AnimalsViewModel(languageEventHandler, database)
            )
        }
        composable(Route.ANIMAL_CREATION.toString()) {
            AnimalCreationView(
                onScreenClose = {
                    navHostController.navigateUp()
                },
                navigateToLogin = {
                    navHostController.navigate(Route.LOGIN.toString())
                },
                viewModel = AnimalsViewModel(languageEventHandler, database)
            )
        }
        composable(Route.LOGIN.toString()) {
            LoginView(
                onScreenClose = {
                    navHostController.navigate(Route.ANIMALS.toString())
                },
                viewModel = LoginViewModel(database)
            )
        }
        composable(Route.FILTERS.toString()) {
            FiltersView(
                onScreenClose = {
                    navHostController.navigateUp()
                },
                viewModel = AnimalsViewModel(languageEventHandler, database)
            )
        }
    }
}