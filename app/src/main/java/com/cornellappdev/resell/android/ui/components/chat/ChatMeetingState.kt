package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style

@Composable
fun MessageMeetingState(sender: String, denied: Boolean, propsal: Boolean) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(if (denied) 0.5f else 1f)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        when (propsal) {
            true -> {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_slash),
                        contentDescription = "calendar",
                        modifier = Modifier
                            .size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lia has cancelled the meeting",
                        style = Style.body2.copy(fontSize = 14.sp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "Send Another Proposal",
                        style = Style.title3.copy(color = ResellPurple)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            false -> {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "calendar",
                        modifier = Modifier
                            .size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lia has confirmed the meeting",
                        style = Style.body2.copy(fontSize = 14.sp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "View Details",
                        style = Style.title3.copy(color = ResellPurple)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

    }
}
