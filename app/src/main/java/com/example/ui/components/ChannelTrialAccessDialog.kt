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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.PaymentMethodType
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
fun ChannelTrialAccessDialog(
    onDismiss: () -> Unit,
    onActivateTrial: (PaymentMethodType, String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.LOCAL_CARD) }
    var isProcessing by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("channel_trial_access_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
            border = BorderStroke(1.5.dp, ZubActionAmber)
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
                                    .background(ZubActionAmber.copy(alpha = 0.2f))
                                    .border(1.dp, ZubActionAmber, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ZubActionAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CHANNEL ACCESS TRIAL",
                                color = ZubActionAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "3-Day Free Trial Attached",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Price & Value Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ZubBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Initial Trial Access Fee", color = ZubTextSecondary, fontSize = 11.sp)
                                Text("₦500 Naira", color = ZubActionAmber, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ZubCyan.copy(alpha = 0.15f))
                                    .border(1.dp, ZubCyan, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("3 DAYS FREE ACCESS", color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Attaches full 3-day free trial across all subscribed channels, marketplace stores, single & searching matchmaking, and 4K cinema theater before full monthly billing.",
                            color = ZubTextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Benefits breakdown
                Text("TRIAL PERKS INCLUDED:", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                listOf(
                    "3 Days Full Free Trial across all subscribed channels",
                    "Initial 500 Hundred Naira (₦500) trial access directly credited to CEO Receiving Wallets",
                    "Unlimited chat & match contact with single men & women",
                    "Exclusive preview access to Marketplace creator channels & studio assets",
                    "Bonus 50 Video Coins credited immediately"
                ).forEach { perk ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(perk, color = ZubTextPrimary, fontSize = 11.sp, lineHeight = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment channel selection
                Text("SELECT PAYMENT CHANNEL:", color = ZubTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                PaymentMethodType.values().forEach { method ->
                    val isSelected = selectedMethod == method
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedMethod = method },
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) ZubCyan.copy(alpha = 0.15f) else ZubSurface),
                        border = BorderStroke(1.dp, if (isSelected) ZubCyan else ZubBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = if (isSelected) ZubCyan else ZubTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(method.title, color = ZubTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(method.description, color = ZubTextMuted, fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Security notice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Encrypted by Central Clearing & Monnify NIBSS Virtual Routing. Auto-cancels if not renewed.",
                        color = ZubTextMuted,
                        fontSize = 9.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isProcessing) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ZubCyan, strokeWidth = 2.dp)
                    }
                } else {
                    NeonButton(
                        text = "PAY ₦500 & ACTIVATE 3 DAYS TRIAL",
                        icon = Icons.Default.Star,
                        onClick = {
                            isProcessing = true
                            val ref = "TRIAL-500-${System.currentTimeMillis() % 1000000}"
                            onActivateTrial(selectedMethod, ref)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activate_trial_button")
                    )
                }
            }
        }
    }
}
