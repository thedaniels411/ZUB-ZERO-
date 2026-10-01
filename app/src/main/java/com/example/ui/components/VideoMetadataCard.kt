package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedVideoMetadata
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary

/**
 * Reusable UI card displaying comprehensive metadata for generated videos,
 * including title, duration, AI model version, resolution, audio format, and render engine.
 */
@Composable
fun VideoMetadataCard(
    metadata: GeneratedVideoMetadata,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_metadata_card"),
        colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Title & AI Model Version Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = ZubCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = metadata.title,
                        color = ZubTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // AI Model Version Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ZubCyan.copy(alpha = 0.2f))
                        .border(1.dp, ZubCyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = ZubCyan,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = metadata.aiModelVersion,
                            color = ZubCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Tech Specs Grid (2x3 Grid)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZubSurface.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Duration & Resolution
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetadataSpecItem(
                        icon = Icons.Default.AccessTime,
                        label = "DURATION",
                        value = "${metadata.formattedDuration} (${metadata.durationSeconds}s)",
                        valueColor = ZubIceBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetadataSpecItem(
                        icon = Icons.Default.HighQuality,
                        label = "RESOLUTION",
                        value = "${metadata.resolution} @ ${metadata.fps}fps",
                        valueColor = ZubTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Aspect Ratio & Codec
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetadataSpecItem(
                        icon = Icons.Default.AspectRatio,
                        label = "ASPECT RATIO",
                        value = metadata.aspectRatio,
                        valueColor = ZubTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MetadataSpecItem(
                        icon = Icons.Default.Save,
                        label = "CODEC & SIZE",
                        value = "${metadata.codec} • ${metadata.formattedFileSize}",
                        valueColor = ZubTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Audio Format & Render Engine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetadataSpecItem(
                        icon = Icons.Default.MusicNote,
                        label = "SOUNDSTAGE",
                        value = metadata.audioFormat,
                        valueColor = ZubActionAmber,
                        modifier = Modifier.weight(1f)
                    )
                    MetadataSpecItem(
                        icon = Icons.Default.AutoAwesome,
                        label = "RENDER ENGINE",
                        value = metadata.renderEngine,
                        valueColor = ZubCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Footer: Timestamp & Technical Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Generated: ${metadata.formattedDate}",
                    color = ZubTextMuted,
                    fontSize = 9.5.sp
                )
                Text(
                    text = "${metadata.scenesCount} Scenes • Latency: ${metadata.generationLatencyMs}ms",
                    color = ZubTextMuted,
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun MetadataSpecItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ZubCyan.copy(alpha = 0.8f),
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                color = ZubTextMuted,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
