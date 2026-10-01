package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PushNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PushNotificationDao {

    @Query("SELECT * FROM push_notifications ORDER BY timestampEpochMs DESC")
    fun getAllNotifications(): Flow<List<PushNotificationEntity>>

    @Query("SELECT * FROM push_notifications ORDER BY timestampEpochMs DESC LIMIT :limit")
    fun getRecentNotifications(limit: Int): Flow<List<PushNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: PushNotificationEntity): Long

    @Query("UPDATE push_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE push_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM push_notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM push_notifications")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM push_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>
}
