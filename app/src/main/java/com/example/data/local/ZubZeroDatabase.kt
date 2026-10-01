package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CoinDao
import com.example.data.local.dao.MarketDao
import com.example.data.local.dao.MovieDao
import com.example.data.local.dao.PushNotificationDao
import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.CachedHotelEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.CoinTransactionEntity
import com.example.data.local.entity.HotelBookingEntity
import com.example.data.local.entity.MarketListingEntity
import com.example.data.local.entity.MovieProjectEntity
import com.example.data.local.entity.PushNotificationEntity
import com.example.data.local.dao.VideoPromptSubmissionDao
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.VideoPromptSubmissionEntity

@Database(
    entities = [
        MovieProjectEntity::class,
        CartItemEntity::class,
        HotelBookingEntity::class,
        MarketListingEntity::class,
        CachedHotelEntity::class,
        CoinTransactionEntity::class,
        PushNotificationEntity::class,
        UserProfileEntity::class,
        VideoPromptSubmissionEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class ZubZeroDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun marketDao(): MarketDao
    abstract fun coinDao(): CoinDao
    abstract fun pushNotificationDao(): PushNotificationDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun videoPromptSubmissionDao(): VideoPromptSubmissionDao

    companion object {
        @Volatile
        private var INSTANCE: ZubZeroDatabase? = null

        fun getInstance(context: Context): ZubZeroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZubZeroDatabase::class.java,
                    "zub_zero_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
