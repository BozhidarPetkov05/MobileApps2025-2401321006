package com.example.autotrack.ui.garage

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.autotrack.data.database.AppDatabase
import com.example.autotrack.data.entity.Car
import com.example.autotrack.data.repository.CarRepository
import com.example.autotrack.viewmodel.CarViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarFormScreen(
    carId: Int? = null,
    onCarSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    carViewModel: CarViewModel = viewModel()
) {
    val context = LocalContext.current

    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("Gasoline") }
    var power by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }

    // Validation error states
    var makeError by remember { mutableStateOf<String?>(null) }
    var modelError by remember { mutableStateOf<String?>(null) }
    var yearError by remember { mutableStateOf<String?>(null) }
    var licensePlateError by remember { mutableStateOf<String?>(null) }
    var mileageError by remember { mutableStateOf<String?>(null) }

    var existingCar by remember { mutableStateOf<Car?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(carId) {
        if (carId != null && carId > 0) {
            val db = AppDatabase.getDatabase(context)
            val car = CarRepository(db.carDao()).getCarById(carId)
            car?.let {
                existingCar = it
                make = it.make
                model = it.model
                year = it.year.toString()
                licensePlate = it.licensePlate
                mileage = it.mileage.toString()
                fuelType = it.fuelType.ifBlank { "Gasoline" }
                power = it.power
                vin = it.vin
                imagePath = it.imageUrl
            }
        }
    }

    val fuelTypes = listOf("Gasoline", "Diesel", "Electric", "Hybrid", "LPG", "CNG")
    var fuelDropdownExpanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imagePath = saveImageToInternalStorage(context, it)
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
                title = { Text(if (existingCar != null) "Edit Vehicle" else "Add Vehicle") },
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
                // Image Picker Box / Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!imagePath.isNullOrBlank()) {
                        AsyncImage(
                            model = imagePath,
                            contentDescription = "Car Photo Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add Vehicle Photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TextButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(if (imagePath == null) "Choose Photo" else "Change Photo")
                    }
                }

                OutlinedTextField(
                    value = make,
                    onValueChange = {
                        if (it.length <= 30) {
                            make = it
                            makeError = if (it.isBlank()) "Vehicle make cannot be empty" else null
                        }
                    },
                    label = { Text("Make (e.g. Toyota, BMW)") },
                    singleLine = true,
                    isError = makeError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (makeError != null) {
                                Text(makeError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Text("${make.length}/30", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = {
                        if (it.length <= 30) {
                            model = it
                            modelError = if (it.isBlank()) "Vehicle model cannot be empty" else null
                        }
                    },
                    label = { Text("Model (e.g. Corolla, X5)") },
                    singleLine = true,
                    isError = modelError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (modelError != null) {
                                Text(modelError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Text("${model.length}/30", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = year,
                    onValueChange = {
                        if (it.length <= 4) {
                            year = it
                            yearError = when {
                                it.isBlank() -> "Manufacturing year is required"
                                it.toIntOrNull() == null -> "Please enter a valid numeric year"
                                it.toInt() !in 1900..2100 -> "Year must be between 1900 and 2100"
                                else -> null
                            }
                        }
                    },
                    label = { Text("Year (e.g. 2020)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = yearError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (yearError != null) {
                                Text(yearError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Text("${year.length}/4", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = licensePlate,
                    onValueChange = {
                        if (it.length <= 15) {
                            licensePlate = it
                            licensePlateError = if (it.isBlank()) "License plate cannot be empty" else null
                        }
                    },
                    label = { Text("License Plate") },
                    singleLine = true,
                    isError = licensePlateError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (licensePlateError != null) {
                                Text(licensePlateError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Text("${licensePlate.length}/15", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mileage,
                    onValueChange = {
                        if (it.length <= 9) {
                            mileage = it
                            mileageError = when {
                                it.isBlank() -> "Current mileage is required"
                                it.toIntOrNull() == null -> "Please enter a valid numeric mileage"
                                it.toInt() < 0 -> "Mileage cannot be a negative number"
                                else -> null
                            }
                        }
                    },
                    label = { Text("Current Mileage (km)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = mileageError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (mileageError != null) {
                                Text(mileageError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Text("${mileage.length}/9", style = MaterialTheme.typography.bodySmall)
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
                    value = power,
                    onValueChange = {
                        if (it.length <= 5 && it.all { char -> char.isDigit() }) {
                            power = it
                        }
                    },
                    label = { Text("Power (HP)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text("${power.length}/5", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vin,
                    onValueChange = {
                        if (it.length <= 17) {
                            vin = it
                        }
                    },
                    label = { Text("VIN (Vehicle Identification Number)") },
                    singleLine = true,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text("${vin.length}/17", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            // Validate all fields
                            makeError = if (make.isBlank()) "Vehicle make cannot be empty" else null
                            modelError = if (model.isBlank()) "Vehicle model cannot be empty" else null
                            yearError = when {
                                year.isBlank() -> "Manufacturing year is required"
                                year.toIntOrNull() == null -> "Please enter a valid numeric year"
                                year.toInt() !in 1900..2100 -> "Year must be between 1900 and 2100"
                                else -> null
                            }
                            licensePlateError = if (licensePlate.isBlank()) "License plate cannot be empty" else null
                            mileageError = when {
                                mileage.isBlank() -> "Current mileage is required"
                                mileage.toIntOrNull() == null -> "Please enter a valid numeric mileage"
                                mileage.toInt() < 0 -> "Mileage cannot be a negative number"
                                else -> null
                            }

                            if (makeError == null && modelError == null && yearError == null && licensePlateError == null && mileageError == null) {
                                val yearInt = year.toInt()
                                val mileageInt = mileage.toInt()

                                if (existingCar != null) {
                                    val updatedCar = existingCar!!.copy(
                                        make = make.trim(),
                                        model = model.trim(),
                                        year = yearInt,
                                        licensePlate = licensePlate.trim(),
                                        mileage = mileageInt,
                                        imageUrl = imagePath,
                                        fuelType = fuelType.trim(),
                                        power = power.trim(),
                                        vin = vin.trim()
                                    )
                                    carViewModel.updateCar(updatedCar)
                                    onCarSaved()
                                } else {
                                    val newCar = Car(
                                        make = make.trim(),
                                        model = model.trim(),
                                        year = yearInt,
                                        licensePlate = licensePlate.trim(),
                                        mileage = mileageInt,
                                        imageUrl = imagePath,
                                        fuelType = fuelType.trim(),
                                        power = power.trim(),
                                        vin = vin.trim()
                                    )
                                    carViewModel.addCar(newCar) {
                                        onCarSaved()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(if (existingCar != null) "Update Vehicle" else "Save Vehicle", style = MaterialTheme.typography.titleMedium)
                    }

                    if (existingCar != null) {
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC5221F),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Delete Vehicle", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && existingCar != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Vehicle") },
            text = { Text("Are you sure you want to delete ${existingCar!!.make} ${existingCar!!.model}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        carViewModel.deleteCar(existingCar!!)
                        onCarSaved()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFC5221F)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "car_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
