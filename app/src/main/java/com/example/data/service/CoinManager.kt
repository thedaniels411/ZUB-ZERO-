package com.example.data.service

import com.example.data.local.dao.CoinDao
import com.example.data.local.entity.CoinTransactionEntity
import com.example.data.model.AdRewardResult
import com.example.data.model.CoinDeductionResult
import com.example.data.model.CoinTransaction
import com.example.data.model.CoinTransactionType
import com.example.data.model.DailyCheckInResult
import com.example.data.model.DailyStreakDayInfo
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * CoinManager service responsible for:
 * 1. Daily check-in rewards & consecutive streak calculations (Days 1–7 with escalating bonuses).
 * 2. Ad-watch rewards & 2-ad cycle completion milestones (+60 coins).
 * 3. Coin deduction & affordability validation for AI video generation (50 coins per 20 seconds / 2.5 coins/sec).
 * 4. Local transaction auditing via Room persistence.
 */
class CoinManager(
    private val coinDao: CoinDao? = null
) {

    companion object {
        // Video Economics
        const val COINS_PER_SECOND = 2.5
        const val STANDARD_VIDEO_SECONDS = 20
        const val STANDARD_VIDEO_COIN_COST = 50 // 50 coins = 20s video (₦350 value)

        // Daily Check-in Economics
        const val BASE_DAILY_REWARD = 30
        const val DAILY_CHECK_IN_COOLDOWN_MS = 24 * 3600 * 1000L // 24 hours
        const val STREAK_RESET_WINDOW_MS = 48 * 3600 * 1000L // 48 hours to preserve streak

        // 7-Day Streak Reward Ladder (Day 1: 30c, Day 2: 35c, Day 3: 40c, Day 4: 45c, Day 5: 50c, Day 6: 60c, Day 7: 100c)
        val STREAK_REWARDS = listOf(30, 35, 40, 45, 50, 60, 100)

        // Ad Rewards Economics
        const val REQUIRED_ADS_FOR_CYCLE = 2
        const val AD_STEP_1_REWARD = 10 // Immediate bonus for watching 1st ad
        const val AD_STEP_2_CYCLE_BONUS = 50 // Completion bonus for 2nd ad (totaling 60 coins for cycle)
        const val REQUIRED_ACTIVITIES_FOR_AD_CYCLE = 6
    }

    // =========================================================================
    // 1. DAILY CHECK-IN REWARDS
    // =========================================================================

    /**
     * Checks if user is eligible to claim the daily check-in reward.
     */
    fun canClaimDailyCheckIn(lastCheckInEpochMs: Long, now: Long = System.currentTimeMillis()): Boolean {
        if (lastCheckInEpochMs == 0L) return true
        return (now - lastCheckInEpochMs) >= DAILY_CHECK_IN_COOLDOWN_MS
    }

    /**
     * Calculates remaining hours until the next daily check-in becomes available.
     */
    fun getHoursUntilNextCheckIn(lastCheckInEpochMs: Long, now: Long = System.currentTimeMillis()): Int {
        if (lastCheckInEpochMs == 0L) return 0
        val diff = now - lastCheckInEpochMs
        if (diff >= DAILY_CHECK_IN_COOLDOWN_MS) return 0
        val remainingMs = DAILY_CHECK_IN_COOLDOWN_MS - diff
        return ((remainingMs + (3600 * 1000L - 1)) / (3600 * 1000L)).toInt().coerceAtLeast(1)
    }

    /**
     * Calculates the streak day for the incoming claim.
     */
    fun calculateNextStreakDay(
        currentStreak: Int,
        lastCheckInEpochMs: Long,
        now: Long = System.currentTimeMillis()
    ): Int {
        if (lastCheckInEpochMs == 0L) return 1
        val elapsed = now - lastCheckInEpochMs

        return when {
            elapsed > STREAK_RESET_WINDOW_MS -> 1 // Streak broken (>48h)
            currentStreak >= 7 -> 1 // Cycle completed, loops back to Day 1
            else -> currentStreak + 1
        }
    }

    /**
     * Returns coin reward corresponding to the streak day (1 to 7).
     */
    fun getDailyRewardForStreakDay(day: Int): Int {
        val index = (day - 1).coerceIn(0, STREAK_REWARDS.size - 1)
        return STREAK_REWARDS[index]
    }

    /**
     * Processes a daily check-in claim for the given user profile.
     * Validates cooldown, updates streak, adds coins, and records transaction.
     */
    suspend fun claimDailyCheckIn(
        user: UserProfile,
        now: Long = System.currentTimeMillis()
    ): Pair<UserProfile, DailyCheckInResult> {
        val lastClaim = user.lastDailyCheckInEpochMs

        if (!canClaimDailyCheckIn(lastClaim, now)) {
            val remainingHours = getHoursUntilNextCheckIn(lastClaim, now)
            val nextEpoch = lastClaim + DAILY_CHECK_IN_COOLDOWN_MS
            return Pair(
                user,
                DailyCheckInResult.AlreadyClaimed(
                    currentStreak = user.checkInStreak,
                    nextClaimEpochMs = nextEpoch,
                    hoursRemaining = remainingHours,
                    message = "Daily reward already claimed. Next check-in in ~$remainingHours hour(s)."
                )
            )
        }

        val newStreak = calculateNextStreakDay(user.checkInStreak, lastClaim, now)
        val coinsToAward = getDailyRewardForStreakDay(newStreak)
        val newBalance = user.coins + coinsToAward
        val nextClaimEpoch = now + DAILY_CHECK_IN_COOLDOWN_MS

        val updatedUser = user.copy(
            coins = newBalance,
            lastDailyCheckInEpochMs = now,
            checkInStreak = newStreak,
            activityCount = user.activityCount + 1
        )

        // Log transaction in Room
        val tx = CoinTransaction(
            type = if (newStreak > 1) CoinTransactionType.DAILY_STREAK_BONUS else CoinTransactionType.DAILY_CHECK_IN,
            amount = coinsToAward,
            balanceAfter = newBalance,
            description = "Day $newStreak Daily Check-in Claim (+$coinsToAward coins)"
        )
        recordTransaction(tx)

        val successMessage = if (newStreak == 7) {
            "🎉 Day 7 JACKPOT Claimed! +$coinsToAward Coins added to your balance!"
        } else {
            "Day $newStreak Daily Reward Claimed! +$coinsToAward Coins added (Streak: $newStreak Days)!"
        }

        return Pair(
            updatedUser,
            DailyCheckInResult.Success(
                coinsAwarded = coinsToAward,
                streakDay = newStreak,
                newBalance = newBalance,
                nextClaimEpochMs = nextClaimEpoch,
                message = successMessage
            )
        )
    }

    /**
     * Returns structured info for a 7-day streak calendar UI.
     */
    fun getStreakWeekInfo(
        currentStreak: Int,
        lastCheckInEpochMs: Long,
        now: Long = System.currentTimeMillis()
    ): List<DailyStreakDayInfo> {
        val canClaimToday = canClaimDailyCheckIn(lastCheckInEpochMs, now)
        val todayStreak = if (canClaimToday) {
            calculateNextStreakDay(currentStreak, lastCheckInEpochMs, now)
        } else {
            currentStreak.coerceIn(1, 7)
        }

        return (1..7).map { day ->
            val reward = getDailyRewardForStreakDay(day)
            val isCompleted = if (canClaimToday) day < todayStreak else day <= todayStreak
            val isCurrent = day == todayStreak
            DailyStreakDayInfo(
                dayNumber = day,
                rewardCoins = reward,
                isCompleted = isCompleted,
                isCurrentDay = isCurrent,
                isClaimable = isCurrent && canClaimToday
            )
        }
    }

    // =========================================================================
    // 2. AD-WATCH REWARDS
    // =========================================================================

    /**
     * Processes an ad-watch event within the 2-ad reward cycle.
     */
    suspend fun processAdWatch(
        user: UserProfile,
        now: Long = System.currentTimeMillis()
    ): Pair<UserProfile, AdRewardResult> {
        val currentAdCount = user.adsWatchedForCycle

        if (currentAdCount < 1) {
            // Watched 1st ad in cycle
            val coinsAwarded = AD_STEP_1_REWARD
            val newBalance = user.coins + coinsAwarded
            val updatedUser = user.copy(
                coins = newBalance,
                adsWatchedForCycle = 1,
                activityCount = user.activityCount + 1
            )

            recordTransaction(
                CoinTransaction(
                    type = CoinTransactionType.AD_WATCH_REWARD,
                    amount = coinsAwarded,
                    balanceAfter = newBalance,
                    description = "Watched Ad 1/2 (+10 Coins micro-reward)"
                )
            )

            return Pair(
                updatedUser,
                AdRewardResult.Progress(
                    adsWatched = 1,
                    adsRequired = REQUIRED_ADS_FOR_CYCLE,
                    coinsAwarded = coinsAwarded,
                    newBalance = newBalance,
                    message = "Advert 1/2 watched! +10 Coins earned. Watch 1 more advert to activate +50 bonus coins!"
                )
            )
        } else {
            // Watched 2nd ad -> Complete cycle!
            val coinsAwarded = AD_STEP_2_CYCLE_BONUS
            val newBalance = user.coins + coinsAwarded
            val updatedUser = user.copy(
                coins = newBalance,
                adsWatchedForCycle = 0, // Reset cycle
                activityCount = 0 // Reset activity threshold
            )

            recordTransaction(
                CoinTransaction(
                    type = CoinTransactionType.AD_CYCLE_BONUS,
                    amount = coinsAwarded,
                    balanceAfter = newBalance,
                    description = "Completed 2-Ad Cycle (+50 Bonus Coins, Total 60c)"
                )
            )

            return Pair(
                updatedUser,
                AdRewardResult.CycleComplete(
                    totalBonusCoins = coinsAwarded,
                    newBalance = newBalance,
                    message = "🎉 2-Ad Cycle Complete! +50 Bonus Coins activated! Total +60 Coins added to your balance."
                )
            )
        }
    }

    // =========================================================================
    // 3. DEDUCTION OF COINS FOR VIDEO GENERATION
    // =========================================================================

    /**
     * Computes the coin cost for generating a video of specific duration.
     * Standard: 20 seconds = 50 coins (2.5 coins/second).
     */
    fun calculateVideoCost(durationSeconds: Int = STANDARD_VIDEO_SECONDS): Int {
        val safeSeconds = durationSeconds.coerceAtLeast(1)
        return (safeSeconds * COINS_PER_SECOND).toInt().coerceAtLeast(1)
    }

    /**
     * Checks if the user has sufficient coins or subscription privileges to render video.
     */
    fun canAffordVideoGeneration(user: UserProfile, durationSeconds: Int = STANDARD_VIDEO_SECONDS): Boolean {
        if (user.activeSubscription != null) return true
        val cost = calculateVideoCost(durationSeconds)
        return user.coins >= cost
    }

    /**
     * Deducts coins for AI video generation.
     * If user holds an active subscription, deduction is bypassed (unlimited rendering).
     */
    suspend fun deductForVideoGeneration(
        user: UserProfile,
        durationSeconds: Int = STANDARD_VIDEO_SECONDS
    ): Pair<UserProfile, CoinDeductionResult> {
        // 1. Subscription bypass
        if (user.activeSubscription != null) {
            return Pair(
                user,
                CoinDeductionResult.BypassedBySubscription(
                    planName = user.activeSubscription.planName,
                    currentCoins = user.coins,
                    durationSeconds = durationSeconds,
                    message = "Unlimited 4K video rendering active with ${user.activeSubscription.planName}. 0 coins deducted!"
                )
            )
        }

        val requiredCost = calculateVideoCost(durationSeconds)

        // 2. Insufficient balance check
        if (user.coins < requiredCost) {
            val deficit = requiredCost - user.coins
            return Pair(
                user,
                CoinDeductionResult.InsufficientCoins(
                    requiredCoins = requiredCost,
                    currentCoins = user.coins,
                    deficit = deficit,
                    message = "Insufficient coins! $requiredCost coins needed for ${durationSeconds}s video render (Balance: ${user.coins}c, deficit: ${deficit}c). Claim daily check-in or watch ads!"
                )
            )
        }

        // 3. Deduction
        val remaining = user.coins - requiredCost
        val updatedUser = user.copy(
            coins = remaining,
            activityCount = user.activityCount + 1
        )

        recordTransaction(
            CoinTransaction(
                type = CoinTransactionType.VIDEO_GENERATION_DEDUCTION,
                amount = -requiredCost,
                balanceAfter = remaining,
                description = "Rendered ${durationSeconds}s AI Movie Sequence (-$requiredCost coins)"
            )
        )

        return Pair(
            updatedUser,
            CoinDeductionResult.Success(
                coinsDeducted = requiredCost,
                remainingCoins = remaining,
                durationSeconds = durationSeconds,
                message = "Deducted $requiredCost coins for ${durationSeconds}s video render. Remaining balance: $remaining coins."
            )
        )
    }

    // =========================================================================
    // 4. TRANSACTION HISTORY & AUDIT
    // =========================================================================

    /**
     * Records a transaction in the local Room database if DAO is available.
     */
    suspend fun recordTransaction(transaction: CoinTransaction) {
        coinDao?.let { dao ->
            val entity = CoinTransactionEntity(
                id = transaction.id,
                transactionType = transaction.type.name,
                amount = transaction.amount,
                balanceAfter = transaction.balanceAfter,
                description = transaction.description,
                timestamp = transaction.timestamp
            )
            dao.insertTransaction(entity)
        }
    }

    /**
     * Flow of recent coin transactions for UI displays.
     */
    fun getRecentTransactions(limit: Int = 15): Flow<List<CoinTransaction>> {
        return coinDao?.getRecentTransactions(limit)?.map { entities ->
            entities.map { entity ->
                val type = try {
                    CoinTransactionType.valueOf(entity.transactionType)
                } catch (e: Exception) {
                    CoinTransactionType.DAILY_CHECK_IN
                }
                CoinTransaction(
                    id = entity.id,
                    type = type,
                    amount = entity.amount,
                    balanceAfter = entity.balanceAfter,
                    description = entity.description,
                    timestamp = entity.timestamp
                )
            }
        } ?: flowOf(emptyList())
    }
}
