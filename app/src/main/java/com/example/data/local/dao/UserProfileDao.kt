package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserProfileDao: Room Data Access Object for persisting and querying user profile records,
 * linking directly with the registration flow to store mobile number, email, and display picture.
 */
@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity): Long

    @Query("""
        UPDATE user_profiles 
        SET fullName = :name, 
            mobileNumber = :mobile, 
            email = :email, 
            facialPictureUri = :photoUri, 
            mediaDisplayType = :mediaType, 
            isRegistered = 1, 
            updatedAtEpochMs = :updatedAt 
        WHERE id = 1
    """)
    suspend fun updateRegistrationDetails(
        name: String,
        mobile: String,
        email: String,
        photoUri: String?,
        mediaType: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE user_profiles SET coins = :coins, updatedAtEpochMs = :updatedAt WHERE id = 1")
    suspend fun updateCoins(coins: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("""
        UPDATE user_profiles 
        SET coins = :coins, 
            checkInStreak = :streak, 
            lastDailyCheckInEpochMs = :lastCheckIn, 
            updatedAtEpochMs = :updatedAt 
        WHERE id = 1
    """)
    suspend fun updateCheckIn(
        coins: Int,
        streak: Int,
        lastCheckIn: Long,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE user_profiles SET activeSubscriptionPlan = :plan, updatedAtEpochMs = :updatedAt WHERE id = 1")
    suspend fun updateSubscription(plan: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM user_profiles")
    suspend fun clearProfile()
}
