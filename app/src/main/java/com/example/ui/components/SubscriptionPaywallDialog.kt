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
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppCurrency
import com.example.data.model.PaymentMethodType
import com.example.data.model.SubscriptionPlan
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

@Composable
fun SubscriptionPaywallDialog(
    currentPlan: SubscriptionPlan?,
    onDismiss: () -> Unit,
    onPlanSubscribed: (SubscriptionPlan, PaymentMethodType, String, AppCurrency) -> Unit
) {
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.ONE_MONTH) }
    var selectedCurrency by remember { mutableStateOf(AppCurrency.NGN) }
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.BANK_TRANSFER) }
    var isProcessing by remember { mutableStateOf(false) }
    var isAuthorized by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("subscription_dialog"),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardMembership, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ZUB-ZERO MAGICAL SUBSCRIPTION",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Standard, Pro & Master Plans",
                            color = ZubTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Financial breakdown applies across all channels, studios, and production bodies in ZUB-ZERO magical app.",
                    color = ZubTextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3-Day Free Trial Attached to Subscribed Channel Notice & Quick Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ZubActionAmber.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardMembership, contentDescription = null, tint = ZubActionAmber, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "3-DAY FREE TRIAL ATTACHED",
                                    color = ZubActionAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ZubActionAmber)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("₦500 INITIAL", color = Color(0xFF03101C), fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enjoy 3 days full free trial across all subscribed channels attracting initial ₦500 (Five Hundred Naira) trial access before regular billing.",
                            color = ZubIceBlue,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Plan cards
                SubscriptionPlan.values().forEach { plan ->
                    val isSelected = selectedPlan == plan
                    val accent = when (plan) {
                        SubscriptionPlan.ONE_MONTH -> ZubCyan
                        SubscriptionPlan.THREE_MONTHS -> ZubElectricViolet
                        SubscriptionPlan.ONE_YEAR -> ZubActionAmber
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPlan = plan }
                            .testTag("plan_card_${plan.name.lowercase()}"),
                        color = if (isSelected) ZubSurfaceVariant else ZubSurface,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) accent else ZubBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, if (isSelected) accent else ZubTextMuted, CircleShape)
                                            .background(if (isSelected) accent else Color.Transparent)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = plan.planName,
                                        color = ZubTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(accent.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = plan.badge,
                                        color = accent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = plan.priceFormatted,
                                color = accent,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            plan.perks.forEach { perk ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = accent, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(perk, color = ZubTextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-Currency Selection for Subscription Payment
                Text(
                    text = "SELECT BILLING CURRENCY (RECEIVING WALLETS ROUTE)",
                    color = ZubTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppCurrency.values().forEach { cur ->
                        val isCurSelected = selectedCurrency == cur
                        val curPriceStr = when (cur) {
                            AppCurrency.USD -> selectedPlan.priceUsdFormatted
                            AppCurrency.GBP -> selectedPlan.priceGbpFormatted
                            AppCurrency.NGN -> "₦${String.format("%,d", selectedPlan.priceNaira)}"
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedCurrency = cur },
                            color = if (isCurSelected) ZubSurfaceVariant else ZubSurface,
                            border = BorderStroke(
                                if (isCurSelected) 1.5.dp else 1.dp,
                                if (isCurSelected) ZubCyan else ZubBorder
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${cur.flagEmoji} ${cur.code}",
                                    color = if (isCurSelected) ZubCyan else ZubTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = curPriceStr,
                                    color = if (isCurSelected) Color.White else ZubTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment channel selection
                Text(
                    text = "SELECT PAYMENT METHOD (LOCAL, USSD, BANK)",
                    color = ZubTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

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
                            if (isSelected) ZubCyan else ZubBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (isSelected) ZubCyan else ZubTextMuted, CircleShape)
                                    .background(if (isSelected) ZubCyan else Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(method.title, color = ZubTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
                        Text("Activating Subscription via Banking Rail...", color = ZubCyan, fontSize = 12.sp)
                    }
                } else {
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
                            Text("Dismiss", color = ZubTextSecondary, fontSize = 12.sp)
                        }

                        NeonButton(
                            text = "Subscribe Now (${when (selectedCurrency) {
                                AppCurrency.USD -> selectedPlan.priceUsdFormatted
                                AppCurrency.GBP -> selectedPlan.priceGbpFormatted
                                AppCurrency.NGN -> "₦${String.format("%,d", selectedPlan.priceNaira)}"
                            }})",
                            onClick = {
                                isProcessing = true
                                val ref = "SUB-${System.currentTimeMillis()}"
                                onPlanSubscribed(selectedPlan, selectedMethod, ref, selectedCurrency)
                            },
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
