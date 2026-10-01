package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBlack
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

@Composable
fun RegistrationDialog(
    initialMobile: String = "",
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onRegistered: (fullName: String, mobile: String, email: String, mediaType: String, photoUri: String?) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf(initialMobile) }
    var email by remember { mutableStateOf(initialEmail) }
    var mediaDisplayType by remember { mutableStateOf("PHOTO") } // PHOTO or VIDEO
    var hasCapturedFace by remember { mutableStateOf(false) }
    var selectedAvatarIndex by remember { mutableStateOf(1) } // 1, 2, 3 simulate facial capture styles
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("registration_dialog"),
            shape = RoundedCornerShape(18.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ZubCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ZUB-ZERO REGISTRATION",
                            color = ZubCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Facial Picture & Identity Profile",
                            color = ZubTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Free 50 coin reward banner
                Surface(
                    color = ZubCyan.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🎁 FREE 50 COINS WELCOME BONUS",
                                color = ZubCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp
                            )
                            Text(
                                text = "Worth ₦350 Naira (gives 20 seconds of video generation). Granted instantly upon registration!",
                                color = ZubTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mandatory facial picture section
                Text(
                    text = "MANDATORY FACIAL PICTURE / DISPLAY",
                    color = ZubActionAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "A real facial picture or display is required for identity & registration on the app.",
                    color = ZubTextMuted,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ZubSurface)
                        .border(1.2.dp, if (hasCapturedFace) ZubCyan else ZubBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(ZubCardBg)
                                .border(2.dp, if (hasCapturedFace) ZubCyan else ZubIceBlue.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (hasCapturedFace) {
                                Image(
                                    painter = painterResource(
                                        id = when (selectedAvatarIndex) {
                                            1 -> R.drawable.ic_launcher_foreground
                                            else -> R.drawable.ic_launcher_foreground
                                        }
                                    ),
                                    contentDescription = "Facial Profile Capture",
                                    modifier = Modifier.size(70.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Take Facial Photo",
                                    tint = ZubCyan,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    hasCapturedFace = true
                                    mediaDisplayType = "PHOTO"
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (hasCapturedFace && mediaDisplayType == "PHOTO") ZubCyan else ZubBorder)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (hasCapturedFace) "Facial Photo Captured ✓" else "Capture Facial Photo",
                                    color = ZubTextPrimary,
                                    fontSize = 11.sp
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    hasCapturedFace = true
                                    mediaDisplayType = "VIDEO"
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (hasCapturedFace && mediaDisplayType == "VIDEO") ZubIceBlue else ZubBorder)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = ZubIceBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Video Display",
                                    color = ZubTextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input fields: Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Adekoya Daniel", color = ZubTextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input fields: Mobile Number
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("Mobile Number (Required)", fontSize = 11.sp) },
                    placeholder = { Text("e.g. +234 801 234 5678", color = ZubTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ZubCyan) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_phone_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input fields: Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address (Required)", fontSize = 11.sp) },
                    placeholder = { Text("e.g. director@zubzero.ai", color = ZubTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ZubIceBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_email_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = ZubTwilightCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ZubBorder)
                    ) {
                        Text("Cancel", color = ZubTextSecondary, fontSize = 12.sp)
                    }

                    NeonButton(
                        text = "Register & Claim 50 Coins",
                        onClick = {
                            if (!hasCapturedFace) {
                                errorMessage = "Please capture or verify your facial picture/display first."
                                return@NeonButton
                            }
                            if (mobileNumber.trim().length < 7) {
                                errorMessage = "Please enter a valid mobile number."
                                return@NeonButton
                            }
                            if (!email.contains("@")) {
                                errorMessage = "Please enter a valid email address."
                                return@NeonButton
                            }
                            errorMessage = null
                            onRegistered(
                                fullName.ifBlank { "Zub-Zero Creator" },
                                mobileNumber.trim(),
                                email.trim(),
                                mediaDisplayType,
                                "facial_photo_profile.jpg"
                            )
                        },
                        modifier = Modifier.height(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legal Attribution Notice
                Surface(
                    color = ZubBlack,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, ZubBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "OWNERSHIP NOTICE: ZUB-ZERO MAGICAL AI APP is owned and censored by THE CEO OF D-DANIEL'S INVENTION BIZ a PERSON OF ADEKOYA DANIEL EBENEZER.",
                        color = ZubTextMuted,
                        fontSize = 8.5.sp,
                        lineHeight = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
