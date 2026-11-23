package com.example.autotrack.ui.maintenance

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
import com.example.autotrack.data.entity.Maintenance
import com.example.autotrack.viewmodel.MaintenanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFormScreen(
    maintenanceId: Int? = null,
    onMaintenanceSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    maintenanceViewModel: MaintenanceViewModel = viewModel()
) {
    val selectedCarId by maintenanceViewModel.selectedCarId.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var odometerError by remember { mutableStateOf<String?>(null) }
    var costError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var existingRecord by remember { mutableStateOf<Maintenance?>(null) }

    LaunchedEffect(maintenanceId) {
        if (maintenanceId != null && maintenanceId > 0) {
            val record = maintenanceViewModel.getMaintenanceById(maintenanceId)
            record?.let {
                existingRecord = it
                title = it.title
                description = it.description.orEmpty()
                odometer = it.odometer.toString()
                cost = it.cost.toString()
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
                title = { Text(if (existingRecord != null) "Edit Maintenance" else "Add Maintenance") },
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
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = if (it.isBlank()) "Service name is required" else null
                    },
                    label = { Text("Service Name (e.g. Oil Change)") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) Text(titleError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = odometer,
                    onValueChange = {
                        odometer = it
                        odometerError = when {
                            it.isBlank() -> "Odometer reading is required"
                            it.toIntOrNull() == null -> "Please enter a valid numeric odometer"
                            it.toInt() < 0 -> "Odometer cannot be negative"
                            else -> null
                        }
                    },
                    label = { Text("Odometer (km)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = odometerError != null,
                    supportingText = {
                        if (odometerError != null) Text(odometerError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cost,
                    onValueChange = {
                        cost = it
                        costError = when {
                            it.isBlank() -> "Cost is required"
                            it.toDoubleOrNull() == null -> "Please enter a valid number for cost"
                            it.toDouble() < 0.0 -> "Cost cannot be negative"
                            else -> null
                        }
                    },
                    label = { Text("Cost (€)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = costError != null,
                    supportingText = {
                        if (costError != null) Text(costError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        titleError = if (title.isBlank()) "Service name is required" else null
                        odometerError = when {
                            odometer.isBlank() -> "Odometer reading is required"
                            odometer.toIntOrNull() == null -> "Please enter a valid numeric odometer"
                            odometer.toInt() < 0 -> "Odometer cannot be negative"
                            else -> null
                        }
                        costError = when {
                            cost.isBlank() -> "Cost is required"
                            cost.toDoubleOrNull() == null -> "Please enter a valid number for cost"
                            cost.toDouble() < 0.0 -> "Cost cannot be negative"
                            else -> null
                        }

                        if (selectedCarId == null || selectedCarId!! <= 0) {
                            errorMessage = "Please select a car on the dashboard first."
                        } else if (titleError == null && odometerError == null && costError == null) {
                            val odomInt = odometer.toInt()
                            val costDouble = cost.toDouble()

                            if (existingRecord != null) {
                                val updated = existingRecord!!.copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    odometer = odomInt,
                                    cost = costDouble
                                )
                                maintenanceViewModel.updateMaintenance(updated) {
                                    onMaintenanceSaved()
                                }
                            } else {
                                val newRecord = Maintenance(
                                    carId = selectedCarId!!,
                                    title = title.trim(),
                                    description = description.trim(),
                                    odometer = odomInt,
                                    cost = costDouble,
                                    date = System.currentTimeMillis()
                                )
                                maintenanceViewModel.addMaintenance(newRecord) {
                                    onMaintenanceSaved()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(if (existingRecord != null) "Update Maintenance" else "Save Maintenance", style = MaterialTheme.typography.titleMedium)
                }

                if (existingRecord != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            maintenanceViewModel.deleteMaintenance(existingRecord!!) {
                                onMaintenanceSaved()
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
                        Text("Delete Maintenance", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
