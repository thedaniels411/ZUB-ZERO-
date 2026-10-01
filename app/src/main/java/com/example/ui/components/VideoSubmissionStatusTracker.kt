package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedMovie
import com.example.data.model.GenerationStageDetail
import com.example.data.model.IdeaToVideoPromptSubmission
import com.example.data.model.MovieGenre
import com.example.data.model.SubmissionFilter
import com.example.data.model.VideoGenerationStage
import com.example.data.model.VideoGenerationStatus
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dedicated visual status tracking and submission pipeline tracker for the Idea-to-Video Studio.
 */
@Composable
fun VideoSubmissionStatusTracker(
    selectedSubmission: IdeaToVideoPromptSubmission?,
    submissionsList: List<IdeaToVideoPromptSubmission>,
    onSelectSubmission: (String) -> Unit,
    onCancelSubmission: (String) -> Unit,
    onRetrySubmission: (String) -> Unit,
    onDeleteSubmission: (String) -> Unit,
    onPlayMovie: (GeneratedMovie) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_submission_status_tracker"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = ZubCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NEURAL PIPELINE & STATUS TRACKER",
                    color = ZubTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            if (selectedSubmission != null && selectedSubmission.isInProgress) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ZubCyan.copy(alpha = 0.2f))
                        .border(1.dp, ZubCyan, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(10.dp),
                            color = ZubCyan,
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "LIVE RENDERING",
                            color = ZubCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Text(
                    text = "${submissionsList.size} Job(s) Tracked",
                    color = ZubTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Focused Submission Stage Breakdown Card
        if (selectedSubmission != null) {
            FocusedSubmissionTrackerCard(
                submission = selectedSubmission,
                onCancel = { onCancelSubmission(selectedSubmission.id) },
                onRetry = { onRetrySubmission(selectedSubmission.id) },
                onPlay = { selectedSubmission.resultMovie?.let { onPlayMovie(it) } }
            )
        }

        // Submissions Queue & History Carousel
        if (submissionsList.isNotEmpty()) {
            SubmissionsHistoryStrip(
                submissions = submissionsList,
                selectedId = selectedSubmission?.id,
                onSelect = onSelectSubmission,
                onDelete = onDeleteSubmission
            )
        }
    }
}

@Composable
private fun FocusedSubmissionTrackerCard(
    submission: IdeaToVideoPromptSubmission,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onPlay: () -> Unit
) {
    val statusColor = when (submission.status) {
        VideoGenerationStatus.QUEUED -> ZubActionAmber
        VideoGenerationStatus.ANALYZING_PROMPT,
        VideoGenerationStatus.SCRIPT_SYNTHESIS,
        VideoGenerationStatus.CAMERA_RIGGING,
        VideoGenerationStatus.NEURAL_RENDERING,
        VideoGenerationStatus.AUDIO_MASTERING -> ZubCyan
        VideoGenerationStatus.COMPLETED -> ZubIceBlue
        VideoGenerationStatus.FAILED,
        VideoGenerationStatus.CANCELLED -> ZubTwilightCrimson
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("focused_submission_card"),
        colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, statusColor.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: ID, Genre Badge, Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ZubSurface)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = submission.id,
                            color = ZubTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    GenreBadge(genre = submission.genre)
                }

                // Status Badge
                StatusPill(status = submission.status, color = statusColor)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Concept Prompt
            Text(
                text = submission.title,
                color = ZubTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "\"${submission.prompt}\"",
                color = ZubTextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Directorial Rig Metadata Tag Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetaPill(label = submission.aspectRatio)
                MetaPill(label = submission.cameraMotion)
                MetaPill(label = "${submission.durationSeconds}s / ${submission.coinCost}c")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Progress & Remaining Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = submission.currentStageName,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (submission.isInProgress) {
                        "~${submission.estimatedTimeRemainingSeconds}s remaining (${(submission.progress * 100).toInt()}%)"
                    } else if (submission.isSuccess) {
                        "Render 100% Complete"
                    } else {
                        submission.status.displayName
                    },
                    color = ZubTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { submission.progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = ZubSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5-Stage Step-by-Step Pipeline Timeline
            Text(
                text = "STAGE BREAKDOWN (5 PIPELINE GATES)",
                color = ZubTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val stages = submission.stageTimeline.ifEmpty {
                    // Fallback default 5 stages if empty
                    listOf(
                        GenerationStageDetail(VideoGenerationStage.STAGE_QUEUE, "Job Ingestion & Verification", "Directorial parameters validated", isCompleted = submission.progress >= 0.1f),
                        GenerationStageDetail(VideoGenerationStage.STAGE_PROMPT_GEMINI, "Gemini Script & Story Parsing", "3-act cinematic breakdown synthesized", isCompleted = submission.progress >= 0.35f),
                        GenerationStageDetail(VideoGenerationStage.STAGE_CAMERA_BLOCKING, "Camera Motion & Optics Rig", "Optical focal lengths & trajectory locked", isCompleted = submission.progress >= 0.6f),
                        GenerationStageDetail(VideoGenerationStage.STAGE_NEURAL_SYNTHESIS, "Neural Volumetric Synthesis", "3D frost shaders and motion frames rendered", isCompleted = submission.progress >= 0.85f),
                        GenerationStageDetail(VideoGenerationStage.STAGE_AUDIO_EXPORT, "Soundstage Mastering & Export", "Soundtrack mastered & packaging ready", isCompleted = submission.progress >= 1.0f)
                    )
                }

                stages.forEachIndexed { idx, stageDetail ->
                    StageRowItem(
                        stepIndex = idx + 1,
                        detail = stageDetail,
                        isCurrentStage = submission.currentStageIndex == (idx + 1) && submission.isInProgress
                    )
                }
            }

            // Error notice if failed
            if (!submission.failureReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ZubTwilightCrimson.copy(alpha = 0.15f))
                        .border(1.dp, ZubTwilightCrimson.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Error: ${submission.failureReason}",
                        color = ZubTwilightCrimson,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (submission.isInProgress) {
                    OutlinedButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ZubTwilightCrimson),
                        border = BorderStroke(1.dp, ZubTwilightCrimson.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("cancel_submission_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel Job", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else if (submission.isSuccess && submission.resultMovie != null) {
                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(containerColor = ZubCyan, contentColor = ZubBlack),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("play_completed_movie_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Play in Cinema Theater", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (submission.status == VideoGenerationStatus.FAILED || submission.status == VideoGenerationStatus.CANCELLED) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = ZubActionAmber, contentColor = ZubBlack),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("retry_submission_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retry Pipeline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StageRowItem(
    stepIndex: Int,
    detail: GenerationStageDetail,
    isCurrentStage: Boolean
) {
    val stepColor = when {
        detail.isCompleted -> ZubIceBlue
        isCurrentStage || detail.isInProgress -> ZubCyan
        else -> ZubTextMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrentStage) ZubCyan.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Stage Indicator Circle
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    when {
                        detail.isCompleted -> ZubIceBlue
                        isCurrentStage -> ZubCyan.copy(alpha = 0.2f)
                        else -> ZubSurface
                    }
                )
                .border(
                    1.dp,
                    stepColor,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (detail.isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = ZubDarkNavy,
                    modifier = Modifier.size(12.dp)
                )
            } else if (isCurrentStage) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp,
                    color = ZubCyan
                )
            } else {
                Text(
                    text = "$stepIndex",
                    color = ZubTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.title,
                color = if (detail.isCompleted || isCurrentStage) ZubTextPrimary else ZubTextMuted,
                fontSize = 11.5.sp,
                fontWeight = if (isCurrentStage) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = detail.description,
                color = ZubTextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (detail.isCompleted) {
            Text("DONE", color = ZubIceBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        } else if (isCurrentStage) {
            Text("ACTIVE", color = ZubCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatusPill(status: VideoGenerationStatus, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.displayName.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MetaPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ZubSurface)
            .border(0.8.dp, ZubBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(text = label, color = ZubTextSecondary, fontSize = 10.sp)
    }
}

@Composable
private fun SubmissionsHistoryStrip(
    submissions: List<IdeaToVideoPromptSubmission>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "SUBMISSIONS QUEUE & HISTORY (${submissions.size})",
            color = ZubTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(submissions) { sub ->
                val isSelected = sub.id == selectedId
                val statusColor = when (sub.status) {
                    VideoGenerationStatus.QUEUED -> ZubActionAmber
                    VideoGenerationStatus.ANALYZING_PROMPT,
                    VideoGenerationStatus.SCRIPT_SYNTHESIS,
                    VideoGenerationStatus.CAMERA_RIGGING,
                    VideoGenerationStatus.NEURAL_RENDERING,
                    VideoGenerationStatus.AUDIO_MASTERING -> ZubCyan
                    VideoGenerationStatus.COMPLETED -> ZubIceBlue
                    VideoGenerationStatus.FAILED,
                    VideoGenerationStatus.CANCELLED -> ZubTwilightCrimson
                }

                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(sub.id) }
                        .testTag("submission_history_item_${sub.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) ZubDarkNavy else ZubCardBg
                    ),
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) statusColor else ZubBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sub.id,
                                color = ZubTextMuted,
                                fontSize = 9.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = sub.status.displayName,
                                    color = statusColor,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = sub.title,
                            color = ZubTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = sub.prompt,
                            color = ZubTextSecondary,
                            fontSize = 10.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val timeStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                                .format(Date(sub.submittedAt))
                            Text(timeStr, color = ZubTextMuted, fontSize = 9.sp)

                            IconButton(
                                onClick = { onDelete(sub.id) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = ZubTextMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
