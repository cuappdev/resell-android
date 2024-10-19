package com.cornellappdev.resell.android.ui.screens.reporting

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
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.report.ReportRow
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.viewmodel.report.ReportReasonViewModel

@Composable
fun ReportReasonScreen(
    reportReasonViewModel: ReportReasonViewModel = hiltViewModel()
) {
    val uiState = reportReasonViewModel.collectUiStateValue()

    Content(
        title = uiState.title,
        subtitle = uiState.subtitle,
        reasons = uiState.reasons,
        onReasonSelected = reportReasonViewModel::onReasonPressed
    )
}


@Preview
@Composable
private fun Content(
    title: String = "title",
    subtitle: String = "subtitle",
    reasons: List<String> = listOf("reason1", "reason2"),
    onReasonSelected: (String) -> Unit = {}
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
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = subtitle,
            style = title1,
        )

        Spacer(Modifier.height(16.dp))

        reasons.forEach {
            ReportRow(
                text = it
            ) {
                onReasonSelected(it)
            }
        }
    }
}
