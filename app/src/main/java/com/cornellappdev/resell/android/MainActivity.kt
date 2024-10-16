package com.cornellappdev.resell.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cornellappdev.resell.android.ui.screens.root.RootNavigation
import com.cornellappdev.resell.android.ui.theme.ResellTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResellTheme {
                RootNavigation()
            }
        }
    }
}
