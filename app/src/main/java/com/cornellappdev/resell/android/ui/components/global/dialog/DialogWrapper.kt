package com.cornellappdev.resell.android.ui.components.global.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.util.clickableNoIndication

@Composable
fun DialogWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp)
            .clickableNoIndication { },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 14.dp)
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun DialogWrapperPreview() = ResellPreview(
    backgroundColor = Color.Black
) {
    DialogWrapper {
        TwoButtonDialog(
            title = "Title",
            description = "Description",
            primaryButtonText = "Primary",
            secondaryButtonText = "Secondary",
            onPrimaryButtonClick = {},
            onSecondaryButtonClick = {}
        )
    }
}
