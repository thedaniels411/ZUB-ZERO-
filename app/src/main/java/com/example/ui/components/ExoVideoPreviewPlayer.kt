package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar
import com.example.R
import com.example.data.model.GeneratedMovie
import com.example.data.model.VideoScene
import com.example.ui.theme.ZubBlack
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson
import kotlinx.coroutines.delay

/**
 * Enhanced reusable video preview UI component using AndroidX Media3 (ExoPlayer & Media3 UI).
 * Robust against remote HTTP errors: operates in real media playback when a valid URI is provided
 * with custom User-Agent, and smoothly falls back to the AI Directorial Storyboard Simulation Engine
 * when no remote media file is attached, avoiding 403 HTTP errors while keeping all controls functional.
 */
@OptIn(UnstableApi::class)
@Composable
fun ExoVideoPreviewPlayer(
    movie: GeneratedMovie?,
    modifier: Modifier = Modifier,
    videoUri: String? = null,
    autoPlay: Boolean = true,
    showDirectorialOverlay: Boolean = true,
    onFullScreenClick: (() -> Unit)? = null,
    onSceneClick: ((Int) -> Unit)? = null,
    onSaveToLocalStorage: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasMediaStream = !videoUri.isNullOrBlank()

    // Compute nominal movie duration from scenes (defaulting to 20 seconds)
    val calculatedMovieDurationMs = ((movie?.scenes?.sumOf { it.durationSeconds } ?: 20) * 1000L).coerceAtLeast(10000L)
    var totalDurationMs by remember(movie) { mutableLongStateOf(calculatedMovieDurationMs) }

    // Playback state
    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isBuffering by remember { mutableStateOf(false) }
    var playbackError by remember { mutableStateOf<String?>(null) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var bufferedPositionMs by remember { mutableLongStateOf(if (hasMediaStream) 0L else calculatedMovieDurationMs) }

    // Volume & Audio state
    var currentVolume by remember { mutableFloatStateOf(1.0f) }
    var isMuted by remember { mutableStateOf(false) }
    var showVolumeSelector by remember { mutableStateOf(false) }

    // Playback speed state
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    // Media3 Resize Mode state (FIT, ZOOM, FILL)
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    // Overlay visibility
    var showControls by remember { mutableStateOf(true) }
    var showCaptions by remember { mutableStateOf(true) }
    var currentSceneIdx by remember { mutableIntStateOf(0) }

    // PlayerView reference holder
    var playerViewRef by remember { mutableStateOf<PlayerView?>(null) }

    // Initialize ExoPlayer with custom User-Agent and safe media loading
    val exoPlayer = remember(videoUri) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("ZubZeroStudio/1.0 (Android; Mobile)")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setAllowCrossProtocolRedirects(true)
        val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = autoPlay
                volume = currentVolume
                if (hasMediaStream) {
                    try {
                        val mediaItem = MediaItem.fromUri(Uri.parse(videoUri))
                        setMediaItem(mediaItem)
                        prepare()
                    } catch (e: Exception) {
                        playbackError = "Media load: ${e.localizedMessage}"
                    }
                }
            }
    }

    // Attach Player Listener if media stream is present
    DisposableEffect(exoPlayer, hasMediaStream) {
        if (!hasMediaStream) {
            onDispose {
                exoPlayer.stop()
                exoPlayer.release()
            }
        } else {
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> isBuffering = true
                        Player.STATE_READY -> {
                            isBuffering = false
                            val dur = exoPlayer.duration
                            if (dur > 0) totalDurationMs = dur
                        }
                        Player.STATE_ENDED -> {
                            isBuffering = false
                            isPlaying = false
                        }
                        Player.STATE_IDLE -> isBuffering = false
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    isBuffering = false
                    playbackError = "Stream error (${error.errorCodeName}). Storyboard mode active."
                }
            }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.stop()
                exoPlayer.release()
            }
        }
    }

    // Manage Lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (hasMediaStream && playbackError == null) {
                        exoPlayer.pause()
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (hasMediaStream && playbackError == null && isPlaying) {
                        exoPlayer.play()
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Timeline Loop: handles both ExoPlayer live stream polling and Storyboard simulation engine
    LaunchedEffect(hasMediaStream, playbackError, exoPlayer, isPlaying, playbackSpeed, totalDurationMs) {
        while (true) {
            if (hasMediaStream && playbackError == null) {
                if (exoPlayer.isPlaying) {
                    currentPositionMs = exoPlayer.currentPosition
                    bufferedPositionMs = exoPlayer.bufferedPosition
                    val dur = exoPlayer.duration
                    if (dur > 0) totalDurationMs = dur
                }
            } else if (isPlaying) {
                // Storyboard simulation mode: advances timeline smoothly
                val stepMs = (250 * playbackSpeed).toLong()
                val nextPos = currentPositionMs + stepMs
                if (nextPos >= totalDurationMs) {
                    currentPositionMs = 0L
                } else {
                    currentPositionMs = nextPos
                }
                bufferedPositionMs = totalDurationMs
            }

            // Map current playback position to the corresponding scene in the movie storyboard
            val scenes = movie?.scenes ?: emptyList()
            if (scenes.isNotEmpty() && totalDurationMs > 0) {
                val progressFraction = (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                val targetScene = (progressFraction * scenes.size).toInt().coerceIn(0, scenes.size - 1)
                if (targetScene != currentSceneIdx) {
                    currentSceneIdx = targetScene
                    onSceneClick?.invoke(targetScene)
                }
            }
            delay(250)
        }
    }

    // Auto-hide controls after inactivity
    LaunchedEffect(showControls, isPlaying, showVolumeSelector) {
        if (showControls && isPlaying && !showVolumeSelector) {
            delay(5000)
            showControls = false
        }
    }

    // Aspect ratio calculation
    val targetAspectRatio = when (movie?.aspectRatio) {
        "2.39:1 Anamorphic" -> 2.39f
        "9:16 Shorts" -> 9f / 16f
        else -> 16f / 9f
    }

    val activeScene = movie?.scenes?.getOrNull(currentSceneIdx) ?: movie?.scenes?.firstOrNull()

    // Unified transport helper lambdas
    val handleTogglePlay = {
        val nextState = !isPlaying
        isPlaying = nextState
        if (hasMediaStream && playbackError == null) {
            if (nextState) exoPlayer.play() else exoPlayer.pause()
        }
    }

    val handleRewind10 = {
        val target = (currentPositionMs - 10000L).coerceAtLeast(0L)
        currentPositionMs = target
        if (hasMediaStream && playbackError == null) {
            exoPlayer.seekTo(target)
        }
    }

    val handleForward10 = {
        val target = (currentPositionMs + 10000L).coerceAtMost(totalDurationMs)
        currentPositionMs = target
        if (hasMediaStream && playbackError == null) {
            exoPlayer.seekTo(target)
        }
    }

    val handleScrub: (Long) -> Unit = { targetMs ->
        val target = targetMs.coerceIn(0L, totalDurationMs)
        currentPositionMs = target
        if (hasMediaStream && playbackError == null) {
            exoPlayer.seekTo(target)
        }
    }

    val handleSkipScene: (Int) -> Unit = { sceneIndex ->
        val scenesCount = movie?.scenes?.size ?: 1
        val targetIdx = sceneIndex.coerceIn(0, scenesCount - 1)
        currentSceneIdx = targetIdx
        val targetMs = (targetIdx.toFloat() / scenesCount.toFloat() * totalDurationMs).toLong()
        currentPositionMs = targetMs
        if (hasMediaStream && playbackError == null) {
            exoPlayer.seekTo(targetMs)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exo_video_preview_player"),
        colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, ZubCyan.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(targetAspectRatio)
                .clip(RoundedCornerShape(16.dp))
                .background(ZubBlack)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                    if (!showControls) showVolumeSelector = false
                }
        ) {
            // View layer: Media3 PlayerView when media stream is active, or high-fidelity storyboard frame
            if (hasMediaStream && playbackError == null) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            setResizeMode(resizeMode)
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(android.graphics.Color.BLACK)
                            playerViewRef = this
                        }
                    },
                    update = { view ->
                        view.setResizeMode(resizeMode)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // High-fidelity storyboard concept frame
                Image(
                    painter = painterResource(id = movie?.imageRes ?: R.drawable.img_cinema_hero),
                    contentDescription = "Cinematic Storyboard Frame",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Dark vignette gradients
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.70f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            // Buffering Spinner
            if (isBuffering) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ZubCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            // Directorial Metadata & Quick Toggles Bar (Top)
            TopDirectorMetadataBar(
                movie = movie,
                hasMediaStream = hasMediaStream && playbackError == null,
                isMuted = isMuted,
                currentVolume = currentVolume,
                playbackSpeed = playbackSpeed,
                resizeMode = resizeMode,
                showCaptions = showCaptions,
                onToggleMute = {
                    isMuted = !isMuted
                    val vol = if (isMuted) 0f else currentVolume
                    exoPlayer.volume = vol
                },
                onVolumeClick = {
                    showVolumeSelector = !showVolumeSelector
                },
                onSpeedClick = {
                    val nextSpeed = when (playbackSpeed) {
                        0.5f -> 1.0f
                        1.0f -> 1.25f
                        1.25f -> 1.5f
                        1.5f -> 2.0f
                        else -> 0.5f
                    }
                    playbackSpeed = nextSpeed
                    if (hasMediaStream && playbackError == null) {
                        exoPlayer.setPlaybackSpeed(nextSpeed)
                    }
                },
                onResizeModeClick = {
                    resizeMode = when (resizeMode) {
                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                    playerViewRef?.setResizeMode(resizeMode)
                },
                onToggleCaptions = { showCaptions = !showCaptions },
                onSaveClick = onSaveToLocalStorage
            )

            // Volume Stepped Presets Selector Popup
            if (showVolumeSelector) {
                VolumePresetsPopup(
                    currentVolume = currentVolume,
                    isMuted = isMuted,
                    onSelectVolume = { vol ->
                        currentVolume = vol
                        isMuted = (vol == 0f)
                        exoPlayer.volume = vol
                        showVolumeSelector = false
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 44.dp, end = 12.dp)
                )
            }

            // Center Play / Pause & Skip Controls Overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = showControls || !isPlaying,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                PlayPauseCenterControl(
                    isPlaying = isPlaying,
                    onTogglePlay = handleTogglePlay,
                    onRewind10 = handleRewind10,
                    onForward10 = handleForward10,
                    onSkipPreviousScene = {
                        handleSkipScene(currentSceneIdx - 1)
                    },
                    onSkipNextScene = {
                        handleSkipScene(currentSceneIdx + 1)
                    }
                )
            }

            // Dialogue Subtitle Overlay
            if (showCaptions && activeScene != null && activeScene.dialogue.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showControls) 90.dp else 18.dp, start = 18.dp, end = 18.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xDD070B12))
                        .border(1.dp, ZubCyan.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = activeScene.dialogue,
                        color = ZubIceBlue,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Enhanced Bottom Control Bar with Media3 DefaultTimeBar & Full Playback Toolbar
            androidx.compose.animation.AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomMedia3ControlsBar(
                    currentPositionMs = currentPositionMs,
                    bufferedPositionMs = bufferedPositionMs,
                    totalDurationMs = totalDurationMs,
                    isPlaying = isPlaying,
                    isMuted = isMuted,
                    currentVolume = currentVolume,
                    movie = movie,
                    activeScene = activeScene,
                    onTogglePlay = handleTogglePlay,
                    onRewind10 = handleRewind10,
                    onForward10 = handleForward10,
                    onScrub = handleScrub,
                    onToggleMute = {
                        isMuted = !isMuted
                        val vol = if (isMuted) 0f else currentVolume
                        exoPlayer.volume = vol
                    },
                    onFullScreenClick = onFullScreenClick,
                    onSaveClick = onSaveToLocalStorage
                )
            }
        }
    }
}

/**
 * AndroidX Media3 UI DefaultTimeBar integration for smooth, frame-accurate seeking.
 */
@OptIn(UnstableApi::class)
@Composable
fun Media3DefaultTimeBarComposable(
    currentPositionMs: Long,
    durationMs: Long,
    bufferedPositionMs: Long,
    onScrub: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            DefaultTimeBar(ctx).apply {
                setPlayedColor(ZubCyan.toArgb())
                setScrubberColor(ZubIceBlue.toArgb())
                setBufferedColor(ZubCyan.copy(alpha = 0.35f).toArgb())
                setUnplayedColor(ZubBorder.copy(alpha = 0.8f).toArgb())
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                addListener(object : TimeBar.OnScrubListener {
                    override fun onScrubStart(timeBar: TimeBar, position: Long) {
                        onScrub(position)
                    }

                    override fun onScrubMove(timeBar: TimeBar, position: Long) {
                        onScrub(position)
                    }

                    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                        if (!canceled) {
                            onScrub(position)
                        }
                    }
                })
            }
        },
        update = { timeBar ->
            val safeDuration = durationMs.coerceAtLeast(1L)
            timeBar.setDuration(safeDuration)
            timeBar.setPosition(currentPositionMs.coerceIn(0L, safeDuration))
            timeBar.setBufferedPosition(bufferedPositionMs.coerceIn(0L, safeDuration))
        },
        modifier = modifier
    )
}

@Composable
private fun TopDirectorMetadataBar(
    movie: GeneratedMovie?,
    hasMediaStream: Boolean,
    isMuted: Boolean,
    currentVolume: Float,
    playbackSpeed: Float,
    resizeMode: Int,
    showCaptions: Boolean,
    onToggleMute: () -> Unit,
    onVolumeClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onResizeModeClick: () -> Unit,
    onToggleCaptions: () -> Unit,
    onSaveClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Branding Badge & Movie Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(ZubCyan.copy(alpha = 0.22f))
                    .border(1.dp, ZubCyan.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ZubCyan,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (hasMediaStream) "MEDIA3 STREAM PREVIEW" else "MEDIA3 STUDIO PREVIEW",
                        color = ZubCyan,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = movie?.title ?: "ZUB-ZERO Preview",
                color = ZubTextPrimary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Right Action Controls Toolbar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Speed Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xBB070B12))
                    .border(0.8.dp, ZubBorder, RoundedCornerShape(6.dp))
                    .clickable { onSpeedClick() }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${playbackSpeed}x",
                    color = ZubCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Aspect Ratio Framing Chip (Media3 AspectRatioFrameLayout)
            val resizeModeLabel = when (resizeMode) {
                AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "ZOOM"
                AspectRatioFrameLayout.RESIZE_MODE_FILL -> "FILL"
                else -> "FIT"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xBB070B12))
                    .border(0.8.dp, ZubBorder, RoundedCornerShape(6.dp))
                    .clickable { onResizeModeClick() }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = resizeModeLabel,
                    color = ZubTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Closed Captions toggle
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (showCaptions) ZubCyan.copy(alpha = 0.25f) else Color(0xBB070B12))
                    .border(0.8.dp, if (showCaptions) ZubCyan else ZubBorder, CircleShape)
                    .clickable { onToggleCaptions() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ClosedCaption,
                    contentDescription = "Toggle Captions",
                    tint = if (showCaptions) ZubCyan else ZubTextMuted,
                    modifier = Modifier.size(13.dp)
                )
            }

            // Save to Local Storage button
            if (onSaveClick != null) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(ZubCyan.copy(alpha = 0.25f))
                        .border(0.8.dp, ZubCyan, CircleShape)
                        .clickable { onSaveClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save Video",
                        tint = ZubCyan,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Volume Toggle & Preset Opener
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xBB070B12))
                    .border(0.8.dp, ZubBorder, RoundedCornerShape(6.dp))
                    .clickable { onVolumeClick() }
                    .padding(horizontal = 5.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            isMuted || currentVolume == 0f -> Icons.AutoMirrored.Filled.VolumeMute
                            currentVolume < 0.5f -> Icons.AutoMirrored.Filled.VolumeDown
                            else -> Icons.AutoMirrored.Filled.VolumeUp
                        },
                        contentDescription = "Volume",
                        tint = if (isMuted) ZubTwilightCrimson else ZubCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isMuted) "0%" else "${(currentVolume * 100).toInt()}%",
                        color = ZubTextPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun VolumePresetsPopup(
    currentVolume: Float,
    isMuted: Boolean,
    onSelectVolume: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, ZubCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
        color = ZubDarkNavy.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "AUDIO VOLUME PRESETS",
                color = ZubTextMuted,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            val presets = listOf(
                0.0f to "Mute (0%)",
                0.25f to "25% Low",
                0.50f to "50% Medium",
                0.75f to "75% High",
                1.00f to "100% Max"
            )

            presets.forEach { (vol, label) ->
                val isSelected = (!isMuted && currentVolume == vol) || (isMuted && vol == 0f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) ZubCyan.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onSelectVolume(vol) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) ZubCyan else ZubTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayPauseCenterControl(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onRewind10: () -> Unit,
    onForward10: () -> Unit,
    onSkipPreviousScene: () -> Unit,
    onSkipNextScene: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Skip Previous Scene
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xAA070B12))
                .border(1.dp, ZubBorder, CircleShape)
                .clickable { onSkipPreviousScene() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous Scene",
                tint = ZubTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        // Rewind 10s
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xAA070B12))
                .border(1.dp, ZubBorder, CircleShape)
                .clickable { onRewind10() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FastRewind,
                contentDescription = "Rewind 10s",
                tint = ZubIceBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        // Center Hero Play / Pause
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(ZubCyan.copy(alpha = 0.25f))
                .border(1.5.dp, ZubCyan, CircleShape)
                .clickable { onTogglePlay() }
                .testTag("exo_play_pause_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = ZubCyan,
                modifier = Modifier.size(28.dp)
            )
        }

        // Fast Forward 10s
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xAA070B12))
                .border(1.dp, ZubBorder, CircleShape)
                .clickable { onForward10() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FastForward,
                contentDescription = "Forward 10s",
                tint = ZubIceBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        // Skip Next Scene
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xAA070B12))
                .border(1.dp, ZubBorder, CircleShape)
                .clickable { onSkipNextScene() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next Scene",
                tint = ZubTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun BottomMedia3ControlsBar(
    currentPositionMs: Long,
    bufferedPositionMs: Long,
    totalDurationMs: Long,
    isPlaying: Boolean,
    isMuted: Boolean,
    currentVolume: Float,
    movie: GeneratedMovie?,
    activeScene: VideoScene?,
    onTogglePlay: () -> Unit,
    onRewind10: () -> Unit,
    onForward10: () -> Unit,
    onScrub: (Long) -> Unit,
    onToggleMute: () -> Unit,
    onFullScreenClick: (() -> Unit)?,
    onSaveClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xEE070B12))
                )
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Row 1: Active Scene Title & Time Counters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = ZubCyan,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SCENE ${activeScene?.sceneNumber ?: 1}: ${activeScene?.title ?: "Sequence"}",
                    color = ZubTextPrimary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val remainingMs = (totalDurationMs - currentPositionMs).coerceAtLeast(0L)
            Text(
                text = "${formatTime(currentPositionMs)} / ${formatTime(totalDurationMs)} (-${formatTime(remainingMs)})",
                color = ZubTextMuted,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 2: Media3 UI DefaultTimeBar Integration
        Media3DefaultTimeBarComposable(
            currentPositionMs = currentPositionMs,
            durationMs = totalDurationMs,
            bufferedPositionMs = bufferedPositionMs,
            onScrub = onScrub,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .testTag("media3_default_time_bar")
        )

        // Row 3: Directorial Transport & Settings Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Controls: Play/Pause, Rewind, Fast Forward, Volume
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Mini Play / Pause Toggle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(ZubCyan.copy(alpha = 0.2f))
                        .clickable { onTogglePlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = ZubCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Rewind 10s
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0x99070B12))
                        .clickable { onRewind10() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "-10s",
                        tint = ZubTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Forward 10s
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0x99070B12))
                        .clickable { onForward10() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "+10s",
                        tint = ZubTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Volume Mute / Level Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x99070B12))
                        .clickable { onToggleMute() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isMuted || currentVolume == 0f) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Volume",
                            tint = if (isMuted) ZubTwilightCrimson else ZubCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isMuted) "Mute" else "${(currentVolume * 100).toInt()}%",
                            color = ZubTextSecondary,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Right Controls: Camera info & Fullscreen
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "OPTICS: ${activeScene?.cameraMovement?.take(22) ?: "35mm Dolly"}",
                    color = ZubTextMuted,
                    fontSize = 8.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (onSaveClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(ZubCyan.copy(alpha = 0.2f))
                            .clickable { onSaveClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Save Video",
                            tint = ZubCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (onFullScreenClick != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0x99070B12))
                            .clickable { onFullScreenClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = ZubTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
