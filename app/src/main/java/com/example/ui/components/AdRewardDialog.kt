package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBlack
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import kotlinx.coroutines.delay

@Composable
fun AdRewardDialog(
    currentAdCount: Int, // 0 or 1
    onDismiss: () -> Unit,
    onAdCompleted: () -> Unit
) {
    var isSimulatingAd by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(5) } // 5-second simulated video ad
    var adFinished by remember { mutableStateOf(false) }

    val adIndex = currentAdCount + 1

    LaunchedEffect(isSimulatingAd) {
        if (isSimulatingAd) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
            }
            adFinished = true
        }
    }

    Dialog(onDismissRequest = { if (!isSimulatingAd || adFinished) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("ad_reward_dialog"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
            border = BorderStroke(1.5.dp, ZubCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ZubActionAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = ZubActionAmber, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "WATCH 2 ADS TO ACTIVATE 60 COINS",
                            color = ZubActionAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Sponsor Reel Advert $adIndex of 2",
                            color = ZubTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Requirement: 2 ads must be watched to activate the 60 coins boost (unlocked after performing 6 activities in ZUB-ZERO).",
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Video Ad Player Simulation Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ZubBlack)
                        .border(1.dp, ZubBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isSimulatingAd && !adFinished) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PlayCircleOutline,
                                contentDescription = "Play Advert",
                                tint = ZubCyan,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Advert #$adIndex: D-Daniel's Innovation Tech Showcase",
                                color = ZubTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap 'Start Advert' below (5 seconds)",
                                color = ZubTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    } else if (isSimulatingAd && !adFinished) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "PLAYING SPONSORED ADVERT...",
                                color = ZubActionAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ZUB-ZERO MAGICAL AI PRODUCTION ENGINES",
                                color = ZubTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            LinearProgressIndicator(
                                progress = { (5 - secondsRemaining) / 5f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ZubCyan,
                                trackColor = ZubBorder
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Reward in: ${secondsRemaining}s",
                                color = ZubIceBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        // Finished this ad
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (adIndex < 2) "Advert 1 Finished! 1 More Needed." else "Both 2 Adverts Complete!",
                                color = ZubCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (adIndex < 2) "Watch 1 more advert to claim 60 coins" else "Ready to activate 60 coins!",
                                color = ZubTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
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
                        Text("Close", color = ZubTextSecondary, fontSize = 12.sp)
                    }

                    if (!isSimulatingAd && !adFinished) {
                        NeonButton(
                            text = "Start Advert #$adIndex",
                            onClick = { isSimulatingAd = true },
                            modifier = Modifier.height(42.dp)
                        )
                    } else if (adFinished) {
                        NeonButton(
                            text = if (adIndex < 2) "Next Advert (2 of 2)" else "Claim 60 Coins",
                            onClick = onAdCompleted,
                            modifier = Modifier.height(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legal Notice
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
