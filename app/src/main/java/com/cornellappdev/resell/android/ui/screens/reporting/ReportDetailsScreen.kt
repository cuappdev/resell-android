package com.cornellappdev.resell.android.ui.screens.reporting

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.Style.body2
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.report.ReportDetailsViewModel

@Composable
fun ReportDetailsScreen(
    reportDetailsViewModel: ReportDetailsViewModel = hiltViewModel()
) {
    val uiState = reportDetailsViewModel.collectUiStateValue()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Content(
            title = uiState.title,
            subtitle = uiState.reason,
            body = uiState.body,
            details = uiState.typedContent,
            onDetailsChanged = reportDetailsViewModel::onTypedContentChanged,
        )

        ResellTextButton(
            text = "Submit",
            state = uiState.buttonState,
            onClick = {
                reportDetailsViewModel.onSubmitPressed()
            },
            modifier = Modifier
                .padding(bottom = 46.dp)
        )
    }
}

@Preview
@Composable
private fun Content(
    title: String = "title",
    subtitle: String = "subtitle",
    body: String = "body",
    details: String = "details",
    onDetailsChanged: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = title,
            leftPainter = R.drawable.ic_chevron_left
        )
        
        Column(modifier = Modifier.defaultHorizontalPadding().padding(vertical = 36.dp)) {
            Text(
                text = "Explain what happened: ",
                style = title1,
            )

            Spacer(Modifier.height(16.dp))

            ResellTextEntry(
                text = details,
                onTextChange = onDetailsChanged,
                inlineLabel = false,
                multiLineHeight = 205.dp,
                singleLine = false,
                // TODO: probably wrong max lines
                maxLines = 20,
                placeholder = body,
                textFontStyle = body2,
            )

        }

    }
}
