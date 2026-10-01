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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppCurrency
import com.example.data.model.CeoBankPayoutDetails
import com.example.data.model.ReceivingWallet
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBorder
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

@Composable
fun CeoWithdrawalDialog(
    receivingWallets: Map<AppCurrency, ReceivingWallet>,
    ceoBankDetails: CeoBankPayoutDetails,
    initialCurrency: AppCurrency = AppCurrency.USD,
    onDismiss: () -> Unit,
    onConfirmWithdrawal: (currency: AppCurrency, amount: Double, bankName: String, accountNumber: String, accountName: String) -> Unit
) {
    var selectedCurrency by remember { mutableStateOf(initialCurrency) }
    val currentWallet = receivingWallets[selectedCurrency]
    val maxAvailable = currentWallet?.balance ?: 0.0

    var withdrawalAmountInput by remember { mutableStateOf(if (maxAvailable > 100) "500" else maxAvailable.toInt().toString()) }
    var destinationBank by remember { mutableStateOf(ceoBankDetails.bankName) }
    var destinationAccountNumber by remember { mutableStateOf(ceoBankDetails.accountNumber) }
    var destinationAccountName by remember { mutableStateOf(ceoBankDetails.accountName) }

    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val amountDouble = withdrawalAmountInput.toDoubleOrNull() ?: 0.0
    val nairaPayout = when (selectedCurrency) {
        AppCurrency.USD -> amountDouble * AppCurrency.USD.exchangeRateToNaira
        AppCurrency.GBP -> amountDouble * AppCurrency.GBP.exchangeRateToNaira
        AppCurrency.NGN -> amountDouble
    }

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("ceo_withdrawal_dialog"),
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
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CEO LOCAL BANK PAYOUT",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Instant Multi-Currency Withdrawal",
                            color = ZubTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ZubActionAmber.copy(alpha = 0.15f))
                            .border(1.dp, ZubActionAmber.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "CEO EXCLUSIVE",
                            color = ZubActionAmber,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Transfer accumulated revenues from USD ($), GBP (£), or NGN (₦) receiving wallets directly into your registered Nigerian bank account.",
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isSuccess) {
                    // Success View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ZubCyan,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Payout Successfully Dispatched!",
                            color = ZubTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₦${String.format("%,.0f", nairaPayout)} has been routed to $destinationBank ($destinationAccountNumber).",
                            color = ZubIceBlue,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(horizontal = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        NeonButton(
                            text = "Done",
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    // 1. Select Source Receiving Wallet
                    Text(
                        text = "1. SELECT SOURCE RECEIVING WALLET",
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
                            val isSelected = selectedCurrency == cur
                            val wallet = receivingWallets[cur]
                            val balance = wallet?.balance ?: 0.0

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedCurrency = cur
                                        errorMessage = null
                                    },
                                color = if (isSelected) ZubSurfaceVariant else ZubSurface,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) ZubCyan else ZubBorder
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${cur.flagEmoji} ${cur.code}",
                                        color = if (isSelected) ZubCyan else ZubTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (cur == AppCurrency.NGN) "₦${String.format("%,.0f", balance)}" else "${cur.symbol}${String.format("%,.0f", balance)}",
                                        color = if (isSelected) Color.White else ZubTextSecondary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Wallet details snippet
                    Surface(
                        color = ZubSurface,
                        border = BorderStroke(1.dp, ZubBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Available Balance:", color = ZubTextMuted, fontSize = 11.sp)
                                Text(
                                    text = if (selectedCurrency == AppCurrency.NGN) "₦${String.format("%,.2f", maxAvailable)}" else "${selectedCurrency.symbol}${String.format("%,.2f", maxAvailable)} ${selectedCurrency.code}",
                                    color = ZubCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Virtual Vault ID:", color = ZubTextMuted, fontSize = 10.sp)
                                Text(
                                    text = currentWallet?.walletAddressOrVirtualAccount ?: "ZUB-ZERO-VAULT",
                                    color = ZubIceBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Withdrawal Amount
                    Text(
                        text = "2. ENTER WITHDRAWAL AMOUNT (${selectedCurrency.code})",
                        color = ZubTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = withdrawalAmountInput,
                        onValueChange = {
                            withdrawalAmountInput = it
                            errorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdrawal_amount_input"),
                        leadingIcon = {
                            Text(
                                text = selectedCurrency.symbol,
                                color = ZubCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = "MAX",
                                color = ZubActionAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clickable { withdrawalAmountInput = maxAvailable.toInt().toString() }
                                    .padding(end = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZubCyan,
                            unfocusedBorderColor = ZubBorder,
                            focusedTextColor = ZubTextPrimary,
                            unfocusedTextColor = ZubTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Naira equivalent calculation box
                    if (selectedCurrency != AppCurrency.NGN && amountDouble > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFF07211B),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Naira Payout Equivalent:",
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "₦${String.format("%,.0f", nairaPayout)}",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Destination Bank Details
                    Text(
                        text = "3. DESTINATION LOCAL BANK (NIGERIA)",
                        color = ZubTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = destinationBank,
                        onValueChange = { destinationBank = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Bank Name", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZubCyan,
                            unfocusedBorderColor = ZubBorder,
                            focusedTextColor = ZubTextPrimary,
                            unfocusedTextColor = ZubTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = destinationAccountNumber,
                        onValueChange = { destinationAccountNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Account Number (10 Digits)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZubCyan,
                            unfocusedBorderColor = ZubBorder,
                            focusedTextColor = ZubTextPrimary,
                            unfocusedTextColor = ZubTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = destinationAccountName,
                        onValueChange = { destinationAccountName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Account Name", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZubCyan,
                            unfocusedBorderColor = ZubBorder,
                            focusedTextColor = ZubTextPrimary,
                            unfocusedTextColor = ZubTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Error Message
                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = err,
                            color = ZubTwilightCrimson,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Actions
                    if (isProcessing) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = ZubCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Routing funds to local bank via NIBSS...", color = ZubCyan, fontSize = 12.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, ZubBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", color = ZubTextSecondary, fontSize = 12.sp)
                            }

                            NeonButton(
                                text = "Withdraw Payout",
                                icon = Icons.Default.ArrowDownward,
                                onClick = {
                                    if (amountDouble <= 0) {
                                        errorMessage = "Please enter a valid withdrawal amount."
                                    } else if (amountDouble > maxAvailable) {
                                        errorMessage = "Amount exceeds available balance of ${selectedCurrency.symbol}${String.format("%,.0f", maxAvailable)}."
                                    } else if (destinationAccountNumber.length < 10) {
                                        errorMessage = "Please enter a 10-digit account number."
                                    } else {
                                        isProcessing = true
                                        onConfirmWithdrawal(
                                            selectedCurrency,
                                            amountDouble,
                                            destinationBank,
                                            destinationAccountNumber,
                                            destinationAccountName
                                        )
                                        isProcessing = false
                                        isSuccess = true
                                    }
                                },
                                modifier = Modifier.weight(1.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
