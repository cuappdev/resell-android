package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style.body1
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.util.defaultHorizontalPadding

@Composable
fun ResellInfoRow(
    modifier: Modifier = Modifier,
    title: String,
    content: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = title1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
        Text(
            text = content,
            style = body1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

@Preview
@Composable
private fun ResellInfoRowPreview() = ResellPreview {
    ResellInfoRow(
        title = "Title",
        content = "Content",
        modifier = Modifier.defaultHorizontalPadding()
    )
    Spacer(modifier = Modifier.height(24.dp))
    ResellInfoRow(
        title = "Breh",
        content = "Man this is some long text and this is the content",
        modifier = Modifier.defaultHorizontalPadding()
    )
    Spacer(modifier = Modifier.height(24.dp))
    ResellInfoRow(
        title = "Man this is some long title and this is the title",
        content = "content",
        modifier = Modifier.defaultHorizontalPadding()
    )
}
