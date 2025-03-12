package com.cornellappdev.resell.android.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.ResellAuthRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun LogOutSheetContent(
    onDismiss: () -> Unit
) {
    val viewModel = hiltViewModel<LogOutSheetContentViewModel>()

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Log Out of Resell?",
            style = Style.heading3
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextButton(
            text = "Log Out",
            onClick = viewModel::onLogOut,
            containerType = ResellTextButtonContainer.PRIMARY_RED
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextButton(
            text = "Cancel",
            onClick = onDismiss,
            containerType = ResellTextButtonContainer.NAKED_PRIMARY
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@HiltViewModel
private class LogOutSheetContentViewModel @Inject constructor(
    private val resellAuthRepository: ResellAuthRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {
    fun onLogOut() {
        rootNavigationSheetRepository.hideSheet()
        viewModelScope.launch {
            resellAuthRepository.logOut()
            rootNavigationRepository.navigate(ResellRootRoute.LANDING)
        }
    }
}
