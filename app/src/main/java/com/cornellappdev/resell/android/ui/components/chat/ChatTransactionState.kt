package com.cornellappdev.resell.android.ui.components.chat


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication


// added transaction state
@Composable
fun ChatTransactionState(
    text: String,
    enabled: Boolean,
    actionText: String?,
    onActionTextClicked: () -> Unit = {},
    icon: Painter? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = "chat state icon",
                    modifier = Modifier
                        .size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = Style.body2.copy(fontSize = 14.sp),
            )
        }

        if (actionText != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = actionText,
                style = Style.title3.copy(color = ResellPurple),
                modifier = Modifier
                    .clickableNoIndication { if (enabled) onActionTextClicked() }
                    .alpha(if (enabled) 1f else 0.5f)
            )
        }
    }
}

@Preview
@Composable
fun ChatTransactionStatePreview() {
    ChatTransactionState(
        text = "This transaction has been completed",
        enabled = true,
        actionText = "Leave Review",
        icon = painterResource(id = R.drawable.ic_bag),
        modifier = Modifier.background(color = Color.White)
    )
}