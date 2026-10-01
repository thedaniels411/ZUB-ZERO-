package com.example.data.generator

import com.example.R
import com.example.data.model.GeneratedMovie
import com.example.data.model.GeneratedVideoMetadata
import com.example.data.model.GeneratorType
import com.example.data.model.MovieGenre
import com.example.data.model.VideoScene
import com.example.data.repository.CatalogData
import kotlinx.coroutines.delay

object AiVideoGeneratorEngine {

    suspend fun generateIdeaToVideo(
        idea: String,
        genre: MovieGenre,
        aspectRatio: String,
        pacing: String
    ): GeneratedMovie {
        // Realistic generation delay simulation
        delay(1200)

        val cleanIdea = idea.trim().ifEmpty { "A clandestine agent operating in a sub-zero futuristic city" }
        val generatedTitle = deriveTitleFromIdea(cleanIdea, genre)
        val logline = "In a world caught between shadows and frost, $cleanIdea unfolds into a desperate battle for redemption."
        val synopsis = "Act I introduces the high-stakes conflict amid breathtaking visual panoramas. Act II tests alliances through relentless trials, culminating in an Act III cinematic showdown that redefines the characters' destinies."

        val scenes = listOf(
            VideoScene(
                sceneNumber = 1,
                title = "Opening Beat: Arrival",
                visualPrompt = "Wide cinematic vista setting the atmosphere for $cleanIdea, lit in cool neon and deep cinematic shadows",
                cameraMovement = "Slow Dolly In (35mm Anamorphic)",
                dialogue = "\"Every decision before this hour was prelude. Now the true mission begins.\"",
                durationSeconds = 6,
                ambientAudio = "Sub-Zero Ambient Synth & Whispering Winds"
            ),
            VideoScene(
                sceneNumber = 2,
                title = "Escalation: The Confrontation",
                visualPrompt = "Dynamic mid-shot capturing intense friction and high stakes related to: $cleanIdea",
                cameraMovement = "Handheld Shaky-Cam with Rapid Cut Zoom",
                dialogue = "\"You knew the price of defying the syndicate. Yet here you stand.\"",
                durationSeconds = 8,
                ambientAudio = "Rising Pulse Drums & Tension Strings"
            ),
            VideoScene(
                sceneNumber = 3,
                title = "Climax: Zenith Point",
                visualPrompt = "Spectacular high-octane climax resolution frame with atmospheric particles and high contrast lighting",
                cameraMovement = "360° Circular Orbit into Horizon Tilt",
                dialogue = "\"History will only remember who walked out of the frost alive.\"",
                durationSeconds = 7,
                ambientAudio = "Full Orchestral Crescendo & Heavy Sub-Bass"
            )
        )

        return GeneratedMovie(
            title = generatedTitle,
            genre = genre,
            generatorType = GeneratorType.IDEA_TO_VIDEO,
            logline = logline,
            synopsis = synopsis,
            scenes = scenes,
            aspectRatio = aspectRatio,
            imageRes = R.drawable.img_cinema_hero,
            metadata = GeneratedVideoMetadata(
                title = generatedTitle,
                durationSeconds = scenes.sumOf { it.durationSeconds },
                aiModelVersion = "gemini-3.5-flash",
                aspectRatio = aspectRatio,
                scenesCount = scenes.size,
                renderEngine = "ZUB-ZERO Neural Render Core v3.2"
            )
        )
    }

    suspend fun generateScriptToVideo(
        scriptText: String,
        genre: MovieGenre,
        voiceStyle: String
    ): GeneratedMovie {
        delay(1300)
        val title = if (scriptText.contains("TITLE:", ignoreCase = true)) {
            scriptText.substringAfter("TITLE:").substringBefore("\n").trim()
        } else {
            "Sub-Zero Screenplay: Act I"
        }

        val parsedScenes = mutableListOf<VideoScene>()
        val lines = scriptText.lines().filter { it.isNotBlank() }

        if (lines.isNotEmpty()) {
            val previewDialogues = lines.filter { it.contains(":") || it.startsWith("\"") }
            val d1 = previewDialogues.getOrNull(0) ?: "\"We only have one shot at infiltrating the mainframe.\""
            val d2 = previewDialogues.getOrNull(1) ?: "\"Then let us make every bullet count.\""
            val d3 = previewDialogues.getOrNull(2) ?: "\"Systems offline. Initiating protocol Sub-Zero.\""

            parsedScenes.add(
                VideoScene(
                    sceneNumber = 1,
                    title = "Scene 1: Establishing Shot",
                    visualPrompt = "Gothic architectural skyline illuminated by storm flashes, visual rendition of: ${lines.firstOrNull()?.take(60)}",
                    cameraMovement = "Boom Down from Overcast Clouds",
                    dialogue = d1,
                    durationSeconds = 6,
                    ambientAudio = "Voice Style ($voiceStyle) & Cold Air Drone"
                )
            )
            parsedScenes.add(
                VideoScene(
                    sceneNumber = 2,
                    title = "Scene 2: Core Exchange",
                    visualPrompt = "Intense two-shot dialogue framing with shallow depth of field and rim lighting",
                    cameraMovement = "Rack Focus between Protagonists",
                    dialogue = d2,
                    durationSeconds = 8,
                    ambientAudio = "Understated Cello & Ticking Clock"
                )
            )
            parsedScenes.add(
                VideoScene(
                    sceneNumber = 3,
                    title = "Scene 3: Scene Climax",
                    visualPrompt = "Fast motion pull-away revealing the full magnitude of the narrative revelation",
                    cameraMovement = "Rapid Dolly Zoom (Vertigo Effect)",
                    dialogue = d3,
                    durationSeconds = 7,
                    ambientAudio = "Shattering Cymbal & Electronic Sub-Drop"
                )
            )
        } else {
            parsedScenes.addAll(CatalogData.getSampleScenesForGenre(genre, title))
        }

        return GeneratedMovie(
            title = title,
            genre = genre,
            generatorType = GeneratorType.SCRIPT_TO_VIDEO,
            logline = "A direct screenplay-to-cinematic translation rendered with precision voice synthesis and shot direction.",
            synopsis = "Screenplay parsed into 3 primary cinematic sequences with dedicated camera blocking and voiceover cues.",
            scenes = parsedScenes,
            aspectRatio = "2.39:1 Anamorphic",
            imageRes = R.drawable.img_cinema_hero,
            metadata = GeneratedVideoMetadata(
                title = title,
                durationSeconds = parsedScenes.sumOf { it.durationSeconds }.takeIf { it > 0 } ?: 21,
                aiModelVersion = "gemini-3.5-flash",
                aspectRatio = "2.39:1 Anamorphic",
                scenesCount = parsedScenes.size,
                renderEngine = "ZUB-ZERO Screenplay Synthesizer v3.5"
            )
        )
    }

    suspend fun generateImageToVideo(
        imageTag: String,
        motionEffect: String,
        cameraSpeed: String
    ): GeneratedMovie {
        delay(1200)
        val title = "Motion Reel: $imageTag ($motionEffect)"
        val scenes = listOf(
            VideoScene(
                sceneNumber = 1,
                title = "Motion Phase 1: Micro-Drift",
                visualPrompt = "Subtle parallax depth displacement activating layers of $imageTag with floating atmospheric frost particles",
                cameraMovement = "$motionEffect (Speed: $cameraSpeed)",
                dialogue = "\"Motion activated. Translating 2D spatial dimensions into 4D spacetime.\"",
                durationSeconds = 5,
                ambientAudio = "Smooth Ambient Drone & Shimmering Bells"
            ),
            VideoScene(
                sceneNumber = 2,
                title = "Motion Phase 2: Dynamic Perspective",
                visualPrompt = "Full depth map camera projection moving through foreground elements into deep vanishing point",
                cameraMovement = "Parallax Push-Through & Light Streaks",
                dialogue = "\"Focus lock sustained. Render engine running at 60 fps neural interpolation.\"",
                durationSeconds = 6,
                ambientAudio = "Subtle Wind Rushes & Glitch Texture"
            )
        )

        return GeneratedMovie(
            title = title,
            genre = MovieGenre.SCI_FI,
            generatorType = GeneratorType.IMAGE_TO_VIDEO,
            logline = "Still frame transformed into fluid multi-angle motion reel with physical depth modeling.",
            synopsis = "Generated using state-of-the-art optical flow synthesis and neural camera trajectories.",
            scenes = scenes,
            aspectRatio = "16:9 Cinema",
            imageRes = R.drawable.img_cinema_hero,
            metadata = GeneratedVideoMetadata(
                title = title,
                durationSeconds = scenes.sumOf { it.durationSeconds },
                aiModelVersion = "imagen-video-3.0",
                aspectRatio = "16:9 Cinema",
                scenesCount = scenes.size,
                renderEngine = "ZUB-ZERO Optical Flow Engine v3.0"
            )
        )
    }

    private fun deriveTitleFromIdea(idea: String, genre: MovieGenre): String {
        val words = idea.split(" ").filter { it.length > 3 }
        val keyword = words.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Protocol"
        return when (genre) {
            MovieGenre.ACTION -> "Sub-Zero: $keyword Strike"
            MovieGenre.DRAMA -> "The Silence of $keyword"
            MovieGenre.TWILIGHT -> "Nocturne: Blood & $keyword"
            MovieGenre.SCI_FI -> "Project $keyword: 2099"
        }
    }
}
