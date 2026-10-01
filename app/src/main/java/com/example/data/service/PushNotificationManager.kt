package com.example.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.ZubZeroDatabase
import com.example.data.local.dao.PushNotificationDao
import com.example.data.local.entity.PushNotificationEntity
import com.example.data.model.PushNotificationItem
import com.example.data.model.PushNotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * PushNotificationManager: Central manager for Android Notification Channels,
 * building and issuing local & FCM push notifications, and persisting them to Room.
 */
class PushNotificationManager private constructor(
    private val context: Context,
    private val notificationDao: PushNotificationDao
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val notificationIdGenerator = AtomicInteger(1001)

    init {
        createNotificationChannels()
    }

    /**
     * Creates the Android O+ notification channels for:
     * 1. New Match Alerts
     * 2. Marketplace Order Updates
     * 3. Subscription Renewals
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            // 1. Channel for Match Alerts
            val matchChannel = NotificationChannel(
                CHANNEL_MATCHES,
                "New Match Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time alerts when you match with someone on ZUB-ZERO Matchmaking"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
            }

            // 2. Channel for Marketplace Order Updates
            val orderChannel = NotificationChannel(
                CHANNEL_ORDERS,
                "Marketplace Order Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Updates on cinema equipment, props, and couture orders & tracking"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200)
                setShowBadge(true)
            }

            // 3. Channel for Subscription Renewals
            val subChannel = NotificationChannel(
                CHANNEL_SUBSCRIPTIONS,
                "Subscription Renewals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notices regarding VIP creator plan renewals, billing cycles, and coin allowances"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 180, 100, 180)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannels(listOf(matchChannel, orderChannel, subChannel))
            Log.d(TAG, "Notification channels initialized successfully")
        }
    }

    /**
     * Returns a reactive flow of all received push notifications from Room.
     */
    fun getNotificationsFlow(): Flow<List<PushNotificationItem>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Dispatches a push notification to Android status bar and stores in Room.
     */
    fun postNotification(
        type: PushNotificationType,
        title: String,
        body: String,
        targetTab: String = when (type) {
            PushNotificationType.NEW_MATCH_ALERT -> "MATCHMAKING"
            PushNotificationType.MARKETPLACE_ORDER -> "MARKETPLACE"
            PushNotificationType.SUBSCRIPTION_RENEWAL -> "MY_HUB"
            PushNotificationType.SYSTEM_BROADCAST -> "HOME"
        },
        payload: Map<String, String> = emptyMap()
    ): PushNotificationItem {
        val notificationItem = PushNotificationItem(
            uuid = UUID.randomUUID().toString(),
            type = type,
            title = title,
            body = body,
            channelId = type.channelId,
            targetTab = targetTab,
            timestampEpochMs = System.currentTimeMillis(),
            isRead = false,
            payload = payload
        )

        // 1. Persist to Room
        scope.launch {
            try {
                notificationDao.insertNotification(PushNotificationEntity.fromDomainModel(notificationItem))
            } catch (e: Exception) {
                Log.e(TAG, "Error saving notification to Room", e)
            }
        }

        // 2. Dispatch Android Status Bar Notification
        dispatchSystemNotification(notificationItem)

        return notificationItem
    }

    private fun dispatchSystemNotification(item: PushNotificationItem) {
        val notificationId = notificationIdGenerator.incrementAndGet()

        // Deep-link intent into MainActivity targeting the specific tab
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_NAV_TAB, item.targetTab)
            putExtra(EXTRA_NOTIFICATION_ID, item.uuid)
            putExtra(EXTRA_NOTIFICATION_TYPE, item.type.name)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, item.channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(item.title)
            .setContentText(item.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(item.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(
                when (item.type) {
                    PushNotificationType.NEW_MATCH_ALERT -> NotificationCompat.CATEGORY_MESSAGE
                    PushNotificationType.MARKETPLACE_ORDER -> NotificationCompat.CATEGORY_STATUS
                    PushNotificationType.SUBSCRIPTION_RENEWAL -> NotificationCompat.CATEGORY_EVENT
                    PushNotificationType.SYSTEM_BROADCAST -> NotificationCompat.CATEGORY_SYSTEM
                }
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        // Add contextual action button based on notification type
        when (item.type) {
            PushNotificationType.NEW_MATCH_ALERT -> {
                notificationBuilder.addAction(
                    android.R.drawable.ic_menu_view,
                    "View Soulmate",
                    pendingIntent
                )
            }
            PushNotificationType.MARKETPLACE_ORDER -> {
                notificationBuilder.addAction(
                    android.R.drawable.ic_menu_agenda,
                    "Track Order",
                    pendingIntent
                )
            }
            PushNotificationType.SUBSCRIPTION_RENEWAL -> {
                notificationBuilder.addAction(
                    android.R.drawable.ic_menu_manage,
                    "Manage Plan",
                    pendingIntent
                )
            }
            else -> {}
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (e: SecurityException) {
            Log.w(TAG, "Notification permission missing (POST_NOTIFICATIONS not granted)", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post notification", e)
        }
    }

    /**
     * Send push notification for a new match alert
     */
    fun sendNewMatchAlert(
        matchName: String,
        matchPercent: Int,
        genre: String = "Romantic Twilight & Drama",
        location: String = "Lagos, Nigeria"
    ): PushNotificationItem {
        return postNotification(
            type = PushNotificationType.NEW_MATCH_ALERT,
            title = "💖 New Soulmate Match: $matchName",
            body = "You have a $matchPercent% compatibility score with $matchName in $genre ($location). Tap to view profile & chat!",
            targetTab = "MATCHMAKING",
            payload = mapOf(
                "candidateName" to matchName,
                "score" to "$matchPercent",
                "genre" to genre
            )
        )
    }

    /**
     * Send push notification for a marketplace order update
     */
    fun sendMarketplaceOrderUpdate(
        orderId: String,
        itemCount: Int,
        totalAmountFormatted: String,
        status: String = "Shipped via DHL Express"
    ): PushNotificationItem {
        return postNotification(
            type = PushNotificationType.MARKETPLACE_ORDER,
            title = "📦 Order $orderId: $status",
            body = "Your order for $itemCount film gear / couture item(s) ($totalAmountFormatted) is now: $status. Tracking details available in My Hub.",
            targetTab = "MY_HUB",
            payload = mapOf(
                "orderId" to orderId,
                "status" to status,
                "amount" to totalAmountFormatted
            )
        )
    }

    /**
     * Send push notification for subscription renewals
     */
    fun sendSubscriptionRenewalAlert(
        planName: String,
        priceFormatted: String,
        nextBillingDate: String,
        bonusCoins: Int = 100
    ): PushNotificationItem {
        return postNotification(
            type = PushNotificationType.SUBSCRIPTION_RENEWAL,
            title = "🔄 Subscription Renewed: $planName",
            body = "Your $planName ($priceFormatted) renewed successfully. +$bonusCoins bonus coins credited! Next renewal: $nextBillingDate.",
            targetTab = "MY_HUB",
            payload = mapOf(
                "planName" to planName,
                "price" to priceFormatted,
                "nextRenewal" to nextBillingDate
            )
        )
    }

    fun markAsRead(id: Long) {
        scope.launch {
            notificationDao.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        scope.launch {
            notificationDao.markAllAsRead()
        }
    }

    fun clearAll() {
        scope.launch {
            notificationDao.clearAll()
        }
    }

    companion object {
        const val TAG = "PushNotificationMgr"
        const val CHANNEL_MATCHES = "channel_matches"
        const val CHANNEL_ORDERS = "channel_orders"
        const val CHANNEL_SUBSCRIPTIONS = "channel_subscriptions"

        const val EXTRA_NAV_TAB = "extra_nav_tab"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_NOTIFICATION_TYPE = "extra_notification_type"

        @Volatile
        private var INSTANCE: PushNotificationManager? = null

        fun getInstance(context: Context): PushNotificationManager {
            return INSTANCE ?: synchronized(this) {
                val db = ZubZeroDatabase.getInstance(context)
                val instance = PushNotificationManager(context.applicationContext, db.pushNotificationDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
