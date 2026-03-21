package com.example.luis_son_comp304sec002_lab03_ex2.data
import androidx.room.PrimaryKey
import androidx.room.Entity

// Represents a product entity in the Room database
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int,
    val name: String,
    val nameDirector: String,
    val price: Double,
    val dateRelease: String,
    val duration: Int,
    val genre: String,
    val isFavorite: Boolean
)