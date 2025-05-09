package com.cornellappdev.resell.android.ui.components.chat.messages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview

@Composable
fun MessagesScrollLoading(
    modifier: Modifier = Modifier,
    count: Int,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            bottom = 100.dp,
        ),
        modifier = modifier,
    ) {
        items(count = count) {
            MessageCardLoading()
        }
    }
}

@Preview
@Composable
private fun MessagesScrollLoadingPreview() = ResellPreview {
    MessagesScrollLoading(count = 3)
}
