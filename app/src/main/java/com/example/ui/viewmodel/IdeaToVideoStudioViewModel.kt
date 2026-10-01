package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ZubZeroDatabase
import com.example.data.model.CoinDeductionResult
import com.example.data.model.GeneratedMovie
import com.example.data.model.IdeaToVideoPromptRequest
import com.example.data.model.IdeaToVideoPromptSubmission
import com.example.data.model.IdeaToVideoStudioUiState
import com.example.data.model.MovieGenre
import com.example.data.model.SubmissionFilter
import com.example.data.model.UserProfile
import com.example.data.model.VideoGenerationStatus
import com.example.data.repository.UserProfileRepository
import com.example.data.repository.VideoPromptSubmissionRepository
import com.example.data.service.CoinManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Architectural ViewModel managing AI Video Prompt Submissions, multi-stage status tracking,
 * pipeline lifecycle, cancellation, retries, and persistence for the Idea-to-Video Studio.
 */
class IdeaToVideoStudioViewModel(
    application: Application,
    private val repository: VideoPromptSubmissionRepository? = null,
    private val coinManager: CoinManager? = null,
    private val userProfileRepo: UserProfileRepository? = null
) : AndroidViewModel(application) {

    private val db = ZubZeroDatabase.getInstance(application)
    private val subRepo = repository ?: VideoPromptSubmissionRepository(db.videoPromptSubmissionDao(), db.movieDao())
    private val coins = coinManager ?: CoinManager(db.coinDao())
    private val profiles = userProfileRepo ?: UserProfileRepository(db.userProfileDao())

    // Directorial Prompt Form Inputs
    val promptInput = MutableStateFlow("")
    val selectedGenre = MutableStateFlow(MovieGenre.ACTION)
    val selectedAspectRatio = MutableStateFlow("16:9 Cinema")
    val selectedPacing = MutableStateFlow("Balanced Cinematic")
    val selectedCameraMotion = MutableStateFlow("Slow Dolly Zoom")
    val selectedAudioMood = MutableStateFlow("Epic Cyber-Orchestral")
    val selectedResolution = MutableStateFlow("4K UHD Cinema")
    val videoDurationSeconds = MutableStateFlow(20)

    // Filtering & Search
    private val _currentFilter = MutableStateFlow(SubmissionFilter.ALL)
    val currentFilter: StateFlow<SubmissionFilter> = _currentFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Tracking Selection
    private val _selectedSubmissionId = MutableStateFlow<String?>(null)
    val selectedSubmissionId: StateFlow<String?> = _selectedSubmissionId.asStateFlow()

    // Status / Alert Notification
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _errorNotification = MutableStateFlow<String?>(null)
    val errorNotification: StateFlow<String?> = _errorNotification.asStateFlow()

    // Observable flow of all submissions from Room DB
    val allSubmissions: StateFlow<List<IdeaToVideoPromptSubmission>> = subRepo.submissionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active in-progress submission (if any)
    val activeSubmission: StateFlow<IdeaToVideoPromptSubmission?> = allSubmissions
        .combine(_selectedSubmissionId) { list, selectedId ->
            if (selectedId != null) {
                list.find { it.id == selectedId }
            } else {
                list.find { it.isInProgress } ?: list.firstOrNull()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Unified UI State Flow
    val uiState: StateFlow<IdeaToVideoStudioUiState> = combine(
        allSubmissions,
        _currentFilter,
        _searchQuery,
        _selectedSubmissionId,
        _isSubmitting,
        _statusMessage,
        _errorNotification
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val submissions = args[0] as List<IdeaToVideoPromptSubmission>
        val filter = args[1] as SubmissionFilter
        val query = args[2] as String
        val selectedId = args[3] as String?
        val submitting = args[4] as Boolean
        val statusMsg = args[5] as String
        val errorMsg = args[6] as String?

        val filtered = submissions.filter { sub ->
            val matchesFilter = when (filter) {
                SubmissionFilter.ALL -> true
                SubmissionFilter.IN_PROGRESS -> sub.isInProgress
                SubmissionFilter.COMPLETED -> sub.isSuccess
                SubmissionFilter.FAILED -> sub.status == VideoGenerationStatus.FAILED || sub.status == VideoGenerationStatus.CANCELLED
            }
            val matchesQuery = if (query.isBlank()) true else {
                sub.prompt.contains(query, ignoreCase = true) ||
                        sub.title.contains(query, ignoreCase = true) ||
                        sub.genre.displayName.contains(query, ignoreCase = true) ||
                        sub.id.contains(query, ignoreCase = true)
            }
            matchesFilter && matchesQuery
        }

        val active = submissions.find { it.isInProgress }
        val selected = if (selectedId != null) submissions.find { it.id == selectedId } else active

        val totalCompleted = submissions.count { it.isSuccess }
        val totalSeconds = submissions.filter { it.isSuccess }.sumOf { it.durationSeconds }

        IdeaToVideoStudioUiState(
            submissions = filtered,
            activeSubmission = active,
            selectedSubmissionForTracking = selected,
            currentFilter = filter,
            searchQuery = query,
            isSubmitting = submitting,
            statusMessage = statusMsg,
            errorNotification = errorMsg,
            totalSecondsRendered = totalSeconds,
            totalMoviesCompleted = totalCompleted
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        IdeaToVideoStudioUiState()
    )

    init {
        viewModelScope.launch {
            subRepo.ensureDefaultSubmissionsSeeded()
        }
    }

    /**
     * Submit an AI video prompt from the idea-to-video studio.
     */
    fun submitPrompt(
        currentUserProfile: UserProfile,
        onInsufficientCoins: (deficit: Int) -> Unit = {},
        onRegistrationRequired: () -> Unit = {},
        onMovieReady: (GeneratedMovie) -> Unit = {}
    ) {
        val prompt = promptInput.value.trim()
        if (prompt.isEmpty()) {
            _errorNotification.value = "Please enter an idea or concept prompt for video generation."
            return
        }

        if (!currentUserProfile.isRegistered) {
            onRegistrationRequired()
            _errorNotification.value = "Registration required before directing AI videos."
            return
        }

        val duration = videoDurationSeconds.value
        val cost = coins.calculateVideoCost(duration)

        if (!coins.canAffordVideoGeneration(currentUserProfile, duration)) {
            val deficit = cost - currentUserProfile.coins
            onInsufficientCoins(deficit)
            _errorNotification.value = "Insufficient coins! $cost coins required for ${duration}s render."
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _statusMessage.value = "Ingesting prompt into AI video generation pipeline..."

            // Deduct coins via CoinManager
            val (updatedProfile, result) = coins.deductForVideoGeneration(currentUserProfile, duration)
            profiles.saveProfile(updatedProfile)

            when (result) {
                is CoinDeductionResult.Success -> {
                    _statusMessage.value = "Deducted ${result.coinsDeducted} coins. Queueing render job..."
                }
                is CoinDeductionResult.BypassedBySubscription -> {
                    _statusMessage.value = "VIP Unlimited Pass activated. Queueing render job..."
                }
                is CoinDeductionResult.InsufficientCoins -> {
                    _isSubmitting.value = false
                    _errorNotification.value = result.message
                    return@launch
                }
            }

            val request = IdeaToVideoPromptRequest(
                prompt = prompt,
                genre = selectedGenre.value,
                aspectRatio = selectedAspectRatio.value,
                pacing = selectedPacing.value,
                cameraMotion = selectedCameraMotion.value,
                audioMood = selectedAudioMood.value,
                durationSeconds = duration,
                coinCost = cost,
                resolution = selectedResolution.value
            )

            try {
                val submission = subRepo.submitPrompt(request, viewModelScope) { completedMovie ->
                    onMovieReady(completedMovie)
                }
                _selectedSubmissionId.value = submission.id
                _statusMessage.value = "Job ${submission.id} initiated in neural render pipeline."
            } catch (e: Exception) {
                _errorNotification.value = "Submission error: ${e.localizedMessage ?: "Failed to start pipeline"}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    /**
     * Cancel an active or queued submission.
     */
    fun cancelSubmission(submissionId: String) {
        viewModelScope.launch {
            subRepo.cancelSubmission(submissionId)
            _statusMessage.value = "Submission $submissionId cancelled."
        }
    }

    /**
     * Retry a failed or cancelled submission.
     */
    fun retrySubmission(submissionId: String, onMovieReady: (GeneratedMovie) -> Unit = {}) {
        viewModelScope.launch {
            val success = subRepo.retrySubmission(submissionId, viewModelScope, onMovieReady)
            if (success) {
                _selectedSubmissionId.value = submissionId
                _statusMessage.value = "Submission $submissionId re-queued."
            } else {
                _errorNotification.value = "Failed to retry submission $submissionId."
            }
        }
    }

    /**
     * Delete a submission from history.
     */
    fun deleteSubmission(submissionId: String) {
        viewModelScope.launch {
            subRepo.deleteSubmission(submissionId)
            if (_selectedSubmissionId.value == submissionId) {
                _selectedSubmissionId.value = null
            }
            _statusMessage.value = "Submission removed from history."
        }
    }

    /**
     * Select a specific submission for focused status tracking.
     */
    fun selectSubmissionForTracking(submissionId: String) {
        _selectedSubmissionId.value = submissionId
    }

    fun clearSelectedSubmission() {
        _selectedSubmissionId.value = null
    }

    fun setFilter(filter: SubmissionFilter) {
        _currentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadInspirationPrompt(inspirationPrompt: String) {
        promptInput.value = inspirationPrompt
    }

    fun clearError() {
        _errorNotification.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = ""
    }
}
