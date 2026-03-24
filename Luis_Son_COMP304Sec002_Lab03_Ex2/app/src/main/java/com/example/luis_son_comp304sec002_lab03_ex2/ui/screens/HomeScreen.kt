package com.example.luis_son_comp304sec002_lab03_ex2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.luis_son_comp304sec002_lab03_ex2.data.Movie
import com.example.luis_son_comp304sec002_lab03_ex2.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(LocalActivity.current)
) {
    val movies by viewModel.allProducts.observeAsState(emptyList())
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie List") },
                actions = {
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favourites")
                    }
                    IconButton(onClick = { navController.navigate("delete") }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Movie")
                    }
                    IconButton(onClick = { navController.navigate("add") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Movie")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isExpandedScreen) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(movies, key = { it.id }) { movie ->
                        MovieItem(
                            movie = movie,
                            onEdit = { navController.navigate("edit/${movie.id}") },
                            onDelete = { viewModel.delete(movie) },
                            onToggleFavorite = { viewModel.toggleFavorite(movie) },
                            modifier = Modifier
                                .clickable { selectedMovie = movie }
                                .background(
                                    if (movie == selectedMovie)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else Color.Transparent
                                )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    selectedMovie?.let { movie ->
                        Column {
                            Text(movie.name, style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("ID: ${movie.id}", style = MaterialTheme.typography.bodyLarge)
                            Text("Director: ${movie.nameDirector}", style = MaterialTheme.typography.bodyLarge)
                            Text("Price: ${"$%.2f".format(movie.price)}", style = MaterialTheme.typography.bodyLarge)
                            Text("Released: ${movie.dateRelease}", style = MaterialTheme.typography.bodyLarge)
                            Text("Duration: ${movie.duration} min", style = MaterialTheme.typography.bodyLarge)
                            Text("Genre: ${movie.genre}", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Favourite: ${if (movie.isFavorite) "Yes" else "No"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } ?: Text(
                        "Select a movie to see details",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            if (movies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No movies yet. Tap + to add one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(movies, key = { it.id }) { movie ->
                        MovieItem(
                            movie = movie,
                            onEdit = { navController.navigate("edit/${movie.id}") },
                            onDelete = { viewModel.delete(movie) },
                            onToggleFavorite = { viewModel.toggleFavorite(movie) }
                        )
                    }
                }
            }
        }
    }
}