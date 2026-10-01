package com.example.data.model

enum class MovieGenre(val displayName: String, val tagline: String) {
    ACTION("Action", "Explosive set pieces, high-octane stunts & combat"),
    DRAMA("Drama", "Deep emotional resonance, prestige cinema & tension"),
    TWILIGHT("Twilight", "Supernatural gothic noir, mist & nocturnal romance"),
    SCI_FI("Sci-Fi Cyber", "Cryo-tech, neon megacities & AI frontiers")
}

enum class GeneratorType(val label: String, val description: String) {
    IDEA_TO_VIDEO("Idea to Video", "Pitch a concept or premise and render a complete scene reel"),
    SCRIPT_TO_VIDEO("Script to Video", "Convert screenplays and formatted dialogue into cinematic shots"),
    IMAGE_TO_VIDEO("Image to Video", "Animate concept stills, posters and pictures into fluid video")
}

data class VideoScene(
    val sceneNumber: Int,
    val title: String,
    val visualPrompt: String,
    val cameraMovement: String,
    val dialogue: String,
    val durationSeconds: Int,
    val ambientAudio: String
)

data class GeneratedMovie(
    val id: Long = 0,
    val title: String,
    val genre: MovieGenre,
    val generatorType: GeneratorType,
    val logline: String,
    val synopsis: String,
    val scenes: List<VideoScene>,
    val aspectRatio: String = "16:9 Cinema",
    val dateCreated: Long = System.currentTimeMillis(),
    val imageRes: Int = 0,
    val rating: Float = 4.9f,
    val reviewsCount: Int = 142,
    val metadata: GeneratedVideoMetadata = GeneratedVideoMetadata(
        title = title,
        durationSeconds = scenes.sumOf { it.durationSeconds }.takeIf { it > 0 } ?: 20,
        aspectRatio = aspectRatio,
        dateGenerated = dateCreated,
        scenesCount = scenes.size.takeIf { it > 0 } ?: 3
    )
)
