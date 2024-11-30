package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication

@Composable
fun ChatAvailability(sender: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .width(284.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickableNoIndication {
                    onClick()
                }
                .background(ResellPurple.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$sender's Availability",
                style = Style.title3.copy(color = ResellPurple),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "chevron",
                tint = ResellPurple,
                modifier = Modifier
                    .size(24.dp)
                    .scale(-1f)
            )
        }
    }
}

@Preview
@Composable
fun ChatAvailabilityPreview() = ResellPreview(padding = 8.dp) {
    ChatAvailability("Lia") {}
    ChatAvailability("My main man his name is richie") {}
}
