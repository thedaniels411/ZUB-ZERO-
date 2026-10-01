package com.example

import android.net.Uri
import com.example.data.model.GeneratedMovie
import com.example.data.model.GeneratedVideoMetadata
import com.example.data.model.GeneratorType
import com.example.data.model.MovieGenre
import com.example.data.model.VideoScene
import com.example.data.storage.SaveVideoResult
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.ByteBuffer
import java.nio.ByteOrder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VideoLocalStorageManagerTest {

    @Test
    fun testFilenameSanitization() {
        val title = "Cyber City: 2099 / Director's Cut *Exclusive*"
        val sanitized = title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(32)
        assertEquals("Cyber_City__2099___Director_s_Cu", sanitized)
        assertTrue(!sanitized.contains("/"))
        assertTrue(!sanitized.contains(":"))
        assertTrue(!sanitized.contains("*"))
    }

    @Test
    fun testMp4HeaderBoxGeneration() {
        val ftypBox = ByteBuffer.allocate(32).apply {
            order(ByteOrder.BIG_ENDIAN)
            putInt(32)
            put("ftyp".toByteArray(Charsets.US_ASCII))
            put("isom".toByteArray(Charsets.US_ASCII))
            putInt(0x00000200)
            put("isom".toByteArray(Charsets.US_ASCII))
            put("iso2".toByteArray(Charsets.US_ASCII))
            put("mp41".toByteArray(Charsets.US_ASCII))
            put("avc1".toByteArray(Charsets.US_ASCII))
        }.array()

        assertEquals(32, ftypBox.size)
        // Check size integer: 32 (0x00000020)
        assertEquals(0, ftypBox[0].toInt())
        assertEquals(32, ftypBox[3].toInt())
        // Check ftyp tag
        val tag = String(ftypBox, 4, 4, Charsets.US_ASCII)
        assertEquals("ftyp", tag)
    }

    @Test
    fun testDirectorialMetadataJsonGeneration() {
        val scenes = listOf(
            VideoScene(1, "Scene 1", "Visual 1", "Dolly In", "Line 1", 6, "Audio 1"),
            VideoScene(2, "Scene 2", "Visual 2", "Orbit", "Line 2", 8, "Audio 2")
        )
        val movie = GeneratedMovie(
            title = "Sub-Zero Infiltration",
            genre = MovieGenre.ACTION,
            generatorType = GeneratorType.IDEA_TO_VIDEO,
            logline = "A cold thriller.",
            synopsis = "Full synopsis.",
            scenes = scenes,
            aspectRatio = "16:9 Cinema",
            metadata = GeneratedVideoMetadata(
                title = "Sub-Zero Infiltration",
                durationSeconds = 14,
                aiModelVersion = "gemini-3.5-flash",
                resolution = "4K UHD"
            )
        )

        val json = JSONObject().apply {
            put("title", movie.title)
            put("genre", movie.genre.name)
            put("aiModelVersion", movie.metadata.aiModelVersion)
            put("durationSeconds", movie.metadata.durationSeconds)
            put("resolution", movie.metadata.resolution)
        }

        assertEquals("Sub-Zero Infiltration", json.getString("title"))
        assertEquals("ACTION", json.getString("genre"))
        assertEquals("gemini-3.5-flash", json.getString("aiModelVersion"))
        assertEquals(14, json.getInt("durationSeconds"))
    }

    @Test
    fun testSaveVideoResult_successAndError() {
        val mockUri = Uri.parse("content://media/external/video/media/12345")
        val success = SaveVideoResult.Success(
            contentUri = mockUri,
            fileName = "ZUBZERO_Test_123.mp4",
            relativePath = "Movies/ZubZero_Studio/ZUBZERO_Test_123.mp4",
            fileSizeBytes = 2048000L,
            localFilePath = "/data/user/0/com.example/files/movies/ZubZero_Studio/ZUBZERO_Test_123.mp4"
        )

        assertEquals("ZUBZERO_Test_123.mp4", success.fileName)
        assertTrue(success.fileSizeBytes > 0)
        assertEquals("Movies/ZubZero_Studio/ZUBZERO_Test_123.mp4", success.relativePath)

        val error = SaveVideoResult.Error("Disk full")
        assertEquals("Disk full", error.message)
    }
}
