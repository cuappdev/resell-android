package com.cornellappdev.resell.android.ui.screens.reporting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.BrushIcon
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.animateResellBrush
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.report.ReportConfirmationViewModel

@Composable
fun ReportConfirmationScreen(
    reportConfirmationViewModel: ReportConfirmationViewModel = hiltViewModel()
) {
    val uiState = reportConfirmationViewModel.collectUiStateValue()

    Content(
        headerTitle = uiState.headerTitle,
        title = uiState.title,
        body = uiState.body,
        blockText = uiState.blockText,
        blockButton = uiState.blockButton,
        onBlock = reportConfirmationViewModel::onBlockPressed,
        onDone = reportConfirmationViewModel::onDonePressed
    )
}

@Preview
@Composable
private fun Content(
    headerTitle: String = "headerTitle",
    title: String = "title",
    body: String = "body",
    blockText: String = "block",
    blockButton: String = "block them",
    onBlock: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    val resellBrush = animateResellBrush(targetGradient = true, start = Offset(100f, 100f))
    val checkBrush = animateResellBrush(targetGradient = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = headerTitle,
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            shape = CircleShape,
            border = BorderStroke(width = 3.dp, brush = resellBrush),
            modifier = Modifier
                .size(89.dp)
                .clickableNoIndication { },
            color = Color.White,
        ) {
            Box(modifier = Modifier.size(46.dp)) {
                BrushIcon(
                    painter = painterResource(id = R.drawable.ic_check),
                    brush = checkBrush,
                    contentDescription = "exit",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = title,
            style = Style.heading2,
            modifier = Modifier.defaultHorizontalPadding(2f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = body,
            style = Style.body2,
            modifier = Modifier.defaultHorizontalPadding(2f),
            textAlign = TextAlign.Center,
            color = AppDev
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = blockText,
            style = Style.heading3,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ResellTextButton(
            text = blockButton,
            onClick = onBlock,
            containerType = ResellTextButtonContainer.SECONDARY_RED
        )

        Spacer(modifier = Modifier.height(12.dp))

        ResellTextButton(
            text = "Done",
            onClick = onDone,
            containerType = ResellTextButtonContainer.PRIMARY
        )

        Spacer(modifier = Modifier.height(45.dp))
    }
}
