package com.cornellappdev.resell.android.ui.components.global.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication

@Composable
fun TwoButtonDialog(
    title: String,
    description: String,
    primaryButtonText: String,
    secondaryButtonText: String?,
    onPrimaryButtonClick: () -> Unit,
    onSecondaryButtonClick: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    primaryButtonContainer: ResellTextButtonContainer = ResellTextButtonContainer.PRIMARY,
    secondaryButtonContainer: ResellTextButtonContainer = ResellTextButtonContainer.NAKED_APPDEV,
    primaryButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
    secondaryButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = Style.title1,
                modifier = Modifier.align(Alignment.Center)
            )

            if (onDismiss != null) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_exit),
                    tint = AppDev,
                    contentDescription = "exit",
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(20.dp)
                        .align(Alignment.CenterEnd)
                        .clickableNoIndication {
                            onDismiss()
                        }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = description,
            style = Style.body2,
            modifier = Modifier.padding(horizontal = 26.dp),
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        ResellTextButton(
            text = primaryButtonText,
            onClick = onPrimaryButtonClick,
            containerType = primaryButtonContainer,
            state = primaryButtonState,
            modifier = Modifier.fillMaxWidth()
        )

        if (secondaryButtonText != null) {
            Spacer(
                modifier = Modifier.height(12.dp)
            )
            ResellTextButton(
                text = secondaryButtonText,
                onClick = onSecondaryButtonClick,
                containerType = secondaryButtonContainer,
                state = secondaryButtonState,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}

@Preview
@Composable
private fun TwoButtonDialogPreview() = ResellPreview {
    TwoButtonDialog(
        title = "Title",
        description = "Description",
        primaryButtonText = "Primary",
        secondaryButtonText = "Secondary",
        onPrimaryButtonClick = {},
        onSecondaryButtonClick = {},
        onDismiss = {},
    )

    Spacer(modifier = Modifier.height(16.dp))

    TwoButtonDialog(
        title = "Title",
        description = "Description",
        primaryButtonText = "Primary",
        secondaryButtonText = null,
        onPrimaryButtonClick = {},
        onSecondaryButtonClick = {},
    )
}
