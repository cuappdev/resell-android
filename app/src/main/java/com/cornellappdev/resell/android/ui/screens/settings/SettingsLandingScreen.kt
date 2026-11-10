package com.cornellappdev.resell.android.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material.ripple
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Warning
import com.cornellappdev.resell.android.viewmodel.settings.SettingsLandingViewModel

@Composable
fun SettingsLandingScreen(
    settingsLandingViewModel: SettingsLandingViewModel = hiltViewModel()
) {
    Content(
        onEditProfile = settingsLandingViewModel::onEditProfileClick,
        onNotifications = settingsLandingViewModel::onNotificationsClick,
        onFeedback = settingsLandingViewModel::onFeedbackClick,
        onBlockedUsers = settingsLandingViewModel::onBlockedUsersClick,
        onTermsAndConditions = settingsLandingViewModel::onTermsClick,
        onPrivacyPolicy = settingsLandingViewModel::onPrivacyClick,
        onLogout = settingsLandingViewModel::onLogoutClick,
        onDeleteAccount = settingsLandingViewModel::onDeleteAccountClick
    )
}

@Preview
@Composable
private fun Content(
    onEditProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onFeedback: () -> Unit = {},
    onBlockedUsers: () -> Unit = {},
    onTermsAndConditions: () -> Unit = {},
    onPrivacyPolicy: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = stringResource(R.string.settings),
        )

        Spacer(Modifier.height(24.dp))

        EntryRow(
            painter = painterResource(id = R.drawable.ic_edit),
            text = stringResource(R.string.edit_profile),
            onClick = onEditProfile,
        )

        EntryRow(
            painter = painterResource(id = R.drawable.ic_notifications),
            text = stringResource(R.string.notifications),
            onClick = onNotifications
        )

        EntryRow(
            painter = painterResource(id = R.drawable.ic_feedback),
            text = stringResource(R.string.send_feedback),
            onClick = onFeedback
        )

        EntryRow(
            painter = painterResource(id = R.drawable.ic_slash),
            text = stringResource(R.string.blocked_users),
            onClick = onBlockedUsers,
        )

        EntryRow(
            painter = painterResource(id = R.drawable.ic_terms),
            text = stringResource(R.string.terms_and_conditions),
            onClick = onTermsAndConditions
        )

        EntryRow(
            painter = painterResource(id = R.drawable.ic_privacy),
            text = stringResource(R.string.privacy_policy),
            onClick = onPrivacyPolicy
        )

        EntryRow(
            painter = painterResource(id = R.drawable.ic_logout),
            text = stringResource(R.string.log_out),
            onClick = onLogout
        )

        EntryRow(
            text = stringResource(R.string.delete_account),
            onClick = onDeleteAccount,
            color = Warning
        )

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun EntryRow(
    painter: Painter? = null,
    color: Color = Color.Black,
    text: String,
    onClick: () -> Unit,
    chevron: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) {
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(24.dp))
            if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = color,
                )
                Spacer(Modifier.width(24.dp))
            }
            Text(
                text = text,
                style = Style.body1,
                color = color,
            )
        }

        if (chevron) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = color,
                modifier = Modifier.padding(end = 24.dp)
            )
        }
    }
}
