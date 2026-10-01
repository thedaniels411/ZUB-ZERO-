package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing user coin transactions and history ledger.
 */
@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey
    val id: String,
    val transactionType: String,
    val amount: Int,
    val balanceAfter: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
