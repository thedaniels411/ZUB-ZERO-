package com.example.data.remote.gemini

import com.example.data.model.GeneratedMovie
import com.example.data.model.MovieGenre
import com.example.data.model.VideoScene

/**
 * Service contract for interfacing with the Gemini API to handle prompt-based
 * movie script generation, scene breakdowns, and storyboard direction for the ZUB-ZERO video studio.
 */
interface GeminiScriptGeneratorService {

    /**
     * Checks if a valid Gemini API key is configured in BuildConfig.
     */
    fun isGeminiApiConfigured(): Boolean

    /**
     * Generates a complete movie package (title, logline, synopsis, 3-act scenes) from a user idea prompt.
     */
    suspend fun generateMovieFromIdea(
        prompt: String,
        genre: MovieGenre,
        aspectRatio: String = "16:9 Cinema",
        pacing: String = "Fast Paced / Dynamic",
        audioScore: String = "Epic Cyber-Orchestral",
        cameraMotion: String = "Slow Dolly Zoom"
    ): GeneratedMovie

    /**
     * Generates a comprehensive screenplay with full scene details and director notes.
     */
    suspend fun generateMovieScript(
        request: MovieScriptPromptRequest
    ): MovieScriptGenerationResult

    /**
     * Expands an existing formatted screenplay into multi-angle cinematic scenes for video rendering.
     */
    suspend fun expandScriptToVideo(
        scriptText: String,
        genre: MovieGenre,
        voiceStyle: String = "Deep Sub-Zero Baritone",
        aspectRatio: String = "2.39:1 Anamorphic"
    ): GeneratedMovie

    /**
     * Synthesizes 3 sequential storyboard scenes from an idea prompt.
     */
    suspend fun generateCinematicScenes(
        prompt: String,
        genre: MovieGenre
    ): List<VideoScene>
}
