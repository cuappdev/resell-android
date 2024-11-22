package com.cornellappdev.resell.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val firestore = FirebaseFirestore.getInstance()
        enableEdgeToEdge()
        setContent {
            ResellTheme {
                CompositionLocalProvider(LocalFireStore provides firestore) {
                    RootNavigation()
                }
            }
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
            }
            else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
