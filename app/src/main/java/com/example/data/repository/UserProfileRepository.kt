package com.example.data.repository

import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * UserProfileRepository: Intermediary repository managing user profile persistence
 * in Room Database and exposing reactive updates to the ViewModel.
 */
class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    /**
     * Reactive flow of the current persisted UserProfile.
     */
    val userProfileFlow: Flow<UserProfile?> = userProfileDao.getUserProfileFlow()
        .map { entity -> entity?.toDomainModel() }

    /**
     * Retrieves the current profile from Room synchronously or returns null if not saved.
     */
    suspend fun getProfile(): UserProfile? {
        return userProfileDao.getUserProfile()?.toDomainModel()
    }

    /**
     * Inserts or updates the full user profile entity in Room.
     */
    suspend fun saveProfile(profile: UserProfile): Long {
        val entity = UserProfileEntity.fromDomainModel(profile)
        return userProfileDao.insertOrUpdateProfile(entity)
    }

    /**
     * Directly records the user registration details into Room database:
     * Full Name, Mobile Number, Email, and Display Picture (facialPictureUri/mediaDisplayType).
     */
    suspend fun saveRegistration(
        fullName: String,
        mobileNumber: String,
        email: String,
        mediaType: String,
        photoUri: String?,
        currentProfile: UserProfile
    ): UserProfile {
        val updated = currentProfile.copy(
            isRegistered = true,
            fullName = fullName,
            mobileNumber = mobileNumber,
            email = email,
            mediaDisplayType = mediaType,
            facialPictureUri = photoUri ?: "facial_verified.jpg",
            coins = if (!currentProfile.hasClaimedFirstTimeBonus) currentProfile.coins + 50 else currentProfile.coins,
            hasClaimedFirstTimeBonus = true
        )
        saveProfile(updated)
        return updated
    }

    /**
     * Updates coins balance in Room.
     */
    suspend fun updateCoins(coins: Int) {
        userProfileDao.updateCoins(coins)
    }

    /**
     * Updates daily check-in streak and timestamp in Room.
     */
    suspend fun updateCheckIn(coins: Int, streak: Int, lastCheckInEpochMs: Long) {
        userProfileDao.updateCheckIn(coins, streak, lastCheckInEpochMs)
    }

    /**
     * Updates active subscription plan in Room.
     */
    suspend fun updateSubscription(planName: String?) {
        userProfileDao.updateSubscription(planName)
    }
}
