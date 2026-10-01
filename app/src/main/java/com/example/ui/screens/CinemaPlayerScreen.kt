package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GeneratedMovie
import com.example.data.model.VideoScene
import com.example.ui.components.GenreBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.RatingBar
import com.example.ui.components.StarRatingPicker
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubSurfaceVariant
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ZubZeroViewModel

@Composable
fun CinemaPlayerScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val activeMovie by viewModel.activeMovie.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentSceneIndex by viewModel.currentSceneIndex.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()

    val movie = activeMovie
    if (movie == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No Movie Selected", color = ZubTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Head over to the AI Studio to render your first cinematic movie.", color = ZubTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(
                    text = "Launch Studio",
                    onClick = { viewModel.setTab(AppNavTab.AI_STUDIO) }
                )
            }
        }
        return
    }

    val currentScene = movie.scenes.getOrNull(currentSceneIndex) ?: movie.scenes.firstOrNull()
    var selectedDetailTab by remember { mutableStateOf(0) } // 0: Shot Sequencer, 1: Screenplay Script
    var userRating by remember { mutableStateOf(5) }

    // Smooth subtle camera drift animation
    val infiniteTransition = rememberInfiniteTransition(label = "camera_drift")
    val cameraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cinema_player_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Video Viewport Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                // Background visual asset with cinematic camera scale
                Image(
                    painter = painterResource(id = R.drawable.img_cinema_hero),
                    contentDescription = "Movie Scene",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(if (isPlaying) cameraScale else 1.02f),
                    contentScale = ContentScale.Crop
                )

                // Cinematic Letterbox & Vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xCC000000),
                                    Color.Transparent,
                                    Color(0xE6000000)
                                )
                            )
                        )
                )

                // Top Viewport Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GenreBadge(genre = movie.genre)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x99000000))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "4K • 60 FPS",
                                color = ZubCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SCENE ${currentSceneIndex + 1} OF ${movie.scenes.size}",
                            color = ZubTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Center Live Dialogue Subtitle
                if (currentScene != null && currentScene.dialogue.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 54.dp, start = 16.dp, end = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xB3050912))
                            .border(0.8.dp, ZubCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentScene.dialogue,
                            color = Color(0xFFF0FDF4),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Bottom Transport Scrub Bar & Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { playbackProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = ZubCyan,
                        trackColor = Color(0x66FFFFFF)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentScene?.cameraMovement ?: "Camera Pan",
                            color = ZubTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.previousScene() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Scene", tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = { viewModel.togglePlayPause() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ZubCyan)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color(0xFF03101C),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.nextScene() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next Scene", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Text(
                            text = "${currentScene?.durationSeconds ?: 6}s",
                            color = ZubCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Movie Meta & Overview
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.title,
                        color = ZubTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.weight(1f)
                    )

                    RatingBar(
                        rating = movie.rating,
                        reviewsCount = movie.reviewsCount,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.logline,
                    color = ZubTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Movie Rating System
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ZubSurface),
                    border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Rate This Movie Reel",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (userRating > 0) "Your rating: $userRating ★" else "Tap star to rate",
                                color = ZubTextMuted,
                                fontSize = 10.sp
                            )
                        }

                        StarRatingPicker(
                            rating = userRating,
                            onRatingSelected = { newRating ->
                                userRating = newRating
                                viewModel.incrementActivity("Rated movie '${movie.title}' $newRating stars")
                                viewModel.showToast("⭐ Thank you! You rated '${movie.title}' $newRating stars!")
                            },
                            starSize = 22.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.showToast("Saved ${movie.title} to local database!") },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, ZubCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Project", color = ZubCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.showToast("Generated shareable 4K reel preview link!") },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, ZubBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = ZubTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Reel", color = ZubTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Tabs: Shot Sequencer vs AI Screenplay
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ZubBorder)
            ) {
                TabRow(
                    selectedTabIndex = selectedDetailTab,
                    containerColor = Color.Transparent,
                    contentColor = ZubCyan,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedDetailTab]),
                            color = ZubCyan,
                            height = 2.5.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedDetailTab == 0,
                        onClick = { selectedDetailTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SHOT SEQUENCER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedDetailTab == 1,
                        onClick = { selectedDetailTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI SCREENPLAY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        }

        if (selectedDetailTab == 0) {
            itemsIndexed(movie.scenes) { index, scene ->
                val isCurrent = index == currentSceneIndex
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.nextScene()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) ZubSurfaceVariant else ZubCardBg
                    ),
                    border = BorderStroke(
                        if (isCurrent) 1.5.dp else 1.dp,
                        if (isCurrent) ZubCyan else ZubBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) ZubCyan else ZubBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${scene.sceneNumber}",
                                color = if (isCurrent) Color(0xFF03101C) else ZubTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = scene.title,
                                color = ZubTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = scene.visualPrompt,
                                color = ZubTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = ZubIceBlue, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(scene.ambientAudio, color = ZubIceBlue, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${scene.durationSeconds}s",
                            color = if (isCurrent) ZubCyan else ZubTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Full Screenplay Script View
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ZubBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SYNOPSIS & NARRATIVE ARC",
                            color = ZubCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = movie.synopsis,
                            color = ZubTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "SCREENPLAY EXCERPTS",
                            color = ZubCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        movie.scenes.forEach { sc ->
                            Surface(
                                color = ZubSurface,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.8.dp, ZubBorder.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "EXT./INT. SCENE ${sc.sceneNumber} - ${sc.title.uppercase()}",
                                        color = ZubActionAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = sc.visualPrompt,
                                        color = ZubTextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "CAMERA DIRECTION: ${sc.cameraMovement}",
                                        color = ZubIceBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (sc.dialogue.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = sc.dialogue,
                                            color = ZubTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
