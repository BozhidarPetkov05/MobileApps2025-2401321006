package com.example.autotrack.ui.maintenance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autotrack.data.entity.Reminder
import com.example.autotrack.viewmodel.MaintenanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderFormScreen(
    reminderId: Int? = null,
    onReminderSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    maintenanceViewModel: MaintenanceViewModel = viewModel()
) {
    val selectedCarId by maintenanceViewModel.selectedCarId.collectAsState()

    var title by remember { mutableStateOf("") }
    var dueMileage by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isCompleted by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var dueMileageError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var existingRecord by remember { mutableStateOf<Reminder?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LaunchedEffect(reminderId) {
        if (reminderId != null && reminderId > 0) {
            val record = maintenanceViewModel.getReminderById(reminderId)
            record?.let {
                existingRecord = it
                title = it.title
                dueMileage = it.dueMileage?.toString().orEmpty()
                dueDate = it.dueDate
                isCompleted = it.isCompleted
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
                title = { Text(if (existingRecord != null) "Edit Reminder" else "Add Reminder") },
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
                        titleError = if (it.isBlank()) "Reminder name is required" else null
                    },
                    label = { Text("Reminder Name (e.g. Oil Change)") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) Text(titleError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueMileage,
                    onValueChange = {
                        dueMileage = it
                        dueMileageError = when {
                            it.isNotBlank() && it.toIntOrNull() == null -> "Please enter a valid numeric mileage"
                            it.isNotBlank() && it.toInt() < 0 -> "Mileage cannot be negative"
                            else -> null
                        }
                    },
                    label = { Text("Due Mileage (km, Optional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = dueMileageError != null,
                    supportingText = {
                        if (dueMileageError != null) Text(dueMileageError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date Picker Field
                OutlinedTextField(
                    value = dateFormat.format(Date(dueDate)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Due Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // Is Completed toggle only on edit screen
                if (existingRecord != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mark as Completed", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it }
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        titleError = if (title.isBlank()) "Reminder name is required" else null
                        dueMileageError = when {
                            dueMileage.isNotBlank() && dueMileage.toIntOrNull() == null -> "Please enter a valid numeric mileage"
                            dueMileage.isNotBlank() && dueMileage.toInt() < 0 -> "Mileage cannot be negative"
                            else -> null
                        }

                        if (selectedCarId == null || selectedCarId!! <= 0) {
                            errorMessage = "Please select a car on the dashboard first."
                        } else if (titleError == null && dueMileageError == null) {
                            val mileageInt = dueMileage.toIntOrNull()

                            if (existingRecord != null) {
                                val updated = existingRecord!!.copy(
                                    title = title.trim(),
                                    dueDate = dueDate,
                                    dueMileage = mileageInt,
                                    isCompleted = isCompleted
                                )
                                maintenanceViewModel.updateReminder(updated) {
                                    onReminderSaved()
                                }
                            } else {
                                val newRecord = Reminder(
                                    carId = selectedCarId!!,
                                    title = title.trim(),
                                    dueDate = dueDate,
                                    dueMileage = mileageInt,
                                    isCompleted = false
                                )
                                maintenanceViewModel.addReminder(newRecord) {
                                    onReminderSaved()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(if (existingRecord != null) "Update Reminder" else "Save Reminder", style = MaterialTheme.typography.titleMedium)
                }

                if (existingRecord != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            maintenanceViewModel.deleteReminder(existingRecord!!) {
                                onReminderSaved()
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
                        Text("Delete Reminder", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dueDate = it }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
