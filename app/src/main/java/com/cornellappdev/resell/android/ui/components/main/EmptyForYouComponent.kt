package com.cornellappdev.resell.android.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun EmptyForYouComponent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .border(width = 1.dp,color = Secondary, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You haven't saved any listings yet.",
            style = Style.body2,
            color = Secondary,
            textAlign = TextAlign.Center
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tap ",
                style = Style.body2,
                color = Secondary
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_save_nofill),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Secondary
            )
            Text(
                text = " on a listing to save.",
                style = Style.body2,
                color = Secondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyForYouComponentPreview() {
    EmptyForYouComponent()
}
