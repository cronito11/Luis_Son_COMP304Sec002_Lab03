package com.example.luis_son_comp304sec002_lab03_ex2.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.luis_son_comp304sec002_lab03_ex2.data.Movie
import com.example.luis_son_comp304sec002_lab03_ex2.data.MovieDatabase
import com.example.luis_son_comp304sec002_lab03_ex2.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MovieRepository
    val allProducts: LiveData<List<Movie>>
    val favoriteProducts: LiveData<List<Movie>>
    private val _addProductSuccess = MutableStateFlow(false)
    val addProductSuccess: StateFlow<Boolean> = _addProductSuccess.asStateFlow()

    // Form state handling
    data class AddMovieState(
        val id: String = "",
        val name: String = "",
        val nameDirector: String = "",
        val price: String = "",
        val dateRelease: String = "",
        val duration: Int = 0,
        val genre: String = "",
        val isFavorite: Boolean = false,
        val errors: List<String> = emptyList()
    )

    private val _addMovieState = MutableStateFlow(AddMovieState())
    val addMovieState: StateFlow<AddMovieState> = _addMovieState.asStateFlow()

    init {
        val dao = MovieDatabase.getDatabase(application).movieDao()
        repository = MovieRepository(dao)
        allProducts = repository.movies.asLiveData()
        favoriteProducts = repository.favoriteMovies.asLiveData()
    }

    fun validateAndAddProduct() {
        val state = _addMovieState.value
        val errors = mutableListOf<String>()

        // ID validation (3 digits, 101-999)
        val id = state.id.toIntOrNull()
        if (id == null || id !in 101..999) errors.add("Invalid ID (101-999)")

        // Price validation
        val price = state.price.toDoubleOrNull()
        if (price == null || price <= 0) errors.add("Price must be positive")

        // Duration validation
        val duration = state.duration
        if (duration <= 0) errors.add("duration must be more than 0")

        // Date validation
        val currentDate = LocalDate.now()
        val releaseDate = try {
            LocalDate.parse(state.dateRelease)
        } catch (e: Exception) {
            null
        }
        if (releaseDate == null || releaseDate.isBefore(currentDate)) {
            errors.add("Invalid delivery date")
        }

        // Category validation
        if (state.genre !in listOf("Family", "Comedy", "Thriller", "Action")) {
            errors.add("Select a category")
        }

        if (errors.isEmpty()) {
            insert(
                Movie(
                    id = id!!,
                    name = state.name,
                    price = price!!,
                    dateRelease = state.dateRelease,
                    duration = duration,
                    genre = state.genre,
                    nameDirector = state.nameDirector,
                    isFavorite = state.isFavorite
                )
            )
            _addMovieState.update { it.copy(errors = emptyList()) }
            _addProductSuccess.value = true  // Set success to true
        } else {
            _addMovieState.update { it.copy(errors = errors) }
            _addProductSuccess.value = false  // Reset success on validation failure
        }
    }

    // Update form fields
    fun updateFormState(
        id: String? = null,
        name: String? = null,
        price: String? = null,
        dateRelease: String? = null,
        genre: String? = null,
        duration: Int? = null,
        nameDirector: String? = null,
        isFavorite: Boolean? = null
    ) {
        _addMovieState.update { current ->
            current.copy(
                id = id ?: current.id,
                name = name ?: current.name,
                price = price ?: current.price,
                dateRelease = dateRelease ?: current.dateRelease,
                genre = genre ?: current.genre,
                duration =  duration?: current.duration,
                nameDirector = nameDirector?: current.nameDirector,
                isFavorite = isFavorite ?: current.isFavorite
            )
        }
    }

    fun toggleFavorite(movie: Movie) {
        val updatedProduct = movie.copy(isFavorite = !movie.isFavorite)
        viewModelScope.launch {
            repository.updateMovie(updatedProduct)
        }
    }

    fun resetSuccessState() {
        _addProductSuccess.value = false
    }

    // CRUD operations
    private fun insert(movie: Movie) = viewModelScope.launch { repository.addMovie(movie) }
    fun update(movie: Movie) = viewModelScope.launch { repository.updateMovie(movie) }
    fun delete(movie: Movie) = viewModelScope.launch { repository.deleteMovie(movie) }
}