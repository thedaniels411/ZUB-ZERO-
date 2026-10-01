package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CoinTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Room-persisted coin balance operations and transactions.
 */
@Dao
interface CoinDao {

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CoinTransactionEntity>>

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 15): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransactionEntity)

    @Query("DELETE FROM coin_transactions")
    suspend fun clearTransactions()
}
