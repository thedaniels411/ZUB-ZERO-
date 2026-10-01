package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_movies")
data class MovieProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val genre: String,
    val generatorType: String,
    val logline: String,
    val synopsis: String,
    val scenesJson: String,
    val aspectRatio: String,
    val dateCreated: Long = System.currentTimeMillis()
)
