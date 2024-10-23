package com.cornellappdev.resell.android.ui.components.global.sheet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.rubikFamily
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding

@Composable
fun ChatMeetingSheet(
    chatMeetingSheetViewModel: ChatMeetingSheetViewModel = hiltViewModel(),
) {
    val uiState = chatMeetingSheetViewModel.collectUiStateValue()

    ChatMeetingSheetContent(
        uiState = uiState,
        onConfirmPressed = chatMeetingSheetViewModel::onConfirmPressed,
        onClosePressed = chatMeetingSheetViewModel::onClosePressed,
    )
}

@Composable
private fun ChatMeetingSheetContent(
    uiState: ChatMeetingSheetViewModel.ChatMeetingStateUi,
    onConfirmPressed: () -> Unit,
    onClosePressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultHorizontalPadding()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(15.dp))

        Text(
            text = uiState.title,
            style = Style.heading3,
            modifier = Modifier.defaultHorizontalPadding(),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(15.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            uiState.content()
        }


        ResellTextButton(
            text = uiState.confirmText,
            onClick = onConfirmPressed,
            state = ResellTextButtonState.ENABLED,
            containerType = uiState.confirmColor
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = uiState.closeText,
            style = Style.title2.copy(fontSize = 18.sp),
            color = Color.Gray,
            modifier = Modifier
                .clickableNoIndication { onClosePressed() }
        )

        Spacer(Modifier.height(46.dp))
    }
}

@Composable
private fun RowScope.EntryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit = {}
) {
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.4f,
        label = "alpha"
    )
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        modifier = Modifier
            .weight(1f)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onClick()
                    }
                } else {
                    Modifier
                        .clickableNoIndication { }
                }
            ),
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = rubikFamily,
                fontWeight = FontWeight(500),
                color = Color.Black,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .alpha(alpha)
        )
    }
}


@Preview
@Composable
private fun SheetContentPreview() = ResellPreview {
    ChatMeetingSheetContent(
        uiState = ChatMeetingSheetViewModel.ChatMeetingStateUi(
            title = "Preview",
            content = {
                Text(
                    text = "Meeting with Lia for Blue Pants confirmed for",
                    style = Style.title4,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Time",
                    style = Style.title3
                )
                Text(
                    text = "Friday, October 23 - 1:30-2:00 PM",
                    style = Style.title4
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "For safety, make sure to meet up in a public space on campus",
                    style = Style.title4
                )

                Spacer(Modifier.height(32.dp))
            }
        ),
        onConfirmPressed = { },
        onClosePressed = { },
    )
}
