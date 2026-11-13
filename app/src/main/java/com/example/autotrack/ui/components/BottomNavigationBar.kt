package com.example.autotrack.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.autotrack.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        Triple(Screen.Dashboard.route, "Dashboard", Icons.Default.Home),
        Triple(Screen.Garage.route, "My Garage", Icons.Default.DirectionsCar),
        Triple(Screen.Maintenance.route, "Maintenance", Icons.Default.Build),
        Triple(Screen.Fuel.route, "Fuel", Icons.Default.LocalGasStation),
        Triple(Screen.Settings.route, "Settings", Icons.Default.Settings)
    )

    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(
                            navController.graph.startDestinationId
                        ) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                }
            )
        }
    }
}
