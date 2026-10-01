package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MatchUserProfile
import com.example.data.model.RelationshipGoal
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubElectricViolet
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatchPreferencesDialog(
    userProfile: MatchUserProfile,
    onDismiss: () -> Unit,
    onApplyPreferences: (
        goal: RelationshipGoal,
        favoriteGenre: String,
        preferredHobbies: List<String>,
        minAge: Int,
        maxAge: Int
    ) -> Unit
) {
    var selectedGoal by remember { mutableStateOf(userProfile.relationshipGoal) }
    var selectedGenre by remember { mutableStateOf(userProfile.favoriteGenre) }
    var minAge by remember { mutableFloatStateOf(userProfile.preferredMinAge.toFloat()) }
    var maxAge by remember { mutableFloatStateOf(userProfile.preferredMaxAge.toFloat()) }
    var selectedHobbies by remember { mutableStateOf(userProfile.preferredHobbies.toSet()) }

    val cinemaGenreOptions = listOf(
        "Romantic Twilight & Drama",
        "Action & Sci-Fi",
        "Drama & Romance",
        "Twilight & Romantic Thrillers",
        "Drama & Mystery",
        "Comedy & Adventure",
        "Art House & Indie Cinema"
    )

    val hobbyOptions = listOf(
        "Film Premiere",
        "Cinema Viewing",
        "Fine Dining",
        "Travel",
        "Art Galleries",
        "Afro-Beats",
        "Tennis",
        "Tech Innovations",
        "Gym & Fitness",
        "Photography"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("match_preferences_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
            border = BorderStroke(1.5.dp, ZubCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ZubCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Algorithm Match Preferences",
                                color = ZubTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tune AI compatibility parameters",
                                color = ZubCyan,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = ZubTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Desired Relationship Goal
                Text(
                    text = "1. TARGET RELATIONSHIP GOAL",
                    color = ZubActionAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                RelationshipGoal.values().forEach { goal ->
                    val isSelected = selectedGoal == goal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ZubTwilightCrimson.copy(alpha = 0.2f) else ZubCardBg)
                            .border(1.dp, if (isSelected) ZubTwilightCrimson else ZubBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedGoal = goal }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (isSelected) ZubTwilightCrimson else ZubTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = goal.label,
                            color = if (isSelected) ZubTextPrimary else ZubTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = ZubTwilightCrimson, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Favorite Cinema Taste & Screen Synergy
                Text(
                    text = "2. CINEMA & SCREEN SYNERGY TASTE",
                    color = ZubCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    cinemaGenreOptions.forEach { genre ->
                        val isSelected = selectedGenre == genre
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ZubCyan.copy(alpha = 0.22f) else ZubCardBg)
                                .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedGenre = genre }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = if (isSelected) ZubCyan else ZubTextMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = genre,
                                    color = if (isSelected) ZubCyan else ZubTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Preferred Age Range
                Text(
                    text = "3. PREFERRED CANDIDATE AGE: ${minAge.toInt()} - ${maxAge.toInt()} YEARS",
                    color = ZubElectricViolet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Min: ${minAge.toInt()}", color = ZubTextMuted, fontSize = 11.sp)
                    Slider(
                        value = minAge,
                        onValueChange = {
                            if (it < maxAge) minAge = it
                        },
                        valueRange = 20f..40f,
                        steps = 20,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(thumbColor = ZubCyan, activeTrackColor = ZubCyan)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Max: ${maxAge.toInt()}", color = ZubTextMuted, fontSize = 11.sp)
                    Slider(
                        value = maxAge,
                        onValueChange = {
                            if (it > minAge) maxAge = it
                        },
                        valueRange = 25f..50f,
                        steps = 25,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(thumbColor = ZubElectricViolet, activeTrackColor = ZubElectricViolet)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Preferred Shared Hobbies
                Text(
                    text = "4. SHARED LIFESTYLE & HOBBIES",
                    color = ZubActionAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    hobbyOptions.forEach { hobby ->
                        val isSelected = selectedHobbies.contains(hobby)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ZubActionAmber.copy(alpha = 0.22f) else ZubCardBg)
                                .border(1.dp, if (isSelected) ZubActionAmber else ZubBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    val current = selectedHobbies.toMutableSet()
                                    if (isSelected) current.remove(hobby) else current.add(hobby)
                                    selectedHobbies = current
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isSelected) "✓ $hobby" else "+ $hobby",
                                color = if (isSelected) ZubActionAmber else ZubTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Apply Button
                NeonButton(
                    text = "✨ Calculate & Re-Rank Compatible Matches",
                    onClick = {
                        onApplyPreferences(
                            selectedGoal,
                            selectedGenre,
                            selectedHobbies.toList(),
                            minAge.toInt(),
                            maxAge.toInt()
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
