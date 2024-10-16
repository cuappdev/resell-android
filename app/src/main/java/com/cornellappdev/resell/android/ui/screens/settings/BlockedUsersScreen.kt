package com.cornellappdev.resell.android.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style.body1
import com.cornellappdev.resell.android.ui.theme.Style.heading2
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.settings.BlockedUsersViewModel

@Composable
fun BlockedUsersScreen(
    blockedUsersViewModel: BlockedUsersViewModel = hiltViewModel(),
) {
    val uiState = blockedUsersViewModel.collectUiStateValue()

    Content(
        blockedUsers = uiState.blockedUsers,
        onUnblockUser = blockedUsersViewModel::onUnblock
    )
}

@Preview
@Composable
private fun Content(
    blockedUsers: List<BlockedUsersViewModel.UiBlockedUser> = emptyList(),
    onUnblockUser: (id: String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = "Blocked Users",
        )

        if (blockedUsers.isEmpty()) {
            EmptyState()
        }

        LazyColumn {
            items(blockedUsers.size) { index ->
                UserPfpRow(
                    imageUrl = blockedUsers[index].imageUrl,
                    username = blockedUsers[index].name,
                    onUnblock = {
                        blockedUsers[index].id
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "No blocked users",
            style = heading2,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Users you have blocked will appear here.",
            style = body1,
            color = AppDev,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
private fun UserPfpRow(
    imageUrl: String,
    username: String,
    onUnblock: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultHorizontalPadding()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePictureView(
                imageUrl = imageUrl,
                modifier = Modifier.size(52.dp)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = username,
                style = body1,
                modifier = Modifier.widthIn(max = 150.dp)
            )
        }

        Button(
            onClick = {
                onUnblock()
            },
            modifier = Modifier
                .padding(start = 16.dp)
                .width(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ResellPurple),
            contentPadding = PaddingValues(vertical = 6.dp, horizontal = 10.dp)
        ) {
            Text(
                text = "Unblock",
                color = Color.White,
                style = body1
            )
        }
    }
}

@Preview
@Composable
private fun UserPfpRowPreview() = ResellPreview {
    UserPfpRow(
        imageUrl = "https://picsum.photos/200",
        username = "username",
        onUnblock = {}
    )

    UserPfpRow(
        imageUrl = "https://picsum.photos/200",
        username = "username that's way too damn long",
        onUnblock = {}
    )
}
