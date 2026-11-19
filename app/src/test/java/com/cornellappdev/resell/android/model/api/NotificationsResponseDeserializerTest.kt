package com.cornellappdev.resell.android.model.api

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NotificationsResponseDeserializerTest {

    private val gson = Gson()

    @Test
    fun testParseJSONArrayResponse() {
        val json = """
            [
                {
                    "id": "notif_1",
                    "title": "Test Title",
                    "body": "Test Body",
                    "read": false
                },
                {
                    "id": "notif_2",
                    "title": "Title 2",
                    "body": "Body 2",
                    "read": true
                }
            ]
        """.trimIndent()

        val response = gson.fromJson(json, NotificationsResponse::class.java)
        assertNotNull(response)
        assertEquals(2, response.items.size)
        assertEquals("notif_1", response.items[0].id)
        assertEquals("Test Title", response.items[0].title)
        assertEquals("notif_2", response.items[1].id)
    }

    @Test
    fun testParseJSONObjectNotificationsResponse() {
        val json = """
            {
                "notifications": [
                    {
                        "id": "notif_3",
                        "title": "Title 3",
                        "body": "Body 3"
                    }
                ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, NotificationsResponse::class.java)
        assertNotNull(response)
        assertEquals(1, response.items.size)
        assertEquals("notif_3", response.items[0].id)
    }

    @Test
    fun testParseSingleNotificationResponseDirectObject() {
        val json = """
            {
                "id": "notif_4",
                "title": "Single Notif Title",
                "read": true
            }
        """.trimIndent()

        val response = gson.fromJson(json, SingleNotificationResponse::class.java)
        assertNotNull(response)
        assertNotNull(response.notification)
        assertEquals("notif_4", response.notification?.id)
    }

    @Test
    fun testParseSingleNotificationResponseWrappedObject() {
        val json = """
            {
                "notification": {
                    "id": "notif_5",
                    "title": "Wrapped Notif Title"
                },
                "message": "Success"
            }
        """.trimIndent()

        val response = gson.fromJson(json, SingleNotificationResponse::class.java)
        assertNotNull(response)
        assertNotNull(response.notification)
        assertEquals("notif_5", response.notification?.id)
        assertEquals("Success", response.message)
    }
}
