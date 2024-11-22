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
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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


    private fun launchRequestPermission(
        onNewlyGranted: () -> Unit,
        onRejected: () -> Unit
    ) {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                onNewlyGranted()
            } else {
                onRejected()
            }
        }
    }

    fun askNotificationPermission(
        onAlreadyGranted: () -> Unit,
        onShowUi: () -> Unit,
        onNewlyGranted: () -> Unit,
        onRejected: () -> Unit
    ) {
        // This is only necessary for API level >= 33 (TIRAMISU)
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
                // Directly ask for the permission
                launchRequestPermission(onNewlyGranted, onRejected)
            }
        }
    }
}
