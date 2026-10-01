package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.MovieProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM saved_movies ORDER BY dateCreated DESC")
    fun getAllMovies(): Flow<List<MovieProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieProjectEntity): Long

    @Query("DELETE FROM saved_movies WHERE id = :id")
    suspend fun deleteMovieById(id: Long)
}
