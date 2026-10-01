package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.GeneratorType
import com.example.data.model.MovieGenre
import com.example.ui.components.GenreBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.VideoSubmissionStatusTracker
import com.example.ui.components.ExoVideoPreviewPlayer
import com.example.ui.components.VideoMetadataCard
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBlack
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubElectricViolet
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubSurfaceVariant
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson
import com.example.ui.viewmodel.ZubZeroViewModel

@Composable
fun AiStudioScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val generatorType by viewModel.generatorType.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationProgress by viewModel.generationProgress.collectAsState()
    val generationStatus by viewModel.generationStatus.collectAsState()

    val ideaText by viewModel.ideaInput.collectAsState()
    val scriptText by viewModel.scriptInput.collectAsState()
    val selectedRatio by viewModel.selectedAspectRatio.collectAsState()
    val selectedMotion by viewModel.selectedCameraMotion.collectAsState()
    val selectedScore by viewModel.selectedAudioScore.collectAsState()
    val videoSubmissions by viewModel.videoSubmissions.collectAsState()
    val selectedTrackingSubmission by viewModel.selectedTrackingSubmission.collectAsState()
    val activeMovie by viewModel.activeMovie.collectAsState()
    val isSavingVideo by viewModel.isSavingVideo.collectAsState()
    val lastSavedVideoResult by viewModel.lastSavedVideoResult.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("ai_studio_screen"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI CINEMA STUDIO",
                                color = ZubTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ZubCyan.copy(alpha = 0.18f))
                                    .border(0.8.dp, ZubCyan.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = ZubCyan,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "GEMINI 3.5",
                                        color = ZubCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Direct, generate & render cinematic video from prompts",
                            color = ZubTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZubCyan.copy(alpha = 0.15f))
                            .border(1.dp, ZubCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.showSubscriptionDialog.value = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        val userProfile by viewModel.userProfile.collectAsState()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = ZubActionAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${userProfile.coins}c (${userProfile.videoSecondsAvailable.toInt()}s)", color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Generator Type Tabs
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ZubBorder)
                ) {
                    TabRow(
                        selectedTabIndex = generatorType.ordinal,
                        containerColor = Color.Transparent,
                        contentColor = ZubCyan,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[generatorType.ordinal]),
                                color = ZubCyan,
                                height = 3.dp
                            )
                        }
                    ) {
                        GeneratorType.values().forEach { type ->
                            Tab(
                                selected = generatorType == type,
                                onClick = { viewModel.setGeneratorType(type) },
                                text = {
                                    Text(
                                        text = type.label,
                                        color = if (generatorType == type) ZubCyan else ZubTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (generatorType == type) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Genre Selector: Action, Drama, Twilight, Sci-Fi
            item {
                Column {
                    Text(
                        text = "1. SELECT CINEMATIC GENRE",
                        color = ZubTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MovieGenre.values().forEach { genre ->
                            val isSelected = selectedGenre == genre
                            val (accentColor, genreBg) = when (genre) {
                                MovieGenre.ACTION -> ZubActionAmber to ZubActionAmber.copy(alpha = 0.15f)
                                MovieGenre.DRAMA -> ZubElectricViolet to ZubElectricViolet.copy(alpha = 0.15f)
                                MovieGenre.TWILIGHT -> ZubTwilightCrimson to ZubTwilightCrimson.copy(alpha = 0.15f)
                                MovieGenre.SCI_FI -> ZubCyan to ZubCyan.copy(alpha = 0.15f)
                            }

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setSelectedGenre(genre) }
                                    .testTag("genre_chip_${genre.name.lowercase()}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) genreBg else ZubCardBg
                                ),
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) accentColor else ZubBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = genre.displayName,
                                        color = if (isSelected) accentColor else ZubTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(accentColor)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input Modality Box
            item {
                when (generatorType) {
                    GeneratorType.IDEA_TO_VIDEO -> {
                        IdeaToVideoInput(
                            idea = ideaText,
                            onIdeaChange = { viewModel.ideaInput.value = it },
                            selectedGenre = selectedGenre,
                            isGeneratingScript = viewModel.isGeneratingScriptOnly.collectAsState().value,
                            generatedScript = viewModel.generatedScriptPreview.collectAsState().value,
                            onGenerateScript = { viewModel.generateScriptFromIdea() },
                            onSendToFullScript = {
                                viewModel.setGeneratorType(GeneratorType.SCRIPT_TO_VIDEO)
                            }
                        )
                    }
                    GeneratorType.SCRIPT_TO_VIDEO -> {
                        ScriptToVideoInput(
                            script = scriptText,
                            onScriptChange = { viewModel.scriptInput.value = it },
                            onLoadTemplate = { template -> viewModel.scriptInput.value = template },
                            genre = selectedGenre
                        )
                    }
                    GeneratorType.IMAGE_TO_VIDEO -> {
                        ImageToVideoInput(
                            selectedMotion = selectedMotion,
                            onMotionSelect = { viewModel.selectedCameraMotion.value = it }
                        )
                    }
                }
            }

            // Director Technical Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ZubBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "DIRECTOR CAMERA & AUDIO RIG",
                            color = ZubTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Aspect Ratio
                        Text(text = "Aspect Ratio", color = ZubTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("16:9 Cinema", "2.39:1 Anamorphic", "9:16 Shorts").forEach { ratio ->
                                val isSelected = selectedRatio == ratio
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ZubCyan.copy(alpha = 0.2f) else ZubSurface)
                                        .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.selectedAspectRatio.value = ratio }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = ratio,
                                        color = if (isSelected) ZubCyan else ZubTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Audio Score
                        Text(text = "Cinematic Audio Mood", color = ZubTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val scores = listOf(
                                "Epic Cyber-Orchestral",
                                "Twilight Gothic Strings",
                                "Action Bass Drop",
                                "Sub-Zero Ambient Drone"
                            )
                            items(scores) { score ->
                                val isSelected = selectedScore == score
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ZubCyan.copy(alpha = 0.2f) else ZubSurface)
                                        .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.selectedAudioScore.value = score }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = if (isSelected) ZubCyan else ZubTextMuted, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = score,
                                            color = if (isSelected) ZubCyan else ZubTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Render Button with Coin Requirement Header
            item {
                val userProfile by viewModel.userProfile.collectAsState()
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = ZubActionAmber,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Render Cost: 50 Coins / 20s",
                                color = ZubTextSecondary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = if (userProfile.activeSubscription != null) "UNLIMITED VIP 👑" else "${userProfile.coins} Coins Available",
                            color = if (userProfile.coins >= 50 || userProfile.activeSubscription != null) ZubCyan else ZubTwilightCrimson,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    NeonButton(
                        text = "RENDER CINEMATIC MOVIE (AI)",
                        icon = Icons.Default.AutoAwesome,
                        onClick = { viewModel.generateMovie() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGenerating
                    )
                }
            }

            // AI Video Prompt Submission Pipeline & Live Status Tracker
            item {
                VideoSubmissionStatusTracker(
                    selectedSubmission = selectedTrackingSubmission,
                    submissionsList = videoSubmissions,
                    onSelectSubmission = { viewModel.selectVideoSubmissionForTracking(it) },
                    onCancelSubmission = { viewModel.cancelVideoSubmission(it) },
                    onRetrySubmission = { viewModel.retryVideoSubmission(it) },
                    onDeleteSubmission = { viewModel.deleteVideoSubmission(it) },
                    onPlayMovie = { movie -> viewModel.playMovie(movie) }
                )
            }

            // Reusable ExoPlayer Video Preview for Generated Content within the Studio
            val movieToPreview = selectedTrackingSubmission?.resultMovie ?: activeMovie
            if (movieToPreview != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = ZubCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "STUDIO VIDEO PREVIEW (EXOPLAYER)",
                                    color = ZubTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Text(
                                text = "4K UHD • 60 FPS",
                                color = ZubIceBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        ExoVideoPreviewPlayer(
                            movie = movieToPreview,
                            modifier = Modifier.fillMaxWidth(),
                            onFullScreenClick = {
                                viewModel.playMovie(movieToPreview)
                            },
                            onSaveToLocalStorage = {
                                viewModel.saveVideoToLocalStorage(movieToPreview)
                            }
                        )

                        // Technical Metadata for Generated Video (Title, Duration, AI Model Version, Codec)
                        VideoMetadataCard(
                            metadata = movieToPreview.metadata,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Save to Local Storage Action Button Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NeonButton(
                                text = if (isSavingVideo) "Saving to Device..." else "Save to Device",
                                icon = Icons.Default.Download,
                                enabled = !isSavingVideo,
                                onClick = {
                                    viewModel.saveVideoToLocalStorage(movieToPreview)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_video_button")
                            )

                            NeonButton(
                                text = "Play in Theater",
                                icon = Icons.Default.PlayArrow,
                                accentColor = ZubIceBlue,
                                onClick = {
                                    viewModel.playMovie(movieToPreview)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("play_in_theater_button")
                            )
                        }
                    }
                }
            }
        }

        // Generating Dialog
        if (isGenerating) {
            Dialog(onDismissRequest = {}) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, ZubCyan)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ZubCyan.copy(alpha = 0.15f))
                                .border(1.5.dp, ZubCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ZUB-ZERO NEURAL ENGINE",
                            color = ZubTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = generationStatus,
                            color = ZubIceBlue,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { generationProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ZubCyan,
                            trackColor = ZubSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${(generationProgress * 100).toInt()}% Synthesized",
                            color = ZubTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Save Video Success Dialog
        lastSavedVideoResult?.let { savedResult ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissSavedVideoDialog() },
                containerColor = ZubDarkNavy,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ZubCyan.copy(alpha = 0.2f))
                            .border(1.5.dp, ZubCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DownloadDone,
                            contentDescription = null,
                            tint = ZubCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Video Saved to Storage",
                        color = ZubTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your 4K video preview has been saved to your device's local Movies directory and is ready in your Gallery.",
                            color = ZubTextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ZubSurface)
                                .border(1.dp, ZubBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "FILE: ${savedResult.fileName}",
                                    color = ZubIceBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "LOCATION: ${savedResult.relativePath}",
                                    color = ZubTextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                Text(
                                    text = "SIZE: ${String.format(Locale.US, "%.1f MB", savedResult.fileSizeBytes / (1024.0 * 1024.0))}",
                                    color = ZubActionAmber,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.shareSavedVideo(savedResult, activeMovie?.title ?: "Video")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", color = ZubCyan, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.openSavedVideo(savedResult)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZubCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = ZubBlack,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open", color = ZubBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissSavedVideoDialog() }) {
                        Text("Dismiss", color = ZubTextSecondary, fontSize = 11.sp)
                    }
                }
            )
        }
    }
}

@Composable
private fun IdeaToVideoInput(
    idea: String,
    onIdeaChange: (String) -> Unit,
    selectedGenre: MovieGenre,
    isGeneratingScript: Boolean = false,
    generatedScript: String? = null,
    onGenerateScript: () -> Unit = {},
    onSendToFullScript: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. PITCH YOUR MOVIE CONCEPT (GEMINI AI)",
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                GenreBadge(genre = selectedGenre)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = idea,
                onValueChange = onIdeaChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .testTag("idea_input_field"),
                placeholder = {
                    Text(
                        "Describe your movie idea, characters, cinematic tone, tension, or world...",
                        color = ZubTextMuted,
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZubCyan,
                    unfocusedBorderColor = ZubBorder,
                    focusedTextColor = ZubTextPrimary,
                    unfocusedTextColor = ZubTextPrimary,
                    cursorColor = ZubCyan
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // AI Action Bar: Generate Screenplay via Gemini
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Screenplay Synthesis Engine",
                    color = ZubTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isGeneratingScript) ZubSurface else ZubCyan.copy(alpha = 0.15f))
                        .border(1.dp, ZubCyan, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isGeneratingScript) { onGenerateScript() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("generate_script_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isGeneratingScript) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp,
                                color = ZubCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Writing Script...",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Draft Screenplay (Gemini)",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Screenplay preview card if generated
            if (!generatedScript.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = ZubCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GEMINI SCRIPT SYNTHESIS",
                                    color = ZubCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Edit in Script Tab →",
                                color = ZubIceBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onSendToFullScript() }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = generatedScript.take(240) + if (generatedScript.length > 240) "..." else "",
                            color = ZubTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Quick Inspiration Prompts:", color = ZubTextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val prompts = when (selectedGenre) {
                    MovieGenre.ACTION -> listOf(
                        "High-speed supercar chase through frozen Shibuya neon bridge",
                        "Stealth operative infiltrating an orbital cryogenic base",
                        "Martial arts duel inside a quantum server vault"
                    )
                    MovieGenre.DRAMA -> listOf(
                        "Two estranged heirs confronting each other at a rain-swept estate",
                        "A disgraced conductor performing their final secret symphony",
                        "A courtroom thriller in an alpine dynasty trial"
                    )
                    MovieGenre.TWILIGHT -> listOf(
                        "Ancient vampire aristocrat sheltering in a gothic misty cathedral",
                        "Werewolf clan sentinel guarding the moonlit silver forest",
                        "Immortal pact sealed beneath twin blood moons"
                    )
                    MovieGenre.SCI_FI -> listOf(
                        "Cryo-sleeper waking up 400 years late on a derelict space freighter",
                        "Rogue neural AI projecting holographic memories across Tokyo",
                        "Cyberpunk detective hunting stolen cybernetic hearts"
                    )
                }
                items(prompts) { p ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ZubSurface)
                            .border(0.8.dp, ZubBorder, RoundedCornerShape(6.dp))
                            .clickable { onIdeaChange(p) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = p,
                            color = ZubTextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScriptToVideoInput(
    script: String,
    onScriptChange: (String) -> Unit,
    onLoadTemplate: (String) -> Unit,
    genre: MovieGenre
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. SCRIPT & SCREENPLAY FORMAT",
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Load Screenplay Preset",
                    color = ZubCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        val template = when (genre) {
                            MovieGenre.ACTION -> "EXT. HIGHWAY OVERPASS - NIGHT\nA black twin-turbo cruiser screeches over frosted asphalt.\n\nDRAKE\n(into headset)\nPerimeter is compromised. Fire the cryo-pulse now!\n\nVOICE (OVER COMMS)\nDetonation in three... two... one."
                            MovieGenre.DRAMA -> "INT. GRAND LIBRARY - DUSK\nRain trickles down towering stained-glass panes.\n\nVICTORIA\nYou gave father your word. And yet you let the dynasty burn.\n\nJULIAN\nSome kingdoms are meant to turn to ash."
                            MovieGenre.TWILIGHT -> "EXT. RAVEN HOLLOW - MIDNIGHT\nHeavy silver fog envelops the ancient stone graveyard.\n\nLUCAS\n(eyes shimmering silver)\nDo not step into the mist. They have awakened.\n\nSERAPHINA\nI stopped fearing the dark three centuries ago."
                            MovieGenre.SCI_FI -> "INT. SUBSITE ZERO - STASIS BAY\nSteam hisses violently from Pod 09.\n\nAI SYSTEM (V.O.)\nVital signs restored. Atmospheric temperature: Sub-Zero.\n\nKAI\nWhere is the extraction squad?"
                        }
                        onLoadTemplate(template)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = script,
                onValueChange = onScriptChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("script_input_field"),
                placeholder = {
                    Text("EXT./INT. SLUGLINE - DAY/NIGHT\nAction description...\nCHARACTER NAME\nDialogue lines...", color = ZubTextMuted, fontSize = 12.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZubCyan,
                    unfocusedBorderColor = ZubBorder,
                    focusedTextColor = ZubTextPrimary,
                    unfocusedTextColor = ZubTextPrimary,
                    cursorColor = ZubCyan
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun ImageToVideoInput(
    selectedMotion: String,
    onMotionSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "2. IMAGE ANIMATOR & MOTION TRAJECTORY",
                color = ZubTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Concept Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, ZubBorder, RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_cinema_hero),
                    contentDescription = "Source Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC070B12))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Source Concept Frame", color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Camera Motion Paths:", color = ZubTextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))

            val motions = listOf(
                "Slow Dolly Zoom",
                "360° Circular Orbit",
                "Low-Altitude FPV Dive",
                "Parallax Depth Push",
                "Horizontal Tracking Pan"
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(motions) { motion ->
                    val isSelected = selectedMotion == motion
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ZubCyan.copy(alpha = 0.2f) else ZubSurface)
                            .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                            .clickable { onMotionSelect(motion) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = motion,
                            color = if (isSelected) ZubCyan else ZubTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
