package com.cornellappdev.resell.android.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun ProfileEmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 58.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            style = Style.heading2,
            text = title,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = Style.body2,
            text = body,
            color = Secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun ProfileEmptyStatePreview() = ResellPreview {
    ProfileEmptyState(
        title = "No active listings",
        body = "When you post a listing, it will be displayed here",
    )
}
