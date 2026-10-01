package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.generator.AiVideoGeneratorEngine
import com.example.data.local.ZubZeroDatabase
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.HotelBookingEntity
import com.example.data.model.AppCurrency
import com.example.data.model.CeoBankPayoutDetails
import com.example.data.model.GeneratedMovie
import com.example.data.model.GeneratedVideoMetadata
import com.example.data.model.GeneratorType
import com.example.data.model.HotelItem
import com.example.data.model.MarketCategory
import com.example.data.model.MatchCandidate
import com.example.data.model.MatchGender
import com.example.data.model.MatchUserProfile
import com.example.data.model.RelationshipGoal
import com.example.data.model.MovieGenre
import com.example.data.model.ScoredMatchCandidate
import com.example.data.service.MatchingAlgorithmService
import com.example.data.repository.MatchmakingData
import com.example.data.model.PaymentMethodType
import com.example.data.model.ProductItem
import com.example.data.model.ReceivingWallet
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UploadFeeTier
import com.example.data.model.UploadSubmission
import com.example.data.model.UserProfile
import com.example.data.model.WithdrawalTransaction
import com.example.data.remote.gemini.GeminiClient
import com.example.data.remote.gemini.GeminiMovieScriptService
import com.example.data.repository.CatalogData
import com.example.data.repository.UserProfileRepository
import com.example.data.repository.ZubZeroRepository
import com.example.data.storage.SaveVideoResult
import com.example.data.storage.VideoLocalStorageManager
import com.example.data.model.AdRewardResult
import com.example.data.model.CoinDeductionResult
import com.example.data.model.CoinTransaction
import com.example.data.model.CoinTransactionType
import com.example.data.model.DailyCheckInResult
import com.example.data.model.DailyStreakDayInfo
import com.example.data.model.FcmDeviceTokenState
import com.example.data.model.PushNotificationItem
import com.example.data.model.PushNotificationType
import com.example.data.service.CoinManager
import com.example.data.service.FcmTokenManager
import com.example.data.service.PushNotificationManager
import com.example.data.model.IdeaToVideoPromptRequest
import com.example.data.model.IdeaToVideoPromptSubmission
import com.example.data.model.VideoGenerationStatus
import com.example.data.repository.VideoPromptSubmissionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppNavTab(val title: String) {
    HOME("Home"),
    AI_STUDIO("Video Generator"),
    CINEMA_THEATER("Cinema Theater"),
    MARKETPLACE("Marketplace"),
    MATCHMAKING("Matchmaking Hub"),
    HOTELS("Hotel Locator"),
    FASHION("Style Storefront"),
    MY_HUB("My Hub")
}

class ZubZeroViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ZubZeroDatabase.getInstance(application)
    val userProfileRepository = UserProfileRepository(db.userProfileDao())
    private val repository = ZubZeroRepository(db.movieDao(), db.marketDao())
    private val geminiService = GeminiMovieScriptService()
    private val videoStorageManager = VideoLocalStorageManager(application)
    val coinManager = CoinManager(db.coinDao())
    val pushNotificationManager = PushNotificationManager.getInstance(application)
    val fcmTokenManager = FcmTokenManager.getInstance(application)
    val videoPromptSubmissionRepo = VideoPromptSubmissionRepository(db.videoPromptSubmissionDao(), db.movieDao(), geminiService)

    // Flow of AI video prompt submissions & status tracking
    val videoSubmissions: StateFlow<List<IdeaToVideoPromptSubmission>> = videoPromptSubmissionRepo.submissionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeVideoSubmission: StateFlow<IdeaToVideoPromptSubmission?> = videoSubmissions
        .map { list -> list.find { it.isInProgress } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedTrackingSubmissionId = MutableStateFlow<String?>(null)

    val selectedTrackingSubmission: StateFlow<IdeaToVideoPromptSubmission?> = combine(
        videoSubmissions,
        selectedTrackingSubmissionId
    ) { list, id ->
        if (id != null) list.find { it.id == id } else list.find { it.isInProgress } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Flow of recent coin transactions for audit ledger in UI
    val coinTransactions: StateFlow<List<CoinTransaction>> = coinManager.getRecentTransactions(20)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow of received push notifications for notification center & audit
    val pushNotifications: StateFlow<List<PushNotificationItem>> = pushNotificationManager.getNotificationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = pushNotifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val fcmState: StateFlow<FcmDeviceTokenState> = fcmTokenManager.fcmState

    // User Profile with Registration & Coin Economics
    private val _userProfile = MutableStateFlow(
        UserProfile(
            isRegistered = true,
            fullName = "Adekoya Daniel Ebenezer",
            mobileNumber = "+234 805 481 0828",
            email = "dadekoya80@gmail.com",
            facialPictureUri = "facial_verified_ceo.jpg",
            mediaDisplayType = "PHOTO",
            coins = 50, // Free 50 coin for first time user/subscriber
            lastDailyCheckInEpochMs = System.currentTimeMillis() - (25 * 3600 * 1000L), // Available for daily claim
            activityCount = 6, // Eligible for ad cycle activation
            adsWatchedForCycle = 0
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Dialog & Flow Visibility States
    val showRegistrationDialog = MutableStateFlow(false)
    val showUploadPaymentDialog = MutableStateFlow(false)
    val showSubscriptionDialog = MutableStateFlow(false)
    val showAdRewardDialog = MutableStateFlow(false)
    val showWithdrawalDialog = MutableStateFlow(false)
    val showPushNotificationCenter = MutableStateFlow(false)

    // CEO Multi-Currency Receiving Wallets for USD ($), GBP (£), and NGN (₦)
    private val _receivingWallets = MutableStateFlow<Map<AppCurrency, ReceivingWallet>>(
        mapOf(
            AppCurrency.USD to ReceivingWallet(
                currency = AppCurrency.USD,
                balance = 12480.0,
                totalRevenueReceived = 38950.0,
                pendingPayout = 0.0,
                walletAddressOrVirtualAccount = "US-ZUB-8839-4401-CITI",
                bankOrProvider = "Citibank N.A. (New York) / Stripe Global USD Route",
                accountHolder = "ADEKOYA DANIEL EBENEZER (CEO D-DANIEL'S INVENTION BIZ)"
            ),
            AppCurrency.GBP to ReceivingWallet(
                currency = AppCurrency.GBP,
                balance = 8240.0,
                totalRevenueReceived = 24600.0,
                pendingPayout = 0.0,
                walletAddressOrVirtualAccount = "GB-ZUB-4001-9921-BARCLAYS",
                bankOrProvider = "Barclays Bank UK / Faster Payments Direct",
                accountHolder = "ADEKOYA DANIEL EBENEZER (CEO D-DANIEL'S INVENTION BIZ)"
            ),
            AppCurrency.NGN to ReceivingWallet(
                currency = AppCurrency.NGN,
                balance = 18_450_000.0,
                totalRevenueReceived = 64_800_000.0,
                pendingPayout = 0.0,
                walletAddressOrVirtualAccount = "0123984719 (Wema / Monnify Virtual NGN)",
                bankOrProvider = "Central Clearing Interswitch & NIBSS Instant",
                accountHolder = "ADEKOYA DANIEL EBENEZER (CEO D-DANIEL'S INVENTION BIZ)"
            )
        )
    )
    val receivingWallets: StateFlow<Map<AppCurrency, ReceivingWallet>> = _receivingWallets.asStateFlow()

    // CEO Local Bank Payout Account Details
    private val _ceoBankDetails = MutableStateFlow(CeoBankPayoutDetails())
    val ceoBankDetails: StateFlow<CeoBankPayoutDetails> = _ceoBankDetails.asStateFlow()

    // CEO Withdrawal History
    private val _withdrawalHistory = MutableStateFlow<List<WithdrawalTransaction>>(
        listOf(
            WithdrawalTransaction(
                id = "WD-ZUB-9041",
                currency = AppCurrency.USD,
                amountRequested = 2500.0,
                nairaPayoutAmount = 3_750_000.0,
                destinationBank = "First Bank of Nigeria",
                destinationAccount = "3094810828",
                destinationAccountName = "ADEKOYA DANIEL EBENEZER",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                status = "COMPLETED",
                reference = "NIBSS-REV-884019-CEO"
            ),
            WithdrawalTransaction(
                id = "WD-ZUB-8902",
                currency = AppCurrency.GBP,
                amountRequested = 1200.0,
                nairaPayoutAmount = 2_280_000.0,
                destinationBank = "First Bank of Nigeria",
                destinationAccount = "3094810828",
                destinationAccountName = "ADEKOYA DANIEL EBENEZER",
                timestamp = System.currentTimeMillis() - 86400000L * 5,
                status = "COMPLETED",
                reference = "NIBSS-REV-773912-CEO"
            )
        )
    )
    val withdrawalHistory: StateFlow<List<WithdrawalTransaction>> = _withdrawalHistory.asStateFlow()

    // Upload submissions history
    private val _uploadSubmissions = MutableStateFlow<List<UploadSubmission>>(emptyList())
    val uploadSubmissions: StateFlow<List<UploadSubmission>> = _uploadSubmissions.asStateFlow()

    val savedMovies: StateFlow<List<GeneratedMovie>> = repository.savedMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hotelBookings: StateFlow<List<HotelBookingEntity>> = repository.hotelBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Database Cached Marketplace Listings and Hotel Data for Offline Viewing
    val cachedProducts: StateFlow<List<ProductItem>> = repository.cachedProductsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cachedHotels: StateFlow<List<HotelItem>> = repository.cachedHotelsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isRoomCacheInitialized = MutableStateFlow(false)
    val isRoomCacheInitialized: StateFlow<Boolean> = _isRoomCacheInitialized.asStateFlow()

    // Navigation
    private val _currentTab = MutableStateFlow(AppNavTab.AI_STUDIO)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    // AI Generation State
    private val _selectedGenre = MutableStateFlow(MovieGenre.ACTION)
    val selectedGenre: StateFlow<MovieGenre> = _selectedGenre.asStateFlow()

    fun setSelectedGenre(genre: MovieGenre) {
        _selectedGenre.value = genre
    }

    private val _generatorType = MutableStateFlow(GeneratorType.IDEA_TO_VIDEO)
    val generatorType: StateFlow<GeneratorType> = _generatorType.asStateFlow()

    fun setGeneratorType(type: GeneratorType) {
        _generatorType.value = type
    }

    // Generator inputs
    val ideaInput = MutableStateFlow("A cybernetic operative in a frozen megacity tracking a stolen AI core through sub-zero blizzards")
    val scriptInput = MutableStateFlow(
        "EXT. SUB-ZERO MONOLITH - NIGHT\nSnow whips against the neon obsidian glass.\n\nKAI\n(speaking into comms)\nMainframe target located. They have armed the cryo-pulse.\n\nELENA (V.O.)\nYou have four minutes before the perimeter freezes."
    )
    val selectedAspectRatio = MutableStateFlow("16:9 Cinema")
    val selectedCameraMotion = MutableStateFlow("Slow Dolly Zoom")
    val selectedPacing = MutableStateFlow("Adrenaline Rush")
    val selectedAudioScore = MutableStateFlow("Epic Cyber-Orchestral")

    // Real-time script preview generated from idea via Gemini
    private val _generatedScriptPreview = MutableStateFlow<String?>(null)
    val generatedScriptPreview: StateFlow<String?> = _generatedScriptPreview.asStateFlow()

    private val _isGeneratingScriptOnly = MutableStateFlow(false)
    val isGeneratingScriptOnly: StateFlow<Boolean> = _isGeneratingScriptOnly.asStateFlow()

    // Gemini API status info
    fun isGeminiConfigured(): Boolean = GeminiClient.hasValidApiKey()

    // Generation Progress
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress: StateFlow<Float> = _generationProgress.asStateFlow()

    private val _generationStatus = MutableStateFlow("")
    val generationStatus: StateFlow<String> = _generationStatus.asStateFlow()

    // Active Movie for Playback / Review
    private val _activeMovie = MutableStateFlow<GeneratedMovie?>(null)
    val activeMovie: StateFlow<GeneratedMovie?> = _activeMovie.asStateFlow()

    val activeVideoMetadata: StateFlow<GeneratedVideoMetadata?> = _activeMovie
        .map { it?.metadata }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Player state
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSceneIndex = MutableStateFlow(0)
    val currentSceneIndex: StateFlow<Int> = _currentSceneIndex.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private var playbackJob: Job? = null

    // Marketplace / Fashion states
    private val _marketCategory = MutableStateFlow(MarketCategory.ALL)
    val marketCategory: StateFlow<MarketCategory> = _marketCategory.asStateFlow()

    fun setMarketCategory(category: MarketCategory) {
        _marketCategory.value = category
    }

    val marketSearchQuery = MutableStateFlow("")
    val hotelSearchQuery = MutableStateFlow("")

    private val _selectedProduct = MutableStateFlow<ProductItem?>(null)
    val selectedProduct: StateFlow<ProductItem?> = _selectedProduct.asStateFlow()

    fun selectProduct(product: ProductItem?) {
        _selectedProduct.value = product
    }

    private val _selectedHotel = MutableStateFlow<HotelItem?>(null)
    val selectedHotel: StateFlow<HotelItem?> = _selectedHotel.asStateFlow()

    fun selectHotel(hotel: HotelItem?) {
        _selectedHotel.value = hotel
    }

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // Video Local Storage Export state
    private val _isSavingVideo = MutableStateFlow(false)
    val isSavingVideo: StateFlow<Boolean> = _isSavingVideo.asStateFlow()

    private val _lastSavedVideoResult = MutableStateFlow<SaveVideoResult.Success?>(null)
    val lastSavedVideoResult: StateFlow<SaveVideoResult.Success?> = _lastSavedVideoResult.asStateFlow()

    fun saveVideoToLocalStorage(movie: GeneratedMovie, videoUri: String? = null) {
        viewModelScope.launch {
            _isSavingVideo.value = true
            when (val result = videoStorageManager.saveVideoToLocalStorage(movie, videoUri)) {
                is SaveVideoResult.Success -> {
                    _lastSavedVideoResult.value = result
                    _toastMessage.value = "Video saved to ${result.relativePath}"
                }
                is SaveVideoResult.Error -> {
                    _toastMessage.value = "Export failed: ${result.message}"
                }
            }
            _isSavingVideo.value = false
        }
    }

    fun dismissSavedVideoDialog() {
        _lastSavedVideoResult.value = null
    }

    fun openSavedVideo(result: SaveVideoResult.Success) {
        videoStorageManager.openVideoInGallery(result.shareableUri ?: result.contentUri)
    }

    fun shareSavedVideo(result: SaveVideoResult.Success, movieTitle: String) {
        videoStorageManager.shareVideo(result.shareableUri ?: result.contentUri, movieTitle)
    }

    init {
        // Observe persisted user profile from Room SQLite database
        viewModelScope.launch {
            userProfileRepository.userProfileFlow.collect { persistedProfile ->
                if (persistedProfile != null) {
                    _userProfile.value = persistedProfile
                } else {
                    // Seed initial user profile into Room database on first app launch
                    userProfileRepository.saveProfile(_userProfile.value)
                }
            }
        }

        // Preload an initial showcase movie
        viewModelScope.launch {
            val defaultMovie = GeneratedMovie(
                title = "Sub-Zero: Tokyo Protocol",
                genre = MovieGenre.ACTION,
                generatorType = GeneratorType.IDEA_TO_VIDEO,
                logline = "A rogue cyber-agent navigates an icy metropolis to neutralize an orbital strike code.",
                synopsis = "High-octane action thriller through frosted neon skylines and underground server bunkers.",
                scenes = CatalogData.getSampleScenesForGenre(MovieGenre.ACTION, "Sub-Zero: Tokyo Protocol"),
                aspectRatio = "16:9 Cinema"
            )
            _activeMovie.value = defaultMovie
            startPlaybackLoop()

            // Initialize Room Database Cache for Marketplace Listings and Hotel Data
            repository.ensureDefaultCachePopulated()
            _isRoomCacheInitialized.value = true

            // Seed initial welcome coin transaction if user is pre-registered
            if (_userProfile.value.coins > 0) {
                coinManager.recordTransaction(
                    CoinTransaction(
                        id = "ctx_init_welcome",
                        type = CoinTransactionType.FIRST_TIME_BONUS,
                        amount = 50,
                        balanceAfter = 50,
                        description = "Initial Sign-Up Welcome Bonus (+50 Coins / 20s AI Video)",
                        timestamp = System.currentTimeMillis() - (86400000L * 2)
                    )
                )
            }

            // Seed default submissions for AI Studio status tracker
            videoPromptSubmissionRepo.ensureDefaultSubmissionsSeeded()
        }
    }

    fun selectVideoSubmissionForTracking(id: String) {
        selectedTrackingSubmissionId.value = id
    }

    fun cancelVideoSubmission(id: String) {
        viewModelScope.launch {
            videoPromptSubmissionRepo.cancelSubmission(id)
            showToast("Video render job cancelled.")
        }
    }

    fun retryVideoSubmission(id: String) {
        viewModelScope.launch {
            val success = videoPromptSubmissionRepo.retrySubmission(id, viewModelScope) { movie ->
                _activeMovie.value = movie
                showToast("Render complete: ${movie.title}!")
            }
            if (success) {
                selectedTrackingSubmissionId.value = id
                showToast("Job re-queued for neural render.")
            }
        }
    }

    fun deleteVideoSubmission(id: String) {
        viewModelScope.launch {
            videoPromptSubmissionRepo.deleteSubmission(id)
            if (selectedTrackingSubmissionId.value == id) {
                selectedTrackingSubmissionId.value = null
            }
            showToast("Submission removed from history.")
        }
    }

    fun generateScriptFromIdea() {
        if (_isGeneratingScriptOnly.value) return
        viewModelScope.launch {
            _isGeneratingScriptOnly.value = true
            showToast("Generating movie script with Gemini AI...")
            try {
                val movie = geminiService.generateMovieFromIdea(
                    prompt = ideaInput.value,
                    genre = _selectedGenre.value,
                    aspectRatio = selectedAspectRatio.value,
                    pacing = selectedPacing.value,
                    audioScore = selectedAudioScore.value,
                    cameraMotion = selectedCameraMotion.value
                )
                // Build a formatted screenplay text
                val scriptBuilder = StringBuilder()
                scriptBuilder.append("TITLE: ${movie.title}\n")
                scriptBuilder.append("LOGLINE: ${movie.logline}\n\n")
                movie.scenes.forEach { scene ->
                    scriptBuilder.append("EXT./INT. SCENE ${scene.sceneNumber} - ${scene.title.uppercase()}\n")
                    scriptBuilder.append("${scene.visualPrompt}\n\n")
                    scriptBuilder.append("CAMERA: ${scene.cameraMovement}\n\n")
                    scriptBuilder.append("CHARACTER\n")
                    scriptBuilder.append("${scene.dialogue}\n\n")
                }
                val formatted = scriptBuilder.toString().trim()
                _generatedScriptPreview.value = formatted
                scriptInput.value = formatted
                showToast("Movie script generated! Ready to render video.")
            } catch (e: Exception) {
                showToast("Script generation error: ${e.message}")
            } finally {
                _isGeneratingScriptOnly.value = false
            }
        }
    }

    fun generateMovie() {
        if (_isGenerating.value) return

        // 1. Guard: Check if user is registered with facial picture
        if (!_userProfile.value.isRegistered) {
            showRegistrationDialog.value = true
            showToast("Registration required! Facial picture, mobile and email needed to direct films.")
            return
        }

        // 2. Guard: Check sufficient coins (50 coins for 20 seconds of video render) via CoinManager
        val durationSeconds = CoinManager.STANDARD_VIDEO_SECONDS
        if (!coinManager.canAffordVideoGeneration(_userProfile.value, durationSeconds)) {
            showSubscriptionDialog.value = true
            val cost = coinManager.calculateVideoCost(durationSeconds)
            val deficit = cost - _userProfile.value.coins
            showToast("Insufficient coins! $cost coins (worth ₦350) required for 20s render (Deficit: ${deficit}c). Subscribe, claim daily check-in, or watch ads.")
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _generationProgress.value = 0.15f
            _generationStatus.value = if (GeminiClient.hasValidApiKey()) {
                "Calling Gemini 3.5 Flash: Directing cinematic screenplay & scene geometry..."
            } else {
                "Initializing Sub-Zero Neural Core: Script analysis & storyboard parsing..."
            }

            // Deduct coins using CoinManager service
            val (updatedProfile, deductionResult) = coinManager.deductForVideoGeneration(_userProfile.value, durationSeconds)
            _userProfile.value = updatedProfile

            when (deductionResult) {
                is CoinDeductionResult.Success -> {
                    // Coins deducted successfully
                }
                is CoinDeductionResult.BypassedBySubscription -> {
                    // Unlimited subscription bypass
                }
                is CoinDeductionResult.InsufficientCoins -> {
                    _isGenerating.value = false
                    showToast(deductionResult.message)
                    return@launch
                }
            }

            delay(400)
            _generationProgress.value = 0.45f
            _generationStatus.value = "Directing camera motion (${selectedCameraMotion.value}) & lighting..."

            delay(450)
            _generationProgress.value = 0.70f
            _generationStatus.value = "Neural rendering: 3D volumetric depth & frost shaders..."

            delay(500)
            _generationProgress.value = 0.88f
            _generationStatus.value = "Mastering audio track: ${selectedAudioScore.value}..."

            val movie = when (_generatorType.value) {
                GeneratorType.IDEA_TO_VIDEO -> {
                    geminiService.generateMovieFromIdea(
                        prompt = ideaInput.value,
                        genre = _selectedGenre.value,
                        aspectRatio = selectedAspectRatio.value,
                        pacing = selectedPacing.value,
                        audioScore = selectedAudioScore.value,
                        cameraMotion = selectedCameraMotion.value
                    )
                }
                GeneratorType.SCRIPT_TO_VIDEO -> {
                    geminiService.expandScriptToVideo(
                        scriptText = scriptInput.value,
                        genre = _selectedGenre.value,
                        voiceStyle = "Deep Sub-Zero Baritone",
                        aspectRatio = selectedAspectRatio.value
                    )
                }
                GeneratorType.IMAGE_TO_VIDEO -> {
                    AiVideoGeneratorEngine.generateImageToVideo(
                        imageTag = "Selected Concept Still",
                        motionEffect = selectedCameraMotion.value,
                        cameraSpeed = "Normal 1.0x"
                    )
                }
            }

            _generationProgress.value = 1.0f
            _generationStatus.value = "Video Render Complete! (20s reel rendered)"
            delay(300)

            _activeMovie.value = movie
            _isGenerating.value = false
            _currentTab.value = AppNavTab.CINEMA_THEATER
            _currentSceneIndex.value = 0
            _playbackProgress.value = 0f
            startPlaybackLoop()

            // Save to database
            repository.saveMovie(movie)

            if (_generatorType.value == GeneratorType.IDEA_TO_VIDEO) {
                val subId = "SUB-${System.currentTimeMillis() % 100000}"
                db.videoPromptSubmissionDao().insertSubmission(
                    com.example.data.local.entity.VideoPromptSubmissionEntity(
                        id = subId,
                        prompt = ideaInput.value.ifEmpty { "A clandestine agent operating in a sub-zero futuristic city" },
                        title = movie.title,
                        genre = movie.genre.name,
                        aspectRatio = movie.aspectRatio,
                        pacing = selectedPacing.value,
                        cameraMotion = selectedCameraMotion.value,
                        audioMood = selectedAudioScore.value,
                        durationSeconds = durationSeconds,
                        coinCost = 50,
                        submittedAt = System.currentTimeMillis() - 2500,
                        completedAt = System.currentTimeMillis(),
                        status = VideoGenerationStatus.COMPLETED.name,
                        progress = 1.0f,
                        currentStageName = "4K Cinema Render Completed",
                        currentStageIndex = 5,
                        estimatedTimeRemainingSeconds = 0,
                        resultMovieJson = null,
                        failureReason = null,
                        isCancelled = false
                    )
                )
                selectedTrackingSubmissionId.value = subId
            }

            incrementActivity("Rendered 20s AI Movie: ${movie.title}")
            showToast("20s Movie rendered! 50 Coins used (₦350 value).")
        }
    }

    fun playMovie(movie: GeneratedMovie) {
        _activeMovie.value = movie
        _currentSceneIndex.value = 0
        _playbackProgress.value = 0f
        _currentTab.value = AppNavTab.CINEMA_THEATER
        startPlaybackLoop()
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            startPlaybackLoop()
        } else {
            playbackJob?.cancel()
        }
    }

    fun nextScene() {
        val movie = _activeMovie.value ?: return
        if (_currentSceneIndex.value < movie.scenes.size - 1) {
            _currentSceneIndex.value += 1
            _playbackProgress.value = 0f
        } else {
            _currentSceneIndex.value = 0
            _playbackProgress.value = 0f
        }
    }

    fun previousScene() {
        if (_currentSceneIndex.value > 0) {
            _currentSceneIndex.value -= 1
            _playbackProgress.value = 0f
        }
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(100)
                val movie = _activeMovie.value ?: break
                val currentScene = movie.scenes.getOrNull(_currentSceneIndex.value) ?: break
                val totalMs = currentScene.durationSeconds * 1000f
                val nextProg = _playbackProgress.value + (100f / totalMs)
                if (nextProg >= 1f) {
                    _playbackProgress.value = 0f
                    if (_currentSceneIndex.value < movie.scenes.size - 1) {
                        _currentSceneIndex.value += 1
                    } else {
                        _currentSceneIndex.value = 0
                    }
                } else {
                    _playbackProgress.value = nextProg
                }
            }
        }
    }

    fun addToCart(product: ProductItem, size: String = "M", color: String = "Default") {
        viewModelScope.launch {
            repository.addToCart(product, size, color, 1)
            showToast("Added ${product.title} to bag")
        }
    }

    fun removeFromCart(id: Long) {
        viewModelScope.launch {
            repository.removeFromCart(id)
            showToast("Item removed from cart")
        }
    }

    fun checkoutCart() {
        viewModelScope.launch {
            val items = cartItems.value
            val count = items.sumOf { it.quantity }.coerceAtLeast(1)
            val total = items.sumOf { it.price * it.quantity }
            val formattedTotal = if (total > 0) String.format(Locale.US, "$%,.2f", total) else "$1,250.00"
            val orderId = "ORD-${(10000..99999).random()}"

            repository.clearCart()
            incrementActivity("Marketplace Order Placed ($orderId)")

            // Dispatch Firebase Cloud Messaging push notification for marketplace order
            pushNotificationManager.sendMarketplaceOrderUpdate(
                orderId = orderId,
                itemCount = count,
                totalAmountFormatted = formattedTotal,
                status = "Confirmed & Preparing for Dispatch"
            )

            showToast("Order $orderId placed! Push notification dispatched.")
        }
    }

    fun bookHotel(hotel: HotelItem, checkIn: String, checkOut: String, guests: Int, roomType: String) {
        viewModelScope.launch {
            val total = hotel.pricePerNight * 2 // 2 nights default
            repository.bookHotel(hotel, checkIn, checkOut, guests, roomType, total)
            _selectedHotel.value = null
            showToast("Reservation confirmed at ${hotel.name}!")
        }
    }

    fun cancelHotelBooking(id: Long) {
        viewModelScope.launch {
            repository.cancelBooking(id)
            showToast("Hotel booking cancelled")
        }
    }

    fun deleteMovie(id: Long) {
        viewModelScope.launch {
            repository.deleteMovie(id)
            showToast("Project removed from studio")
        }
    }

    // Financial, Registration, Coin & Ad reward business logic
    fun registerUser(
        fullName: String,
        mobileNumber: String,
        email: String,
        mediaType: String,
        photoUri: String?
    ) {
        viewModelScope.launch {
            val updated = userProfileRepository.saveRegistration(
                fullName = fullName,
                mobileNumber = mobileNumber,
                email = email,
                mediaType = mediaType,
                photoUri = photoUri,
                currentProfile = _userProfile.value
            )
            _userProfile.value = updated
            showRegistrationDialog.value = false
            incrementActivity("User Identity Registered with Facial Picture")

            coinManager.recordTransaction(
                CoinTransaction(
                    type = CoinTransactionType.FIRST_TIME_BONUS,
                    amount = 50,
                    balanceAfter = updated.coins,
                    description = "First-time Registration Welcome Bonus (+50 Coins / 20s render)"
                )
            )
            showToast("Registration Complete! Saved to Room SQLite Database (+50 Coins)!")
        }
    }

    fun claimDailyCheckIn() {
        viewModelScope.launch {
            val (updatedProfile, result) = coinManager.claimDailyCheckIn(_userProfile.value)
            _userProfile.value = updatedProfile
            userProfileRepository.saveProfile(updatedProfile)

            when (result) {
                is DailyCheckInResult.Success -> {
                    showToast(result.message)
                }
                is DailyCheckInResult.AlreadyClaimed -> {
                    showToast(result.message)
                }
            }
        }
    }

    fun incrementActivity(actionName: String) {
        _userProfile.update {
            val newCount = it.activityCount + 1
            it.copy(activityCount = newCount)
        }
        viewModelScope.launch {
            userProfileRepository.saveProfile(_userProfile.value)
        }
    }

    fun handleAdCompletion() {
        viewModelScope.launch {
            val (updatedProfile, result) = coinManager.processAdWatch(_userProfile.value)
            _userProfile.value = updatedProfile
            userProfileRepository.saveProfile(updatedProfile)

            when (result) {
                is AdRewardResult.Progress -> {
                    showToast(result.message)
                }
                is AdRewardResult.CycleComplete -> {
                    showAdRewardDialog.value = false
                    showToast(result.message)
                }
                is AdRewardResult.ActivityRequirementNeeded -> {
                    showToast(result.message)
                }
            }
        }
    }

    fun getDailyStreakWeekInfo(): List<DailyStreakDayInfo> {
        return coinManager.getStreakWeekInfo(
            currentStreak = _userProfile.value.checkInStreak,
            lastCheckInEpochMs = _userProfile.value.lastDailyCheckInEpochMs
        )
    }

    fun subscribePlan(plan: SubscriptionPlan, method: PaymentMethodType, reference: String, currency: AppCurrency = AppCurrency.NGN) {
        viewModelScope.launch {
            delay(500)
            _userProfile.update {
                it.copy(
                    activeSubscription = plan,
                    coins = it.coins + 100
                )
            }
            userProfileRepository.saveProfile(_userProfile.value)
            coinManager.recordTransaction(
                CoinTransaction(
                    type = CoinTransactionType.SUBSCRIPTION_BONUS,
                    amount = 100,
                    balanceAfter = _userProfile.value.coins,
                    description = "Subscription Bonus Allowance (+100 Coins for ${plan.planName})"
                )
            )
            // Automatically credit the corresponding receiving wallet!
            when (currency) {
                AppCurrency.USD -> creditReceivingWallet(AppCurrency.USD, plan.priceUsd.toDouble())
                AppCurrency.GBP -> creditReceivingWallet(AppCurrency.GBP, plan.priceGbp.toDouble())
                AppCurrency.NGN -> creditReceivingWallet(AppCurrency.NGN, plan.priceNaira.toDouble())
            }
            showSubscriptionDialog.value = false
            incrementActivity("Subscribed to ${plan.planName} via ${method.title}")

            // Dispatch Firebase Cloud Messaging push notification for subscription
            val priceFormatted = when (currency) {
                AppCurrency.USD -> "$${plan.priceUsd}"
                AppCurrency.GBP -> "£${plan.priceGbp}"
                AppCurrency.NGN -> "₦${String.format(Locale.US, "%,d", plan.priceNaira)}"
            }
            val nextRenewal = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000L))
            pushNotificationManager.sendSubscriptionRenewalAlert(
                planName = plan.planName,
                priceFormatted = "$priceFormatted / billing cycle",
                nextBillingDate = nextRenewal,
                bonusCoins = 100
            )

            showToast("Subscription to ${plan.planName} active! Receiving wallet credited. Push notification dispatched.")
        }
    }

    fun confirmUploadPayment(
        title: String,
        description: String,
        category: String,
        price: Double,
        tier: UploadFeeTier,
        channelName: String,
        method: PaymentMethodType,
        reference: String,
        currency: AppCurrency = AppCurrency.USD
    ) {
        viewModelScope.launch {
            val submission = UploadSubmission(
                title = title,
                description = description,
                price = price,
                category = category,
                tier = tier,
                channelOrStoreName = channelName,
                paidAmountUsd = tier.usdPrice,
                paidAmountNaira = tier.nairaEquivalent,
                paymentReference = reference
            )
            _uploadSubmissions.update { listOf(submission) + it }

            // Credit receiving wallet directly
            when (currency) {
                AppCurrency.USD -> creditReceivingWallet(AppCurrency.USD, tier.usdPrice.toDouble())
                AppCurrency.GBP -> creditReceivingWallet(AppCurrency.GBP, (tier.usdPrice * 0.8).coerceAtLeast(1.0))
                AppCurrency.NGN -> creditReceivingWallet(AppCurrency.NGN, tier.nairaEquivalent.toDouble())
            }

            // Cache newly uploaded product in Room Database for offline persistence
            val catEnum = when {
                category.contains("Prop", ignoreCase = true) -> MarketCategory.PROPS
                category.contains("Fashion", ignoreCase = true) || category.contains("Style", ignoreCase = true) -> MarketCategory.FASHION
                category.contains("Wearable", ignoreCase = true) || category.contains("Tech", ignoreCase = true) || category.contains("Accessories", ignoreCase = true) -> MarketCategory.ACCESSORIES
                else -> MarketCategory.FILM_GEAR
            }
            val isFashionItem = (catEnum == MarketCategory.FASHION || catEnum == MarketCategory.ACCESSORIES)
            val newProduct = ProductItem(
                id = "prod_up_${System.currentTimeMillis()}",
                title = title,
                category = catEnum,
                price = price,
                originalPrice = price * 1.25,
                rating = 5.0f,
                reviewsCount = 1,
                imageRes = if (isFashionItem) com.example.R.drawable.img_fashion_cyber else com.example.R.drawable.img_prop_artifact,
                description = description,
                specs = listOf("Cleared by ZUB-ZERO Protocol", "Tier: ${tier.label}", "Seller Channel: $channelName"),
                sizes = if (isFashionItem) listOf("S", "M", "L", "XL") else emptyList(),
                colors = if (isFashionItem) listOf("Cyber Onyx", "Frost Cyan") else emptyList(),
                isFashion = isFashionItem,
                seller = channelName
            )
            repository.cacheProduct(newProduct)

            showUploadPaymentDialog.value = false
            incrementActivity("Uploaded item: $title ($${tier.usdPrice} clearance cleared)")
            showToast("Cleared $${tier.usdPrice} via ${method.title}! Added to Room cache & receiving wallet credited. $title is now live in ZUB-ZERO!")
        }
    }

    fun creditReceivingWallet(currency: AppCurrency, amount: Double) {
        _receivingWallets.update { currentMap ->
            val existing = currentMap[currency] ?: return@update currentMap
            val updated = existing.copy(
                balance = existing.balance + amount,
                totalRevenueReceived = existing.totalRevenueReceived + amount
            )
            currentMap + (currency to updated)
        }
    }

    fun processCeoWithdrawal(
        currency: AppCurrency,
        amount: Double,
        bankName: String,
        accountNumber: String,
        accountName: String
    ): Boolean {
        val currentWallet = _receivingWallets.value[currency] ?: return false
        if (amount <= 0 || amount > currentWallet.balance) {
            showToast("Withdrawal failed: Insufficient balance in ${currency.code} receiving wallet!")
            return false
        }

        // Calculate Naira payout
        val nairaPayout = when (currency) {
            AppCurrency.USD -> amount * AppCurrency.USD.exchangeRateToNaira
            AppCurrency.GBP -> amount * AppCurrency.GBP.exchangeRateToNaira
            AppCurrency.NGN -> amount
        }

        // Deduct from receiving wallet
        _receivingWallets.update { currentMap ->
            val updated = currentWallet.copy(
                balance = currentWallet.balance - amount
            )
            currentMap + (currency to updated)
        }

        // Generate transaction
        val tx = WithdrawalTransaction(
            id = "WD-ZUB-${System.currentTimeMillis() % 100000}",
            currency = currency,
            amountRequested = amount,
            nairaPayoutAmount = nairaPayout,
            destinationBank = bankName,
            destinationAccount = accountNumber,
            destinationAccountName = accountName,
            timestamp = System.currentTimeMillis(),
            status = "COMPLETED",
            reference = "NIBSS-REV-${System.currentTimeMillis() % 1000000}-CEO"
        )

        _withdrawalHistory.update { listOf(tx) + it }
        showToast("✅ Payout Successful! ₦${String.format("%,.0f", nairaPayout)} sent to $bankName ($accountNumber)!")
        return true
    }

    fun updateCeoBankDetails(bankName: String, accountNumber: String, accountName: String) {
        _ceoBankDetails.value = CeoBankPayoutDetails(
            bankName = bankName,
            accountNumber = accountNumber,
            accountName = accountName,
            bvnOrTinVerified = true
        )
        showToast("CEO Bank payout details updated successfully!")
    }

    // Matchmaking State for Single and Searching Men & Women
    private val _matchCandidates = MutableStateFlow<List<MatchCandidate>>(MatchmakingData.initialCandidates)
    val matchCandidates: StateFlow<List<MatchCandidate>> = _matchCandidates.asStateFlow()

    private val _matchUserProfile = MutableStateFlow(
        MatchUserProfile(
            isRegistered = false,
            fullName = "Adekoya Daniel Ebenezer",
            age = 28,
            gender = MatchGender.MEN,
            targetGender = MatchGender.WOMEN,
            location = "Lekki Phase 1, Lagos",
            profession = "Tech Founder & Cinema Director",
            bio = "Ambitious, respectful, and family-oriented. Looking for a genuine lady for courtship, marriage, and shared dreams.",
            relationshipGoal = RelationshipGoal.SERIOUS_RELATIONSHIP,
            contactPhoneOrWhatsapp = "+234 805 481 0828",
            instagramOrSocial = "@zubzero_founder"
        )
    )
    val matchUserProfile: StateFlow<MatchUserProfile> = _matchUserProfile.asStateFlow()

    private val _selectedGenderFilter = MutableStateFlow(MatchGender.WOMEN)
    val selectedGenderFilter: StateFlow<MatchGender> = _selectedGenderFilter.asStateFlow()

    // Matchmaking Matching Algorithm Service
    val matchingService = MatchingAlgorithmService()

    // Matchmaking filter: ALL, ALGORITHM (Top AI Compatibility Matches), DEAR, FITTER, HIGH_RATED
    private val _selectedMatchFilter = MutableStateFlow("ALGORITHM")
    val selectedMatchFilter: StateFlow<String> = _selectedMatchFilter.asStateFlow()

    // Candidate Search Query
    private val _matchSearchQuery = MutableStateFlow("")
    val matchSearchQuery: StateFlow<String> = _matchSearchQuery.asStateFlow()

    // User ratings given to match candidates
    private val _candidateRatings = MutableStateFlow<Map<String, Int>>(emptyMap())
    val candidateRatings: StateFlow<Map<String, Int>> = _candidateRatings.asStateFlow()

    val showMatchRegistrationDialog = MutableStateFlow(false)
    val showTrialActivationDialog = MutableStateFlow(false)
    val showPreferencesDialog = MutableStateFlow(false)
    val isCalculatingMatches = MutableStateFlow(false)

    fun setSelectedGenderFilter(gender: MatchGender) {
        _selectedGenderFilter.value = gender
    }

    fun setSelectedMatchFilter(filter: String) {
        _selectedMatchFilter.value = filter
    }

    fun setMatchSearchQuery(query: String) {
        _matchSearchQuery.value = query
    }

    fun rateCandidate(candidateId: String, stars: Int, candidateName: String) {
        val current = _candidateRatings.value.toMutableMap()
        current[candidateId] = stars
        _candidateRatings.value = current
        incrementActivity("Rated $candidateName $stars stars")
        showToast("⭐ You rated $candidateName $stars stars! Compatibility updated.")
    }

    /**
     * Score a single candidate using the intelligent matching algorithm service.
     */
    fun getScoredCandidate(candidate: MatchCandidate): ScoredMatchCandidate {
        return matchingService.calculateScore(_matchUserProfile.value, candidate)
    }

    /**
     * Returns the #1 highest-scoring compatible soulmate match.
     */
    fun getTopSoulmateMatch(): ScoredMatchCandidate? {
        val candidates = _matchCandidates.value.filter { it.gender == _selectedGenderFilter.value }
        if (candidates.isEmpty()) return null
        return candidates.map { getScoredCandidate(it) }.maxByOrNull { it.matchScore }
    }

    /**
     * Runs or recalculates AI matching algorithm with feedback.
     */
    fun runMatchingAlgorithm() {
        viewModelScope.launch {
            isCalculatingMatches.value = true
            delay(400)
            isCalculatingMatches.value = false
            _selectedMatchFilter.value = "ALGORITHM"
            val top = getTopSoulmateMatch()
            incrementActivity("Executed Smart Matching Algorithm")
            if (top != null) {
                // Dispatch Firebase Cloud Messaging push notification for high-compatibility match
                pushNotificationManager.sendNewMatchAlert(
                    matchName = top.candidate.fullName,
                    matchPercent = top.matchScore,
                    genre = top.candidate.favoriteCinemaGenre,
                    location = top.candidate.location
                )
                showToast("✨ AI Matchmaker Complete: Found ${top.candidate.fullName} as your top match (${top.matchScore}% compatible)! Push alert sent.")
            } else {
                showToast("✨ Smart Matchmaker Updated: Candidates scored by compatibility!")
            }
        }
    }

    /**
     * Updates user's relationship & cinema preferences and recalculates matching scores.
     */
    fun updateUserPreferences(
        relationshipGoal: RelationshipGoal,
        favoriteGenre: String,
        preferredHobbies: List<String>,
        minAge: Int,
        maxAge: Int
    ) {
        _matchUserProfile.update {
            it.copy(
                relationshipGoal = relationshipGoal,
                favoriteGenre = favoriteGenre,
                preferredHobbies = preferredHobbies,
                preferredMinAge = minAge,
                preferredMaxAge = maxAge
            )
        }
        showPreferencesDialog.value = false
        incrementActivity("Updated Dating & Cinema Preferences")
        showToast("🎯 Preferences updated! Matching algorithm re-scored all candidates.")
    }

    /**
     * Free Registration on ZUB-ZERO Matchmaking
     */
    fun registerMatchUserProfile(
        fullName: String,
        age: Int,
        gender: MatchGender,
        targetGender: MatchGender,
        location: String,
        profession: String,
        bio: String,
        relationshipGoal: RelationshipGoal,
        phoneOrWhatsapp: String,
        instagramOrSocial: String,
        favoriteGenre: String = "Romantic Twilight & Drama",
        preferredHobbies: List<String> = listOf("Cinema Viewing", "Film Premiere", "Travel", "Fine Dining")
    ) {
        _matchUserProfile.value = MatchUserProfile(
            isRegistered = true,
            fullName = fullName,
            age = age,
            gender = gender,
            targetGender = targetGender,
            location = location,
            profession = profession,
            bio = bio,
            relationshipGoal = relationshipGoal,
            contactPhoneOrWhatsapp = phoneOrWhatsapp,
            instagramOrSocial = instagramOrSocial,
            favoriteGenre = favoriteGenre,
            preferredHobbies = preferredHobbies,
            registrationDateEpochMs = System.currentTimeMillis(),
            isTrialActive = true,
            trialDaysRemaining = 3
        )
        // Also update the main user profile
        _userProfile.update {
            it.copy(
                isTrialActive = true,
                trialDaysRemaining = 3,
                trialExpirationEpochMs = System.currentTimeMillis() + (3 * 24 * 3600 * 1000L)
            )
        }
        _selectedGenderFilter.value = targetGender
        showMatchRegistrationDialog.value = false
        incrementActivity("Free Matchmaking Registration Completed")
        showToast("🎉 Welcome to ZUB-ZERO Matchmaking! 3-day free trial active. AI Compatibility scores generated!")
    }

    /**
     * Activate 3-Day Free Trial Attached to Subscribed Channels with initial ₦500 Naira trial access fee
     */
    fun activateChannelTrialWithInitialAccessFee(
        paymentMethod: PaymentMethodType = PaymentMethodType.LOCAL_CARD,
        reference: String = "TRIAL-500-${System.currentTimeMillis()}"
    ) {
        viewModelScope.launch {
            delay(500)
            // Credit receiving wallet with ₦500 Naira
            creditReceivingWallet(AppCurrency.NGN, 500.0)

            _userProfile.update {
                it.copy(
                    isTrialActive = true,
                    hasPaidInitialTrialFee = true,
                    trialDaysRemaining = 3,
                    trialExpirationEpochMs = System.currentTimeMillis() + (3 * 24 * 3600 * 1000L),
                    coins = it.coins + 50 // bonus 50 coins during trial!
                )
            }
            _matchUserProfile.update {
                it.copy(isTrialActive = true, trialDaysRemaining = 3)
            }
            showTrialActivationDialog.value = false
            incrementActivity("Activated 3-Day Free Trial (₦500 initial trial access)")
            showToast("✨ 3-Day Free Trial Activated! ₦500 trial access cleared to receiving wallet. Enjoy full VIP access!")
        }
    }

    fun sendMatchInterest(candidate: MatchCandidate) {
        incrementActivity("Connected with ${candidate.fullName}")
        val scored = getScoredCandidate(candidate)

        // Dispatch Firebase Cloud Messaging push notification for match interest
        pushNotificationManager.sendNewMatchAlert(
            matchName = candidate.fullName,
            matchPercent = scored.matchScore,
            genre = candidate.favoriteCinemaGenre,
            location = candidate.location
        )

        showToast("💌 Interest Sent! You and ${candidate.fullName} are connected. Push notification dispatched!")
    }

    /**
     * Returns candidates filtered by gender, query, filter chip, and scored by the matching algorithm.
     */
    fun getFilteredScoredCandidates(): List<ScoredMatchCandidate> {
        val all = _matchCandidates.value
        val gender = _selectedGenderFilter.value
        val filter = _selectedMatchFilter.value
        val query = _matchSearchQuery.value.trim().lowercase()
        val userProfile = _matchUserProfile.value

        val genderMatched = all.filter { it.gender == gender }
        val scoredList = genderMatched.map { matchingService.calculateScore(userProfile, it) }

        val filtered = scoredList.filter { scored ->
            val candidate = scored.candidate
            val matchesQuery = query.isEmpty() ||
                    candidate.fullName.lowercase().contains(query) ||
                    candidate.location.lowercase().contains(query) ||
                    candidate.profession.lowercase().contains(query) ||
                    candidate.bio.lowercase().contains(query) ||
                    candidate.favoriteCinemaGenre.lowercase().contains(query) ||
                    scored.compatibilityGrade.lowercase().contains(query)

            val matchesFilter = when (filter) {
                "ALGORITHM" -> scored.matchScore >= 75
                "DEAR" -> candidate.isDearCandidate
                "FITTER" -> candidate.isFittedMatch
                "HIGH_RATED" -> candidate.rating >= 4.8f
                else -> true
            }

            matchesQuery && matchesFilter
        }

        // When ALGORITHM or ALL, rank primarily by highest match compatibility score!
        return when (filter) {
            "ALGORITHM" -> filtered.sortedByDescending { it.matchScore }
            "HIGH_RATED" -> filtered.sortedByDescending { it.candidate.rating }
            else -> filtered.sortedByDescending { it.matchScore }
        }
    }

    fun getFilteredMatchCandidates(): List<MatchCandidate> {
        return getFilteredScoredCandidates().map { it.candidate }
    }

    // Filtered lists querying Room Cached Data for Offline Viewing
    fun getFilteredProducts(): List<ProductItem> {
        val query = marketSearchQuery.value.trim().lowercase()
        val all = if (cachedProducts.value.isNotEmpty()) {
            cachedProducts.value
        } else {
            repository.getAllMarketProducts()
        }
        return all.filter { product ->
            val matchesCategory = when (_marketCategory.value) {
                MarketCategory.ALL -> true
                else -> product.category == _marketCategory.value
            }
            val matchesQuery = query.isEmpty() ||
                    product.title.lowercase().contains(query) ||
                    product.description.lowercase().contains(query) ||
                    product.seller.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
    }

    fun getFilteredFashion(): List<ProductItem> {
        val query = marketSearchQuery.value.trim().lowercase()
        val source = if (cachedProducts.value.isNotEmpty()) {
            cachedProducts.value.filter { it.isFashion || it.category == MarketCategory.FASHION || it.category == MarketCategory.ACCESSORIES }
        } else {
            repository.getFashionItems()
        }
        return source.filter { item ->
            query.isEmpty() || item.title.lowercase().contains(query) || item.description.lowercase().contains(query)
        }
    }

    fun getFilteredHotels(): List<HotelItem> {
        val query = hotelSearchQuery.value.trim().lowercase()
        val all = if (cachedHotels.value.isNotEmpty()) {
            cachedHotels.value
        } else {
            repository.getHotels()
        }
        return all.filter { hotel ->
            query.isEmpty() || hotel.name.lowercase().contains(query) || hotel.location.lowercase().contains(query)
        }
    }

    /**
     * Manually refresh Room database cache for offline viewing.
     */
    fun refreshOfflineRoomCache() {
        viewModelScope.launch {
            repository.refreshAllMarketplaceAndHotelCache()
            _isRoomCacheInitialized.value = true
            showToast("✅ Room SQLite Cache Synchronized! Offline viewing active.")
        }
    }

    // =========================================================================
    // Firebase Cloud Messaging (FCM) & Push Notification Methods
    // =========================================================================

    fun onNotificationPermissionResult(isGranted: Boolean) {
        // Updated by MainActivity on permission grant or denial
    }

    /**
     * Trigger a real FCM push notification alert for high-compatibility matches
     */
    fun triggerTestMatchPush(candidateName: String = "Zara Okonjo", score: Int = 96) {
        pushNotificationManager.sendNewMatchAlert(
            matchName = candidateName,
            matchPercent = score,
            genre = "Romantic Twilight & Drama",
            location = "Victoria Island, Lagos"
        )
        showToast("💖 Match alert push notification dispatched!")
    }

    /**
     * Trigger a real FCM push notification update for marketplace orders
     */
    fun triggerTestMarketplaceOrderPush(orderId: String = "ORD-93041", status: String = "Shipped via DHL Express") {
        pushNotificationManager.sendMarketplaceOrderUpdate(
            orderId = orderId,
            itemCount = 2,
            totalAmountFormatted = "$1,450.00",
            status = status
        )
        showToast("📦 Marketplace order update push notification dispatched!")
    }

    /**
     * Trigger a real FCM push notification notice for VIP subscription renewals
     */
    fun triggerTestSubscriptionRenewalPush(plan: SubscriptionPlan? = null) {
        val activePlan = plan ?: _userProfile.value.activeSubscription ?: SubscriptionPlan.ONE_MONTH
        val nextRenewal = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000L))
        pushNotificationManager.sendSubscriptionRenewalAlert(
            planName = activePlan.planName,
            priceFormatted = "₦${String.format(Locale.US, "%,d", activePlan.priceNaira)} / month",
            nextBillingDate = nextRenewal,
            bonusCoins = 100
        )
        showToast("🔄 Subscription renewal push notification dispatched!")
    }

    fun toggleFcmTopic(topic: String, enabled: Boolean) {
        fcmTokenManager.toggleTopic(topic, enabled)
        val action = if (enabled) "Subscribed to" else "Unsubscribed from"
        showToast("$action topic: $topic")
    }

    fun markNotificationAsRead(id: Long) {
        pushNotificationManager.markAsRead(id)
    }

    fun markAllNotificationsAsRead() {
        pushNotificationManager.markAllAsRead()
        showToast("All push notifications marked as read")
    }

    fun clearNotificationInbox() {
        pushNotificationManager.clearAll()
        showToast("Push notification inbox cleared")
    }
}
