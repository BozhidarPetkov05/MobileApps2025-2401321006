package com.example.autotrack.ui.garage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autotrack.data.entity.Car
import com.example.autotrack.viewmodel.CarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarFormScreen(
    onCarAdded: () -> Unit,
    onNavigateBack: () -> Unit,
    carViewModel: CarViewModel = viewModel()
) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("Gasoline") }
    var power by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fuelTypes = listOf("Gasoline", "Diesel", "Electric", "Hybrid", "LPG", "CNG")
    var fuelDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Add Vehicle") },
                actions = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Cancel")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = { Text("Make (e.g. Toyota, BMW)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model (e.g. Corolla, X5)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year (e.g. 2020)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = { Text("License Plate") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Current Mileage (km)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Fuel Type Dropdown Menu
                ExposedDropdownMenuBox(
                    expanded = fuelDropdownExpanded,
                    onExpandedChange = { fuelDropdownExpanded = !fuelDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = fuelType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fuel Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = fuelDropdownExpanded,
                        onDismissRequest = { fuelDropdownExpanded = false }
                    ) {
                        fuelTypes.forEach { fuel ->
                            DropdownMenuItem(
                                text = { Text(fuel) },
                                onClick = {
                                    fuelType = fuel
                                    fuelDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = power,
                    onValueChange = { power = it },
                    label = { Text("Power (e.g. 150 HP)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vin,
                    onValueChange = { vin = it },
                    label = { Text("VIN (Vehicle Identification Number)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val yearInt = year.toIntOrNull()
                        val mileageInt = mileage.toIntOrNull()
                        if (make.isBlank() || model.isBlank() || yearInt == null || licensePlate.isBlank() || mileageInt == null) {
                            errorMessage = "Please fill in Make, Model, Year, License Plate, and Mileage with valid numbers."
                        } else {
                            val newCar = Car(
                                make = make.trim(),
                                model = model.trim(),
                                year = yearInt,
                                licensePlate = licensePlate.trim(),
                                mileage = mileageInt,
                                fuelType = fuelType.trim(),
                                power = power.trim(),
                                vin = vin.trim()
                            )
                            carViewModel.addCar(newCar) {
                                onCarAdded()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Save Vehicle", style = MaterialTheme.typography.titleMedium)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
