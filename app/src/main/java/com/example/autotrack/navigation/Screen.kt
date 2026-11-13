package com.example.autotrack.navigation

sealed class Screen(val route: String) {

    // Authentication
    data object Login : Screen("login")
    data object Register : Screen("register")

    // Main screens
    data object Dashboard : Screen("dashboard")
    data object Garage : Screen("garage")
    data object Maintenance : Screen("maintenance")
    data object Fuel : Screen("fuel")
    data object Settings : Screen("settings")

    // Additional screens
    data object CarDetails : Screen("car_details/{carId}") {
        fun createRoute(carId: Int) = "car_details/$carId"
    }

    data object AddCar : Screen("add_car")
    data object EditCar : Screen("edit_car/{carId}")

    data object AddFuel : Screen("add_fuel")
    data object EditFuel : Screen("edit_fuel/{fuelId}")

    data object AddMaintenance : Screen("add_maintenance")
    data object EditMaintenance : Screen("edit_maintenance/{maintenanceId}")

    data object AddReminder : Screen("add_reminder")
    data object EditReminder : Screen("edit_reminder/{reminderId}")
}
