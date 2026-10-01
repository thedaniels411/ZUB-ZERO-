package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCurrency
import com.example.data.model.ReceivingWallet
import com.example.data.model.WithdrawalTransaction
import com.example.ui.components.CeoWithdrawalDialog
import com.example.ui.components.NeonButton
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
import com.example.ui.viewmodel.ZubZeroViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CeoManagementScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val receivingWallets by viewModel.receivingWallets.collectAsState()
    val ceoBankDetails by viewModel.ceoBankDetails.collectAsState()
    val withdrawalHistory by viewModel.withdrawalHistory.collectAsState()
    val showWithdrawalDialog by viewModel.showWithdrawalDialog.collectAsState()

    var selectedWithdrawalCurrency by remember { mutableStateOf(AppCurrency.USD) }

    // Dialog for withdrawal flow
    if (showWithdrawalDialog) {
        CeoWithdrawalDialog(
            receivingWallets = receivingWallets,
            ceoBankDetails = ceoBankDetails,
            initialCurrency = selectedWithdrawalCurrency,
            onDismiss = { viewModel.showWithdrawalDialog.value = false },
            onConfirmWithdrawal = { currency, amount, bankName, accountNumber, accountName ->
                viewModel.processCeoWithdrawal(
                    currency = currency,
                    amount = amount,
                    bankName = bankName,
                    accountNumber = accountNumber,
                    accountName = accountName
                )
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ceo_management_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ownership & Legal Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.5.dp, ZubCyan)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF03263B), ZubCardBg)
                                )
                            )
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .background(ZubCyan.copy(alpha = 0.2f))
                                        .border(1.5.dp, ZubCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "CEO Verified",
                                        tint = ZubCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CEO MANAGEMENT PORTAL",
                                        color = ZubCyan,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "ADEKOYA DANIEL EBENEZER",
                                        color = ZubTextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ZubActionAmber.copy(alpha = 0.15f))
                                    .border(1.dp, ZubActionAmber.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "EXECUTIVE CENSOR",
                                    color = ZubActionAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = ZubBlack.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.8.dp, ZubBorder.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "LEGAL STATEMENT: ZUB-ZERO MAGICAL AI APP is owned and censored by THE CEO OF D-DANIEL'S INVENTION BIZ a PERSON OF ADEKOYA DANIEL EBENEZER. All subscriptions, clearance fees, and payouts are tracked and managed via multi-currency receiving wallets below.",
                                color = ZubIceBlue,
                                fontSize = 9.5.sp,
                                lineHeight = 13.5.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Multi-Currency Receiving Wallets (USD, GBP, NGN)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RECEIVING WALLETS OVERVIEW",
                        color = ZubTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Live balances from all subscriptions and payments",
                        color = ZubTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ZubCyan.copy(alpha = 0.15f))
                        .border(1.dp, ZubCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "3 ACTIVE WALLETS",
                        color = ZubCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3 Cards: USD, GBP, NGN
        items(listOf(AppCurrency.USD, AppCurrency.GBP, AppCurrency.NGN)) { currency ->
            val wallet = receivingWallets[currency] ?: ReceivingWallet(
                currency = currency,
                balance = 0.0,
                totalRevenueReceived = 0.0,
                pendingPayout = 0.0,
                walletAddressOrVirtualAccount = "ZUB-${currency.code}-VAULT",
                bankOrProvider = "Virtual Clearing Rail"
            )

            ReceivingWalletCard(
                wallet = wallet,
                onWithdrawClick = {
                    selectedWithdrawalCurrency = currency
                    viewModel.showWithdrawalDialog.value = true
                }
            )
        }

        // Section: CEO Destination Bank Account Details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.2.dp, ZubBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CEO LOCAL BANK PAYOUT ACCOUNT",
                                color = ZubTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VERIFIED BVN",
                                color = Color(0xFF10B981),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = ZubSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ZubBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Settlement Bank:", color = ZubTextMuted, fontSize = 11.sp)
                                Text(ceoBankDetails.bankName, color = ZubTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("NUBAN Account:", color = ZubTextMuted, fontSize = 11.sp)
                                Text(
                                    ceoBankDetails.accountNumber,
                                    color = ZubCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Beneficiary Name:", color = ZubTextMuted, fontSize = 11.sp)
                                Text(ceoBankDetails.accountName, color = ZubIceBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("SWIFT / BIC:", color = ZubTextMuted, fontSize = 11.sp)
                                Text(ceoBankDetails.swiftCode, color = ZubTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section: Withdrawal Payout History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = ZubActionAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WITHDRAWAL PAYOUT HISTORY",
                        color = ZubTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = "${withdrawalHistory.size} Payouts",
                    color = ZubTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        if (withdrawalHistory.isEmpty()) {
            item {
                Surface(
                    color = ZubSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ZubBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No withdrawals processed yet.",
                            color = ZubTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Use the 'Withdraw Payout' button on any wallet above.",
                            color = ZubTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        } else {
            items(withdrawalHistory) { tx ->
                WithdrawalTransactionCard(tx = tx)
            }
        }
    }
}

@Composable
private fun ReceivingWalletCard(
    wallet: ReceivingWallet,
    onWithdrawClick: () -> Unit
) {
    val (accentColor, bgGradients) = when (wallet.currency) {
        AppCurrency.USD -> ZubCyan to listOf(Color(0xFF03263B), ZubCardBg)
        AppCurrency.GBP -> ZubElectricViolet to listOf(Color(0xFF280B3B), ZubCardBg)
        AppCurrency.NGN -> Color(0xFF10B981) to listOf(Color(0xFF042B1A), ZubCardBg)
    }

    val currency = wallet.currency

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("wallet_card_${currency.code}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.6f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(bgGradients))
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Flag and Currency Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .border(1.dp, accentColor, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = currency.flagEmoji, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${currency.code} RECEIVING WALLET",
                                color = ZubTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = currency.currencyName,
                                color = ZubTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(0.8.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AUTO-COLLECT",
                            color = accentColor,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Available Balance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Available Balance",
                            color = ZubTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (currency == AppCurrency.NGN) "₦${String.format("%,.0f", wallet.balance)}" else "${currency.symbol}${String.format("%,.2f", wallet.balance)}",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (currency != AppCurrency.NGN) {
                            val nairaEst = wallet.balance * currency.exchangeRateToNaira
                            Text(
                                text = "≈ ₦${String.format("%,.0f", nairaEst)} Naira equivalent",
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Withdraw Button
                    NeonButton(
                        text = "Withdraw",
                        icon = Icons.Default.ArrowDownward,
                        onClick = onWithdrawClick,
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("withdraw_button_${currency.code}")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Vault Details
                Surface(
                    color = ZubBlack.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.6.dp, ZubBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Virtual Account / Vault ID:", color = ZubTextMuted, fontSize = 10.sp)
                            Text(
                                wallet.walletAddressOrVirtualAccount,
                                color = ZubIceBlue,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Lifetime Received:", color = ZubTextMuted, fontSize = 10.sp)
                            Text(
                                text = if (currency == AppCurrency.NGN) "₦${String.format("%,.0f", wallet.totalRevenueReceived)}" else "${currency.symbol}${String.format("%,.2f", wallet.totalRevenueReceived)}",
                                color = ZubTextSecondary,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Banking Partner:", color = ZubTextMuted, fontSize = 10.sp)
                            Text(
                                wallet.bankOrProvider,
                                color = ZubTextMuted,
                                fontSize = 9.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WithdrawalTransactionCard(
    tx: WithdrawalTransaction
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(tx.timestamp) { dateFormat.format(Date(tx.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ZubSurface),
        border = BorderStroke(1.dp, ZubBorder)
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
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${tx.currency.code} Payout to ${tx.destinationBank}",
                            color = ZubTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formattedDate,
                            color = ZubTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (tx.currency == AppCurrency.NGN) "₦${String.format("%,.0f", tx.amountRequested)}" else "${tx.currency.symbol}${String.format("%,.0f", tx.amountRequested)}",
                        color = ZubTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "₦${String.format("%,.0f", tx.nairaPayoutAmount)} Paid",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref: ${tx.reference} • Acct: ${tx.destinationAccount}",
                    color = ZubTextMuted,
                    fontSize = 9.5.sp,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = tx.status,
                        color = Color(0xFF10B981),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
