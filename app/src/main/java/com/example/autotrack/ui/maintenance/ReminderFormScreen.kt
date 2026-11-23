package com.example.autotrack.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autotrack.data.entity.Reminder
import com.example.autotrack.viewmodel.MaintenanceViewModel

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
    var titleError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var existingRecord by remember { mutableStateOf<Reminder?>(null) }

    LaunchedEffect(reminderId) {
        if (reminderId != null && reminderId > 0) {
            val record = maintenanceViewModel.getReminderById(reminderId)
            record?.let {
                existingRecord = it
                title = it.title
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
                        titleError = if (it.isBlank()) "Reminder title is required" else null
                    },
                    label = { Text("Reminder Title (e.g. Annual Inspection)") },
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) Text(titleError!!, color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        titleError = if (title.isBlank()) "Reminder title is required" else null

                        if (selectedCarId == null || selectedCarId!! <= 0) {
                            errorMessage = "Please select a car on the dashboard first."
                        } else if (titleError == null) {
                            if (existingRecord != null) {
                                val updated = existingRecord!!.copy(
                                    title = title.trim()
                                )
                                maintenanceViewModel.updateReminder(updated) {
                                    onReminderSaved()
                                }
                            } else {
                                val newRecord = Reminder(
                                    carId = selectedCarId!!,
                                    title = title.trim(),
                                    dueDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
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
}
