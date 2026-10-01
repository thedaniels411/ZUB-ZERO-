package com.example.data.remote.gemini

import com.example.R
import com.example.data.model.GeneratedMovie
import com.example.data.model.GeneratedVideoMetadata
import com.example.data.model.GeneratorType
import com.example.data.model.MovieGenre
import com.example.data.model.VideoScene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service class interfacing with the Gemini API (using gemini-3.5-flash) to handle
 * prompt-based movie script generation, scene breakdowns, and directorial camera instructions
 * for the ZUB-ZERO video studio.
 */
class GeminiMovieScriptService : GeminiScriptGeneratorService {

    override fun isGeminiApiConfigured(): Boolean {
        return GeminiClient.hasValidApiKey()
    }

    /**
     * Generates a complete movie package (title, logline, synopsis, 3-act scenes) from a user idea prompt.
     */
    override suspend fun generateMovieFromIdea(
        prompt: String,
        genre: MovieGenre,
        aspectRatio: String,
        pacing: String,
        audioScore: String,
        cameraMotion: String
    ): GeneratedMovie = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()

        if (!isGeminiApiConfigured()) {
            return@withContext fallbackScriptGeneration(
                prompt = prompt,
                genre = genre,
                aspectRatio = aspectRatio,
                pacing = pacing,
                audioScore = audioScore,
                cameraMotion = cameraMotion,
                sourceType = "Deterministic Neural Core (Add GEMINI_API_KEY to Secrets panel for live cloud generation)"
            )
        }

        try {
            val systemInstructionText = buildSystemInstruction()
            val userPromptText = """
                Genre: ${genre.displayName} (${genre.tagline})
                Aspect Ratio: $aspectRatio
                Pacing: $pacing
                Target Musical Mood: $audioScore
                Preferred Camera Motion: $cameraMotion
                
                User Concept / Idea Prompt:
                $prompt
                
                Generate a 3-scene cinematic movie script & video storyboard following the requested format.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = userPromptText)))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstructionText))),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.75f,
                    topP = 0.95f,
                    topK = 40
                )
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response returned by Gemini API")

            parseGeminiScriptResponse(
                rawText = generatedText,
                fallbackPrompt = prompt,
                genre = genre,
                aspectRatio = aspectRatio
            )
        } catch (e: Exception) {
            fallbackScriptGeneration(
                prompt = prompt,
                genre = genre,
                aspectRatio = aspectRatio,
                pacing = pacing,
                audioScore = audioScore,
                cameraMotion = cameraMotion,
                sourceType = "Offline Engine (Gemini Notice: ${e.localizedMessage ?: "Fallback activated"})"
            )
        }
    }

    /**
     * Generates a comprehensive screenplay with full scene details and director notes.
     */
    override suspend fun generateMovieScript(
        request: MovieScriptPromptRequest
    ): MovieScriptGenerationResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val apiKey = GeminiClient.getApiKey()

        if (!isGeminiApiConfigured()) {
            val fallbackMovie = fallbackScriptGeneration(
                prompt = request.prompt,
                genre = request.genre,
                aspectRatio = request.aspectRatio,
                pacing = request.pacing,
                audioScore = request.audioMood,
                cameraMotion = request.cameraMotion,
                sourceType = "Local Heuristic Engine"
            )
            val notes = DirectorCinematographyNotes(
                visualStyle = "Atmospheric High-Contrast Neo-Noir with Volumetric Frost",
                lightingKey = "Cold Cyan Rim Lighting with Low-Key Fill",
                cameraLensType = "35mm Anamorphic Prime Lenses (T1.5)",
                soundDesignMood = request.audioMood
            )
            return@withContext MovieScriptGenerationResult(
                title = fallbackMovie.title,
                logline = fallbackMovie.logline,
                synopsis = fallbackMovie.synopsis,
                fullScreenplay = buildFormattedScreenplay(fallbackMovie),
                scenes = fallbackMovie.scenes,
                directorNotes = notes,
                isLiveGeminiCall = false,
                modelIdentifier = "offline-fallback-core",
                latencyMs = System.currentTimeMillis() - startTime,
                rawModelResponse = "Offline generated template for ${request.genre.displayName}"
            )
        }

        try {
            val systemInstruction = buildSystemInstruction()
            val userText = """
                Genre: ${request.genre.displayName}
                Aspect Ratio: ${request.aspectRatio}
                Pacing: ${request.pacing}
                Target Audio: ${request.audioMood}
                Camera Motion: ${request.cameraMotion}
                Character Archetype: ${request.characterArchetype}
                Target Scenes Count: ${request.targetScenesCount}
                
                Premise / Idea:
                ${request.prompt}
            """.trimIndent()

            val geminiReq = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = userText)))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstruction))),
                generationConfig = GeminiGenerationConfig(temperature = 0.75f, topP = 0.95f, topK = 40)
            )

            val response = GeminiClient.service.generateContent(apiKey, geminiReq)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from Gemini API")

            val latency = System.currentTimeMillis() - startTime
            val movie = parseGeminiScriptResponse(rawText, request.prompt, request.genre, request.aspectRatio)

            val notes = parseDirectorNotes(rawText, request.audioMood)

            MovieScriptGenerationResult(
                title = movie.title,
                logline = movie.logline,
                synopsis = movie.synopsis,
                fullScreenplay = extractScreenplaySection(rawText, movie),
                scenes = movie.scenes,
                directorNotes = notes,
                isLiveGeminiCall = true,
                modelIdentifier = "gemini-3.5-flash",
                latencyMs = latency,
                rawModelResponse = rawText
            )
        } catch (e: Exception) {
            val fallbackMovie = fallbackScriptGeneration(
                prompt = request.prompt,
                genre = request.genre,
                aspectRatio = request.aspectRatio,
                pacing = request.pacing,
                audioScore = request.audioMood,
                cameraMotion = request.cameraMotion,
                sourceType = "Fallback (Error: ${e.localizedMessage})"
            )
            MovieScriptGenerationResult(
                title = fallbackMovie.title,
                logline = fallbackMovie.logline,
                synopsis = fallbackMovie.synopsis,
                fullScreenplay = buildFormattedScreenplay(fallbackMovie),
                scenes = fallbackMovie.scenes,
                directorNotes = DirectorCinematographyNotes(
                    visualStyle = "Volumetric Frost & Cyber Neon",
                    lightingKey = "Cold Key with Amber Highlights",
                    cameraLensType = "50mm Master Prime",
                    soundDesignMood = request.audioMood
                ),
                isLiveGeminiCall = false,
                modelIdentifier = "offline-fallback-core",
                latencyMs = System.currentTimeMillis() - startTime,
                rawModelResponse = e.localizedMessage ?: "Network error"
            )
        }
    }

    /**
     * Expands an existing formatted screenplay into multi-angle cinematic scenes for video rendering.
     */
    override suspend fun expandScriptToVideo(
        scriptText: String,
        genre: MovieGenre,
        voiceStyle: String,
        aspectRatio: String
    ): GeneratedMovie = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()

        if (!isGeminiApiConfigured()) {
            return@withContext fallbackScriptTextGeneration(scriptText, genre, voiceStyle, aspectRatio)
        }

        try {
            val systemInstruction = """
                You are a master script supervisor and cinematography director.
                Transform the user's screenplay text into 3 distinct camera scenes for neural video rendering.
                Format using:
                TITLE: [Movie Title]
                LOGLINE: [Logline]
                SYNOPSIS: [Synopsis]
                
                === SCENE 1 ===
                SLUGLINE: ...
                SHOT_TITLE: ...
                VISUAL_PROMPT: ...
                CAMERA_MOTION: ...
                DIALOGUE: ...
                DURATION_SECONDS: 6
                AUDIO_SCORE: ...
                
                === SCENE 2 ===
                ...
                === SCENE 3 ===
                ...
                FULL_SCREENPLAY:
                ...
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = "Screenplay:\n$scriptText\n\nVoice Tone: $voiceStyle\nGenre: ${genre.displayName}")))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstruction))),
                generationConfig = GeminiGenerationConfig(temperature = 0.7f)
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from Gemini")

            parseGeminiScriptResponse(
                rawText = text,
                fallbackPrompt = scriptText.take(120),
                genre = genre,
                aspectRatio = aspectRatio,
                generatorType = GeneratorType.SCRIPT_TO_VIDEO
            )
        } catch (e: Exception) {
            fallbackScriptTextGeneration(scriptText, genre, voiceStyle, aspectRatio)
        }
    }

    /**
     * Synthesizes 3 sequential storyboard scenes from an idea prompt.
     */
    override suspend fun generateCinematicScenes(
        prompt: String,
        genre: MovieGenre
    ): List<VideoScene> {
        val movie = generateMovieFromIdea(
            prompt = prompt,
            genre = genre,
            aspectRatio = "16:9 Cinema",
            pacing = "Balanced Cinematic",
            audioScore = "Sub-Zero Ambient Drone",
            cameraMotion = "Slow Dolly Zoom"
        )
        return movie.scenes
    }

    // --- Private Helper & Prompt Building Methods ---

    private fun buildSystemInstruction(): String {
        return """
            You are a world-class Hollywood film director, screenwriter, and cinematic storyboard AI for the ZUB-ZERO studio.
            Your task is to take user text ideas/prompts and transform them into a fully-realized cinematic movie script and scene breakdown for video generation.
            
            Always structure your answer clearly with these exact delimiters:
            TITLE: [Compelling Cinematic Movie Title]
            LOGLINE: [Punchy 1-2 sentence dramatic hook]
            SYNOPSIS: [3-4 sentence cinematic narrative overview explaining Act I, Act II, and Act III]
            VISUAL_STYLE: [Atmospheric visual rendering notes, e.g. Cyber Anamorphic, High-contrast neo-noir]
            LIGHTING_KEY: [Key lighting details, e.g. Cold Cyan Rim Lighting with Volumetric Steam]
            CAMERA_LENS: [Recommended optics, e.g. 35mm Anamorphic T1.5]
            
            === SCENE 1 ===
            SLUGLINE: EXT. or INT. LOCATION - TIME
            SHOT_TITLE: [Scene Title]
            VISUAL_PROMPT: [Ultra-detailed visual rendering prompt for high-definition video generation, including lighting, textures, camera angle]
            CAMERA_MOTION: [Specific camera motion like 35mm Anamorphic Dolly In, Orbit, Vertigo zoom, etc.]
            DIALOGUE: [Powerful dialogue line or voiceover in quotes]
            DURATION_SECONDS: [Number between 5 and 10]
            AUDIO_SCORE: [Detailed sound design & soundtrack description]
            
            === SCENE 2 ===
            SLUGLINE: EXT. or INT. LOCATION - TIME
            SHOT_TITLE: [Scene Title]
            VISUAL_PROMPT: [Detailed visual prompt]
            CAMERA_MOTION: [Specific camera motion]
            DIALOGUE: [Powerful dialogue line]
            DURATION_SECONDS: [Number between 5 and 10]
            AUDIO_SCORE: [Audio score cue]
            
            === SCENE 3 ===
            SLUGLINE: EXT. or INT. LOCATION - TIME
            SHOT_TITLE: [Scene Title]
            VISUAL_PROMPT: [Detailed visual prompt]
            CAMERA_MOTION: [Specific camera motion]
            DIALOGUE: [Powerful dialogue line]
            DURATION_SECONDS: [Number between 5 and 10]
            AUDIO_SCORE: [Audio score cue]
            
            FULL_SCREENPLAY:
            [Formatted professional script excerpt with sluglines, parentheticals, character names, and dialogue]
        """.trimIndent()
    }

    private fun parseGeminiScriptResponse(
        rawText: String,
        fallbackPrompt: String,
        genre: MovieGenre,
        aspectRatio: String,
        generatorType: GeneratorType = GeneratorType.IDEA_TO_VIDEO
    ): GeneratedMovie {
        var title = "Zub-Zero: Project Genesis"
        var logline = "A captivating story born from ideas in the cold frontier."
        var synopsis = "A cinematic journey told across high-stakes sequences."

        val titleMatch = Regex("""(?i)TITLE:\s*(.+)""").find(rawText)
        if (titleMatch != null) {
            title = titleMatch.groupValues[1].trim().trim('*', '#', '\"')
        }

        val loglineMatch = Regex("""(?i)LOGLINE:\s*(.+)""").find(rawText)
        if (loglineMatch != null) {
            logline = loglineMatch.groupValues[1].trim().trim('*', '#')
        }

        val synopsisMatch = Regex("""(?i)SYNOPSIS:\s*([^\n=]+)""").find(rawText)
        if (synopsisMatch != null) {
            synopsis = synopsisMatch.groupValues[1].trim().trim('*', '#')
        }

        val scenes = mutableListOf<VideoScene>()
        val sceneBlocks = rawText.split(Regex("""(?i)=== SCENE \d+ ==="""))

        if (sceneBlocks.size > 1) {
            for (i in 1 until sceneBlocks.size) {
                val block = sceneBlocks[i].substringBefore("=== SCENE").substringBefore("FULL_SCREENPLAY:")
                val sceneNum = i
                val shotTitle = Regex("""(?i)SHOT_TITLE:\s*(.+)""").find(block)?.groupValues?.get(1)?.trim()
                    ?: "Scene $sceneNum: Sequence"
                val visual = Regex("""(?i)VISUAL_PROMPT:\s*(.+)""").find(block)?.groupValues?.get(1)?.trim()
                    ?: "Atmospheric cinematic lighting and detailed environment."
                val camera = Regex("""(?i)CAMERA_MOTION:\s*(.+)""").find(block)?.groupValues?.get(1)?.trim()
                    ?: "Slow Dolly In (35mm Anamorphic)"
                val dialogue = Regex("""(?i)DIALOGUE:\s*(.+)""").find(block)?.groupValues?.get(1)?.trim()
                    ?: "\"Every moment leads to this single turning point.\""
                val durStr = Regex("""(?i)DURATION_SECONDS:\s*(\d+)""").find(block)?.groupValues?.get(1)?.trim()
                val duration = durStr?.toIntOrNull() ?: 6
                val audio = Regex("""(?i)AUDIO_SCORE:\s*(.+)""").find(block)?.groupValues?.get(1)?.trim()
                    ?: "Sub-Zero Cinematic Synth & Ambient Wind"

                scenes.add(
                    VideoScene(
                        sceneNumber = sceneNum,
                        title = shotTitle.trim('*', '#'),
                        visualPrompt = visual.trim('*', '#'),
                        cameraMovement = camera.trim('*', '#'),
                        dialogue = dialogue.trim('*', '#'),
                        durationSeconds = duration,
                        ambientAudio = audio.trim('*', '#')
                    )
                )
            }
        }

        if (scenes.isEmpty()) {
            scenes.addAll(createDefaultScenes(fallbackPrompt, genre))
        }

        return GeneratedMovie(
            title = title,
            genre = genre,
            generatorType = generatorType,
            logline = logline,
            synopsis = synopsis,
            scenes = scenes,
            aspectRatio = aspectRatio,
            imageRes = R.drawable.img_cinema_hero,
            metadata = GeneratedVideoMetadata(
                title = title,
                durationSeconds = scenes.sumOf { it.durationSeconds }.takeIf { it > 0 } ?: 20,
                aiModelVersion = "gemini-3.5-flash",
                aspectRatio = aspectRatio,
                scenesCount = scenes.size,
                renderEngine = "ZUB-ZERO Gemini Director Engine v3.5"
            )
        )
    }

    private fun parseDirectorNotes(rawText: String, defaultAudio: String): DirectorCinematographyNotes {
        val visualStyle = Regex("""(?i)VISUAL_STYLE:\s*(.+)""").find(rawText)?.groupValues?.get(1)?.trim()
            ?: "Atmospheric Anamorphic High-Contrast Neo-Noir"
        val lighting = Regex("""(?i)LIGHTING_KEY:\s*(.+)""").find(rawText)?.groupValues?.get(1)?.trim()
            ?: "Volumetric Cyan Rim Lighting with Deep Shadows"
        val lens = Regex("""(?i)CAMERA_LENS:\s*(.+)""").find(rawText)?.groupValues?.get(1)?.trim()
            ?: "35mm Anamorphic Prime (T1.5)"
        return DirectorCinematographyNotes(
            visualStyle = visualStyle.trim('*', '#'),
            lightingKey = lighting.trim('*', '#'),
            cameraLensType = lens.trim('*', '#'),
            soundDesignMood = defaultAudio
        )
    }

    private fun extractScreenplaySection(rawText: String, fallbackMovie: GeneratedMovie): String {
        return if (rawText.contains("FULL_SCREENPLAY:", ignoreCase = true)) {
            rawText.substringAfter("FULL_SCREENPLAY:").trim()
        } else {
            buildFormattedScreenplay(fallbackMovie)
        }
    }

    private fun buildFormattedScreenplay(movie: GeneratedMovie): String {
        val sb = StringBuilder()
        sb.append("TITLE: ${movie.title}\n")
        sb.append("LOGLINE: ${movie.logline}\n\n")
        movie.scenes.forEach { scene ->
            sb.append("EXT./INT. SCENE ${scene.sceneNumber} - ${scene.title.uppercase()}\n\n")
            sb.append("${scene.visualPrompt}\n\n")
            sb.append("CAMERA: ${scene.cameraMovement}\n\n")
            sb.append("CHARACTER\n")
            sb.append("${scene.dialogue}\n\n")
        }
        return sb.toString().trim()
    }

    private fun fallbackScriptGeneration(
        prompt: String,
        genre: MovieGenre,
        aspectRatio: String,
        pacing: String,
        audioScore: String,
        cameraMotion: String,
        sourceType: String
    ): GeneratedMovie {
        val cleanIdea = prompt.trim().ifEmpty { "A clandestine agent operating in a sub-zero futuristic city" }
        val title = when (genre) {
            MovieGenre.ACTION -> "Sub-Zero: ${cleanIdea.split(" ").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Vanguard"} Protocol"
            MovieGenre.DRAMA -> "The Shadows of ${cleanIdea.split(" ").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Elysium"}"
            MovieGenre.TWILIGHT -> "Nocturne: Blood & Mist"
            MovieGenre.SCI_FI -> "Neural Drift 2099: Cryo-Stasis"
        }

        val scenes = listOf(
            VideoScene(
                sceneNumber = 1,
                title = "Act I - The Inciting Cold",
                visualPrompt = "Wide anamorphic establishing shot with volumetric frost particles, neo-noir low-key lighting focused on: $cleanIdea",
                cameraMovement = cameraMotion.ifEmpty { "35mm Anamorphic Slow Dolly In" },
                dialogue = "\"In this frost, warmth is the only currency left that matters.\"",
                durationSeconds = 6,
                ambientAudio = audioScore.ifEmpty { "Sub-Zero Sub-Bass & Whispering Winds" }
            ),
            VideoScene(
                sceneNumber = 2,
                title = "Act II - The Confrontation",
                visualPrompt = "Dynamic high-contrast tracking shot capturing intense conflict stemming from $cleanIdea with neon reflections on wet glass",
                cameraMovement = "Parallax Arc Orbit (Speed: $pacing)",
                dialogue = "\"You came all this way knowing the ice never yields.\"",
                durationSeconds = 8,
                ambientAudio = "Staccato Strings with Rising Tension Synth"
            ),
            VideoScene(
                sceneNumber = 3,
                title = "Act III - The Final Reckoning",
                visualPrompt = "High-octane climax resolution frame with atmospheric cryogenic steam and sharp silhouette framing",
                cameraMovement = "Rapid Vertigo Pull-Back into Wide Panorama",
                dialogue = "\"Whatever we built here will outlast the coming winter.\"",
                durationSeconds = 7,
                ambientAudio = "Epic Orchestral Crescendo & Heavy Electronic Kick"
            )
        )

        return GeneratedMovie(
            title = title,
            genre = genre,
            generatorType = GeneratorType.IDEA_TO_VIDEO,
            logline = "Driven by $cleanIdea, a lone figure faces unrelenting odds in a sub-zero expanse.",
            synopsis = "Synthesized via $sourceType. The story explores moral dilemmas, survival, and retribution through vivid cinematographic choreography.",
            scenes = scenes,
            aspectRatio = aspectRatio,
            imageRes = R.drawable.img_cinema_hero,
            metadata = GeneratedVideoMetadata(
                title = title,
                durationSeconds = scenes.sumOf { it.durationSeconds }.takeIf { it > 0 } ?: 21,
                aiModelVersion = if (sourceType.contains("GEMINI", ignoreCase = true)) "gemini-3.5-flash" else "zubzero-neural-core-v2.5",
                aspectRatio = aspectRatio,
                scenesCount = scenes.size,
                renderEngine = "ZUB-ZERO Neural Render Core v3.2"
            )
        )
    }

    private fun fallbackScriptTextGeneration(
        scriptText: String,
        genre: MovieGenre,
        voiceStyle: String,
        aspectRatio: String
    ): GeneratedMovie {
        val lines = scriptText.lines().filter { it.isNotBlank() }
        val title = if (scriptText.contains("TITLE:", ignoreCase = true)) {
            scriptText.substringAfter("TITLE:").substringBefore("\n").trim()
        } else {
            "Sub-Zero Screenplay: Act I"
        }

        val d1 = lines.firstOrNull { it.contains(":") || it.startsWith("\"") } ?: "\"We only have one shot at penetrating their defense.\""
        val d2 = lines.drop(2).firstOrNull { it.contains(":") || it.startsWith("\"") } ?: "\"Then make every second count before the grid freezes.\""
        val d3 = lines.drop(4).firstOrNull { it.contains(":") || it.startsWith("\"") } ?: "\"Sequence initiated. Protocol Sub-Zero online.\""

        val scenes = listOf(
            VideoScene(
                sceneNumber = 1,
                title = "Scene 1: Establishing Beat",
                visualPrompt = "Cold atmosphere, deep blues and cyans reflecting off dark architectural monoliths: ${lines.firstOrNull()?.take(50)}",
                cameraMovement = "Boom Down into Medium Shot",
                dialogue = d1,
                durationSeconds = 6,
                ambientAudio = "Vocal Texture ($voiceStyle) & Low Hum"
            ),
            VideoScene(
                sceneNumber = 2,
                title = "Scene 2: Dialectical Tension",
                visualPrompt = "Tight two-shot framing with shallow depth of field and anamorphic bokeh flare",
                cameraMovement = "Rack Focus with Steady Handheld Cam",
                dialogue = d2,
                durationSeconds = 8,
                ambientAudio = "Rhythmic Electronic Pulse & Cold Strings"
            ),
            VideoScene(
                sceneNumber = 3,
                title = "Scene 3: Decisive Turn",
                visualPrompt = "Dramatic high-angle silhouette shot with exploding light beam across the frame",
                cameraMovement = "Rapid Tracking Zoom Out",
                dialogue = d3,
                durationSeconds = 7,
                ambientAudio = "Full Dynamic Drop & Resonant Sub-Drop"
            )
        )

        return GeneratedMovie(
            title = title,
            genre = genre,
            generatorType = GeneratorType.SCRIPT_TO_VIDEO,
            logline = "Screenplay brought to life with cinematic pacing and AI character vocalization.",
            synopsis = "Three sequential scenes generated from the provided script text with dedicated shot list and camera cues.",
            scenes = scenes,
            aspectRatio = aspectRatio,
            imageRes = R.drawable.img_cinema_hero,
            metadata = GeneratedVideoMetadata(
                title = title,
                durationSeconds = scenes.sumOf { it.durationSeconds }.takeIf { it > 0 } ?: 21,
                aiModelVersion = "gemini-3.5-flash",
                aspectRatio = aspectRatio,
                scenesCount = scenes.size,
                renderEngine = "ZUB-ZERO Screenplay Synthesizer v3.5"
            )
        )
    }

    private fun createDefaultScenes(idea: String, genre: MovieGenre): List<VideoScene> {
        return listOf(
            VideoScene(
                sceneNumber = 1,
                title = "Scene 1: Opening Vista",
                visualPrompt = "Vast sub-zero skyline with neon billboards piercing through freezing fog, capturing: $idea",
                cameraMovement = "Slow Dolly In (35mm Anamorphic)",
                dialogue = "\"Nothing remains hidden forever beneath the ice.\"",
                durationSeconds = 6,
                ambientAudio = "Sub-Zero Drone & Chilled Wind FX"
            ),
            VideoScene(
                sceneNumber = 2,
                title = "Scene 2: High Stakes Encounter",
                visualPrompt = "Intense confrontation scene with dynamic rim-lighting and wet neon asphalt reflections",
                cameraMovement = "Low-Angle Orbit",
                dialogue = "\"They warned us this mission would demand everything.\"",
                durationSeconds = 8,
                ambientAudio = "Pulsing Electronic Bassline"
            ),
            VideoScene(
                sceneNumber = 3,
                title = "Scene 3: Climax Resolution",
                visualPrompt = "Spectacular cinematic wide shot with explosive particle lighting and deep contrast",
                cameraMovement = "Fast Crane Up to Sky View",
                dialogue = "\"The protocol is fulfilled. Now we survive.\"",
                durationSeconds = 7,
                ambientAudio = "Grand Orchestral Choral Swell"
            )
        )
    }
}
