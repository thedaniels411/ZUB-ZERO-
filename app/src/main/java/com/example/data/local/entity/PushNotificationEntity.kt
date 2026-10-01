package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PushNotificationItem
import com.example.data.model.PushNotificationType

@Entity(tableName = "push_notifications")
data class PushNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String,
    val type: String,
    val title: String,
    val body: String,
    val channelId: String,
    val targetTab: String,
    val timestampEpochMs: Long,
    val isRead: Boolean = false,
    val payloadJson: String = "{}"
) {
    fun toDomainModel(): PushNotificationItem {
        return PushNotificationItem(
            id = id,
            uuid = uuid,
            type = PushNotificationType.fromString(type),
            title = title,
            body = body,
            channelId = channelId,
            targetTab = targetTab,
            timestampEpochMs = timestampEpochMs,
            isRead = isRead,
            payload = emptyMap()
        )
    }

    companion object {
        fun fromDomainModel(item: PushNotificationItem): PushNotificationEntity {
            return PushNotificationEntity(
                id = item.id,
                uuid = item.uuid,
                type = item.type.name,
                title = item.title,
                body = item.body,
                channelId = item.channelId,
                targetTab = item.targetTab,
                timestampEpochMs = item.timestampEpochMs,
                isRead = item.isRead,
                payloadJson = "{}"
            )
        }
    }
}
