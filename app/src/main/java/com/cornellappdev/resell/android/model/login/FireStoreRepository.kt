package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.model.chats.BuyerSellerData
import com.cornellappdev.resell.android.model.chats.ChatDocument
import com.cornellappdev.resell.android.model.chats.UserDocument
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreRepository @Inject constructor(
    private val fireStore: FirebaseFirestore
) {

    private val historyCollection = fireStore.collection("history")
    private val chatsCollection = fireStore.collection("chats")

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

    suspend fun getBuyerHistory(email: String): List<BuyerSellerData> {
        val buyers = historyCollection.document(email)
            .collection("buyers").get().await()

        return buyers.documents.mapNotNull {
            makeBuyerSellerData(it)
        }
    }

    suspend fun getSellerHistory(email: String): List<BuyerSellerData> {
        val sellers = historyCollection.document(email)
            .collection("sellers").get().await()

        return sellers.documents.mapNotNull {
            makeBuyerSellerData(it)
        }
    }

    private fun makeBuyerSellerData(it: DocumentSnapshot): BuyerSellerData {
        // Please don't look at this. This is the worst file I've ever written in my life.
        val confirmedTime = it.get("confirmedTime")?.toString() ?: ""
        val imageUrl = it.get("image")?.toString() ?: ""
        val name = it.get("name")?.toString() ?: ""
        val recentMessage = it.get("recentMessage")?.toString() ?: ""
        val recentMessageTime = it.get("recentMessageTime")?.toString() ?: ""
        val recentSender = it.get("recentSender")?.toString() ?: ""
        val viewed = it.getBoolean("viewed") ?: false

        val raw = (it.get("item") as Map<String, Any>).mapValues { it?.value?.toString() }
        val userMap =
            ((it.get("item") as Map<String, Any>)["user"] as Map<String, Any>).mapValues { it?.value?.toString() }

        fun parseToList(input: String): List<String> {
            return input
                .removePrefix("[") // Remove the leading '['
                .removeSuffix("]") // Remove the trailing ']'
                .split(", ")        // Split by ", "
                .map { it.trim() }  // Trim whitespace just in case
        }

        val user = User(
            id = userMap["id"] ?: "",
            familyName = userMap["familyName"] ?: "",
            email = userMap["email"] ?: "",
            givenName = userMap["givenName"] ?: "",
            username = userMap["username"] ?: "",
            netid = userMap["netid"] ?: "",
            admin = userMap["admin"]?.toBoolean() ?: false,
            photoUrl = userMap["photoUrl"] ?: "",
            bio = userMap["bio"] ?: "",
            googleId = userMap["googleId"] ?: ""
        )

        val post = Post(
            id = raw["id"] ?: "",
            title = raw["title"] ?: "",
            description = raw["description"] ?: "",
            price = (raw["price"] ?: "0.0").toDouble(),
            user = user,
            archive = (raw["archive"] ?: "").toBoolean(),
            location = raw["location"] ?: "",
            created = raw["created"] ?: "",
            altered = raw["altered_price"] ?: "",
            images = parseToList(raw["images"] ?: ""),
            categories = parseToList(raw["categories"] ?: ""),
        )

        return BuyerSellerData(
            confirmedTime = confirmedTime,
            image = imageUrl,
            name = name,
            recentMessage = recentMessage,
            recentMessageTime = recentMessageTime,
            recentSender = recentSender,
            viewed = viewed,
            item = post,
            confirmedViewed = viewed,
        )
    }

    fun subscribeToChat(
        myEmail: String,
        email: String,
        onSnapshotUpdate: (List<ChatDocument>) -> Unit
    ) {
        // Remove old subscription.
        lastSubscription?.remove()

        val chatDocRef = chatsCollection.document(myEmail).collection(email)
            .orderBy("createdAt", Query.Direction.DESCENDING)

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

                val userMap =
                    (it.get("user") as Map<String, Any>).mapValues { it?.value?.toString() }

                val userDoc = UserDocument(
                    id = userMap["_id"] ?: "",
                    avatar = userMap["avatar"] ?: "",
                    name = userMap["name"] ?: "",
                )

                // TODO Availability and Product Documents

                val chatDoc = ChatDocument(
                    id = it.get("_id")?.toString() ?: "",
                    createdAt = it.get("createdAt")?.toString() ?: "",
                    user = userDoc,
                    availability = null,
                    product = null,
                    image = it.get("image")?.toString() ?: "",
                    text = it.get("text")?.toString() ?: "",
                )

                chatDoc
            }

            onSnapshotUpdate(messages)
        }
    }

    suspend fun sendTextMessage(
        buyerEmail: String,
        sellerEmail: String,
        chatDocument: ChatDocument,
    ) {
        // Reference to the desired collection
        val chatRef = fireStore.collection("chats")
            .document(buyerEmail)
            .collection(sellerEmail)

        val gson = Gson()
        val chatDocumentMap = gson.toJsonTree(chatDocument).asJsonObject
        chatRef.add(chatDocumentMap).await()
    }
}

data class FirebaseDoc(
    val venmo: String = "",
    val onboarded: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val fcmToken: String = ""
)
