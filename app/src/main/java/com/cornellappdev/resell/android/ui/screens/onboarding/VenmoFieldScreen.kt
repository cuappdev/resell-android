package com.cornellappdev.resell.android.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Venmo
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.onboarding.VenmoFieldViewModel

@Composable
fun VenmoFieldScreen(
    venmoFieldViewModel: VenmoFieldViewModel = hiltViewModel(),
) {
    val uiState = venmoFieldViewModel.collectUiStateValue()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader()

        ResellTextEntry(
            label = "Venmo Handle",
            text = uiState.handle,
            onTextChange = { venmoFieldViewModel.onHandleChanged(it) },
            inlineLabel = false,
            modifier = Modifier.defaultHorizontalPadding()
        )

        Spacer(Modifier.weight(1f))

        Column {
            ResellTextButton(
                text = "Continue",
                state = uiState.buttonState,
                onClick = venmoFieldViewModel::onContinueClick
            )
            ResellTextButton(
                text = "Skip",
                state = ResellTextButtonState.ENABLED,
                onClick = venmoFieldViewModel::onSkipClick,
                containerType = ResellTextButtonContainer.NAKED
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun ScreenHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultHorizontalPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    style = Style.heading3,
                    text = "Link your",
                    modifier = Modifier.padding(end = 6.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_venmo_logo),
                    contentDescription = null,
                    tint = Venmo
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        Text(
            text = "Your Venmo handle will only be visible to people " +
                    "interested in buying your listing.",
            style = Style.body1,
            color = Secondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(56.dp))
    }
}
