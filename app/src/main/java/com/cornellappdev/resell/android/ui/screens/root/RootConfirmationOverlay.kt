package com.cornellappdev.resell.android.ui.screens.root

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.confirmation.ConfirmationSurface
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationViewModel

@Composable
fun RootConfirmationOverlay(
    rootConfirmationViewModel: RootConfirmationViewModel = hiltViewModel()
) {
    val uiState = rootConfirmationViewModel.collectUiStateValue()

    val opacity by animateFloatAsState(
        targetValue = if (uiState.show) 1f else 0f,
        label = "opacity",
        animationSpec = tween(500)
    )

    if (opacity == 0f) return

    Column(
        modifier = Modifier.statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        ConfirmationSurface(
            icon = uiState.painterRes?.let {
                painterResource(id = it)
            },
            color = uiState.color,
            title = uiState.title,
            onDismiss = { rootConfirmationViewModel.onDismiss() },
            modifier = Modifier.alpha(opacity)
        )
    }
}
