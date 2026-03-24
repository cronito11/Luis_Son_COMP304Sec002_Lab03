package com.example.luis_son_comp304sec002_lab03_ex2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.luis_son_comp304sec002_lab03_ex2.ui.viewmodel.ProductViewModel
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMovieScreen(
    navController: NavController,
    movieId: Int?,
    viewModel: ProductViewModel = viewModel()
) {
    val movies by viewModel.allProducts.observeAsState(emptyList())

    if (movieId == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val movie = movies.find { it.id == movieId }
    if (movie == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var editedName         by remember { mutableStateOf(movie.name) }
    var editedDirector     by remember { mutableStateOf(movie.nameDirector) }
    var editedPrice        by remember { mutableStateOf(movie.price.toString()) }
    var editedDateRelease  by remember { mutableStateOf(movie.dateRelease) }
    var editedDurationText by remember { mutableStateOf(movie.duration.toString()) }
    var editedGenre        by remember { mutableStateOf(movie.genre) }
    var editedFavorite     by remember { mutableStateOf(movie.isFavorite) }
    var expanded           by remember { mutableStateOf(false) }
    var showDatePicker     by remember { mutableStateOf(false) }
    var errors             by remember { mutableStateOf(listOf<String>()) }

    val genres = listOf("Family", "Comedy", "Thriller", "Action")

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis(),
            yearRange = 2025..2030
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        editedDateRelease = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Movie") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            errors.forEach { error ->
                Text(
                    text = "• $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Movie Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = editedDirector,
                onValueChange = { editedDirector = it },
                label = { Text("Director") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = editedPrice,
                onValueChange = { editedPrice = it },
                label = { Text("DVD Price ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(
                    if (editedDateRelease.isEmpty()) "Select Release Date"
                    else "Release Date: $editedDateRelease"
                )
            }

            OutlinedTextField(
                value = editedDurationText,
                onValueChange = { editedDurationText = it },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = editedGenre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Genre") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    genres.forEach { genre ->
                        DropdownMenuItem(
                            text = { Text(genre) },
                            onClick = { editedGenre = genre; expanded = false }
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Favorite?")
                Switch(checked = editedFavorite, onCheckedChange = { editedFavorite = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.delete(movie)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }

                Button(onClick = {
                    val newErrors = mutableListOf<String>()
                    if (editedName.isBlank())      newErrors += "Title is required"
                    if (editedDirector.isBlank())  newErrors += "Director is required"
                    val price = editedPrice.toDoubleOrNull()
                    if (price == null || price <= 0) newErrors += "Price must be a positive number"
                    val duration = editedDurationText.toIntOrNull()
                    if (duration == null || duration <= 0) newErrors += "Duration must be a positive number"
                    if (editedDateRelease.isBlank()) newErrors += "Release date is required"
                    if (editedGenre.isBlank())       newErrors += "Genre is required"
                    errors = newErrors

                    if (newErrors.isEmpty()) {
                        viewModel.update(
                            movie.copy(
                                name         = editedName.trim(),
                                nameDirector = editedDirector.trim(),
                                price        = price!!,
                                dateRelease  = editedDateRelease,
                                duration     = duration!!,
                                genre        = editedGenre,
                                isFavorite   = editedFavorite
                            )
                        )
                        navController.popBackStack()
                    }
                }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save")
                }
            }
        }
    }
}