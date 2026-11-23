package com.example.autotrack.ui.fuel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autotrack.data.entity.FuelRecord
import com.example.autotrack.viewmodel.FuelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelFormScreen(
    fuelId: Int? = null,
    onFuelSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    fuelViewModel: FuelViewModel = viewModel()
) {
    val selectedCarId by fuelViewModel.selectedCarId.collectAsState()

    var odometer by remember { mutableStateOf("") }
    var liters by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("Gasoline") }
    var price by remember { mutableStateOf("") }

    // Validation error states
    var odometerError by remember { mutableStateOf<String?>(null) }
    var litersError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fuelTypes = listOf("Gasoline", "Diesel", "Electric", "Hybrid", "LPG", "CNG")
    var fuelDropdownExpanded by remember { mutableStateOf(false) }

    var existingRecord by remember { mutableStateOf<FuelRecord?>(null) }

    LaunchedEffect(fuelId) {
        if (fuelId != null && fuelId > 0) {
            val record = fuelViewModel.getFuelRecordById(fuelId)
            record?.let {
                existingRecord = it
                odometer = it.odometer.toString()
                liters = it.liters.toString()
                fuelType = it.fuelType.ifBlank { "Gasoline" }
                price = it.price.toString()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(if (existingRecord != null) "Edit Refueling" else "Add Refueling") },
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
                    value = odometer,
                    onValueChange = {
                        if (it.length <= 9) {
                            odometer = it
                            odometerError = when {
                                it.isBlank() -> "Odometer reading is required"
                                it.toIntOrNull() == null -> "Please enter a valid numeric odometer"
                                it.toInt() < 0 -> "Odometer cannot be negative"
                                else -> null
                            }
                        }
                    },
                    label = { Text("Odometer (km)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = odometerError != null,
                    supportingText = {
                        if (odometerError != null) {
                            Text(odometerError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = liters,
                    onValueChange = {
                        if (it.length <= 8) {
                            liters = it
                            litersError = when {
                                it.isBlank() -> "Litres amount is required"
                                it.toDoubleOrNull() == null -> "Please enter a valid number for litres"
                                it.toDouble() <= 0.0 -> "Litres must be greater than 0"
                                else -> null
                            }
                        }
                    },
                    label = { Text("Litres") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = litersError != null,
                    supportingText = {
                        if (litersError != null) {
                            Text(litersError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
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
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
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
                    value = price,
                    onValueChange = {
                        if (it.length <= 8) {
                            price = it
                            priceError = when {
                                it.isBlank() -> "Price is required"
                                it.toDoubleOrNull() == null -> "Please enter a valid number for price"
                                it.toDouble() < 0.0 -> "Price cannot be negative"
                                else -> null
                            }
                        }
                    },
                    label = { Text("Price (€)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = priceError != null,
                    supportingText = {
                        if (priceError != null) {
                            Text(priceError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
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
                        // Validate all fields
                        odometerError = when {
                            odometer.isBlank() -> "Odometer reading is required"
                            odometer.toIntOrNull() == null -> "Please enter a valid numeric odometer"
                            odometer.toInt() < 0 -> "Odometer cannot be negative"
                            else -> null
                        }
                        litersError = when {
                            liters.isBlank() -> "Litres amount is required"
                            liters.toDoubleOrNull() == null -> "Please enter a valid number for litres"
                            liters.toDouble() <= 0.0 -> "Litres must be greater than 0"
                            else -> null
                        }
                        priceError = when {
                            price.isBlank() -> "Price is required"
                            price.toDoubleOrNull() == null -> "Please enter a valid number for price"
                            price.toDouble() < 0.0 -> "Price cannot be negative"
                            else -> null
                        }

                        if (selectedCarId == null || selectedCarId!! <= 0) {
                            errorMessage = "Please select a car on the dashboard first."
                        } else if (odometerError == null && litersError == null && priceError == null) {
                            val odomInt = odometer.toInt()
                            val litersDouble = liters.toDouble()
                            val priceDouble = price.toDouble()

                            if (existingRecord != null) {
                                val updated = existingRecord!!.copy(
                                    odometer = odomInt,
                                    liters = litersDouble,
                                    fuelType = fuelType.trim(),
                                    price = priceDouble
                                )
                                fuelViewModel.updateFuelRecord(updated) {
                                    onFuelSaved()
                                }
                            } else {
                                val newRecord = FuelRecord(
                                    carId = selectedCarId!!,
                                    date = System.currentTimeMillis(),
                                    odometer = odomInt,
                                    liters = litersDouble,
                                    fuelType = fuelType.trim(),
                                    price = priceDouble
                                )
                                fuelViewModel.addFuelRecord(newRecord) {
                                    onFuelSaved()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(if (existingRecord != null) "Update Refueling" else "Save Refueling", style = MaterialTheme.typography.titleMedium)
                }

                if (existingRecord != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            fuelViewModel.deleteFuelRecord(existingRecord!!) {
                                onFuelSaved()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC5221F),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Delete Record", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
