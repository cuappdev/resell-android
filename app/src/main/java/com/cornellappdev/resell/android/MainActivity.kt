package com.cornellappdev.resell.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cornellappdev.resell.android.model.api.NotificationData
import com.cornellappdev.resell.android.model.settings.NotificationsRepository
import com.cornellappdev.resell.android.ui.screens.root.RootNavigation
import com.cornellappdev.resell.android.ui.theme.ResellTheme
import com.cornellappdev.resell.android.util.LocalFireStore
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootConfirmationRepository: RootConfirmationRepository

    @Inject
    lateinit var notificationsRepository: NotificationsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val firestore = FirebaseFirestore.getInstance()
        enableEdgeToEdge()

        handleNotificationIntent(intent)

        setContent {
            ResellTheme {
                CompositionLocalProvider(LocalFireStore provides firestore) {
                    RootNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        Log.d("helpme", "Intent extras: ${intent.extras}")
        intent.extras?.let { extras ->
            val notification = NotificationData.ChatNotification(
                name = extras.getString("name").orEmpty(),
                email = extras.getString("email").orEmpty(),
                pfp = extras.getString("pfp").orEmpty(),
                postJson = extras.getString("postJson").orEmpty(),
                isBuyer = extras.getBoolean("isBuyer").toString()
            )

            Log.d("helpme", "Notification: $notification")

            // if any of the extras are missing, don't nav
            if (notification.name.isEmpty() || notification.email.isEmpty() || notification.pfp.isEmpty() || notification.postJson.isEmpty()) {
                return
            }

            notificationsRepository.actOnNotification(notification)
        }
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            rootConfirmationRepository.showError(
                message = "You will not receive notifications for new messages about your orders."
            )
        }
    }

    fun askNotificationPermission(
        onAlreadyGranted: () -> Unit,
        onShowUi: () -> Unit,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                onAlreadyGranted()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                onShowUi()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
