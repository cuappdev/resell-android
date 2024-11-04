package com.cornellappdev.resell.android.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = "Notification Preferences",
        )

        Spacer(Modifier.height(24.dp))

        SwitchRow(
            title = "Pause All Notifications",
            checked = pause,
            enabled = true,
            onCheckedChange = onPauseChange
        )

        SwitchRow(
            title = "Chat Notifications",
            checked = chat,
            enabled = !pause,
            onCheckedChange = onChatChange
        )

        SwitchRow(
            title = "Listings Notifications",
            checked = listings,
            enabled = !pause,
            onCheckedChange = onListingsChange
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .defaultHorizontalPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Style.body1,
        )

        Switch(
            checked = checked && enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = IconInactive,
                checkedTrackColor = ResellPurple,
                uncheckedTrackColor = Color.White,
                checkedBorderColor = ResellPurple,
                uncheckedBorderColor = IconInactive
            ),
            enabled = enabled,
        )
    }
}
