package com.example.data.model

/**
 * Status enumeration for the AI video generation pipeline.
 */
enum class VideoGenerationStatus(
    val displayName: String,
    val stageDescription: String,
    val isTerminal: Boolean,
    val isSuccess: Boolean
) {
    QUEUED(
        displayName = "Queued",
        stageDescription = "Waiting in neural pipeline queue...",
        isTerminal = false,
        isSuccess = false
    ),
    ANALYZING_PROMPT(
        displayName = "Prompt Analysis",
        stageDescription = "Parsing concept, characters & thematic beats with Gemini AI...",
        isTerminal = false,
        isSuccess = false
    ),
    SCRIPT_SYNTHESIS(
        displayName = "Screenplay Synthesis",
        stageDescription = "Synthesizing 3-act scene breakdowns and dialogue cues...",
        isTerminal = false,
        isSuccess = false
    ),
    CAMERA_RIGGING(
        displayName = "Camera Rigging",
        stageDescription = "Calculating optics, focal length & trajectory paths...",
        isTerminal = false,
        isSuccess = false
    ),
    NEURAL_RENDERING(
        displayName = "Neural Rendering",
        stageDescription = "Synthesizing volumetric 3D frost frames & visual shaders...",
        isTerminal = false,
        isSuccess = false
    ),
    AUDIO_MASTERING(
        displayName = "Audio Mastering",
        stageDescription = "Mastering multi-channel cinematic audio & ambient SFX...",
        isTerminal = false,
        isSuccess = false
    ),
    COMPLETED(
        displayName = "Completed",
        stageDescription = "Render finalized! Ready for 4K cinema playback.",
        isTerminal = true,
        isSuccess = true
    ),
    FAILED(
        displayName = "Failed",
        stageDescription = "Generation interrupted or encountered an engine error.",
        isTerminal = true,
        isSuccess = false
    ),
    CANCELLED(
        displayName = "Cancelled",
        stageDescription = "Submission was cancelled by the director.",
        isTerminal = true,
        isSuccess = false
    )
}

/**
 * The 5 sequential pipeline stages of the idea-to-video studio.
 */
enum class VideoGenerationStage(
    val stepIndex: Int,
    val stageTitle: String,
    val targetProgress: Float
) {
    STAGE_QUEUE(1, "Job Ingestion & Validation", 0.10f),
    STAGE_PROMPT_GEMINI(2, "Gemini Script & Story Parsing", 0.35f),
    STAGE_CAMERA_BLOCKING(3, "Camera Motion & Optics Rig", 0.60f),
    STAGE_NEURAL_SYNTHESIS(4, "Neural Volumetric Synthesis", 0.85f),
    STAGE_AUDIO_EXPORT(5, "Soundstage Mastering & Final Packaging", 1.0f)
}

/**
 * Stage detail tracking item for visual timeline representation in UI.
 */
data class GenerationStageDetail(
    val stage: VideoGenerationStage,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isInProgress: Boolean = false,
    val progress: Float = 0f,
    val timestamp: Long? = null
)

/**
 * Request payload for creating an Idea-To-Video submission.
 */
data class IdeaToVideoPromptRequest(
    val prompt: String,
    val genre: MovieGenre = MovieGenre.ACTION,
    val aspectRatio: String = "16:9 Cinema",
    val pacing: String = "Fast Paced / Dynamic",
    val cameraMotion: String = "Slow Dolly Zoom",
    val audioMood: String = "Epic Cyber-Orchestral",
    val durationSeconds: Int = 20,
    val coinCost: Int = 50,
    val resolution: String = "4K UHD Cinema"
)

/**
 * Full state representation of an AI Video Prompt Submission with end-to-end status tracking.
 */
data class IdeaToVideoPromptSubmission(
    val id: String,
    val prompt: String,
    val title: String,
    val genre: MovieGenre,
    val aspectRatio: String = "16:9 Cinema",
    val pacing: String = "Balanced Cinematic",
    val cameraMotion: String = "Slow Dolly Zoom",
    val audioMood: String = "Sub-Zero Ambient Drone",
    val durationSeconds: Int = 20,
    val coinCost: Int = 50,
    val submittedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val status: VideoGenerationStatus = VideoGenerationStatus.QUEUED,
    val progress: Float = 0f,
    val currentStageName: String = "Job Ingestion",
    val currentStageIndex: Int = 1,
    val totalStages: Int = 5,
    val stageTimeline: List<GenerationStageDetail> = emptyList(),
    val estimatedTimeRemainingSeconds: Int = 15,
    val resultMovie: GeneratedMovie? = null,
    val failureReason: String? = null,
    val isCancelled: Boolean = false
) {
    val isFinished: Boolean get() = status.isTerminal
    val isSuccess: Boolean get() = status == VideoGenerationStatus.COMPLETED
    val isInProgress: Boolean get() = !status.isTerminal
}

/**
 * Filter options for the submissions list.
 */
enum class SubmissionFilter(val label: String) {
    ALL("All Submissions"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    FAILED("Failed / Cancelled")
}

/**
 * UI State for the Idea-To-Video Studio and Status Tracker.
 */
data class IdeaToVideoStudioUiState(
    val submissions: List<IdeaToVideoPromptSubmission> = emptyList(),
    val activeSubmission: IdeaToVideoPromptSubmission? = null,
    val selectedSubmissionForTracking: IdeaToVideoPromptSubmission? = null,
    val currentFilter: SubmissionFilter = SubmissionFilter.ALL,
    val searchQuery: String = "",
    val isSubmitting: Boolean = false,
    val statusMessage: String = "",
    val errorNotification: String? = null,
    val totalSecondsRendered: Int = 0,
    val totalMoviesCompleted: Int = 0
)
