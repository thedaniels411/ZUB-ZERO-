package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ZubZeroDatabase
import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UserProfile
import com.example.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class UserProfileDatabaseTest {

    private lateinit var database: ZubZeroDatabase
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var repository: UserProfileRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ZubZeroDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userProfileDao = database.userProfileDao()
        repository = UserProfileRepository(userProfileDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testEntityDomainMapping_preservesAllDetails() {
        val domain = UserProfile(
            isRegistered = true,
            fullName = "Adekoya Daniel Ebenezer",
            mobileNumber = "+234 805 481 0828",
            email = "dadekoya80@gmail.com",
            facialPictureUri = "facial_verified_ceo.jpg",
            mediaDisplayType = "PHOTO",
            activeSubscription = SubscriptionPlan.ONE_MONTH,
            coins = 150,
            checkInStreak = 5
        )

        val entity = UserProfileEntity.fromDomainModel(domain, id = 1L)
        assertEquals(1L, entity.id)
        assertEquals("Adekoya Daniel Ebenezer", entity.fullName)
        assertEquals("+234 805 481 0828", entity.mobileNumber)
        assertEquals("dadekoya80@gmail.com", entity.email)
        assertEquals("facial_verified_ceo.jpg", entity.facialPictureUri)
        assertEquals("PHOTO", entity.mediaDisplayType)
        assertEquals(150, entity.coins)
        assertEquals(5, entity.checkInStreak)

        val convertedBack = entity.toDomainModel()
        assertEquals(domain.fullName, convertedBack.fullName)
        assertEquals(domain.mobileNumber, convertedBack.mobileNumber)
        assertEquals(domain.email, convertedBack.email)
        assertEquals(domain.facialPictureUri, convertedBack.facialPictureUri)
        assertEquals(domain.coins, convertedBack.coins)
        assertEquals(domain.activeSubscription, convertedBack.activeSubscription)
    }

    @Test
    fun testUserProfileDao_insertAndQueryRegistrationDetails() = runBlocking {
        val initial = UserProfileEntity(
            id = 1L,
            fullName = "New Creator",
            mobileNumber = "+1 555 123 4567",
            email = "creator@zubzero.ai",
            facialPictureUri = "creator_face_avatar.png",
            mediaDisplayType = "VIDEO",
            isRegistered = true,
            coins = 50
        )

        userProfileDao.insertOrUpdateProfile(initial)

        val queried = userProfileDao.getUserProfile()
        assertNotNull(queried)
        assertEquals("New Creator", queried?.fullName)
        assertEquals("+1 555 123 4567", queried?.mobileNumber)
        assertEquals("creator@zubzero.ai", queried?.email)
        assertEquals("creator_face_avatar.png", queried?.facialPictureUri)
        assertEquals("VIDEO", queried?.mediaDisplayType)
        assertTrue(queried?.isRegistered == true)
        assertEquals(50, queried?.coins)
    }

    @Test
    fun testRegistrationFlow_updatesDetailsInRoom() = runBlocking {
        val baseProfile = UserProfile(
            isRegistered = false,
            fullName = "Guest",
            mobileNumber = "",
            email = "",
            coins = 0,
            hasClaimedFirstTimeBonus = false
        )

        repository.saveProfile(baseProfile)

        // Simulate registration dialog completion
        val registeredProfile = repository.saveRegistration(
            fullName = "Adekoya Daniel",
            mobileNumber = "+234 805 481 0828",
            email = "dadekoya80@gmail.com",
            mediaType = "PHOTO",
            photoUri = "captured_selfie_001.jpg",
            currentProfile = baseProfile
        )

        assertEquals("Adekoya Daniel", registeredProfile.fullName)
        assertEquals("+234 805 481 0828", registeredProfile.mobileNumber)
        assertEquals("dadekoya80@gmail.com", registeredProfile.email)
        assertEquals("captured_selfie_001.jpg", registeredProfile.facialPictureUri)
        assertTrue(registeredProfile.isRegistered)
        assertEquals(50, registeredProfile.coins) // +50 welcome bonus

        // Verify flow emits the registered details from Room
        val flowValue = repository.userProfileFlow.first()
        assertNotNull(flowValue)
        assertEquals("+234 805 481 0828", flowValue?.mobileNumber)
        assertEquals("dadekoya80@gmail.com", flowValue?.email)
        assertEquals("captured_selfie_001.jpg", flowValue?.facialPictureUri)
    }

    @Test
    fun testUpdateCoinsAndCheckIn_updatesRoomPersistence() = runBlocking {
        val profile = UserProfile(
            isRegistered = true,
            fullName = "VIP Director",
            mobileNumber = "12345",
            email = "vip@film.com",
            coins = 50,
            checkInStreak = 1
        )
        repository.saveProfile(profile)

        repository.updateCheckIn(coins = 85, streak = 2, lastCheckInEpochMs = 9999999L)

        val updated = userProfileDao.getUserProfile()
        assertEquals(85, updated?.coins)
        assertEquals(2, updated?.checkInStreak)
        assertEquals(9999999L, updated?.lastDailyCheckInEpochMs)
    }
}
