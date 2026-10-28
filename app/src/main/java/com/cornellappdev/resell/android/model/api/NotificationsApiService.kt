package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.model.classes.InAppNotification
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.lang.reflect.Type

interface NotificationsApiService {
    @GET("notif/new")
    suspend fun getNewNotifications(): NotificationsResponse

    @GET("notif/recent")
    suspend fun getRecentNotifications(): NotificationsResponse

    @GET("notif/last7days")
    suspend fun getLast7DaysNotifications(): NotificationsResponse

    @GET("notif/last30days")
    suspend fun getLast30DaysNotifications(): NotificationsResponse

    @POST("notif/read/{id}")
    suspend fun markNotificationAsRead(
        @Path("id") notificationId: String
    ): SingleNotificationResponse

    @DELETE("notif/id/{id}")
    suspend fun deleteNotification(
        @Path("id") notificationId: String
    )
}

@JsonAdapter(NotificationsResponseDeserializer::class)
data class NotificationsResponse(
    @SerializedName("notifications") val notifications: List<InAppNotification>? = null,
    @SerializedName("notifs") val notifs: List<InAppNotification>? = null,
) {
    val items: List<InAppNotification>
        get() = notifications ?: notifs ?: emptyList()
}

class NotificationsResponseDeserializer : JsonDeserializer<NotificationsResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): NotificationsResponse {
        if (json == null || json.isJsonNull) {
            return NotificationsResponse()
        }

        // Handle case where response is a JSON Array: [ { ... }, { ... } ]
        if (json.isJsonArray) {
            val list = mutableListOf<InAppNotification>()
            for (element in json.asJsonArray) {
                try {
                    val notif: InAppNotification? = context?.deserialize(element, InAppNotification::class.java)
                    if (notif != null) {
                        list.add(notif)
                    }
                } catch (e: Exception) {
                    Log.e("NotificationsResponseDeserializer", "Error parsing notification item", e)
                }
            }
            return NotificationsResponse(notifications = list)
        }

        // Handle case where response is a JSON Object: { "notifications": [...] } or { "notifs": [...] }
        if (json.isJsonObject) {
            val obj = json.asJsonObject
            val arrayElement = obj.get("notifications")
                ?: obj.get("notifs")
                ?: obj.get("items")
                ?: obj.get("data")

            if (arrayElement != null && arrayElement.isJsonArray) {
                val list = mutableListOf<InAppNotification>()
                for (element in arrayElement.asJsonArray) {
                    try {
                        val notif: InAppNotification? = context?.deserialize(element, InAppNotification::class.java)
                        if (notif != null) {
                            list.add(notif)
                        }
                    } catch (e: Exception) {
                        Log.e("NotificationsResponseDeserializer", "Error parsing notification item", e)
                    }
                }
                return NotificationsResponse(notifications = list)
            }
        }

        return NotificationsResponse()
    }
}

@JsonAdapter(SingleNotificationResponseDeserializer::class)
data class SingleNotificationResponse(
    @SerializedName("notification") val notification: InAppNotification? = null,
    @SerializedName("message") val message: String? = null,
)

class SingleNotificationResponseDeserializer : JsonDeserializer<SingleNotificationResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SingleNotificationResponse {
        if (json == null || json.isJsonNull) {
            return SingleNotificationResponse()
        }

        if (json.isJsonObject) {
            val obj = json.asJsonObject
            val notifElement = obj.get("notification") ?: obj.get("notif") ?: obj.get("data")
            if (notifElement != null && notifElement.isJsonObject) {
                val notif: InAppNotification? = context?.deserialize(notifElement, InAppNotification::class.java)
                val msg = obj.get("message")?.takeIf { !it.isJsonNull }?.asString
                return SingleNotificationResponse(notification = notif, message = msg)
            } else if (obj.has("id") || obj.has("_id")) {
                val notif: InAppNotification? = context?.deserialize(obj, InAppNotification::class.java)
                return SingleNotificationResponse(notification = notif)
            }
        }

        return SingleNotificationResponse()
    }
}