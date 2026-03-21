package com.example.luis_son_comp304sec002_lab03_ex1.data
import androidx.room.PrimaryKey
import androidx.room.Entity

// Represents a product entity in the Room database
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val deliveryDate: String,
    val category: String,
    val isFavorite: Boolean,
    val quantity: Int,
)