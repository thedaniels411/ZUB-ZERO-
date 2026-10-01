package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_prompt_submissions")
data class VideoPromptSubmissionEntity(
    @PrimaryKey val id: String,
    val prompt: String,
    val title: String,
    val genre: String,
    val aspectRatio: String,
    val pacing: String,
    val cameraMotion: String,
    val audioMood: String,
    val durationSeconds: Int,
    val coinCost: Int,
    val submittedAt: Long,
    val completedAt: Long?,
    val status: String,
    val progress: Float,
    val currentStageName: String,
    val currentStageIndex: Int,
    val estimatedTimeRemainingSeconds: Int,
    val resultMovieJson: String?,
    val failureReason: String?,
    val isCancelled: Boolean = false
)
