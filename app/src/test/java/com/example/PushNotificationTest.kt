package com.example

import com.example.data.model.PushNotificationItem
import com.example.data.model.PushNotificationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PushNotificationTest {

    @Test
    fun testPushNotificationType_parsingFromFcmStrings() {
        assertEquals(PushNotificationType.NEW_MATCH_ALERT, PushNotificationType.fromString("NEW_MATCH_ALERT"))
        assertEquals(PushNotificationType.NEW_MATCH_ALERT, PushNotificationType.fromString("match"))
        assertEquals(PushNotificationType.NEW_MATCH_ALERT, PushNotificationType.fromString("MATCH_ALERT"))

        assertEquals(PushNotificationType.MARKETPLACE_ORDER, PushNotificationType.fromString("MARKETPLACE_ORDER"))
        assertEquals(PushNotificationType.MARKETPLACE_ORDER, PushNotificationType.fromString("order"))
        assertEquals(PushNotificationType.MARKETPLACE_ORDER, PushNotificationType.fromString("ORDER_UPDATE"))

        assertEquals(PushNotificationType.SUBSCRIPTION_RENEWAL, PushNotificationType.fromString("SUBSCRIPTION_RENEWAL"))
        assertEquals(PushNotificationType.SUBSCRIPTION_RENEWAL, PushNotificationType.fromString("subscription"))
        assertEquals(PushNotificationType.SUBSCRIPTION_RENEWAL, PushNotificationType.fromString("RENEWAL"))

        assertEquals(PushNotificationType.SYSTEM_BROADCAST, PushNotificationType.fromString("unknown_type"))
        assertEquals(PushNotificationType.SYSTEM_BROADCAST, PushNotificationType.fromString(null))
    }

    @Test
    fun testNotificationChannels_andTopicMapping() {
        assertEquals("channel_matches", PushNotificationType.NEW_MATCH_ALERT.channelId)
        assertEquals("new_matches", PushNotificationType.NEW_MATCH_ALERT.topicName)

        assertEquals("channel_orders", PushNotificationType.MARKETPLACE_ORDER.channelId)
        assertEquals("marketplace_orders", PushNotificationType.MARKETPLACE_ORDER.topicName)

        assertEquals("channel_subscriptions", PushNotificationType.SUBSCRIPTION_RENEWAL.channelId)
        assertEquals("subscription_renewals", PushNotificationType.SUBSCRIPTION_RENEWAL.topicName)
    }

    @Test
    fun testNotificationItem_matchAlertDestination() {
        val item = PushNotificationItem(
            type = PushNotificationType.NEW_MATCH_ALERT,
            title = "💖 New Soulmate Match: Zara Okonjo",
            body = "96% compatibility in Romantic Twilight & Drama",
            payload = mapOf("candidateName" to "Zara Okonjo", "score" to "96")
        )

        assertEquals("MATCHMAKING", item.targetTab)
        assertEquals("channel_matches", item.channelId)
        assertNotNull(item.uuid)
        assertEquals("96", item.payload["score"])
    }

    @Test
    fun testNotificationItem_marketplaceOrderDestination() {
        val item = PushNotificationItem(
            type = PushNotificationType.MARKETPLACE_ORDER,
            title = "📦 Order #ORD-84920: Shipped",
            body = "Your camera equipment is on the way",
            payload = mapOf("orderId" to "ORD-84920", "status" to "Shipped")
        )

        assertEquals("MY_HUB", item.targetTab)
        assertEquals("channel_orders", item.channelId)
        assertEquals("ORD-84920", item.payload["orderId"])
    }

    @Test
    fun testNotificationItem_subscriptionRenewalDestination() {
        val item = PushNotificationItem(
            type = PushNotificationType.SUBSCRIPTION_RENEWAL,
            title = "🔄 Subscription Renewed: VIP Mogul Pro",
            body = "Your plan has renewed for ₦150,000 / year. +100 Coins credited!",
            payload = mapOf("plan" to "VIP Mogul Pro", "coins" to "100")
        )

        assertEquals("MY_HUB", item.targetTab)
        assertEquals("channel_subscriptions", item.channelId)
        assertEquals("VIP Mogul Pro", item.payload["plan"])
    }
}
