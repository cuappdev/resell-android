package com.cornellappdev.resell.android.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellSwitchRow
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.settings.NotificationsViewModel

@Composable
fun NotificationSettings(
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState = notificationsViewModel.collectUiStateValue()

    NotificationsSettingsContent(
        pause = uiState.pauseAllNotifications,
        onPauseChange = notificationsViewModel::onTogglePauseAllNotifications,
        chat = uiState.chatRoot,
        onChatChange = notificationsViewModel::onToggleChatNotifications,
        listings = uiState.listingsRoot,
        onListingsChange = notificationsViewModel::onToggleNewListingsNotifications,
    )
}

@Preview
@Composable
private fun NotificationsSettingsContent(
    pause: Boolean = false,
    onPauseChange: (Boolean) -> Unit = {},
    chat: Boolean = false,
    onChatChange: (Boolean) -> Unit = {},
    listings: Boolean = false,
    onListingsChange: (Boolean) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ResellHeader(
            title = stringResource(R.string.notification_preferences),
        )

        Spacer(Modifier.height(24.dp))

        ResellSwitchRow(
            title = stringResource(R.string.pause_all_notifications),
            checked = pause,
            enabled = true,
            onCheckedChange = onPauseChange,
        )

        ResellSwitchRow(
            title = stringResource(R.string.chat_notifications),
            checked = chat,
            enabled = !pause,
            onCheckedChange = onChatChange,
        )

        ResellSwitchRow(
            title = stringResource(R.string.listings_notifications),
            checked = listings,
            enabled = !pause,
            onCheckedChange = onListingsChange,
        )
    }
}