package com.example.data.service

import android.util.Log
import com.example.data.model.PushNotificationType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * ZubZeroFirebaseMessagingService: Receives Firebase Cloud Messaging (FCM)
 * push messages in foreground and background, maps them to match alerts,
 * marketplace order updates, or subscription renewals, and displays system notifications.
 */
class ZubZeroFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "FCM onNewToken triggered: $token")
        // Pass token to manager and re-subscribe to core notification topics
        FcmTokenManager.getInstance(this).updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        // Determine title and body from notification payload or data payload
        val rawTitle = notification?.title ?: data["title"] ?: "ZUB-ZERO Notification"
        val rawBody = notification?.body ?: data["body"] ?: "You have a new update in ZUB-ZERO."
        val typeString = data["type"] ?: data["category"] ?: inferTypeFromContent(rawTitle, rawBody)
        val type = PushNotificationType.fromString(typeString)

        val targetTab = data["target_tab"] ?: when (type) {
            PushNotificationType.NEW_MATCH_ALERT -> "MATCHMAKING"
            PushNotificationType.MARKETPLACE_ORDER -> "MY_HUB"
            PushNotificationType.SUBSCRIPTION_RENEWAL -> "MY_HUB"
            PushNotificationType.SYSTEM_BROADCAST -> "HOME"
        }

        Log.i(TAG, "Dispatching incoming FCM push: type=$type, title='$rawTitle', body='$rawBody'")

        // Dispatch local notification via PushNotificationManager
        val manager = PushNotificationManager.getInstance(this)
        manager.postNotification(
            type = type,
            title = rawTitle,
            body = rawBody,
            targetTab = targetTab,
            payload = data
        )
    }

    private fun inferTypeFromContent(title: String, body: String): String {
        val combined = "$title $body".lowercase()
        return when {
            combined.contains("match") || combined.contains("soulmate") || combined.contains("dating") -> "NEW_MATCH_ALERT"
            combined.contains("order") || combined.contains("shipped") || combined.contains("gear") || combined.contains("tracking") -> "MARKETPLACE_ORDER"
            combined.contains("subscription") || combined.contains("renew") || combined.contains("plan") || combined.contains("vip") -> "SUBSCRIPTION_RENEWAL"
            else -> "SYSTEM_BROADCAST"
        }
    }

    companion object {
        const val TAG = "ZubZeroFcmService"
    }
}
