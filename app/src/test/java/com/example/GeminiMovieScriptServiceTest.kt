package com.example

import com.example.data.model.GeneratorType
import com.example.data.model.MovieGenre
import com.example.data.remote.gemini.GeminiMovieScriptService
import com.example.data.remote.gemini.MovieScriptPromptRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GeminiMovieScriptServiceTest {

    private lateinit var scriptService: GeminiMovieScriptService

    @Before
    fun setUp() {
        scriptService = GeminiMovieScriptService()
    }

    @Test
    fun testGenerateMovieFromIdea_actionGenre_generatesThreeScenes() = runBlocking {
        val movie = scriptService.generateMovieFromIdea(
            prompt = "A cryogenic strike operative infiltrating a server bunker",
            genre = MovieGenre.ACTION,
            aspectRatio = "16:9 Cinema",
            pacing = "High Octane",
            audioScore = "Action Bass Drop",
            cameraMotion = "Slow Dolly Zoom"
        )

        assertNotNull(movie)
        assertTrue(movie.title.isNotBlank())
        assertEquals(MovieGenre.ACTION, movie.genre)
        assertEquals(GeneratorType.IDEA_TO_VIDEO, movie.generatorType)
        assertTrue(movie.logline.isNotBlank())
        assertTrue(movie.synopsis.isNotBlank())
        assertEquals(3, movie.scenes.size)

        // Verify sequential scene numbers
        assertEquals(1, movie.scenes[0].sceneNumber)
        assertEquals(2, movie.scenes[1].sceneNumber)
        assertEquals(3, movie.scenes[2].sceneNumber)

        // Verify scene details
        assertTrue(movie.scenes[0].visualPrompt.isNotBlank())
        assertTrue(movie.scenes[0].cameraMovement.isNotBlank())
        assertTrue(movie.scenes[0].dialogue.isNotBlank())
    }

    @Test
    fun testGenerateMovieFromIdea_sciFiGenre_customAspectAndAudio() = runBlocking {
        val movie = scriptService.generateMovieFromIdea(
            prompt = "Rogue neural AI awakening in orbital space station",
            genre = MovieGenre.SCI_FI,
            aspectRatio = "2.39:1 Anamorphic",
            pacing = "Methodical Tension",
            audioScore = "Sub-Zero Ambient Synth",
            cameraMotion = "360 Orbit"
        )

        assertEquals(MovieGenre.SCI_FI, movie.genre)
        assertEquals("2.39:1 Anamorphic", movie.aspectRatio)
        assertEquals(3, movie.scenes.size)
        assertTrue(movie.title.contains("Neural", ignoreCase = true) || movie.title.contains("Sub-Zero", ignoreCase = true) || movie.title.contains("Rogue", ignoreCase = true))
    }

    @Test
    fun testGenerateMovieScript_comprehensiveResultAndDirectorNotes() = runBlocking {
        val request = MovieScriptPromptRequest(
            prompt = "Two ancient clans sealing a forbidden truce in the misty forest",
            genre = MovieGenre.TWILIGHT,
            aspectRatio = "2.39:1 Anamorphic",
            pacing = "Slow Burn",
            audioMood = "Twilight Gothic Strings",
            cameraMotion = "Vertigo Zoom Out"
        )

        val result = scriptService.generateMovieScript(request)

        assertNotNull(result)
        assertTrue(result.title.isNotBlank())
        assertTrue(result.logline.isNotBlank())
        assertTrue(result.synopsis.isNotBlank())
        assertTrue(result.fullScreenplay.isNotBlank())
        assertEquals(3, result.scenes.size)

        // Verify director notes
        assertNotNull(result.directorNotes)
        assertTrue(result.directorNotes.visualStyle.isNotBlank())
        assertTrue(result.directorNotes.lightingKey.isNotBlank())
        assertTrue(result.directorNotes.cameraLensType.isNotBlank())
        assertEquals("Twilight Gothic Strings", result.directorNotes.soundDesignMood)
    }

    @Test
    fun testExpandScriptToVideo_parsesFormattedScreenplay() = runBlocking {
        val scriptText = """
            TITLE: Frost Protocol
            
            EXT. TOKYO HIGHWAY - NIGHT
            Snow flurries obscure the neon horizon.
            
            DRAKE
            Perimeter compromised. Initiate extraction!
            
            KAI
            Hold the perimeter line!
        """.trimIndent()

        val movie = scriptService.expandScriptToVideo(
            scriptText = scriptText,
            genre = MovieGenre.ACTION,
            voiceStyle = "Deep Baritone",
            aspectRatio = "2.39:1 Anamorphic"
        )

        assertNotNull(movie)
        assertEquals(GeneratorType.SCRIPT_TO_VIDEO, movie.generatorType)
        assertEquals("Frost Protocol", movie.title)
        assertEquals(3, movie.scenes.size)
    }

    @Test
    fun testGenerateCinematicScenes_returnsThreeScenes() = runBlocking {
        val scenes = scriptService.generateCinematicScenes(
            prompt = "A detective unraveling a cryogenic conspiracy",
            genre = MovieGenre.DRAMA
        )

        assertEquals(3, scenes.size)
        scenes.forEach { scene ->
            assertTrue(scene.durationSeconds > 0)
            assertTrue(scene.title.isNotBlank())
            assertTrue(scene.visualPrompt.isNotBlank())
        }
    }
}
