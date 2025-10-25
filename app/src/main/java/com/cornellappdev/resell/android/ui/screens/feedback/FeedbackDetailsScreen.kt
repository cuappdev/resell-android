package com.cornellappdev.resell.android.ui.screens.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.theme.Style.heading3
import com.cornellappdev.resell.android.viewmodel.feedback.FeedbackDetailsViewModel

@Composable
fun FeedbackDetailsScreen(
    feedbackDetailsViewModel: FeedbackDetailsViewModel = hiltViewModel()
) {
    val uiState = feedbackDetailsViewModel.collectUiStateValue()

    Content(
        title = uiState.title,
        subtitle = uiState.subtitle,
        details = uiState.typedContent,
        placeholder = uiState.body,
        onDetailsChanged = feedbackDetailsViewModel::onTypedContentChanged,
        onBackArrow = feedbackDetailsViewModel::onBackArrow,
        buttonState = uiState.buttonState,
        onSubmitClick = feedbackDetailsViewModel::onSubmitPressed
    )
}

@Preview
@Composable
private fun Content(
    title: String = "Submit Feedback",
    subtitle: String = "Explain what happened: ",
    details: String = "",
    placeholder: String = "Describe any issues that occurred during your transaction with Ravina Patel.",
    onDetailsChanged: (String) -> Unit = {},
    onBackArrow: () -> Unit = {},
    buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
    onSubmitClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            ResellHeader(
                title = title,
                leftPainter = R.drawable.ic_chevron_left,
                onLeftClick = {
                    onBackArrow()
                }
            )

            Spacer(Modifier.height(36.dp))

            Text(
                text = subtitle,
                style = heading3,
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            // TODO Incorrect text entry design
            ResellTextEntry(
                text = details,
                onTextChange = onDetailsChanged,
                inlineLabel = false,
                multiLineHeight = 203.dp,
                modifier = Modifier.padding(24.dp),
                singleLine = false,
                // TODO: probably wrong max lines
                maxLines = 20,
                placeholder = placeholder
            )
            // Spacer to ensure content is not blocked by button
            Spacer(modifier = Modifier.height(100.dp))
        }
        ResellTextButton(
            text = "Submit",
            state = buttonState,
            onClick = onSubmitClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp)
        )
    }
}