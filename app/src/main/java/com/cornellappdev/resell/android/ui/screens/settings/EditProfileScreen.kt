package com.cornellappdev.resell.android.ui.screens.settings

import android.graphics.Bitmap
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellHeaderCore
import com.cornellappdev.resell.android.ui.components.global.ResellInfoRow
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.components.settings.ProfilePictureEdit
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style.body2
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.util.singlePhotoPicker
import com.cornellappdev.resell.android.viewmodel.settings.EditProfileViewModel

@Composable
fun EditProfileScreen(
    editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState = editProfileViewModel.collectUiStateValue()
    val singlePhotoPicker = singlePhotoPicker {
        if (it != null) {
            editProfileViewModel.onImageSelected(it)
        } else {
            editProfileViewModel.onImageLoadFail()
        }
    }

    Content(
        canSubmit = uiState.canSubmit,
        onSubmit = editProfileViewModel::onSubmit,
        onImageTapped = {
            singlePhotoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        imageBitmap = uiState.imageBitmap,
        name = uiState.name,
        netId = uiState.netId,
        username = uiState.username,
        onUsernameChanged = editProfileViewModel::onUsernameChanged,
        venmoHandle = uiState.venmo,
        onVenmoHandleChanged = editProfileViewModel::onVenmoHandleChanged,
        bio = uiState.bio,
        onBioChanged = editProfileViewModel::onBioChanged
    )
}

@Preview
@Composable
private fun Content(
    canSubmit: Boolean = true,
    onSubmit: () -> Unit = {},
    imageBitmap: Bitmap? = null,
    onImageTapped: () -> Unit = {},
    name: String = "",
    netId: String = "",
    username: String = "",
    onUsernameChanged: (String) -> Unit = {},
    venmoHandle: String = "",
    onVenmoHandleChanged: (String) -> Unit = {},
    bio: String = "",
    onBioChanged: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeaderCore(
            title = "Edit Profile",
            rightContent = {
                Text(
                    text = "Save",
                    color = ResellPurple.copy(
                        alpha = if (canSubmit) 1f else 0.4f
                    ),
                    style = title1
                )
            },
            onRightClick = {
                if (canSubmit) {
                    onSubmit()
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfilePictureEdit(
            imageBitmap = imageBitmap,
            onImageTapped = onImageTapped,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(modifier = Modifier.defaultHorizontalPadding()) {
            ResellInfoRow(
                title = "Name",
                content = name
            )

            Spacer(modifier = Modifier.height(40.dp))

            ResellInfoRow(
                title = "NetID",
                content = netId
            )

            Spacer(modifier = Modifier.height(40.dp))

            ResellTextEntry(
                inlineLabel = true,
                label = "Username",
                text = username,
                onTextChange = onUsernameChanged
            )

            Spacer(modifier = Modifier.height(40.dp))

            ResellTextEntry(
                inlineLabel = true,
                label = "Venmo Handle",
                text = venmoHandle,
                onTextChange = onVenmoHandleChanged,
                leadingIcon = {
                    Text(
                        text = "@",
                        style = title1
                    )
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            ResellTextEntry(
                inlineLabel = true,
                label = "Bio",
                text = bio,
                maxLines = 3,
                singleLine = false,
                onTextChange = onBioChanged,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${bio.length}/200",
                style = body2.copy(
                    color = Color.Gray
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
