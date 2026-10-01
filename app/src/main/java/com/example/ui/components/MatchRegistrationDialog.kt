package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MatchGender
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

@Composable
fun MatchRegistrationDialog(
    currentProfile: MatchUserProfile,
    onDismiss: () -> Unit,
    onRegister: (
        fullName: String,
        age: Int,
        gender: MatchGender,
        targetGender: MatchGender,
        location: String,
        profession: String,
        bio: String,
        relationshipGoal: RelationshipGoal,
        phoneOrWhatsapp: String,
        instagramOrSocial: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf(currentProfile.fullName.ifEmpty { "Adekoya Daniel Ebenezer" }) }
    var ageText by remember { mutableStateOf(currentProfile.age.toString()) }
    var gender by remember { mutableStateOf(currentProfile.gender) }
    var targetGender by remember { mutableStateOf(currentProfile.targetGender) }
    var location by remember { mutableStateOf(currentProfile.location) }
    var profession by remember { mutableStateOf(currentProfile.profession) }
    var bio by remember { mutableStateOf(currentProfile.bio) }
    var relationshipGoal by remember { mutableStateOf(currentProfile.relationshipGoal) }
    var phoneOrWhatsapp by remember { mutableStateOf(currentProfile.contactPhoneOrWhatsapp.ifEmpty { "+234 805 481 0828" }) }
    var instagramOrSocial by remember { mutableStateOf(currentProfile.instagramOrSocial.ifEmpty { "@zubzero_founder" }) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("match_registration_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
            border = BorderStroke(1.5.dp, ZubCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(ZubElectricViolet.copy(alpha = 0.2f))
                                    .border(1.dp, ZubElectricViolet, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = ZubElectricViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FREE REGISTRATION",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Single & Searching Profile",
                            color = ZubTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ZubSurface)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = ZubTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Free Registration Notice Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ZubCyan.copy(alpha = 0.12f))
                        .border(1.dp, ZubCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🎁 100% FREE TO REGISTER! Includes immediate 3-Day Free Trial access to connect with single and searching men & women.",
                        color = ZubIceBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Full Name
                Text("YOUR FULL NAME", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("match_fullname_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Gender Selection (I am...)
                Text("I AM A:", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MatchGender.values().forEach { g ->
                        val isSelected = gender == g
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ZubCyan.copy(alpha = 0.2f) else ZubSurface)
                                .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(10.dp))
                                .clickable { gender = g }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${g.emoji} ${g.label}",
                                color = if (isSelected) ZubCyan else ZubTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target Gender Selection (Looking for...)
                Text("LOOKING FOR:", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MatchGender.values().forEach { tg ->
                        val isSelected = targetGender == tg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ZubElectricViolet.copy(alpha = 0.2f) else ZubSurface)
                                .border(1.dp, if (isSelected) ZubElectricViolet else ZubBorder, RoundedCornerShape(10.dp))
                                .clickable { targetGender = tg }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${tg.emoji} ${tg.label}",
                                color = if (isSelected) ZubElectricViolet else ZubTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Age & Location Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(0.4f)) {
                        Text("AGE", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { if (it.length <= 2 && it.all { char -> char.isDigit() }) ageText = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ZubCyan,
                                unfocusedBorderColor = ZubBorder,
                                focusedTextColor = ZubTextPrimary,
                                unfocusedTextColor = ZubTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                    Column(modifier = Modifier.weight(0.6f)) {
                        Text("LOCATION (CITY, STATE)", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ZubCyan,
                                unfocusedBorderColor = ZubBorder,
                                focusedTextColor = ZubTextPrimary,
                                unfocusedTextColor = ZubTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Profession
                Text("PROFESSION / CAREER", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = profession,
                    onValueChange = { profession = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Relationship Goal
                Text("RELATIONSHIP INTENTION", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    RelationshipGoal.values().forEach { goal ->
                        val isSelected = relationshipGoal == goal
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ZubCyan.copy(alpha = 0.15f) else ZubSurface)
                                .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                                .clickable { relationshipGoal = goal }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = goal.label,
                                color = if (isSelected) ZubCyan else ZubTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bio
                Text("ABOUT YOU & WHAT YOU'RE SEARCHING FOR", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone / WhatsApp for verified match connection
                Text("PHONE / WHATSAPP NUMBER", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = phoneOrWhatsapp,
                    onValueChange = { phoneOrWhatsapp = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                NeonButton(
                    text = "REGISTER FREE & ACTIVATE 3-DAY TRIAL",
                    icon = Icons.Default.Favorite,
                    onClick = {
                        val parsedAge = ageText.toIntOrNull() ?: 26
                        onRegister(
                            fullName,
                            parsedAge,
                            gender,
                            targetGender,
                            location,
                            profession,
                            bio,
                            relationshipGoal,
                            phoneOrWhatsapp,
                            instagramOrSocial
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_match_registration")
                )
            }
        }
    }
}
