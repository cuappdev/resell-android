package com.cornellappdev.resell.android.ui.components.global.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun TwoButtonSheet(
    viewModel: TwoButtonSheetViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectUiStateValue()

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiState.title,
            style = Style.heading3,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = uiState.description,
            style = Style.body1,
            textAlign = uiState.textAlign,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        ResellTextButton(
            text = uiState.primaryText,
            modifier = Modifier.width(200.dp),
            onClick = uiState.primaryCallback,
            state = uiState.primaryButtonState,
            containerType = uiState.primaryContainerType
        )

        Spacer(Modifier.height(12.dp))

        ResellTextButton(
            text = uiState.secondaryText,
            modifier = Modifier.width(200.dp),
            onClick = uiState.secondaryCallback,
            state = uiState.secondaryButtonState,
            containerType = uiState.secondaryContainerType
        )

        Spacer(Modifier.height(45.dp))
    }

}
