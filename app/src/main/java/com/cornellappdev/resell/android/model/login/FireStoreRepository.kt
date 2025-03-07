package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.api.User
import com.cornellappdev.resell.android.model.chats.AvailabilityBlock
import com.cornellappdev.resell.android.model.chats.AvailabilityDocument
import com.cornellappdev.resell.android.model.chats.BuyerSellerData
import com.cornellappdev.resell.android.model.chats.ChatDocument
import com.cornellappdev.resell.android.model.chats.ChatDocumentAny
import com.cornellappdev.resell.android.model.chats.ChatDocumentAnyMeetingInfo
import com.cornellappdev.resell.android.model.chats.MeetingInfo
import com.cornellappdev.resell.android.model.chats.UserDocument
import com.cornellappdev.resell.android.viewmodel.main.ChatViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
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

        val raw = (it.get("item") as Map<*, *>).mapValues { it?.value?.toString() }
        val userMap =
            ((it.get("item") as Map<*, *>)["user"] as Map<*, *>).mapValues { it?.value?.toString() }

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
            altered_price = raw["altered_price"] ?: "",
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
            email = it.id
        )
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
        buyerEmail: String,
        sellerEmail: String,
        onSnapshotUpdate: (List<ChatDocument>) -> Unit
    ) {
        // Remove old subscription.
        lastSubscription?.remove()

        val chatDocRef = chatsCollection.document(buyerEmail).collection(sellerEmail)
            .orderBy("createdAt", Query.Direction.ASCENDING)

        lastSubscription = chatDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FireStoreRepository", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                Log.e("FireStoreRepository", "Current data null")
                return@addSnapshotListener
            }

            Log.d("FireStoreRepository", "Current data: ${snapshot.documents}")

            val messages = snapshot.documents.mapNotNull {

                val userMap =
                    (it.get("user") as Map<String, Any>).mapValues { it?.value?.toString() }

                val productMap =
                    (it.get("product") as? Map<String, Any>)?.mapValues { it?.value?.toString() }

                val productUserMap =
                    if (productMap?.get("user") != null) {
                        val doc = it.get("product") as Map<String, Any>
                        val postUserMap = doc.get("user") as Map<String, Any>
                        postUserMap.mapValues { it?.value?.toString() }
                    } else {
                        null
                    }

                val availabilityArray: List<Map<String, Any>>? =
                    it.get("availability") as? List<Map<String, Any>>

                val meetingInfoMap =
                    (it.get("meetingInfo") as? Map<String, Any>)?.mapValues { it?.value?.toString() }

                val userDoc = UserDocument(
                    _id = userMap["_id"] ?: "",
                    avatar = userMap["avatar"] ?: "",
                    name = userMap["name"] ?: "",
                )

                val postUser = if (productUserMap != null) {
                    User(
                        id = productUserMap["id"] ?: "",
                        username = productUserMap["username"] ?: "",
                        netid = productUserMap["netid"] ?: "",
                        givenName = productUserMap["givenName"] ?: "",
                        familyName = productUserMap["familyName"] ?: "",
                        email = productUserMap["email"] ?: "",
                        photoUrl = productUserMap["photoUrl"] ?: "",
                        bio = productUserMap["bio"] ?: "",
                        admin = productUserMap["admin"]?.toBoolean() ?: false,
                        googleId = productUserMap["googleId"] ?: "",
                    )
                } else {
                    null
                }

                // TODO Availability Documents
                val post = productMap?.get("id")?.let { _ ->
                    Post(
                        id = productMap["id"] ?: "",
                        title = productMap["title"] ?: "",
                        description = productMap["description"] ?: "",
                        price = (productMap["price"] ?: "0.0").toDouble(),
                        user = postUser,
                        archive = (productMap["archive"] ?: "").toBoolean(),
                        location = productMap["location"] ?: "",
                        created = productMap["created"] ?: "",
                        altered_price = productMap["altered_price"] ?: "",
                        images = (it.get("product") as Map<String, Any>).get("images") as? List<String>
                            ?: listOf(),
                        categories = (it.get("product") as Map<String, Any>).get("categories") as? List<String>
                            ?: listOf(),
                    )
                }

                val meetingInfo = meetingInfoMap?.let {
                    MeetingInfo(
                        state = meetingInfoMap["state"] ?: "",
                        proposeTime = meetingInfoMap["proposeTime"] ?: "",
                        proposer = meetingInfoMap["proposer"],
                        canceler = meetingInfoMap["canceler"],
                        mostRecent = false,
                    )
                }

                val availability = if (availabilityArray.isNullOrEmpty()) {
                    null
                } else {
                    AvailabilityDocument(
                        availabilities = availabilityArray.map {
                            AvailabilityBlock(
                                startDate = it["startDate"] as? Timestamp ?: Timestamp(0, 0),
                                id = (it["id"] as? String ?: "").toIntOrNull() ?: 0,
                            )
                        },
                    )
                }

                val chatDoc = ChatDocument(
                    _id = it.get("_id")?.toString() ?: "",
                    createdAt = it.getTimestamp("createdAt") ?: Timestamp(0, 0),
                    user = userDoc,
                    availability = availability,
                    product = post,
                    image = it.get("image")?.toString() ?: "",
                    text = it.get("text")?.toString() ?: "",
                    meetingInfo = meetingInfo
                )

                chatDoc
            }

            onSnapshotUpdate(messages)
        }
    }

    suspend fun sendChatMessage(
        buyerEmail: String,
        sellerEmail: String,
        chatDocument: ChatDocument,
    ) {

        // Make into an empty object if applicable instead of null cuz react native crashes
        var anyable: Any = ChatDocumentAny(
            _id = chatDocument._id,
            createdAt = Timestamp.now(),
            user = chatDocument.user,
            availability = chatDocument.availability?.toFirebaseArray() ?: mapOf<String, Any>(),
            product = chatDocument.product ?: mapOf<String, Any>(),
            image = chatDocument.image,
            text = chatDocument.text
        )

        // Use meeting info structure instead
        if (chatDocument.meetingInfo != null) {
            anyable = ChatDocumentAnyMeetingInfo(
                _id = chatDocument._id,
                createdAt = Timestamp.now(),
                user = chatDocument.user,
                image = chatDocument.image,
                text = chatDocument.text,
                meetingInfo = chatDocument.meetingInfo.toFirebaseMap()
            )
        }

        // Reference to the desired collection
        val chatRef = fireStore.collection("chats")
            .document(buyerEmail)
            .collection(sellerEmail)

        chatRef.add(anyable).await()
    }

    suspend fun sendProductMessage(
        buyerEmail: String,
        sellerEmail: String,
        otherDocument: ChatDocument,
        post: Post
    ) {
        val currentTimeMillis = System.currentTimeMillis()
        val chatDocument = otherDocument.copy(
            _id = currentTimeMillis.toString(),
            createdAt = Timestamp.now(),
            image = "",
            text = "",
            availability = null,
            product = post
        )

        val anyable = ChatDocumentAny(
            _id = chatDocument._id,
            createdAt = chatDocument.createdAt,
            user = chatDocument.user,
            availability = chatDocument.availability?.toFirebaseArray() ?: mapOf<String, Any>(),
            product = chatDocument.product ?: mapOf<String, Any>(),
            image = chatDocument.image,
            text = chatDocument.text
        )

        val chatRef = fireStore.collection("chats")
            .document(buyerEmail)
            .collection(sellerEmail)

        chatRef.add(anyable).await()
    }

    /**
     * Call for the seller to update their `buyer` history.
     */
    suspend fun updateBuyerHistory(
        sellerEmail: String,
        buyerEmail: String,
        data: BuyerSellerData
    ) {
        val docRef = historyCollection.document(sellerEmail)
            .collection("buyers")
            .document(buyerEmail)

        docRef.set(data).await()
    }

    /**
     * Call for the buyer to update their `seller` history.
     */
    suspend fun updateSellerHistory(
        buyerEmail: String,
        sellerEmail: String,
        data: BuyerSellerData
    ) {
        val docRef = historyCollection.document(buyerEmail)
            .collection("sellers")
            .document(sellerEmail)

        docRef.set(data).await()
    }

    suspend fun updateItems(
        email: String,
        postId: String,
        post: Post,
    ) {
        val docRef = historyCollection.document(email).collection("items").document(postId)

        docRef.set(post).await()
    }
}

data class FirebaseDoc(
    val venmo: String = "",
    val onboarded: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val fcmToken: String = ""
)
