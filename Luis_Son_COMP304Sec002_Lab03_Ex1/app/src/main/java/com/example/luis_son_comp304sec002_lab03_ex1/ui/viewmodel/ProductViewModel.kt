package com.example.luis_son_comp304sec002_lab03_ex1.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.luis_son_comp304sec002_lab03_ex1.data.Product
import com.example.luis_son_comp304sec002_lab03_ex1.data.ProductDatabase
import com.example.luis_son_comp304sec002_lab03_ex1.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>
    val favoriteProducts: LiveData<List<Product>>
    private val _addProductSuccess = MutableStateFlow(false)
    val addProductSuccess: StateFlow<Boolean> = _addProductSuccess.asStateFlow()

    // Form state handling
    data class AddProductState(
        val id: String = "",
        val name: String = "",
        val price: String = "",
        val deliveryDate: String = "",
        val category: String = "",
        val quantity : String = "",
        val isFavorite: Boolean = false,
        val errors: List<String> = emptyList()
    )

    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState: StateFlow<AddProductState> = _addProductState.asStateFlow()

    init {
        val dao = ProductDatabase.getDatabase(application).productDao()
        repository = ProductRepository(dao)
        allProducts = repository.products.asLiveData()
        favoriteProducts = repository.favoriteProducts.asLiveData()
    }

    fun validateAndAddProduct() {
        val state = _addProductState.value
        val errors = mutableListOf<String>()

        // ID validation (3 digits, 101-999)
        val id = state.id.toIntOrNull()
        if (id == null || id !in 101..999) errors.add("Invalid ID (101-999)")

        // Price validation
        val price = state.price.toDoubleOrNull()
        if (price == null || price <= 0) errors.add("Price must be positive")

        val quantity = state.quantity.toIntOrNull();
        if(quantity == null || quantity <= 0) errors.add("Quantity cannot be zero or negative")

        // Date validation
        val currentDate = LocalDate.now()
        val deliveryDate = try {
            LocalDate.parse(state.deliveryDate)
        } catch (e: Exception) {
            null
        }
        if (deliveryDate == null || deliveryDate.isBefore(currentDate)) {
            errors.add("Invalid delivery date")
        }

        // Category validation
        if (state.category !in listOf("Electronics", "Appliances", "Cell Phone", "Media")) {
            errors.add("Select a category")
        }

        if (errors.isEmpty()) {
            insert(
                Product(
                    id = id!!,
                    name = state.name,
                    price = price!!,
                    deliveryDate = state.deliveryDate,
                    category = state.category,
                    quantity = quantity!!,
                    isFavorite = state.isFavorite
                )
            )
            _addProductState.update { it.copy(errors = emptyList()) }
            _addProductSuccess.value = true  // Set success to true
        } else {
            _addProductState.update { it.copy(errors = errors) }
            _addProductSuccess.value = false  // Reset success on validation failure
        }
    }


    // Update form fields
    fun updateFormState(
        id: String? = null,
        name: String? = null,
        price: String? = null,
        deliveryDate: String? = null,
        category: String? = null,
        quantity: String? = null,
        isFavorite: Boolean? = null
    ) {
        _addProductState.update { current ->
            current.copy(
                id = id ?: current.id,
                name = name ?: current.name,
                price = price ?: current.price,
                deliveryDate = deliveryDate ?: current.deliveryDate,
                category = category ?: current.category,
                quantity = quantity ?: current.quantity,
                isFavorite = isFavorite ?: current.isFavorite
            )
        }
    }

    fun toggleFavorite(product: Product) {
        val updatedProduct = product.copy(isFavorite = !product.isFavorite)
        viewModelScope.launch {
            repository.updateProduct(updatedProduct)
        }
    }

    fun resetSuccessState() {
        _addProductSuccess.value = false
    }

    // CRUD operations
    private fun insert(product: Product) = viewModelScope.launch { repository.addProduct(product) }
    fun update(product: Product) = viewModelScope.launch { repository.updateProduct(product) }
    fun delete(product: Product) = viewModelScope.launch { repository.deleteProduct(product) }
}