package com.example.autotrack.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autotrack.ui.auth.LoginScreen
import com.example.autotrack.ui.auth.RegisterScreen
import com.example.autotrack.ui.components.BottomNavigationBar
import com.example.autotrack.ui.dashboard.DashboardScreen
import com.example.autotrack.ui.settings.SettingsScreen
import com.example.autotrack.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val startDestination = if (authViewModel.currentUser == null) Screen.Login.route else Screen.Dashboard.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Garage.route,
        Screen.Maintenance.route,
        Screen.Fuel.route,
        Screen.Settings.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) },
            popEnterTransition = { fadeIn(animationSpec = tween(150)) },
            popExitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }

            composable(Screen.Garage.route) {
                PlaceholderScreen("My Garage")
            }

            composable(Screen.Maintenance.route) {
                PlaceholderScreen("Maintenance")
            }

            composable(Screen.Fuel.route) {
                PlaceholderScreen("Fuel")
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route)
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route)
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route)
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.CarDetails.route,
                arguments = listOf(
                    navArgument("carId") {
                        type = NavType.IntType
                    }
                )
            ) {
                PlaceholderScreen("Car Details")
            }

            composable(Screen.AddCar.route) {
                PlaceholderScreen("Add Car")
            }

            composable(
                route = Screen.EditCar.route,
                arguments = listOf(
                    navArgument("carId") {
                        type = NavType.IntType
                    }
                )
            ) {
                PlaceholderScreen("Edit Car")
            }

            composable(Screen.AddFuel.route) {
                PlaceholderScreen("Add Fuel")
            }

            composable(
                route = Screen.EditFuel.route,
                arguments = listOf(
                    navArgument("fuelId") {
                        type = NavType.IntType
                    }
                )
            ) {
                PlaceholderScreen("Edit Fuel")
            }

            composable(Screen.AddMaintenance.route) {
                PlaceholderScreen("Add Maintenance")
            }

            composable(
                route = Screen.EditMaintenance.route,
                arguments = listOf(
                    navArgument("maintenanceId") {
                        type = NavType.IntType
                    }
                )
            ) {
                PlaceholderScreen("Edit Maintenance")
            }

            composable(Screen.AddReminder.route) {
                PlaceholderScreen("Add Reminder")
            }

            composable(
                route = Screen.EditReminder.route,
                arguments = listOf(
                    navArgument("reminderId") {
                        type = NavType.IntType
                    }
                )
            ) {
                PlaceholderScreen("Edit Reminder")
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
