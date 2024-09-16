package com.cornellappdev.android.resell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cornellappdev.android.resell.ui.screens.main.MainTabScaffold
import com.cornellappdev.android.resell.ui.theme.ResellTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResellTheme {
                MainTabScaffold()
            }
        }
    }
}
