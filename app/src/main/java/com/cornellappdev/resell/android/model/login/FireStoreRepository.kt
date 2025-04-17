package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.api.StartAndEnd
import com.cornellappdev.resell.android.model.chats.ChatDocument
import com.cornellappdev.resell.android.model.chats.RawChatHeaderData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreRepository @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    private val refactoredChatsCollection = fireStore.collection("chats_refactored")

    private var lastSubscription: ListenerRegistration? = null
    private var lastBuyerSubscription: ListenerRegistration? = null
    private var lastSellerSubscription: ListenerRegistration? = null

    suspend fun saveNotificationsEnabled(userEmail: String, notificationsEnabled: Boolean) {
        try {
            val userDocRef = fireStore.collection("user").document(userEmail)
            userDocRef.update("notificationsEnabled", notificationsEnabled).await()
            Log.d("FireStoreRepository", "Notifications enabled saved successfully")
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error saving notifications enabled: ", e)
            throw e
        }
    }

    /**
     * Returns true if the most recent message has been read, or if it is from the current user.
     * Thus, false if the most recent message is from the other user AND has not been read.
     */
    suspend fun getMostRecentMessageRead(
        chatId: String,
        myId: String
    ): Boolean {
        val messages = refactoredChatsCollection.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING).get().await()

        return messages.documents.lastOrNull()?.let {
            it.get("read") as? Boolean == true || it.get("senderID") == myId
        } ?: true
    }

    suspend fun getMostRecentMessageId(
        chatId: String
    ): String? {
        val messages = refactoredChatsCollection.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING).get().await()

        return messages.documents.lastOrNull()?.id
    }

    fun subscribeToChat(
        chatId: String,
        onSnapshotUpdate: (List<ChatDocument>) -> Unit
    ) {
        // Remove old subscription.
        lastSubscription?.remove()

        val chatDocRef = refactoredChatsCollection.document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        lastSubscription = chatDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FireStoreRepository", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                Log.e("FireStoreRepository", "Current data null")
                return@addSnapshotListener
            }

            val messages = snapshot.documents.mapNotNull {
                val docs = mutableListOf<ChatDocument>()
                val images = it.get("images") as? List<String>
                // Separate image message into separate messages
                if (!images.isNullOrEmpty()) {
                    images.forEach { url ->
                        val doc = ChatDocument(
                            id = it.id,
                            images = listOf(url),
                            timestamp = it.getTimestamp("timestamp") ?: Timestamp(0, 0),
                            senderId = it.get("senderID") as? String ?: "",
                            text = null,
                            accepted = null,
                            startDate = null,
                            endDate = null,
                            availabilities = null,
                            type = "chat"
                        )
                        docs += doc
                    }
                    val text = it.get("text")?.toString() ?: ""
                    if (text.isNotEmpty()) {
                        val doc = ChatDocument(
                            id = it.id,
                            timestamp = it.getTimestamp("timestamp") ?: Timestamp(0, 0),
                            text = text,
                            accepted = null,
                            startDate = null,
                            endDate = null,
                            availabilities = null,
                            type = "chat",
                            senderId = it.get("senderID") as? String ?: "",
                            images = emptyList()
                        )
                        docs += doc
                    }
                } else {
                    val avail = (it.get("availabilities") as? List<Map<String, *>>)?.map {
                        val startMap = it["startDate"] as Map<*, *>
                        val startTime = Timestamp(
                            (startMap["seconds"] as Number).toLong(),
                            (startMap["nanoseconds"] as Number).toInt()
                        )
                        val endMap = it["endDate"] as Map<*, *>
                        val endTime = Timestamp(
                            (endMap["seconds"] as Number).toLong(),
                            (endMap["nanoseconds"] as Number).toInt()
                        )
                        StartAndEnd(
                            startDate = startTime,
                            endDate = endTime
                        )
                    }

                    val chatDoc = ChatDocument(
                        id = it.id,
                        timestamp = it.getTimestamp("timestamp") ?: Timestamp(0, 0),
                        text = it.get("text")?.toString() ?: "",
                        accepted = it.get("accepted") as? Boolean,
                        startDate = it.getTimestamp("startDate"),
                        endDate = it.getTimestamp("endDate"),
                        images = it.get("images") as? List<String>,
                        availabilities = avail,
                        type = it.get("type") as? String ?: "",
                        senderId = it.get("senderID") as? String ?: "",
                    )

                    docs += chatDoc
                }
                docs
            }.flatten()
            onSnapshotUpdate(messages)
        }
    }

    fun subscribeToBuyerHistory(
        myId: String,
        onSnapshotUpdate: (List<RawChatHeaderData>) -> Unit
    ) {
        // Remove old subscription.
        lastBuyerSubscription?.remove()
        lastBuyerSubscription = refactoredChatsCollection.whereEqualTo("sellerID", myId)
            .addSnapshotListener { snapshot, _ ->
                val data = snapshot?.documents?.mapNotNull { documentSnapshot ->
                    val chatId = documentSnapshot.id
                    val listingId = documentSnapshot.get("listingID") as? String
                    val sellerId = documentSnapshot.get("sellerID") as? String
                    val buyerId = documentSnapshot.get("buyerID") as? String
                    val updatedAt = documentSnapshot.get("updatedAt") as? Timestamp
                    val lastMessage = documentSnapshot.get("lastMessage") as? String
                    val userIds = documentSnapshot.get("userIDs") as? List<*>

                    RawChatHeaderData(
                        listingID = listingId ?: "",
                        sellerID = sellerId ?: "",
                        buyerID = buyerId ?: "",
                        updatedAt = updatedAt ?: Timestamp(0, 0),
                        lastMessage = lastMessage ?: "",
                        userIDs = userIds?.map { it.toString() } ?: emptyList(),
                        chatID = chatId
                    )
                }
                onSnapshotUpdate(data ?: emptyList())
            }
    }

    fun subscribeToSellerHistory(
        myId: String,
        onSnapshotUpdate: (List<RawChatHeaderData>) -> Unit
    ) {
        // Remove old subscription.
        lastSellerSubscription?.remove()
        lastSellerSubscription = refactoredChatsCollection.whereEqualTo("buyerID", myId)
            .addSnapshotListener { snapshot, _ ->
                val data = snapshot?.documents?.mapNotNull { documentSnapshot ->
                    val chatId = documentSnapshot.id
                    val listingId = documentSnapshot.get("listingID") as? String
                    val sellerId = documentSnapshot.get("sellerID") as? String
                    val buyerId = documentSnapshot.get("buyerID") as? String
                    val updatedAt = documentSnapshot.get("updatedAt") as? Timestamp
                    val lastMessage = documentSnapshot.get("lastMessage") as? String
                    val userIds = documentSnapshot.get("userIDs") as? List<*>

                    RawChatHeaderData(
                        listingID = listingId ?: "",
                        sellerID = sellerId ?: "",
                        buyerID = buyerId ?: "",
                        updatedAt = updatedAt ?: Timestamp(0, 0),
                        lastMessage = lastMessage ?: "",
                        userIDs = userIds?.map { it.toString() } ?: emptyList(),
                        chatID = chatId
                    )
                }
                onSnapshotUpdate(data ?: emptyList())
            }
    }

    /**
     * Returns the chat ID of the chat that matches the given [buyerId], [sellerId], and [listingId].
     * If no chat is found, generates a new UUID and returns it.
     */
    suspend fun findChatWith(
        buyerId: String,
        sellerId: String,
        listingId: String
    ): String {
        val result = refactoredChatsCollection.whereEqualTo(
            "buyerID",
            buyerId
        ).whereEqualTo(
            "sellerID",
            sellerId
        ).whereEqualTo("listingID", listingId).get().await()

        if (result.documents.isEmpty()) {
            // Generate UUID, make sure it is unique.
            var uuid = UUID.randomUUID().toString()
            while (refactoredChatsCollection.document(uuid).get().await().exists()) {
                uuid = UUID.randomUUID().toString()
            }
            return uuid.uppercase()
        }

        return result.documents.first().id
    }
}
