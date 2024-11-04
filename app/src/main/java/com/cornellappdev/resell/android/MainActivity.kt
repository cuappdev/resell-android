package com.cornellappdev.resell.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
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
}
