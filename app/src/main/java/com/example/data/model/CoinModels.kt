package com.example.data.model

/**
 * Types of coin transactions supported by the CoinManager service.
 */
enum class CoinTransactionType(val label: String, val isCredit: Boolean) {
    DAILY_CHECK_IN("Daily Check-in Reward", true),
    DAILY_STREAK_BONUS("Daily Streak Bonus", true),
    AD_WATCH_REWARD("Ad-Watch Reward", true),
    AD_CYCLE_BONUS("2-Ad Cycle Complete Bonus", true),
    VIDEO_GENERATION_DEDUCTION("AI Video Generation", false),
    FIRST_TIME_BONUS("Welcome Bonus", true),
    SUBSCRIPTION_BONUS("Subscription Allowance", true),
    ADMIN_GRANT("Executive Grant", true)
}

/**
 * Audit record of a coin balance transaction.
 */
data class CoinTransaction(
    val id: String = "ctx_${System.currentTimeMillis()}_${(100..999).random()}",
    val type: CoinTransactionType,
    val amount: Int, // positive for credits, negative for debits
    val balanceAfter: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Result outcomes for Daily Check-in claims.
 */
sealed class DailyCheckInResult {
    data class Success(
        val coinsAwarded: Int,
        val streakDay: Int,
        val newBalance: Int,
        val nextClaimEpochMs: Long,
        val message: String
    ) : DailyCheckInResult()

    data class AlreadyClaimed(
        val currentStreak: Int,
        val nextClaimEpochMs: Long,
        val hoursRemaining: Int,
        val message: String
    ) : DailyCheckInResult()
}

/**
 * Result outcomes for Ad-watch completions.
 */
sealed class AdRewardResult {
    data class Progress(
        val adsWatched: Int,
        val adsRequired: Int = 2,
        val coinsAwarded: Int,
        val newBalance: Int,
        val message: String
    ) : AdRewardResult()

    data class CycleComplete(
        val totalBonusCoins: Int,
        val newBalance: Int,
        val message: String
    ) : AdRewardResult()

    data class ActivityRequirementNeeded(
        val currentActivities: Int,
        val requiredActivities: Int = 6,
        val message: String
    ) : AdRewardResult()
}

/**
 * Result outcomes for video generation deductions.
 */
sealed class CoinDeductionResult {
    data class Success(
        val coinsDeducted: Int,
        val remainingCoins: Int,
        val durationSeconds: Int,
        val message: String
    ) : CoinDeductionResult()

    data class BypassedBySubscription(
        val planName: String,
        val currentCoins: Int,
        val durationSeconds: Int,
        val message: String
    ) : CoinDeductionResult()

    data class InsufficientCoins(
        val requiredCoins: Int,
        val currentCoins: Int,
        val deficit: Int,
        val message: String
    ) : CoinDeductionResult()
}

/**
 * Information describing each day of a 7-day reward streak calendar.
 */
data class DailyStreakDayInfo(
    val dayNumber: Int,
    val rewardCoins: Int,
    val isCompleted: Boolean,
    val isCurrentDay: Boolean,
    val isClaimable: Boolean
)
