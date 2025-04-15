package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.api.StartAndEnd
import com.cornellappdev.resell.android.model.chats.ChatDocument
import com.cornellappdev.resell.android.model.chats.RawChatHeaderData
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreRepository @Inject constructor(
    private val fireStore: FirebaseFirestore
) {

    private val historyCollection = fireStore.collection("history")
    private val chatsCollection = fireStore.collection("chats")

    private val refactoredChatsCollection = fireStore.collection("chats_refactored")

    private var lastSubscription: ListenerRegistration? = null

    /**
     * Checks if the specified email has been onboarded.
     * @param email The email to check.
     */
    suspend fun getUserOnboarded(email: String, onError: () -> Unit, onSuccess: (Boolean) -> Unit) {
        try {
            val doc = fireStore.collection("user").document(email)
                .get().await()

            val user = doc.toObject(FirebaseDoc::class.java)
            onSuccess(user?.onboarded ?: false)
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error getting user: ", e)
            onError()
        }
    }

    suspend fun getVenmoHandle(email: String): String {
        try {
            val doc = fireStore.collection("user").document(email)
                .get().await()

            val user = doc.toObject(FirebaseDoc::class.java)
            return user?.venmo ?: ""
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error getting user: ", e)
            return ""
        }
    }

    suspend fun getUserFCMToken(email: String): String? {
        try {
            val doc = fireStore.collection("user").document(email)
                .get().await()

            val user = doc.toObject(FirebaseDoc::class.java)
            return user?.fcmToken
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error getting fcm token: ", e)
            return null
        }
    }

    suspend fun getNotificationsEnabled(email: String): Boolean {
        try {
            val doc = fireStore.collection("user").document(email)
                .get().await()

            val user = doc.toObject(FirebaseDoc::class.java)
            return user?.notificationsEnabled ?: true
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error getting fcm token: ", e)
            return true
        }
    }

    /**
     * Saves the specified device token to the user's document in FireStore.
     */
    suspend fun saveDeviceToken(userEmail: String, deviceToken: String) {
        try {
            val userDocRef = fireStore.collection("user").document(userEmail)
            userDocRef.update("fcmToken", deviceToken).await()
            Log.d("FireStoreRepository", "Device token saved successfully")
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error saving device token: ", e)
            throw e
        }
    }

    /**
     * Saves that the user has been onboarded.
     */
    suspend fun saveOnboarded(userEmail: String) {
        try {
            val userDocRef = fireStore.collection("user").document(userEmail)
            userDocRef.update("onboarded", true).await()
            Log.d("FireStoreRepository", "Onboarded saved successfully")
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error saving onboarded: ", e)
            throw e
        }
    }

    suspend fun saveVenmo(userEmail: String, venmo: String) {
        try {
            val userDocRef = fireStore.collection("user").document(userEmail)
            userDocRef.update("venmo", venmo).await()
            Log.d("FireStoreRepository", "Venmo saved successfully")
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error saving venmo: ", e)
            throw e
        }
    }

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

    suspend fun markChatAsRead(
        myEmail: String,
        otherEmail: String,
        chatType: ChatViewModel.ChatType,
    ) {
        val docRef = when (chatType) {
            // Self is buying from the other; other is a seller.
            // Must write to myEmail/sellers/otherEmail/viewed
            ChatViewModel.ChatType.Purchases -> {
                historyCollection.document(myEmail)
                    .collection("sellers").document(otherEmail)
            }

            // Self is selling to the other; other is a buyer.
            // Must write to myEmail/buyers/otherEmail/viewed
            ChatViewModel.ChatType.Offers -> {
                historyCollection.document(myEmail)
                    .collection("buyers").document(otherEmail)
            }
        }
        val doc = docRef.get().await()

        if (doc.exists()) {
            docRef.update("viewed", true)
        } else {
            Log.e("FirestoreError", "Document does not exist!")
        }
    }

    fun subscribeToChat(
        chatId: String,
        onSnapshotUpdate: (List<ChatDocument>) -> Unit
    ) {
        // Remove old subscription.
        lastSubscription?.remove()

        val chatDocRef = chatsCollection.document(chatId).collection("messages")
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
                            id = url,
                            images = listOf(url),
                            timestamp = it.getTimestamp("createdAt") ?: Timestamp(0, 0),
                            senderId = it.get("senderId") as? String ?: "",
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
                            timestamp = it.getTimestamp("createdAt") ?: Timestamp(0, 0),
                            text = text,
                            accepted = null,
                            startDate = null,
                            endDate = null,
                            availabilities = null,
                            type = "chat",
                            senderId = it.get("senderId") as? String ?: "",
                            images = emptyList()
                        )
                        docs += doc
                    }
                } else {
                    val chatDoc = ChatDocument(
                        id = it.id,
                        timestamp = it.getTimestamp("createdAt") ?: Timestamp(0, 0),
                        text = it.get("text")?.toString() ?: "",
                        accepted = it.get("accepted") as? Boolean,
                        startDate = it.getTimestamp("startDate"),
                        endDate = it.getTimestamp("endDate"),
                        images = it.get("images") as? List<String>,
                        availabilities = it.get("availabilities") as? List<StartAndEnd>,
                        type = it.get("type") as? String ?: "",
                        senderId = it.get("senderId") as? String ?: "",
                    )

                    docs += chatDoc
                }
                docs
            }
            onSnapshotUpdate(messages.flatten())
        }
    }

    fun subscribeToBuyerHistory(
        myId: String,
        onSnapshotUpdate: (List<RawChatHeaderData>) -> Unit
    ) {
        refactoredChatsCollection.whereEqualTo("buyerID", myId)
            .addSnapshotListener { snapshot, _ ->
                val data = snapshot?.documents?.mapNotNull { documentSnapshot ->
                    val chatId = documentSnapshot.id
                    val listingId = documentSnapshot.get("listingID") as? String
                    val sellerId = documentSnapshot.get("sellerID") as? String
                    val buyerId = documentSnapshot.get("buyerID") as? String
                    val updatedAt = documentSnapshot.get("updatedAt") as? Timestamp
                    val lastMessage = documentSnapshot.get("lastMessage") as? String
                    val userIds = documentSnapshot.get("userIds") as? List<*>

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
        refactoredChatsCollection.whereEqualTo("sellerID", myId)
            .addSnapshotListener { snapshot, _ ->
                val data = snapshot?.documents?.mapNotNull { documentSnapshot ->
                    val chatId = documentSnapshot.id
                    val listingId = documentSnapshot.get("listingID") as? String
                    val sellerId = documentSnapshot.get("sellerID") as? String
                    val buyerId = documentSnapshot.get("buyerID") as? String
                    val updatedAt = documentSnapshot.get("updatedAt") as? Timestamp
                    val lastMessage = documentSnapshot.get("lastMessage") as? String
                    val userIds = documentSnapshot.get("userIds") as? List<*>

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

    data class FirebaseDoc(
        val venmo: String = "",
        val onboarded: Boolean = false,
        val notificationsEnabled: Boolean = true,
        val fcmToken: String = ""
    )
}
