package com.example.data.remote.gemini

import com.example.data.model.MovieGenre
import com.example.data.model.VideoScene
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @field:Json(name = "contents") val contents: List<GeminiContent>,
    @field:Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @field:Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @field:Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @field:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @field:Json(name = "temperature") val temperature: Float? = 0.7f,
    @field:Json(name = "topP") val topP: Float? = 0.95f,
    @field:Json(name = "topK") val topK: Int? = 40
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @field:Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @field:Json(name = "content") val content: GeminiContent? = null
)

/**
 * Directorial input request for prompt-based movie script synthesis with Gemini API.
 */
data class MovieScriptPromptRequest(
    val prompt: String,
    val genre: MovieGenre = MovieGenre.ACTION,
    val aspectRatio: String = "16:9 Cinema",
    val pacing: String = "Fast Paced / Dynamic",
    val audioMood: String = "Epic Cyber-Orchestral",
    val cameraMotion: String = "Slow Dolly Zoom",
    val characterArchetype: String = "Cyber-Operative Specialist",
    val targetScenesCount: Int = 3
)

/**
 * Complete AI-generated movie screenplay result with scene breakdown and directorial notes.
 */
data class MovieScriptGenerationResult(
    val title: String,
    val logline: String,
    val synopsis: String,
    val fullScreenplay: String,
    val scenes: List<VideoScene>,
    val directorNotes: DirectorCinematographyNotes,
    val isLiveGeminiCall: Boolean,
    val modelIdentifier: String = "gemini-3.5-flash",
    val latencyMs: Long = 0,
    val rawModelResponse: String = ""
)

/**
 * Technical camera and lighting notes parsed from the Gemini response.
 */
data class DirectorCinematographyNotes(
    val visualStyle: String,
    val lightingKey: String,
    val cameraLensType: String,
    val soundDesignMood: String
)

data class ScriptAnalysisResult(
    val title: String,
    val logline: String,
    val synopsis: String,
    val screenplayFormatted: String,
    val scenes: List<GeneratedSceneDetail>,
    val cinematographyNotes: String,
    val rawAiText: String
)

data class GeneratedSceneDetail(
    val sceneNumber: Int,
    val title: String,
    val slugline: String,
    val visualPrompt: String,
    val cameraMovement: String,
    val dialogue: String,
    val durationSeconds: Int,
    val ambientAudio: String
)
