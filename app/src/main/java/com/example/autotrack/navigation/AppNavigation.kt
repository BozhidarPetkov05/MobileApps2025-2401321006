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
import androidx.navigation.NavBackStackEntry
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
import com.example.autotrack.ui.fuel.FuelFormScreen
import com.example.autotrack.ui.fuel.FuelScreen
import com.example.autotrack.ui.garage.CarFormScreen
import com.example.autotrack.ui.garage.GarageScreen
import com.example.autotrack.ui.maintenance.MaintenanceFormScreen
import com.example.autotrack.ui.maintenance.MaintenanceScreen
import com.example.autotrack.ui.maintenance.ReminderFormScreen
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
                DashboardScreen(
                    onNavigateToAddCar = {
                        navController.navigate(Screen.AddCar.route)
                    }
                )
            }

            composable(Screen.Garage.route) {
                GarageScreen(
                    onNavigateToAddCar = {
                        navController.navigate(Screen.AddCar.route)
                    },
                    onNavigateToEditCar = { carId ->
                        navController.navigate(Screen.EditCar.route.replace("{carId}", carId.toString()))
                    }
                )
            }

            composable(Screen.Maintenance.route) {
                MaintenanceScreen(
                    onNavigateToAddMaintenance = {
                        navController.navigate(Screen.AddMaintenance.route)
                    },
                    onNavigateToEditMaintenance = { maintenanceId ->
                        navController.navigate(Screen.EditMaintenance.route.replace("{maintenanceId}", maintenanceId.toString()))
                    },
                    onNavigateToAddReminder = {
                        navController.navigate(Screen.AddReminder.route)
                    },
                    onNavigateToEditReminder = { reminderId ->
                        navController.navigate(Screen.EditReminder.route.replace("{reminderId}", reminderId.toString()))
                    },
                    onNavigateToAddCar = {
                        navController.navigate(Screen.AddCar.route)
                    }
                )
            }

            composable(Screen.Fuel.route) {
                FuelScreen(
                    onNavigateToAddFuel = {
                        navController.navigate(Screen.AddFuel.route)
                    },
                    onNavigateToEditFuel = { fuelId ->
                        navController.navigate(Screen.EditFuel.route.replace("{fuelId}", fuelId.toString()))
                    },
                    onNavigateToAddCar = {
                        navController.navigate(Screen.AddCar.route)
                    }
                )
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
                CarFormScreen(
                    carId = null,
                    onCarSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.EditCar.route,
                arguments = listOf(
                    navArgument("carId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry: NavBackStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId")
                CarFormScreen(
                    carId = carId,
                    onCarSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AddFuel.route) {
                FuelFormScreen(
                    fuelId = null,
                    onFuelSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.EditFuel.route,
                arguments = listOf(
                    navArgument("fuelId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry: NavBackStackEntry ->
                val fuelId = backStackEntry.arguments?.getInt("fuelId")
                FuelFormScreen(
                    fuelId = fuelId,
                    onFuelSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AddMaintenance.route) {
                MaintenanceFormScreen(
                    maintenanceId = null,
                    onMaintenanceSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.EditMaintenance.route,
                arguments = listOf(
                    navArgument("maintenanceId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry: NavBackStackEntry ->
                val maintenanceId = backStackEntry.arguments?.getInt("maintenanceId")
                MaintenanceFormScreen(
                    maintenanceId = maintenanceId,
                    onMaintenanceSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.AddReminder.route) {
                ReminderFormScreen(
                    reminderId = null,
                    onReminderSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.EditReminder.route,
                arguments = listOf(
                    navArgument("reminderId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry: NavBackStackEntry ->
                val reminderId = backStackEntry.arguments?.getInt("reminderId")
                ReminderFormScreen(
                    reminderId = reminderId,
                    onReminderSaved = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
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
