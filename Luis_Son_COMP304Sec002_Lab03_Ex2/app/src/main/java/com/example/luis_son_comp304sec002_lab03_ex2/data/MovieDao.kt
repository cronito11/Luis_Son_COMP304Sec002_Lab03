package com.example.luis_son_comp304sec002_lab03_ex2.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Data Access Object for product-related database operations
@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAll(): Flow<List<Movie>>    // Retrieves all products as a Flow

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie) // Inserts a product, replacing if conflict occurs

    @Update
    suspend fun update(movie: Movie) // Updates an existing product

    @Delete
    suspend fun delete(movie: Movie) // Deletes a product

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<Movie>> // Retrieves favorite products as a Flow

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSampleMovies(movies: List<Movie>) // Inserts sample data, ignoring conflicts
}