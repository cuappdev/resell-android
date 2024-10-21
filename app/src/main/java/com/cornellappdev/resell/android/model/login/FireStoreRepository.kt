package com.cornellappdev.resell.android.model.login

import com.cornellappdev.resell.android.model.classes.login.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreRepository @Inject constructor(
    private val fireStore: FirebaseFirestore
) {

    /**
     * Checks if the specified email has been onboarded.
     * @param email The email to check.
     */
    suspend fun getUserOnboarded(email: String, onError: () -> Unit, onSuccess: (Boolean) -> Unit) {
        try {
            val doc = fireStore.collection("user").document(email)
                .get().await()

            val user = doc.toObject(FirebaseUser::class.java)
            onSuccess(user?.onboarded ?: false)
        } catch (e: Exception) {
            onError()
        }
    }
}
