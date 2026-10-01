package com.example

import com.example.data.model.AdRewardResult
import com.example.data.model.CoinDeductionResult
import com.example.data.model.DailyCheckInResult
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UserProfile
import com.example.data.service.CoinManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoinManagerTest {

    private lateinit var coinManager: CoinManager

    @Before
    fun setUp() {
        coinManager = CoinManager() // without Room DAO for fast JVM unit testing
    }

    @Test
    fun testDailyCheckIn_firstTime_awardsDay1Reward() = runBlocking {
        val initialUser = UserProfile(
            coins = 50,
            lastDailyCheckInEpochMs = 0L,
            checkInStreak = 1
        )

        val (updatedUser, result) = coinManager.claimDailyCheckIn(initialUser)

        assertTrue(result is DailyCheckInResult.Success)
        val success = result as DailyCheckInResult.Success
        assertEquals(30, success.coinsAwarded) // Day 1 base reward: 30 coins
        assertEquals(1, success.streakDay)
        assertEquals(80, updatedUser.coins)
        assertEquals(1, updatedUser.checkInStreak)
    }

    @Test
    fun testDailyCheckIn_consecutiveStreak_escalatesRewards() = runBlocking {
        val now = 100_000_000_000L
        val day2Time = now + (25 * 3600 * 1000L) // 25h later (valid consecutive day)

        val userDay1 = UserProfile(
            coins = 80,
            lastDailyCheckInEpochMs = now,
            checkInStreak = 1
        )

        val (userDay2, resultDay2) = coinManager.claimDailyCheckIn(userDay1, day2Time)
        assertTrue(resultDay2 is DailyCheckInResult.Success)
        val success2 = resultDay2 as DailyCheckInResult.Success
        assertEquals(35, success2.coinsAwarded) // Day 2: 35 coins
        assertEquals(2, success2.streakDay)
        assertEquals(115, userDay2.coins)
    }

    @Test
    fun testDailyCheckIn_cooldownNotElapsed_returnsAlreadyClaimed() = runBlocking {
        val now = System.currentTimeMillis()
        val user = UserProfile(
            coins = 100,
            lastDailyCheckInEpochMs = now - (5 * 3600 * 1000L), // claimed 5h ago
            checkInStreak = 2
        )

        val (sameUser, result) = coinManager.claimDailyCheckIn(user, now)
        assertTrue(result is DailyCheckInResult.AlreadyClaimed)
        val claimed = result as DailyCheckInResult.AlreadyClaimed
        assertEquals(100, sameUser.coins)
        assertTrue(claimed.hoursRemaining > 0)
    }

    @Test
    fun testDailyCheckIn_streakBrokenAfter48h_resetsToDay1() = runBlocking {
        val now = 100_000_000_000L
        val brokenTime = now + (50 * 3600 * 1000L) // 50 hours later (>48h window)

        val user = UserProfile(
            coins = 100,
            lastDailyCheckInEpochMs = now,
            checkInStreak = 5
        )

        val (updatedUser, result) = coinManager.claimDailyCheckIn(user, brokenTime)
        assertTrue(result is DailyCheckInResult.Success)
        val success = result as DailyCheckInResult.Success
        assertEquals(1, success.streakDay)
        assertEquals(30, success.coinsAwarded) // Resets to Day 1: 30 coins
        assertEquals(130, updatedUser.coins)
    }

    @Test
    fun testAdWatchReward_firstAd_grantsMicroRewardAndProgress() = runBlocking {
        val user = UserProfile(
            coins = 50,
            adsWatchedForCycle = 0,
            activityCount = 6
        )

        val (updatedUser, result) = coinManager.processAdWatch(user)
        assertTrue(result is AdRewardResult.Progress)
        val progress = result as AdRewardResult.Progress
        assertEquals(1, progress.adsWatched)
        assertEquals(10, progress.coinsAwarded)
        assertEquals(60, updatedUser.coins)
        assertEquals(1, updatedUser.adsWatchedForCycle)
    }

    @Test
    fun testAdWatchReward_secondAd_completesCycleAndGrantsBonus() = runBlocking {
        val user = UserProfile(
            coins = 60,
            adsWatchedForCycle = 1,
            activityCount = 6
        )

        val (updatedUser, result) = coinManager.processAdWatch(user)
        assertTrue(result is AdRewardResult.CycleComplete)
        val complete = result as AdRewardResult.CycleComplete
        assertEquals(50, complete.totalBonusCoins)
        assertEquals(110, updatedUser.coins) // 60 + 50 = 110 (total 60 earned across 2 ads)
        assertEquals(0, updatedUser.adsWatchedForCycle)
        assertEquals(0, updatedUser.activityCount)
    }

    @Test
    fun testVideoGenerationDeduction_sufficientCoins_deducts50Coins() = runBlocking {
        val user = UserProfile(
            coins = 70,
            activeSubscription = null
        )

        val (updatedUser, result) = coinManager.deductForVideoGeneration(user, durationSeconds = 20)
        assertTrue(result is CoinDeductionResult.Success)
        val success = result as CoinDeductionResult.Success
        assertEquals(50, success.coinsDeducted)
        assertEquals(20, success.remainingCoins)
        assertEquals(20, updatedUser.coins)
    }

    @Test
    fun testVideoGenerationDeduction_insufficientCoins_returnsFailure() = runBlocking {
        val user = UserProfile(
            coins = 30, // Needs 50 coins
            activeSubscription = null
        )

        val (sameUser, result) = coinManager.deductForVideoGeneration(user, durationSeconds = 20)
        assertTrue(result is CoinDeductionResult.InsufficientCoins)
        val fail = result as CoinDeductionResult.InsufficientCoins
        assertEquals(50, fail.requiredCoins)
        assertEquals(20, fail.deficit)
        assertEquals(30, sameUser.coins) // Coins unaltered
    }

    @Test
    fun testVideoGenerationDeduction_subscriber_bypassesCoinDeduction() = runBlocking {
        val user = UserProfile(
            coins = 15,
            activeSubscription = SubscriptionPlan.ONE_MONTH
        )

        assertTrue(coinManager.canAffordVideoGeneration(user, durationSeconds = 20))

        val (sameUser, result) = coinManager.deductForVideoGeneration(user, durationSeconds = 20)
        assertTrue(result is CoinDeductionResult.BypassedBySubscription)
        assertEquals(15, sameUser.coins) // 0 coins deducted for VIP subscriber
    }
}
