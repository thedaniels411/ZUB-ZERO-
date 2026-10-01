package com.example.data.model

import java.util.UUID

/**
 * Categorized Push Notification Types for ZUB-ZERO ecosystem
 */
enum class PushNotificationType(
    val channelId: String,
    val topicName: String,
    val displayName: String,
    val defaultTitle: String,
    val tag: String
) {
    NEW_MATCH_ALERT(
        channelId = "channel_matches",
        topicName = "new_matches",
        displayName = "New Match Alerts",
        defaultTitle = "💖 New Soulmate Match Alert!",
        tag = "MATCH"
    ),
    MARKETPLACE_ORDER(
        channelId = "channel_orders",
        topicName = "marketplace_orders",
        displayName = "Marketplace Order Updates",
        defaultTitle = "📦 Marketplace Order Update",
        tag = "ORDER"
    ),
    SUBSCRIPTION_RENEWAL(
        channelId = "channel_subscriptions",
        topicName = "subscription_renewals",
        displayName = "Subscription Renewals",
        defaultTitle = "🔄 Subscription Renewal Notice",
        tag = "SUBSCRIPTION"
    ),
    SYSTEM_BROADCAST(
        channelId = "channel_matches",
        topicName = "system_broadcasts",
        displayName = "System & Security Broadcasts",
        defaultTitle = "⚡ ZUB-ZERO System Update",
        tag = "SYSTEM"
    );

    companion object {
        fun fromString(typeString: String?): PushNotificationType {
            return when (typeString?.uppercase()) {
                "NEW_MATCH_ALERT", "MATCH", "MATCH_ALERT" -> NEW_MATCH_ALERT
                "MARKETPLACE_ORDER", "ORDER", "ORDER_UPDATE" -> MARKETPLACE_ORDER
                "SUBSCRIPTION_RENEWAL", "SUBSCRIPTION", "RENEWAL" -> SUBSCRIPTION_RENEWAL
                else -> SYSTEM_BROADCAST
            }
        }
    }
}

/**
 * In-memory / UI Representation of a received Push Notification
 */
data class PushNotificationItem(
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val type: PushNotificationType,
    val title: String,
    val body: String,
    val channelId: String = type.channelId,
    val targetTab: String = when (type) {
        PushNotificationType.NEW_MATCH_ALERT -> "MATCHMAKING"
        PushNotificationType.MARKETPLACE_ORDER -> "MY_HUB"
        PushNotificationType.SUBSCRIPTION_RENEWAL -> "MY_HUB"
        PushNotificationType.SYSTEM_BROADCAST -> "HOME"
    },
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val payload: Map<String, String> = emptyMap()
)

/**
 * State representing Firebase Cloud Messaging registration token & subscribed topics
 */
data class FcmDeviceTokenState(
    val token: String? = null,
    val isTokenAvailable: Boolean = false,
    val subscribedTopics: Set<String> = setOf("new_matches", "marketplace_orders", "subscription_renewals"),
    val lastUpdatedEpochMs: Long = System.currentTimeMillis(),
    val isPermissionGranted: Boolean = true
)

/**
 * Metadata for user-configurable FCM Push topics
 */
data class FcmTopicItem(
    val topicKey: String,
    val title: String,
    val description: String,
    val isSubscribed: Boolean,
    val type: PushNotificationType
)
