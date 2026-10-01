package com.example

import com.example.data.model.GeneratedMovie
import com.example.data.model.GeneratedVideoMetadata
import com.example.data.model.GeneratorType
import com.example.data.model.MovieGenre
import com.example.data.model.VideoScene
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedVideoMetadataTest {

    @Test
    fun testGeneratedVideoMetadata_propertiesAndDefaults() {
        val metadata = GeneratedVideoMetadata(
            title = "Sub-Zero: Neon Horizon",
            durationSeconds = 25,
            aiModelVersion = "gemini-3.5-flash",
            resolution = "4K UHD (3840x2160)",
            fps = 60,
            aspectRatio = "16:9 Cinema",
            codec = "H.265 / HEVC",
            renderEngine = "ZUB-ZERO Neural Render Core v3.2",
            scenesCount = 3
        )

        assertEquals("Sub-Zero: Neon Horizon", metadata.title)
        assertEquals(25, metadata.durationSeconds)
        assertEquals("gemini-3.5-flash", metadata.aiModelVersion)
        assertEquals("4K UHD (3840x2160)", metadata.resolution)
        assertEquals(60, metadata.fps)
        assertEquals("16:9 Cinema", metadata.aspectRatio)
        assertEquals("H.265 / HEVC", metadata.codec)
        assertEquals(3, metadata.scenesCount)
    }

    @Test
    fun testFormattedDuration() {
        val m1 = GeneratedVideoMetadata(title = "Teaser", durationSeconds = 6)
        assertEquals("00:06", m1.formattedDuration)

        val m2 = GeneratedVideoMetadata(title = "Short", durationSeconds = 20)
        assertEquals("00:20", m2.formattedDuration)

        val m3 = GeneratedVideoMetadata(title = "Feature", durationSeconds = 125)
        assertEquals("02:05", m3.formattedDuration)
    }

    @Test
    fun testFormattedFileSize() {
        // 50 MB
        val m1 = GeneratedVideoMetadata(
            title = "Video 1",
            estimatedFileSizeBytes = 50L * 1024L * 1024L
        )
        assertEquals("50.0 MB", m1.formattedFileSize)

        // 1.5 GB
        val m2 = GeneratedVideoMetadata(
            title = "Video 2",
            estimatedFileSizeBytes = (1.5 * 1024.0 * 1024.0 * 1024.0).toLong()
        )
        assertEquals("1.50 GB", m2.formattedFileSize)
    }

    @Test
    fun testTechnicalSummary() {
        val metadata = GeneratedVideoMetadata(
            title = "Nocturne",
            durationSeconds = 20,
            aiModelVersion = "gemini-3.5-flash",
            resolution = "4K UHD",
            fps = 60,
            codec = "H.265"
        )

        val summary = metadata.technicalSummary
        assertTrue(summary.contains("4K UHD"))
        assertTrue(summary.contains("60 FPS"))
        assertTrue(summary.contains("gemini-3.5-flash"))
        assertTrue(summary.contains("00:20"))
    }

    @Test
    fun testGeneratedMovie_metadataIntegration() {
        val scenes = listOf(
            VideoScene(1, "Scene 1", "Visual 1", "Dolly", "Line 1", 6, "Audio 1"),
            VideoScene(2, "Scene 2", "Visual 2", "Orbit", "Line 2", 8, "Audio 2"),
            VideoScene(3, "Scene 3", "Visual 3", "Zoom", "Line 3", 7, "Audio 3")
        )

        val movie = GeneratedMovie(
            title = "Sub-Zero: Protocol",
            genre = MovieGenre.ACTION,
            generatorType = GeneratorType.IDEA_TO_VIDEO,
            logline = "A high-stakes thriller in the frost.",
            synopsis = "Full 3-act narrative.",
            scenes = scenes,
            aspectRatio = "2.39:1 Anamorphic"
        )

        assertNotNull(movie.metadata)
        assertEquals("Sub-Zero: Protocol", movie.metadata.title)
        assertEquals(21, movie.metadata.durationSeconds)
        assertEquals("2.39:1 Anamorphic", movie.metadata.aspectRatio)
        assertEquals(3, movie.metadata.scenesCount)
        assertEquals("gemini-3.5-flash", movie.metadata.aiModelVersion)
    }
}
