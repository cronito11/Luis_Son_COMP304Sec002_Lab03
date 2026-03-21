package com.example.luis_son_comp304sec002_lab03_ex1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.luis_son_comp304sec002_lab03_ex1.data.Product
import com.example.luis_son_comp304sec002_lab03_ex1.ui.viewmodel.ProductViewModel

// Main screen displaying all products
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(LocalActivity.current)
) {
    val products by viewModel.allProducts.observeAsState(emptyList())
    println("HomeScreen products: $products") // Log to verify data
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = remember(products, searchQuery) {
        val query = searchQuery.toIntOrNull()

        if (query == null) {
            products
        } else {
            products.filter {
                it.id == query
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product List") },
                actions = {
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                    IconButton(onClick = { navController.navigate("add") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Product")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Product ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isExpandedScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filteredProducts) { product ->
                            ProductItem(
                                product = product,
                                onEdit = {
                                    navController.navigate("edit/${product.id}")
                                },
                                onDelete = { viewModel.delete(product) },
                                onToggleFavorite = { viewModel.toggleFavorite(product) },
                                modifier = Modifier
                                    .clickable { selectedProduct = product }
                                    .background(
                                        if (product == selectedProduct)
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
                        selectedProduct?.let { product ->
                            Column {
                                Text(product.name, style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Price: $${product.price}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Category: ${product.category}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "Delivery Date: ${product.deliveryDate}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } ?: Text(
                            "Select a product",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(filteredProducts) { product ->
                        ProductItem(
                            product = product,
                            onEdit = { navController.navigate("edit/${product.id}") },
                            onDelete = { viewModel.delete(product) },
                            onToggleFavorite = { viewModel.toggleFavorite(product) },
                            modifier = Modifier
                                .background(
                                    if (product == selectedProduct)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }
        }
    }
}