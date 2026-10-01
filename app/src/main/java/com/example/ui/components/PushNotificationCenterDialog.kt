package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FcmDeviceTokenState
import com.example.data.model.PushNotificationItem
import com.example.data.model.PushNotificationType
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBlack
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubElectricViolet
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PushNotificationCenterDialog(
    fcmState: FcmDeviceTokenState,
    notifications: List<PushNotificationItem>,
    onDismiss: () -> Unit,
    onNavigateToTab: (String) -> Unit,
    onTriggerMatchPush: () -> Unit,
    onTriggerOrderPush: () -> Unit,
    onTriggerRenewalPush: () -> Unit,
    onToggleTopic: (String, Boolean) -> Unit,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedToken by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.5.dp, ZubCyan.copy(alpha = 0.7f)), RoundedCornerShape(20.dp))
                .testTag("push_notification_center_dialog"),
            color = ZubDarkNavy
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ZubCyan.copy(alpha = 0.2f))
                                .border(1.dp, ZubCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Push Notifications",
                                tint = ZubCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Firebase Cloud Messaging",
                                color = ZubTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Real-time Push Notifications & Topics Hub",
                                color = ZubCyan,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_push_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ZubTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FCM Token Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                    border = BorderStroke(1.dp, ZubBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (fcmState.isTokenAvailable) Color(0xFF00E676) else ZubTwilightCrimson)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (fcmState.isTokenAvailable) "FCM Token Registered & Listening" else "FCM Initializing...",
                                    color = if (fcmState.isTokenAvailable) Color(0xFF00E676) else ZubActionAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            fcmState.token?.let { token ->
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(token))
                                        copiedToken = true
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ZubCyan),
                                    border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        imageVector = if (copiedToken) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy Token",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (copiedToken) "Copied!" else "Copy Token",
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = fcmState.token ?: "Retrieving registration token from Firebase Messaging...",
                            color = ZubTextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ZubBlack.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Send / Test Buttons for the 3 User Request Scenarios
                Text(
                    text = "DISPATCH TEST PUSH NOTIFICATIONS",
                    color = ZubCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Match Alert Button
                    OutlinedButton(
                        onClick = onTriggerMatchPush,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_trigger_match_push"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ZubTwilightCrimson.copy(alpha = 0.15f),
                            contentColor = ZubTwilightCrimson
                        ),
                        border = BorderStroke(1.dp, ZubTwilightCrimson.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Match",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Match",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    // 2. Marketplace Order Update Button
                    OutlinedButton(
                        onClick = onTriggerOrderPush,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_trigger_order_push"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ZubCyan.copy(alpha = 0.15f),
                            contentColor = ZubCyan
                        ),
                        border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalMall,
                            contentDescription = "Order",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Order Update",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    // 3. Subscription Renewal Button
                    OutlinedButton(
                        onClick = onTriggerRenewalPush,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_trigger_renewal_push"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ZubActionAmber.copy(alpha = 0.15f),
                            contentColor = ZubActionAmber
                        ),
                        border = BorderStroke(1.dp, ZubActionAmber.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Renewal",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VIP Renewal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Topic Subscriptions Management
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ZubCardBg.copy(alpha = 0.7f)),
                    border = BorderStroke(1.dp, ZubBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "FCM TOPIC SUBSCRIPTIONS",
                            color = ZubTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        TopicToggleRow(
                            title = "new_matches",
                            subtitle = "Alerts when someone connects or likes your dating profile",
                            icon = Icons.Default.Favorite,
                            iconColor = ZubTwilightCrimson,
                            isSubscribed = fcmState.subscribedTopics.contains("new_matches"),
                            onToggle = { onToggleTopic("new_matches", it) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = ZubBorder.copy(alpha = 0.5f)
                        )

                        TopicToggleRow(
                            title = "marketplace_orders",
                            subtitle = "Status alerts when film gear/costumes ship or deliver",
                            icon = Icons.Default.LocalMall,
                            iconColor = ZubCyan,
                            isSubscribed = fcmState.subscribedTopics.contains("marketplace_orders"),
                            onToggle = { onToggleTopic("marketplace_orders", it) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = ZubBorder.copy(alpha = 0.5f)
                        )

                        TopicToggleRow(
                            title = "subscription_renewals",
                            subtitle = "Receipts & coin drop notices for VIP recurring plans",
                            icon = Icons.Default.Sync,
                            iconColor = ZubActionAmber,
                            isSubscribed = fcmState.subscribedTopics.contains("subscription_renewals"),
                            onToggle = { onToggleTopic("subscription_renewals", it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Push Notification History / Inbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECEIVED NOTIFICATIONS (${notifications.size})",
                        color = ZubTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row {
                        if (notifications.isNotEmpty()) {
                            Text(
                                text = "Read All",
                                color = ZubCyan,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clickable { onMarkAllRead() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Clear",
                                color = ZubTwilightCrimson,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clickable { onClearAll() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ZubBlack.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = ZubTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No push notifications yet",
                                color = ZubTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Use the test buttons above or place orders/connect with matches to see live pushes!",
                                color = ZubTextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications, key = { it.uuid }) { item ->
                            NotificationHistoryCard(
                                item = item,
                                onClick = {
                                    onNavigateToTab(item.targetTab)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicToggleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    isSubscribed: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = ZubTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = ZubTextMuted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Switch(
            checked = isSubscribed,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ZubCyan,
                checkedTrackColor = ZubCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = ZubTextMuted,
                uncheckedTrackColor = ZubBlack
            )
        )
    }
}

@Composable
private fun NotificationHistoryCard(
    item: PushNotificationItem,
    onClick: () -> Unit
) {
    val (accentColor, icon) = when (item.type) {
        PushNotificationType.NEW_MATCH_ALERT -> Pair(ZubTwilightCrimson, Icons.Default.Favorite)
        PushNotificationType.MARKETPLACE_ORDER -> Pair(ZubCyan, Icons.Default.LocalMall)
        PushNotificationType.SUBSCRIPTION_RENEWAL -> Pair(ZubActionAmber, Icons.Default.Refresh)
        PushNotificationType.SYSTEM_BROADCAST -> Pair(ZubElectricViolet, Icons.Default.Notifications)
    }

    val timeFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedTime = remember(item.timestampEpochMs) { timeFormat.format(Date(item.timestampEpochMs)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${item.type.name}"),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) ZubCardBg.copy(alpha = 0.6f) else ZubDarkNavy
        ),
        border = BorderStroke(
            1.dp,
            if (item.isRead) ZubBorder else accentColor.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        color = ZubTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formattedTime,
                        color = ZubTextMuted,
                        fontSize = 9.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.body,
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open in ${item.targetTab}",
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}
