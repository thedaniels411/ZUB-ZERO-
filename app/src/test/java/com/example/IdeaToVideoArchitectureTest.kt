package com.example

import com.example.data.model.GenerationStageDetail
import com.example.data.model.IdeaToVideoPromptRequest
import com.example.data.model.IdeaToVideoPromptSubmission
import com.example.data.model.MovieGenre
import com.example.data.model.SubmissionFilter
import com.example.data.model.VideoGenerationStage
import com.example.data.model.VideoGenerationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IdeaToVideoArchitectureTest {

    @Test
    fun testVideoGenerationStatus_terminalAndSuccessStates() {
        assertFalse(VideoGenerationStatus.QUEUED.isTerminal)
        assertFalse(VideoGenerationStatus.QUEUED.isSuccess)

        assertFalse(VideoGenerationStatus.ANALYZING_PROMPT.isTerminal)
        assertFalse(VideoGenerationStatus.SCRIPT_SYNTHESIS.isTerminal)
        assertFalse(VideoGenerationStatus.CAMERA_RIGGING.isTerminal)
        assertFalse(VideoGenerationStatus.NEURAL_RENDERING.isTerminal)
        assertFalse(VideoGenerationStatus.AUDIO_MASTERING.isTerminal)

        assertTrue(VideoGenerationStatus.COMPLETED.isTerminal)
        assertTrue(VideoGenerationStatus.COMPLETED.isSuccess)

        assertTrue(VideoGenerationStatus.FAILED.isTerminal)
        assertFalse(VideoGenerationStatus.FAILED.isSuccess)

        assertTrue(VideoGenerationStatus.CANCELLED.isTerminal)
        assertFalse(VideoGenerationStatus.CANCELLED.isSuccess)
    }

    @Test
    fun testIdeaToVideoPromptSubmission_computedProperties() {
        val inProgressSubmission = IdeaToVideoPromptSubmission(
            id = "SUB-TEST-001",
            prompt = "A high-octane hypercar pursuit through a frozen neon tunnel",
            title = "Sub-Zero: Hyper Velocity",
            genre = MovieGenre.ACTION,
            status = VideoGenerationStatus.NEURAL_RENDERING,
            progress = 0.75f,
            currentStageName = "Volumetric Frame Synthesis",
            currentStageIndex = 4
        )

        assertTrue(inProgressSubmission.isInProgress)
        assertFalse(inProgressSubmission.isFinished)
        assertFalse(inProgressSubmission.isSuccess)

        val completedSubmission = inProgressSubmission.copy(
            status = VideoGenerationStatus.COMPLETED,
            progress = 1.0f,
            currentStageIndex = 5
        )

        assertFalse(completedSubmission.isInProgress)
        assertTrue(completedSubmission.isFinished)
        assertTrue(completedSubmission.isSuccess)
    }

    @Test
    fun testVideoGenerationStages_sequentialOrder() {
        val stages = VideoGenerationStage.values()
        assertEquals(5, stages.size)
        assertEquals(1, stages[0].stepIndex)
        assertEquals(2, stages[1].stepIndex)
        assertEquals(3, stages[2].stepIndex)
        assertEquals(4, stages[3].stepIndex)
        assertEquals(5, stages[4].stepIndex)

        assertTrue(stages[0].targetProgress < stages[1].targetProgress)
        assertTrue(stages[1].targetProgress < stages[2].targetProgress)
        assertTrue(stages[2].targetProgress < stages[3].targetProgress)
        assertTrue(stages[3].targetProgress <= stages[4].targetProgress)
    }

    @Test
    fun testIdeaToVideoPromptRequest_defaults() {
        val request = IdeaToVideoPromptRequest(
            prompt = "Cryogenic detective exploring stasis chambers",
            genre = MovieGenre.SCI_FI
        )

        assertEquals("Cryogenic detective exploring stasis chambers", request.prompt)
        assertEquals(MovieGenre.SCI_FI, request.genre)
        assertEquals("16:9 Cinema", request.aspectRatio)
        assertEquals(20, request.durationSeconds)
        assertEquals(50, request.coinCost)
    }

    @Test
    fun testSubmissionFilteringLogic() {
        val sub1 = IdeaToVideoPromptSubmission(
            id = "SUB-1",
            prompt = "Cyberpunk chase",
            title = "Tokyo Drift",
            genre = MovieGenre.ACTION,
            status = VideoGenerationStatus.COMPLETED,
            progress = 1.0f
        )
        val sub2 = IdeaToVideoPromptSubmission(
            id = "SUB-2",
            prompt = "Gothic vampire castle",
            title = "Mist & Blood",
            genre = MovieGenre.TWILIGHT,
            status = VideoGenerationStatus.NEURAL_RENDERING,
            progress = 0.65f
        )
        val sub3 = IdeaToVideoPromptSubmission(
            id = "SUB-3",
            prompt = "Estranged family trial",
            title = "The Will",
            genre = MovieGenre.DRAMA,
            status = VideoGenerationStatus.FAILED,
            progress = 0.4f
        )

        val list = listOf(sub1, sub2, sub3)

        // Filter: ALL
        assertEquals(3, list.size)

        // Filter: IN_PROGRESS
        val inProgress = list.filter { it.isInProgress }
        assertEquals(1, inProgress.size)
        assertEquals("SUB-2", inProgress.first().id)

        // Filter: COMPLETED
        val completed = list.filter { it.isSuccess }
        assertEquals(1, completed.size)
        assertEquals("SUB-1", completed.first().id)

        // Filter: FAILED
        val failed = list.filter { it.status == VideoGenerationStatus.FAILED || it.status == VideoGenerationStatus.CANCELLED }
        assertEquals(1, failed.size)
        assertEquals("SUB-3", failed.first().id)
    }
}
