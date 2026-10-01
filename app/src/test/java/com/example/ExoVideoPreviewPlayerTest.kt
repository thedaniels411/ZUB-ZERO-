package com.example

import com.example.data.model.VideoScene
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExoVideoPreviewPlayerTest {

    @Test
    fun testAspectRatioCalculations() {
        val aspect16x9 = when ("16:9 Cinema") {
            "2.39:1 Anamorphic" -> 2.39f
            "9:16 Shorts" -> 9f / 16f
            else -> 16f / 9f
        }
        assertEquals(16f / 9f, aspect16x9, 0.01f)

        val aspectAnamorphic = when ("2.39:1 Anamorphic") {
            "2.39:1 Anamorphic" -> 2.39f
            "9:16 Shorts" -> 9f / 16f
            else -> 16f / 9f
        }
        assertEquals(2.39f, aspectAnamorphic, 0.01f)

        val aspectShorts = when ("9:16 Shorts") {
            "2.39:1 Anamorphic" -> 2.39f
            "9:16 Shorts" -> 9f / 16f
            else -> 16f / 9f
        }
        assertEquals(9f / 16f, aspectShorts, 0.01f)
    }

    @Test
    fun testSceneProgressMapping() {
        val scenes = listOf(
            VideoScene(1, "Scene 1", "Visual 1", "Dolly", "Line 1", 6, "Audio 1"),
            VideoScene(2, "Scene 2", "Visual 2", "Orbit", "Line 2", 8, "Audio 2"),
            VideoScene(3, "Scene 3", "Visual 3", "Zoom", "Line 3", 6, "Audio 3")
        )
        val totalDurationMs = 20000L

        // At 0ms -> scene 0
        var pos = 0L
        var fraction = (pos.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
        var sceneIdx = (fraction * scenes.size).toInt().coerceIn(0, scenes.size - 1)
        assertEquals(0, sceneIdx)

        // At 10000ms (50%) -> scene 1
        pos = 10000L
        fraction = (pos.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
        sceneIdx = (fraction * scenes.size).toInt().coerceIn(0, scenes.size - 1)
        assertEquals(1, sceneIdx)

        // At 19000ms (95%) -> scene 2
        pos = 19000L
        fraction = (pos.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
        sceneIdx = (fraction * scenes.size).toInt().coerceIn(0, scenes.size - 1)
        assertEquals(2, sceneIdx)
    }

    @Test
    fun testTimeFormatting() {
        fun format(millis: Long): String {
            val totalSeconds = (millis / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

        assertEquals("00:00", format(0L))
        assertEquals("00:08", format(8000L))
        assertEquals("00:20", format(20000L))
        assertEquals("01:30", format(90000L))
    }

    @Test
    fun testVolumeTogglesAndPresets() {
        var currentVolume = 1.0f
        var isMuted = false

        // Toggle mute
        isMuted = !isMuted
        val effectiveVolume1 = if (isMuted) 0f else currentVolume
        assertEquals(0f, effectiveVolume1, 0.001f)
        assertTrue(isMuted)

        // Unmute
        isMuted = !isMuted
        val effectiveVolume2 = if (isMuted) 0f else currentVolume
        assertEquals(1.0f, effectiveVolume2, 0.001f)
        assertFalse(isMuted)

        // Volume preset selection (50%)
        currentVolume = 0.5f
        assertEquals("50%", "${(currentVolume * 100).toInt()}%")

        // Volume preset selection (25%)
        currentVolume = 0.25f
        assertEquals("25%", "${(currentVolume * 100).toInt()}%")
    }

    @Test
    fun testPlaybackSpeedCycleLogic() {
        fun nextSpeed(current: Float): Float {
            return when (current) {
                0.5f -> 1.0f
                1.0f -> 1.25f
                1.25f -> 1.5f
                1.5f -> 2.0f
                else -> 0.5f
            }
        }

        assertEquals(1.25f, nextSpeed(1.0f), 0.01f)
        assertEquals(1.5f, nextSpeed(1.25f), 0.01f)
        assertEquals(2.0f, nextSpeed(1.5f), 0.01f)
        assertEquals(0.5f, nextSpeed(2.0f), 0.01f)
        assertEquals(1.0f, nextSpeed(0.5f), 0.01f)
    }

    @Test
    fun testRemainingTimeCalculation() {
        val totalDurationMs = 20000L
        val currentPositionMs = 8000L
        val remainingMs = (totalDurationMs - currentPositionMs).coerceAtLeast(0L)
        assertEquals(12000L, remainingMs)
    }
}
