package com.cornellappdev.resell.android.ui.components.newpost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Style.body1
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.ui.theme.rubikFamily
import com.cornellappdev.resell.android.util.clickableNoIndication

@Composable
fun MoneyEntry(
    text: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    onPressed: () -> Unit,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = title1,
                modifier = Modifier.padding(
                    bottom = 8.dp
                ),
            )
        }
        Row(
            modifier = Modifier
                .widthIn(min = 142.dp)
                .height(39.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Wash)
                .clickableNoIndication { onPressed() },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp,
                )
            ) {
                Text(
                    text = "$",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = rubikFamily,
                        fontWeight = FontWeight(500),
                        color = AppDev,
                    ).plus(Style.noHeight)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    style = body1.plus(Style.noHeight),
                )
            }
        }
    }
}

@Preview
@Composable
private fun MoneyEntryPreview() =
    ResellPreview { MoneyEntry(text = "123.00", onPressed = {}, label = "Price") }
