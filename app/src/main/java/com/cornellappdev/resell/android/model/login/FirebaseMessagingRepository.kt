package com.cornellappdev.resell.android.model.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
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
    private val rootConfirmationRepository: RootConfirmationRepository,
    @ApplicationContext private val context: Context,
) {
    private val _requestNotificationsEventFlow = MutableStateFlow<UIEvent<Unit>?>(null)
    val requestNotificationsEventFlow = _requestNotificationsEventFlow.asStateFlow()

    suspend fun getDeviceFCMToken(): String? {
        return try {
            // Get the FCM token
            firebaseMessaging.token.await()
        } catch (e: Exception) {
            // Handle any exceptions that occur
            e.printStackTrace()
            null
        }
    }

    fun requestNotificationsPermission() {
        _requestNotificationsEventFlow.value = UIEvent(Unit)
    }
}
