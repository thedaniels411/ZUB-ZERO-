package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class to manage metadata for AI-generated videos within the ZUB-ZERO video studio.
 * Tracks technical encoding properties, duration, AI model version, render latency,
 * and cinematic directorial specifications.
 */
@JsonClass(generateAdapter = true)
data class GeneratedVideoMetadata(
    @field:Json(name = "title") val title: String,
    @field:Json(name = "durationSeconds") val durationSeconds: Int = 20,
    @field:Json(name = "aiModelVersion") val aiModelVersion: String = "gemini-3.5-flash",
    @field:Json(name = "resolution") val resolution: String = "4K UHD (3840x2160)",
    @field:Json(name = "fps") val fps: Int = 60,
    @field:Json(name = "aspectRatio") val aspectRatio: String = "16:9 Cinema",
    @field:Json(name = "codec") val codec: String = "H.265 / HEVC",
    @field:Json(name = "audioFormat") val audioFormat: String = "Dolby Atmos 5.1 (48kHz)",
    @field:Json(name = "renderEngine") val renderEngine: String = "ZUB-ZERO Neural Render Core v3.2",
    @field:Json(name = "dateGenerated") val dateGenerated: Long = System.currentTimeMillis(),
    @field:Json(name = "generationLatencyMs") val generationLatencyMs: Long = 1850L,
    @field:Json(name = "scenesCount") val scenesCount: Int = 3,
    @field:Json(name = "bitRateMbps") val bitRateMbps: Double = 45.0,
    @field:Json(name = "estimatedFileSizeBytes") val estimatedFileSizeBytes: Long = (20L * 45L * 1024L * 1024L) / 8L
) {
    /**
     * Formats duration as MM:SS string (e.g., "00:20").
     */
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }

    /**
     * Formats file size in readable MB or GB.
     */
    val formattedFileSize: String
        get() {
            val mb = estimatedFileSizeBytes.toDouble() / (1024.0 * 1024.0)
            return if (mb >= 1000.0) {
                String.format(Locale.US, "%.2f GB", mb / 1024.0)
            } else {
                String.format(Locale.US, "%.1f MB", mb)
            }
        }

    /**
     * Human-readable generation date/time format.
     */
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
            return sdf.format(Date(dateGenerated))
        }

    /**
     * Technical one-line specification string for player HUDs and inspection panels.
     */
    val technicalSummary: String
        get() = "$resolution • $fps FPS • $codec • $aiModelVersion • $formattedDuration"
}
