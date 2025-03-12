package com.cornellappdev.resell.android.model.login

import android.content.Context
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessagingRepository @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
) {
    private val _requestNotificationsEventFlow = MutableStateFlow<UIEvent<Unit>?>(null)
    val requestNotificationsEventFlow = _requestNotificationsEventFlow.asStateFlow()

    suspend fun getDeviceFCMToken(): String {
        return try {
            // Get the FCM token
            firebaseMessaging.token.await()
        } catch (e: Exception) {
            // Handle any exceptions that occur
            e.printStackTrace()
            throw e
        }
    }

    /**
     * If the user has not granted notifications permission, request it.
     *
     * Does nothing if the device API level is lower than 33, or if the user has already granted notifications permission.
     */
    fun requestNotificationsPermission() {
        _requestNotificationsEventFlow.value = UIEvent(Unit)
    }
}
