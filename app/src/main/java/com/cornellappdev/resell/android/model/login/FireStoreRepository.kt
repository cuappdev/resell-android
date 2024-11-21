package com.cornellappdev.resell.android.model.login

import android.util.Log
import com.cornellappdev.resell.android.model.chats.BuyerSellerData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreRepository @Inject constructor(
    private val fireStore: FirebaseFirestore
) {

    private val historyCollection = fireStore.collection("history")

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
            it?.toObject(BuyerSellerData::class.java)
        }
    }

    suspend fun getSellerHistory(email: String): List<BuyerSellerData> {
        val sellers = historyCollection.document(email)
            .collection("sellers").get().await()

        return sellers.documents.mapNotNull {
            it?.toObject(BuyerSellerData::class.java)
        }
    }
}

data class FirebaseDoc(
    val venmo: String = "",
    val onboarded: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val fcmToken: String = ""
)
