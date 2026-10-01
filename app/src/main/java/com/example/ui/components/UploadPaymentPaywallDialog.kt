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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PaymentMethodType
import com.example.data.model.UploadFeeTier
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
fun UploadPaymentPaywallDialog(
    initialTier: UploadFeeTier = UploadFeeTier.STANDARD_PROD,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (UploadFeeTier, PaymentMethodType, String) -> Unit
) {
    var selectedTier by remember { mutableStateOf(initialTier) }
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.LOCAL_CARD) }
    var isProcessing by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(1) } // 1 = select tier & payment method, 2 = checkout/ussd/card simulation

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("upload_payment_dialog"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
            border = BorderStroke(1.5.dp, ZubCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MANDATORY UPLOAD CLEARANCE",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Text(
                            text = "Product, Store & Goods Listing Payment",
                            color = ZubTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Per ZUB-ZERO official policy, an upfront clearance fee is required before uploading products, stores, channels, goods or production units.",
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (step == 1) {
                    // Upload Fee Tiers ($10, $50, $80)
                    Text(
                        text = "1. CHOOSE UPLOAD OR ESTABLISHMENT TIER",
                        color = ZubTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    UploadFeeTier.values().forEach { tier ->
                        val isSelected = selectedTier == tier
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedTier = tier }
                                .testTag("upload_tier_${tier.usdPrice}"),
                            color = if (isSelected) ZubSurfaceVariant else ZubSurface,
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) ZubCyan else ZubBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ZubCyan else ZubCardBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$${tier.usdPrice}",
                                        color = if (isSelected) Color(0xFF03101C) else ZubCyan,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = tier.label,
                                            color = ZubTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ZubActionAmber.copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = tier.badge,
                                                color = ZubActionAmber,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = tier.description,
                                        color = ZubTextMuted,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Accepted Payment Methods: Local payment, USSD, Bank Payment
                    Text(
                        text = "2. SELECT PAYMENT CHANNEL",
                        color = ZubTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    PaymentMethodType.values().forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedMethod = method },
                            color = if (isSelected) ZubSurfaceVariant else ZubSurface,
                            border = BorderStroke(
                                if (isSelected) 1.dp else 0.8.dp,
                                if (isSelected) ZubIceBlue else ZubBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, if (isSelected) ZubCyan else ZubTextMuted, CircleShape)
                                        .background(if (isSelected) ZubCyan else Color.Transparent)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = method.title,
                                        color = ZubTextPrimary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = method.description,
                                        color = ZubTextMuted,
                                        fontSize = 9.5.sp
                                    )
                                }
                            }
                        }
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
                            text = "Proceed to Pay ($${selectedTier.usdPrice})",
                            onClick = { step = 2 },
                            modifier = Modifier.height(42.dp)
                        )
                    }
                } else {
                    // Step 2: Instant Gateway Mock & Authorization
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ZubSurface)
                            .border(1.dp, ZubBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount Due:", color = ZubTextSecondary, fontSize = 12.sp)
                                Text(
                                    text = "$${selectedTier.usdPrice} USD (~₦${selectedTier.nairaEquivalent})",
                                    color = ZubCyan,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Selected Method:", color = ZubTextSecondary, fontSize = 12.sp)
                                Text(selectedMethod.title, color = ZubTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            when (selectedMethod) {
                                PaymentMethodType.USSD -> {
                                    Text(
                                        text = "Dial any of these banking USSD codes to clear clearance fee:",
                                        color = ZubTextMuted,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "GTBank: *737*000*${selectedTier.nairaEquivalent}#\nZenith: *966*000*${selectedTier.nairaEquivalent}#\nFirstBank: *894*000*${selectedTier.nairaEquivalent}#\nUBA: *919*000*${selectedTier.nairaEquivalent}#",
                                        color = ZubActionAmber,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                                PaymentMethodType.BANK_TRANSFER -> {
                                    Text(
                                        text = "Pay to Dedicated ZUB-ZERO Clearing Account:",
                                        color = ZubTextMuted,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Bank: Wema / Providus Bank\nAccount: 9054810828\nBeneficiary: D-DANIEL'S INVENTION BIZ\nRef: ZUB-UP-${System.currentTimeMillis() % 100000}",
                                        color = ZubCyan,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                                PaymentMethodType.LOCAL_CARD -> {
                                    Text(
                                        text = "Card Processing via Interswitch / Paystack Direct:",
                                        color = ZubTextMuted,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Card: 5399 **** **** 4810 (Verve/Mastercard)\nHolder: ADEKOYA DANIEL EBENEZER\nSecurity: 3D Secure Verified Token",
                                        color = ZubIceBlue,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isProcessing) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = ZubCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Authorizing Payment & Clearance...", color = ZubCyan, fontSize = 12.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { step = 1 },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, ZubBorder)
                            ) {
                                Text("Back", color = ZubTextSecondary, fontSize = 12.sp)
                            }

                            NeonButton(
                                text = "Confirm & Clear Upload",
                                onClick = {
                                    isProcessing = true
                                    val ref = "ZUB-TXN-${System.currentTimeMillis()}"
                                    onPaymentConfirmed(selectedTier, selectedMethod, ref)
                                },
                                modifier = Modifier.height(42.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mandatory Ownership & Censorship Legal Notice
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
