package com.example.luis_son_comp304sec002_lab03_ex2.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.also
import kotlin.jvm.java

// Room database containing the products table
@Database(entities = [Movie::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao // Provides access to ProductDao

    companion object {
        @Volatile
        private var Instance: MovieDatabase? = null

        // Singleton pattern to get or create the database instance
        fun getDatabase(context: Context): MovieDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate database with sample data on first creation
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).movieDao().insertSampleMovies(
                                    listOf(
                                        Movie(
                                            101,
                                            "Phone",
                                            nameDirector = "David Fincher",
                                            price = 12.99,
                                            dateRelease = "2015-06-12",
                                            duration = 120,
                                            genre = "Thriller",
                                            isFavorite = true
                                        ),
                                        Movie(
                                            102,
                                            "The Journey",
                                            nameDirector = "Christopher Nolan",
                                            price = 15.99,
                                            dateRelease = "2019-11-20",
                                            duration = 140,
                                            genre = "Adventure",
                                            isFavorite = false
                                        ),
                                        Movie(
                                            103,
                                            "Lost City",
                                            nameDirector = "Patty Jenkins",
                                            price = 10.50,
                                            dateRelease = "2021-03-05",
                                            duration = 110,
                                            genre = "Action",
                                            isFavorite = true
                                        )
                                    )
                                )
                            }
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}