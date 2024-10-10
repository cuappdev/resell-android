package com.cornellappdev.resell.android.ui.screens.newpost

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.resell.android.util.LocalNewPostNavigator
import com.cornellappdev.resell.android.viewmodel.navigation.NewPostNavigationViewModel
import kotlinx.serialization.Serializable

@Composable
fun NewPostNavigation(
    newPostNavigationViewModel: NewPostNavigationViewModel = hiltViewModel()
) {
    val nav = rememberNavController()
    val uiState = newPostNavigationViewModel.collectUiStateValue()

    LaunchedEffect(uiState.navigationEvent) {
        uiState.navigationEvent?.consumeSuspend {
            nav.navigate(it)
        }
    }

    CompositionLocalProvider(LocalNewPostNavigator provides nav) {
        NavHost(
            navController = LocalNewPostNavigator.current,
            startDestination = ResellNewPostScreen.ImageUpload,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<ResellNewPostScreen.ImageUpload> {
                ImageUploadScreen()
            }

            composable<ResellNewPostScreen.PostDetails> {
                PostDetailsEntryScreen()
            }

            composable<ResellNewPostScreen.RequestDetails> {

            }
        }
    }
}

@Serializable
sealed class ResellNewPostScreen {

    @Serializable
    data object ImageUpload : ResellNewPostScreen()

    @Serializable
    data object PostDetails : ResellNewPostScreen()

    @Serializable
    data object RequestDetails : ResellNewPostScreen()
}
