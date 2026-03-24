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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository
    val allProducts: LiveData<List<Movie>>
    val favoriteProducts: LiveData<List<Movie>>

    private val _addProductSuccess = MutableStateFlow(false)
    val addProductSuccess: StateFlow<Boolean> = _addProductSuccess.asStateFlow()

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
        viewModelScope.launch {
            val state = _addMovieState.value
            val errors = mutableListOf<String>()

            val id = state.id.trim().toIntOrNull()
            if (id == null || id !in 101..999) errors.add("ID must be between 101 and 999")

            if (state.name.isBlank()) errors.add("Title is required")

            if (state.nameDirector.isBlank()) errors.add("Director is required")

            val price = state.price.trim().toDoubleOrNull()
            if (price == null || price <= 0) errors.add("Price must be a positive number")

            val currentDate = LocalDate.now()
            val releaseDate = try { LocalDate.parse(state.dateRelease) } catch (e: Exception) { null }
            if (releaseDate == null || releaseDate.isBefore(currentDate)) {
                errors.add("Release date must be today or in the future")
            }

            if (state.duration <= 0) errors.add("Duration must be greater than 0")

            if (state.genre.isBlank()) errors.add("Please select a genre")

            if (errors.isNotEmpty()) {
                _addMovieState.update { it.copy(errors = errors) }
                _addProductSuccess.value = false
                return@launch
            }

            val existing = repository.movies.first()
            if (existing.any { it.id == id }) {
                _addMovieState.update {
                    it.copy(errors = listOf("A movie with ID $id already exists"))
                }
                _addProductSuccess.value = false
                return@launch
            }

            repository.addMovie(
                Movie(
                    id           = id!!,
                    name         = state.name.trim(),
                    nameDirector = state.nameDirector.trim(),
                    price        = price!!,
                    dateRelease  = state.dateRelease,
                    duration     = state.duration,
                    genre        = state.genre,
                    isFavorite   = state.isFavorite
                )
            )
            _addMovieState.update { it.copy(errors = emptyList()) }
            _addProductSuccess.value = true
        }
    }

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
                id           = id           ?: current.id,
                name         = name         ?: current.name,
                price        = price        ?: current.price,
                dateRelease  = dateRelease  ?: current.dateRelease,
                genre        = genre        ?: current.genre,
                duration     = duration     ?: current.duration,
                nameDirector = nameDirector ?: current.nameDirector,
                isFavorite   = isFavorite   ?: current.isFavorite
            )
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(isFavorite = !movie.isFavorite))
        }
    }

    fun resetSuccessState() {
        _addProductSuccess.value = false
        _addMovieState.value = AddMovieState()
    }

    fun update(movie: Movie) = viewModelScope.launch { repository.updateMovie(movie) }
    fun delete(movie: Movie) = viewModelScope.launch { repository.deleteMovie(movie) }
}