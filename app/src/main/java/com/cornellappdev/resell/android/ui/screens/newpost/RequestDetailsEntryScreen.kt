package com.cornellappdev.resell.android.ui.screens.newpost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.components.newpost.MinMaxMoneyEntry
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.newpost.RequestDetailsEntryViewModel

@Composable
fun RequestDetailsEntryScreen(
    requestDetailsEntryViewModel: RequestDetailsEntryViewModel = hiltViewModel(),
) {
    val uiState = requestDetailsEntryViewModel.collectUiStateValue()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        RequestDetailsContent(
            uiState = uiState,
            onMinPressed = requestDetailsEntryViewModel::onMinPricePressed,
            onMaxPressed = requestDetailsEntryViewModel::onMaxPricePressed,
            onDescriptionChanged = requestDetailsEntryViewModel::onDescriptionChanged,
            onTitleChanged = requestDetailsEntryViewModel::onTitleChanged,
        )

        ResellTextButton(
            text = "Next",
            onClick = requestDetailsEntryViewModel::onConfirmPost,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp),
            state = uiState.buttonState,
        )
    }
}


@Preview
@Composable
private fun RequestDetailsContent(
    uiState: RequestDetailsEntryViewModel.RequestDetailsEntryViewState =
        RequestDetailsEntryViewModel.RequestDetailsEntryViewState(),
    onMinPressed: () -> Unit = {},
    onMaxPressed: () -> Unit = {},
    onTitleChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = "Request Details",
        )

        Spacer(Modifier.height(24.dp))

        ResellTextEntry(
            label = "Title",
            text = uiState.title,
            onTextChange = onTitleChanged,
            inlineLabel = false,
            modifier = Modifier.defaultHorizontalPadding()
        )

        Spacer(Modifier.height(32.dp))

        MinMaxMoneyEntry(
            label = "Price Range",
            minText = uiState.minPrice,
            maxText = uiState.maxPrice,
            onMinPressed = onMinPressed,
            onMaxPressed = onMaxPressed,
            modifier = Modifier
                .defaultHorizontalPadding()
                .align(Alignment.Start)
        )

        Spacer(Modifier.height(32.dp))

        ResellTextEntry(
            label = "Item Description",
            text = uiState.description,
            onTextChange = onDescriptionChanged,
            inlineLabel = false,
            multiLineHeight = 255.dp,
            singleLine = false,
            // TODO: probably wrong max lines
            maxLines = 20,
            modifier = Modifier.defaultHorizontalPadding(),
            placeholder = "enter item details..."
        )

        Spacer(Modifier.height(100.dp))
    }
}
