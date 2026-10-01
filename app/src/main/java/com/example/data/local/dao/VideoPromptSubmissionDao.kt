package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.VideoPromptSubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoPromptSubmissionDao {

    @Query("SELECT * FROM video_prompt_submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<VideoPromptSubmissionEntity>>

    @Query("SELECT * FROM video_prompt_submissions WHERE id = :id LIMIT 1")
    suspend fun getSubmissionById(id: String): VideoPromptSubmissionEntity?

    @Query("SELECT * FROM video_prompt_submissions WHERE id = :id LIMIT 1")
    fun getSubmissionFlowById(id: String): Flow<VideoPromptSubmissionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: VideoPromptSubmissionEntity)

    @Update
    suspend fun updateSubmission(submission: VideoPromptSubmissionEntity)

    @Query("UPDATE video_prompt_submissions SET status = :status, progress = :progress, currentStageName = :stageName, currentStageIndex = :stageIndex WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, progress: Float, stageName: String, stageIndex: Int)

    @Query("UPDATE video_prompt_submissions SET status = 'CANCELLED', isCancelled = 1 WHERE id = :id")
    suspend fun cancelSubmission(id: String)

    @Query("DELETE FROM video_prompt_submissions WHERE id = :id")
    suspend fun deleteSubmissionById(id: String)

    @Query("DELETE FROM video_prompt_submissions")
    suspend fun clearAllSubmissions()
}
