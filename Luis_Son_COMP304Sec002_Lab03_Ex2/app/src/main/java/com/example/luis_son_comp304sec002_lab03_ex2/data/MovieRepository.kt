package com.example.luis_son_comp304sec002_lab03_ex2.data
import kotlinx.coroutines.flow.Flow

// Repository class managing data operations between UI and database
class MovieRepository(private val dao: MovieDao) {

    val movies: Flow<List<Movie>> = dao.getAll()
    val favoriteMovies: Flow<List<Movie>> = dao.getFavorites() as Flow<List<Movie>>

    suspend fun addMovie(movie: Movie) = dao.insert(movie)
    suspend fun updateMovie(movie: Movie) = dao.update(movie)
    suspend fun deleteMovie(movie: Movie) = dao.delete(movie)

    suspend fun insertSampleMovies(movies: List<Movie>) {
        dao.insertSampleMovies(movies) // (maybe rename this too)
    }
}