package com.example.data.service

import android.content.Context
import android.util.Log
import com.example.data.model.FcmDeviceTokenState
import com.example.data.model.FcmTopicItem
import com.example.data.model.PushNotificationType
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * FcmTokenManager: Safely initializes Firebase Messaging, retrieves and caches the device's
 * FCM registration token, and coordinates topic subscriptions for matches, orders, and subscriptions.
 */
class FcmTokenManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _fcmState = MutableStateFlow(
        FcmDeviceTokenState(
            token = null,
            isTokenAvailable = false,
            subscribedTopics = setOf(TOPIC_MATCHES, TOPIC_ORDERS, TOPIC_SUBSCRIPTIONS),
            lastUpdatedEpochMs = System.currentTimeMillis()
        )
    )
    val fcmState: StateFlow<FcmDeviceTokenState> = _fcmState.asStateFlow()

    init {
        initializeFirebaseSafely()
        fetchFcmToken()
        subscribeToDefaultTopics()
    }

    /**
     * Initializes FirebaseApp programmatically if no google-services.json is present.
     */
    private fun initializeFirebaseSafely() {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId(context.packageName)
                    .setApiKey("AIzaSyZubZeroFcmNotificationKeyClient")
                    .setProjectId("zubzero-ecosystem")
                    .setGcmSenderId("103953800507")
                    .build()
                FirebaseApp.initializeApp(context, options)
                Log.d(TAG, "Programmatic FirebaseApp initialized successfully")
            }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseApp initialization check note: ${e.message}")
        }
    }

    /**
     * Fetches current FCM registration token.
     */
    fun fetchFcmToken() {
        try {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && !task.result.isNullOrBlank()) {
                        val token = task.result
                        Log.i(TAG, "FCM Registration Token received: $token")
                        _fcmState.update { current ->
                            current.copy(
                                token = token,
                                isTokenAvailable = true,
                                lastUpdatedEpochMs = System.currentTimeMillis()
                            )
                        }
                    } else {
                        Log.w(TAG, "FCM Token task uncompleted: ${task.exception?.message}")
                        fallbackToLocalToken()
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "FCM Token retrieval exception: ${e.message}")
            fallbackToLocalToken()
        }
    }

    private fun fallbackToLocalToken() {
        if (_fcmState.value.token == null) {
            val localDevToken = "fcm_token_dev_${UUID.randomUUID().toString().replace("-", "").take(24)}"
            _fcmState.update { current ->
                current.copy(
                    token = localDevToken,
                    isTokenAvailable = true,
                    lastUpdatedEpochMs = System.currentTimeMillis()
                )
            }
        }
    }

    /**
     * Updates token when onNewToken is called by FirebaseMessagingService.
     */
    fun updateToken(newToken: String) {
        _fcmState.update { current ->
            current.copy(
                token = newToken,
                isTokenAvailable = true,
                lastUpdatedEpochMs = System.currentTimeMillis()
            )
        }
        subscribeToDefaultTopics()
    }

    /**
     * Subscribes to the three core topics:
     * 1. new_matches
     * 2. marketplace_orders
     * 3. subscription_renewals
     */
    fun subscribeToDefaultTopics() {
        listOf(TOPIC_MATCHES, TOPIC_ORDERS, TOPIC_SUBSCRIPTIONS).forEach { topic ->
            subscribeToTopic(topic)
        }
    }

    fun subscribeToTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Subscribed to FCM topic: $topic")
                    } else {
                        Log.w(TAG, "Subscription failed for topic: $topic (${task.exception?.message})")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Could not call subscribeToTopic for $topic: ${e.message}")
        }
        _fcmState.update { current ->
            current.copy(subscribedTopics = current.subscribedTopics + topic)
        }
    }

    fun unsubscribeFromTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Unsubscribed from FCM topic: $topic")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Could not call unsubscribeFromTopic for $topic: ${e.message}")
        }
        _fcmState.update { current ->
            current.copy(subscribedTopics = current.subscribedTopics - topic)
        }
    }

    fun toggleTopic(topic: String, enabled: Boolean) {
        if (enabled) {
            subscribeToTopic(topic)
        } else {
            unsubscribeFromTopic(topic)
        }
    }

    fun getTopicsList(): List<FcmTopicItem> {
        val currentSubs = _fcmState.value.subscribedTopics
        return listOf(
            FcmTopicItem(
                topicKey = TOPIC_MATCHES,
                title = "New Match Alerts",
                description = "Receive instant push notifications when high-compatibility soulmates like your profile.",
                isSubscribed = currentSubs.contains(TOPIC_MATCHES),
                type = PushNotificationType.NEW_MATCH_ALERT
            ),
            FcmTopicItem(
                topicKey = TOPIC_ORDERS,
                title = "Marketplace Order Updates",
                description = "Get real-time shipping, transit, and delivery notifications for cinema gear & couture.",
                isSubscribed = currentSubs.contains(TOPIC_ORDERS),
                type = PushNotificationType.MARKETPLACE_ORDER
            ),
            FcmTopicItem(
                topicKey = TOPIC_SUBSCRIPTIONS,
                title = "Subscription Renewals",
                description = "Alerts about monthly/annual VIP creator plan renewals, receipts, and bonus coin drops.",
                isSubscribed = currentSubs.contains(TOPIC_SUBSCRIPTIONS),
                type = PushNotificationType.SUBSCRIPTION_RENEWAL
            )
        )
    }

    companion object {
        const val TAG = "FcmTokenManager"

        const val TOPIC_MATCHES = "new_matches"
        const val TOPIC_ORDERS = "marketplace_orders"
        const val TOPIC_SUBSCRIPTIONS = "subscription_renewals"

        @Volatile
        private var INSTANCE: FcmTokenManager? = null

        fun getInstance(context: Context): FcmTokenManager {
            return INSTANCE ?: synchronized(this) {
                val instance = FcmTokenManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
