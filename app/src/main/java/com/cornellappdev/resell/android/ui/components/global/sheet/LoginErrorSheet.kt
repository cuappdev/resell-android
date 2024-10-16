package com.cornellappdev.resell.android.ui.components.global.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun LoginErrorSheet(
    text: String = "Please sign in with\na Cornell email",
    onTryAgainClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(top = 24.dp, bottom = 36.dp),
            style = Style.heading3,
            textAlign = TextAlign.Center
        )

        ResellTextButton(
            text = "Try Again", onClick = onTryAgainClicked,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}
