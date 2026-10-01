package com.example.data.repository

import com.example.R
import com.example.data.local.dao.MovieDao
import com.example.data.local.dao.VideoPromptSubmissionDao
import com.example.data.local.entity.MovieProjectEntity
import com.example.data.local.entity.VideoPromptSubmissionEntity
import com.example.data.model.GeneratedMovie
import com.example.data.model.GeneratedVideoMetadata
import com.example.data.model.GenerationStageDetail
import com.example.data.model.GeneratorType
import com.example.data.model.IdeaToVideoPromptRequest
import com.example.data.model.IdeaToVideoPromptSubmission
import com.example.data.model.MovieGenre
import com.example.data.model.VideoGenerationStage
import com.example.data.model.VideoGenerationStatus
import com.example.data.model.VideoScene
import com.example.data.remote.gemini.GeminiMovieScriptService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository coordinating AI video prompt submission lifecycle, Room persistence,
 * Gemini AI script synthesis, neural rendering pipeline, and real-time status tracking.
 */
class VideoPromptSubmissionRepository(
    private val submissionDao: VideoPromptSubmissionDao,
    private val movieDao: MovieDao,
    private val geminiService: GeminiMovieScriptService = GeminiMovieScriptService()
) {

    // Map of active pipeline jobs for cancellation and live status management
    private val activeJobs = ConcurrentHashMap<String, Job>()

    /**
     * Flow of all submitted prompts with computed stage timelines and status.
     */
    val submissionsFlow: Flow<List<IdeaToVideoPromptSubmission>> = submissionDao.getAllSubmissions()
        .map { entities ->
            entities.map { entityToDomain(it) }
        }
        .distinctUntilChanged()

    /**
     * Observe a specific submission in real-time.
     */
    fun getSubmissionFlow(id: String): Flow<IdeaToVideoPromptSubmission?> {
        return submissionDao.getSubmissionFlowById(id).map { entity ->
            entity?.let { entityToDomain(it) }
        }
    }

    /**
     * Get a submission once by ID.
     */
    suspend fun getSubmissionById(id: String): IdeaToVideoPromptSubmission? = withContext(Dispatchers.IO) {
        val entity = submissionDao.getSubmissionById(id)
        entity?.let { entityToDomain(it) }
    }

    /**
     * Submit a new AI video prompt and register it in the neural pipeline.
     */
    suspend fun submitPrompt(
        request: IdeaToVideoPromptRequest,
        scope: CoroutineScope,
        onMovieReady: (GeneratedMovie) -> Unit = {}
    ): IdeaToVideoPromptSubmission = withContext(Dispatchers.IO) {
        val submissionId = "SUB-${System.currentTimeMillis() % 100000}-${UUID.randomUUID().toString().take(4).uppercase()}"
        val initialTitle = deriveWorkingTitle(request.prompt, request.genre)

        val initialSubmission = IdeaToVideoPromptSubmission(
            id = submissionId,
            prompt = request.prompt,
            title = initialTitle,
            genre = request.genre,
            aspectRatio = request.aspectRatio,
            pacing = request.pacing,
            cameraMotion = request.cameraMotion,
            audioMood = request.audioMood,
            durationSeconds = request.durationSeconds,
            coinCost = request.coinCost,
            submittedAt = System.currentTimeMillis(),
            status = VideoGenerationStatus.QUEUED,
            progress = 0.05f,
            currentStageName = "Job Ingestion & Validation",
            currentStageIndex = 1,
            stageTimeline = buildTimeline(VideoGenerationStatus.QUEUED, 0.05f),
            estimatedTimeRemainingSeconds = 14
        )

        submissionDao.insertSubmission(domainToEntity(initialSubmission))

        // Launch pipeline processing in background
        val job = scope.launch(Dispatchers.IO) {
            runPipeline(submissionId, request, onMovieReady)
        }
        activeJobs[submissionId] = job

        return@withContext initialSubmission
    }

    /**
     * Run the multi-stage AI video generation pipeline with granular progress tracking.
     */
    private suspend fun runPipeline(
        submissionId: String,
        request: IdeaToVideoPromptRequest,
        onMovieReady: (GeneratedMovie) -> Unit
    ) {
        try {
            // Stage 1: Queued & Validating
            updateSubmissionState(
                id = submissionId,
                status = VideoGenerationStatus.QUEUED,
                progress = 0.12f,
                stageName = "Job Ingested: Validating parameters",
                stageIndex = 1,
                remainingSeconds = 14
            )
            delay(400)

            // Stage 2: Prompt Analysis & Gemini Screenplay Generation
            updateSubmissionState(
                id = submissionId,
                status = VideoGenerationStatus.ANALYZING_PROMPT,
                progress = 0.25f,
                stageName = "Gemini AI: Parsing narrative arc & scene beats",
                stageIndex = 2,
                remainingSeconds = 11
            )
            delay(450)

            updateSubmissionState(
                id = submissionId,
                status = VideoGenerationStatus.SCRIPT_SYNTHESIS,
                progress = 0.40f,
                stageName = "Screenplay Synthesis: Generating dialogue & camera cues",
                stageIndex = 2,
                remainingSeconds = 9
            )

            // Execute Gemini Script Service
            val generatedMovie = geminiService.generateMovieFromIdea(
                prompt = request.prompt,
                genre = request.genre,
                aspectRatio = request.aspectRatio,
                pacing = request.pacing,
                audioScore = request.audioMood,
                cameraMotion = request.cameraMotion
            )

            // Stage 3: Camera Motion & 3D Optics Rigging
            updateSubmissionState(
                id = submissionId,
                status = VideoGenerationStatus.CAMERA_RIGGING,
                progress = 0.60f,
                stageName = "Camera Rigging: Trajectory (${request.cameraMotion}) & lenses",
                stageIndex = 3,
                remainingSeconds = 6
            )
            delay(500)

            // Stage 4: Neural Volumetric Rendering
            updateSubmissionState(
                id = submissionId,
                status = VideoGenerationStatus.NEURAL_RENDERING,
                progress = 0.80f,
                stageName = "Neural Rendering: Volumetric 3D shaders & frost particles",
                stageIndex = 4,
                remainingSeconds = 4
            )
            delay(550)

            // Stage 5: Audio Mastering & Packaging
            updateSubmissionState(
                id = submissionId,
                status = VideoGenerationStatus.AUDIO_MASTERING,
                progress = 0.94f,
                stageName = "Audio Mastering: ${request.audioMood} mix",
                stageIndex = 5,
                remainingSeconds = 1
            )
            delay(450)

            // Final: Completed
            val finalMovie = generatedMovie.copy(
                aspectRatio = request.aspectRatio,
                imageRes = R.drawable.img_cinema_hero,
                metadata = GeneratedVideoMetadata(
                    title = generatedMovie.title,
                    durationSeconds = request.durationSeconds,
                    aiModelVersion = "gemini-3.5-flash",
                    resolution = request.resolution,
                    aspectRatio = request.aspectRatio,
                    renderEngine = "ZUB-ZERO Neural Render Core v3.2",
                    dateGenerated = System.currentTimeMillis(),
                    scenesCount = generatedMovie.scenes.size
                )
            )

            // Save movie to Room movie database
            val scenesJson = serializeScenes(finalMovie.scenes)
            val movieEntity = MovieProjectEntity(
                title = finalMovie.title,
                genre = finalMovie.genre.name,
                generatorType = GeneratorType.IDEA_TO_VIDEO.name,
                logline = finalMovie.logline,
                synopsis = finalMovie.synopsis,
                scenesJson = scenesJson,
                aspectRatio = finalMovie.aspectRatio,
                dateCreated = System.currentTimeMillis()
            )
            val savedMovieId = movieDao.insertMovie(movieEntity)
            val completedMovie = finalMovie.copy(id = savedMovieId)

            // Update submission record as completed
            val currentRecord = submissionDao.getSubmissionById(submissionId)
            if (currentRecord != null && !currentRecord.isCancelled) {
                val movieJson = serializeMovie(completedMovie)
                val completedEntity = currentRecord.copy(
                    title = completedMovie.title,
                    status = VideoGenerationStatus.COMPLETED.name,
                    progress = 1.0f,
                    currentStageName = "4K Cinema Render Completed",
                    currentStageIndex = 5,
                    estimatedTimeRemainingSeconds = 0,
                    completedAt = System.currentTimeMillis(),
                    resultMovieJson = movieJson
                )
                submissionDao.updateSubmission(completedEntity)
                withContext(Dispatchers.Main) {
                    onMovieReady(completedMovie)
                }
            }

        } catch (e: CancellationException) {
            // Task was cancelled
            submissionDao.cancelSubmission(submissionId)
        } catch (e: Exception) {
            val currentRecord = submissionDao.getSubmissionById(submissionId)
            if (currentRecord != null && !currentRecord.isCancelled) {
                val failedEntity = currentRecord.copy(
                    status = VideoGenerationStatus.FAILED.name,
                    failureReason = e.localizedMessage ?: "Unknown generation pipeline error"
                )
                submissionDao.updateSubmission(failedEntity)
            }
        } finally {
            activeJobs.remove(submissionId)
        }
    }

    private suspend fun updateSubmissionState(
        id: String,
        status: VideoGenerationStatus,
        progress: Float,
        stageName: String,
        stageIndex: Int,
        remainingSeconds: Int
    ) {
        val current = submissionDao.getSubmissionById(id) ?: return
        if (current.isCancelled) return

        val updated = current.copy(
            status = status.name,
            progress = progress,
            currentStageName = stageName,
            currentStageIndex = stageIndex,
            estimatedTimeRemainingSeconds = remainingSeconds
        )
        submissionDao.updateSubmission(updated)
    }

    /**
     * Cancel an active or queued submission.
     */
    suspend fun cancelSubmission(id: String) = withContext(Dispatchers.IO) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        submissionDao.cancelSubmission(id)
    }

    /**
     * Retry a failed or cancelled submission.
     */
    suspend fun retrySubmission(
        id: String,
        scope: CoroutineScope,
        onMovieReady: (GeneratedMovie) -> Unit = {}
    ): Boolean = withContext(Dispatchers.IO) {
        val current = submissionDao.getSubmissionById(id) ?: return@withContext false
        val request = IdeaToVideoPromptRequest(
            prompt = current.prompt,
            genre = parseGenre(current.genre),
            aspectRatio = current.aspectRatio,
            pacing = current.pacing,
            cameraMotion = current.cameraMotion,
            audioMood = current.audioMood,
            durationSeconds = current.durationSeconds,
            coinCost = current.coinCost
        )

        // Reset state
        val reset = current.copy(
            status = VideoGenerationStatus.QUEUED.name,
            progress = 0.05f,
            currentStageName = "Re-queued for pipeline execution",
            currentStageIndex = 1,
            estimatedTimeRemainingSeconds = 14,
            failureReason = null,
            isCancelled = false
        )
        submissionDao.updateSubmission(reset)

        val job = scope.launch(Dispatchers.IO) {
            runPipeline(id, request, onMovieReady)
        }
        activeJobs[id] = job
        return@withContext true
    }

    /**
     * Delete a submission from history.
     */
    suspend fun deleteSubmission(id: String) = withContext(Dispatchers.IO) {
        activeJobs[id]?.cancel()
        activeJobs.remove(id)
        submissionDao.deleteSubmissionById(id)
    }

    /**
     * Ensure at least one showcase completed submission is seeded if empty.
     */
    suspend fun ensureDefaultSubmissionsSeeded() = withContext(Dispatchers.IO) {
        val existing = submissionDao.getAllSubmissions()
        // Check if database has any record
        val sample = submissionDao.getSubmissionById("SUB-SHOWCASE-01")
        if (sample == null) {
            val sampleScenes = listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "Opening: Arrival at Neo-Shibuya",
                    visualPrompt = "Wide anamorphic skyline with neon reflections on icy skyscrapers",
                    cameraMovement = "Slow Dolly In (35mm Anamorphic)",
                    dialogue = "\"The protocol has started. There is no turning back.\"",
                    durationSeconds = 6,
                    ambientAudio = "Sub-Zero Synthesizer & Howling Wind"
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "Pursuit on Frozen Elevated Rail",
                    visualPrompt = "Dynamic high-contrast tracking shot with rain-slicked rails and cyber cruisers",
                    cameraMovement = "360° Circular Orbit",
                    dialogue = "\"They locked down all perimeter sectors. We jump now!\"",
                    durationSeconds = 8,
                    ambientAudio = "Rising Tension Strings & Heavy Sub-Bass"
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "Climactic Server Core Breach",
                    visualPrompt = "Volumetric cryogenic steam erupting as rogue mainframe decompresses",
                    cameraMovement = "Rapid Vertigo Zoom Out",
                    dialogue = "\"Sub-Zero Core neutralized. Extracting payload.\"",
                    durationSeconds = 6,
                    ambientAudio = "Grand Orchestral Choral Crescendo"
                )
            )

            val sampleMovie = GeneratedMovie(
                title = "Sub-Zero: Tokyo Protocol",
                genre = MovieGenre.ACTION,
                generatorType = GeneratorType.IDEA_TO_VIDEO,
                logline = "A rogue operative infiltrates an icy subterranean datacenter during a total communications blackout.",
                synopsis = "High-octane action thriller through frosted neon skylines and underground server bunkers.",
                scenes = sampleScenes,
                aspectRatio = "16:9 Cinema",
                imageRes = R.drawable.img_cinema_hero
            )

            val seededSubmission = IdeaToVideoPromptSubmission(
                id = "SUB-SHOWCASE-01",
                prompt = "A clandestine cyber-agent infiltrating an orbital cryogenic base over Tokyo",
                title = "Sub-Zero: Tokyo Protocol",
                genre = MovieGenre.ACTION,
                aspectRatio = "16:9 Cinema",
                pacing = "Fast Paced / Dynamic",
                cameraMotion = "Slow Dolly Zoom",
                audioMood = "Epic Cyber-Orchestral",
                durationSeconds = 20,
                coinCost = 50,
                submittedAt = System.currentTimeMillis() - (1000L * 60 * 45),
                completedAt = System.currentTimeMillis() - (1000L * 60 * 44),
                status = VideoGenerationStatus.COMPLETED,
                progress = 1.0f,
                currentStageName = "4K Cinema Render Completed",
                currentStageIndex = 5,
                stageTimeline = buildTimeline(VideoGenerationStatus.COMPLETED, 1.0f),
                estimatedTimeRemainingSeconds = 0,
                resultMovie = sampleMovie
            )

            submissionDao.insertSubmission(domainToEntity(seededSubmission))
        }
    }

    // Helper functions

    private fun entityToDomain(entity: VideoPromptSubmissionEntity): IdeaToVideoPromptSubmission {
        val genre = parseGenre(entity.genre)
        val status = parseStatus(entity.status)
        val resultMovie = entity.resultMovieJson?.let { deserializeMovie(it, genre) }
        val timeline = buildTimeline(status, entity.progress)

        return IdeaToVideoPromptSubmission(
            id = entity.id,
            prompt = entity.prompt,
            title = entity.title,
            genre = genre,
            aspectRatio = entity.aspectRatio,
            pacing = entity.pacing,
            cameraMotion = entity.cameraMotion,
            audioMood = entity.audioMood,
            durationSeconds = entity.durationSeconds,
            coinCost = entity.coinCost,
            submittedAt = entity.submittedAt,
            completedAt = entity.completedAt,
            status = status,
            progress = entity.progress,
            currentStageName = entity.currentStageName,
            currentStageIndex = entity.currentStageIndex,
            stageTimeline = timeline,
            estimatedTimeRemainingSeconds = entity.estimatedTimeRemainingSeconds,
            resultMovie = resultMovie,
            failureReason = entity.failureReason,
            isCancelled = entity.isCancelled
        )
    }

    private fun domainToEntity(domain: IdeaToVideoPromptSubmission): VideoPromptSubmissionEntity {
        return VideoPromptSubmissionEntity(
            id = domain.id,
            prompt = domain.prompt,
            title = domain.title,
            genre = domain.genre.name,
            aspectRatio = domain.aspectRatio,
            pacing = domain.pacing,
            cameraMotion = domain.cameraMotion,
            audioMood = domain.audioMood,
            durationSeconds = domain.durationSeconds,
            coinCost = domain.coinCost,
            submittedAt = domain.submittedAt,
            completedAt = domain.completedAt,
            status = domain.status.name,
            progress = domain.progress,
            currentStageName = domain.currentStageName,
            currentStageIndex = domain.currentStageIndex,
            estimatedTimeRemainingSeconds = domain.estimatedTimeRemainingSeconds,
            resultMovieJson = domain.resultMovie?.let { serializeMovie(it) },
            failureReason = domain.failureReason,
            isCancelled = domain.isCancelled
        )
    }

    private fun buildTimeline(status: VideoGenerationStatus, currentProgress: Float): List<GenerationStageDetail> {
        val stages = listOf(
            VideoGenerationStage.STAGE_QUEUE to Pair("Job Ingestion & Verification", "Validating directorial settings & allocating render core"),
            VideoGenerationStage.STAGE_PROMPT_GEMINI to Pair("Gemini AI Script & Story Parsing", "Extracting characters, 3-act narrative beats and screenplay dialogue"),
            VideoGenerationStage.STAGE_CAMERA_BLOCKING to Pair("Camera Rigging & Lens Optics", "Synthesizing dynamic trajectory paths, focal lengths and depth-of-field"),
            VideoGenerationStage.STAGE_NEURAL_SYNTHESIS to Pair("Neural Volumetric Frame Synthesis", "Rendering multi-angle volumetric frames and frost particle lighting"),
            VideoGenerationStage.STAGE_AUDIO_EXPORT to Pair("Soundstage Mastering & Packaging", "Mixing orchestral soundscapes, foley audio, and final 4K cinema packaging")
        )

        return stages.mapIndexed { index, (stage, details) ->
            val stageNum = index + 1
            val isCompleted = when {
                status == VideoGenerationStatus.COMPLETED -> true
                status == VideoGenerationStatus.FAILED || status == VideoGenerationStatus.CANCELLED -> false
                currentProgress >= stage.targetProgress -> true
                else -> false
            }
            val isInProgress = when {
                status.isTerminal -> false
                isCompleted -> false
                index == 0 && currentProgress < stages[0].first.targetProgress -> true
                index > 0 && currentProgress >= stages[index - 1].first.targetProgress && currentProgress < stage.targetProgress -> true
                else -> false
            }

            GenerationStageDetail(
                stage = stage,
                title = details.first,
                description = details.second,
                isCompleted = isCompleted,
                isInProgress = isInProgress,
                progress = if (isCompleted) 1.0f else if (isInProgress) ((currentProgress - (if (index > 0) stages[index - 1].first.targetProgress else 0f)) / (stage.targetProgress - (if (index > 0) stages[index - 1].first.targetProgress else 0f))).coerceIn(0f, 1f) else 0f
            )
        }
    }

    private fun parseGenre(name: String): MovieGenre {
        return try {
            MovieGenre.valueOf(name)
        } catch (e: Exception) {
            MovieGenre.ACTION
        }
    }

    private fun parseStatus(name: String): VideoGenerationStatus {
        return try {
            VideoGenerationStatus.valueOf(name)
        } catch (e: Exception) {
            VideoGenerationStatus.QUEUED
        }
    }

    private fun deriveWorkingTitle(prompt: String, genre: MovieGenre): String {
        val clean = prompt.trim()
        val word = clean.split(" ").firstOrNull { it.length > 3 }?.replaceFirstChar { it.uppercase() } ?: "Vanguard"
        return when (genre) {
            MovieGenre.ACTION -> "Sub-Zero: $word Strike"
            MovieGenre.DRAMA -> "The Shadows of $word"
            MovieGenre.TWILIGHT -> "Nocturne: Blood & $word"
            MovieGenre.SCI_FI -> "Neural Drift: $word"
        }
    }

    private fun serializeMovie(movie: GeneratedMovie): String {
        val json = JSONObject()
        json.put("id", movie.id)
        json.put("title", movie.title)
        json.put("genre", movie.genre.name)
        json.put("generatorType", movie.generatorType.name)
        json.put("logline", movie.logline)
        json.put("synopsis", movie.synopsis)
        json.put("aspectRatio", movie.aspectRatio)
        json.put("dateCreated", movie.dateCreated)

        val scenesArray = JSONArray()
        movie.scenes.forEach { scene ->
            val sceneObj = JSONObject()
            sceneObj.put("sceneNumber", scene.sceneNumber)
            sceneObj.put("title", scene.title)
            sceneObj.put("visualPrompt", scene.visualPrompt)
            sceneObj.put("cameraMovement", scene.cameraMovement)
            sceneObj.put("dialogue", scene.dialogue)
            sceneObj.put("durationSeconds", scene.durationSeconds)
            sceneObj.put("ambientAudio", scene.ambientAudio)
            scenesArray.put(sceneObj)
        }
        json.put("scenes", scenesArray)
        return json.toString()
    }

    private fun deserializeMovie(jsonStr: String, defaultGenre: MovieGenre): GeneratedMovie? {
        return try {
            val json = JSONObject(jsonStr)
            val scenesArray = json.optJSONArray("scenes") ?: JSONArray()
            val scenes = mutableListOf<VideoScene>()
            for (i in 0 until scenesArray.length()) {
                val item = scenesArray.getJSONObject(i)
                scenes.add(
                    VideoScene(
                        sceneNumber = item.optInt("sceneNumber", i + 1),
                        title = item.optString("title", "Scene ${i + 1}"),
                        visualPrompt = item.optString("visualPrompt", ""),
                        cameraMovement = item.optString("cameraMovement", "35mm Dolly"),
                        dialogue = item.optString("dialogue", ""),
                        durationSeconds = item.optInt("durationSeconds", 6),
                        ambientAudio = item.optString("ambientAudio", "")
                    )
                )
            }
            GeneratedMovie(
                id = json.optLong("id", 0),
                title = json.optString("title", "Generated Film"),
                genre = defaultGenre,
                generatorType = GeneratorType.IDEA_TO_VIDEO,
                logline = json.optString("logline", ""),
                synopsis = json.optString("synopsis", ""),
                scenes = scenes,
                aspectRatio = json.optString("aspectRatio", "16:9 Cinema"),
                dateCreated = json.optLong("dateCreated", System.currentTimeMillis()),
                imageRes = R.drawable.img_cinema_hero
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun serializeScenes(scenes: List<VideoScene>): String {
        val array = JSONArray()
        scenes.forEach { scene ->
            val item = JSONObject()
            item.put("sceneNumber", scene.sceneNumber)
            item.put("title", scene.title)
            item.put("visualPrompt", scene.visualPrompt)
            item.put("cameraMovement", scene.cameraMovement)
            item.put("dialogue", scene.dialogue)
            item.put("durationSeconds", scene.durationSeconds)
            item.put("ambientAudio", scene.ambientAudio)
            array.put(item)
        }
        return array.toString()
    }
}
