package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UserProfile

/**
 * UserProfileEntity: Room database entity for storing user profile details,
 * specifically mobile number, email, display picture (facial picture URI),
 * registration status, and related economic/subscription state.
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = 1L,
    val fullName: String,
    val mobileNumber: String,
    val email: String,
    val facialPictureUri: String?,
    val mediaDisplayType: String = "PHOTO", // PHOTO or VIDEO
    val isRegistered: Boolean = true,
    val activeSubscriptionPlan: String? = null,
    val coins: Int = 50,
    val checkInStreak: Int = 1,
    val lastDailyCheckInEpochMs: Long = 0L,
    val activityCount: Int = 0,
    val adsWatchedForCycle: Int = 0,
    val hasClaimedFirstTimeBonus: Boolean = true,
    val isTrialActive: Boolean = false,
    val trialDaysRemaining: Int = 3,
    val hasPaidInitialTrialFee: Boolean = false,
    val trialExpirationEpochMs: Long = 0L,
    val registeredAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
) {
    /**
     * Converts Room persistence entity to UI/Domain UserProfile model.
     */
    fun toDomainModel(): UserProfile {
        val subscription = activeSubscriptionPlan?.let { planName ->
            try {
                SubscriptionPlan.valueOf(planName)
            } catch (e: Exception) {
                SubscriptionPlan.entries.firstOrNull { it.planName == planName }
            }
        }

        return UserProfile(
            isRegistered = isRegistered,
            fullName = fullName,
            mobileNumber = mobileNumber,
            email = email,
            facialPictureUri = facialPictureUri,
            mediaDisplayType = mediaDisplayType,
            activeSubscription = subscription,
            coins = coins,
            lastDailyCheckInEpochMs = lastDailyCheckInEpochMs,
            activityCount = activityCount,
            adsWatchedForCycle = adsWatchedForCycle,
            hasClaimedFirstTimeBonus = hasClaimedFirstTimeBonus,
            checkInStreak = checkInStreak,
            isTrialActive = isTrialActive,
            trialDaysRemaining = trialDaysRemaining,
            hasPaidInitialTrialFee = hasPaidInitialTrialFee,
            trialExpirationEpochMs = trialExpirationEpochMs
        )
    }

    companion object {
        /**
         * Maps domain UserProfile into Room entity for database storage.
         */
        fun fromDomainModel(domain: UserProfile, id: Long = 1L): UserProfileEntity {
            return UserProfileEntity(
                id = id,
                fullName = domain.fullName,
                mobileNumber = domain.mobileNumber,
                email = domain.email,
                facialPictureUri = domain.facialPictureUri,
                mediaDisplayType = domain.mediaDisplayType,
                isRegistered = domain.isRegistered,
                activeSubscriptionPlan = domain.activeSubscription?.name,
                coins = domain.coins,
                checkInStreak = domain.checkInStreak,
                lastDailyCheckInEpochMs = domain.lastDailyCheckInEpochMs,
                activityCount = domain.activityCount,
                adsWatchedForCycle = domain.adsWatchedForCycle,
                hasClaimedFirstTimeBonus = domain.hasClaimedFirstTimeBonus,
                isTrialActive = domain.isTrialActive,
                trialDaysRemaining = domain.trialDaysRemaining,
                hasPaidInitialTrialFee = domain.hasPaidInitialTrialFee,
                trialExpirationEpochMs = domain.trialExpirationEpochMs,
                updatedAtEpochMs = System.currentTimeMillis()
            )
        }
    }
}
