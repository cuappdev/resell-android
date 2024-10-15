package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.Primary
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Warning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ResellTextButtonState {
    ENABLED, DISABLED, SPINNING
}

enum class ResellTextButtonContainer(
    val color: Color,
    val textColor: Color,
    val borderStroke: BorderStroke? = null,
) {
    PRIMARY(
        color = ResellPurple,
        textColor = Color.White
    ),
    SECONDARY(
        color = Color.White,
        borderStroke = BorderStroke(1.5.dp, ResellPurple),
        textColor = ResellPurple
    ),
    PRIMARY_RED(
        color = Warning,
        textColor = Color.White,
    ),
    SECONDARY_RED(
        color = Color.White,
        textColor = Warning,
        borderStroke = BorderStroke(1.5.dp, Warning),
    ),
    NAKED(
        color = Color.White,
        textColor = ResellPurple,
    ),
    NAKED_RED(
        color = Color.White,
        textColor = Warning,
    ),
    NAKED_PRIMARY(
        color = Color.White,
        textColor = Primary,
    )
}

@Composable
fun ResellTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: ResellTextButtonState = ResellTextButtonState.ENABLED,
    containerType: ResellTextButtonContainer = ResellTextButtonContainer.PRIMARY,
) {

    // Delay spinning response by some millis to improve animation response
    var spinningDelayedState by remember { mutableStateOf(state) }
    LaunchedEffect(state) {
        if (state == ResellTextButtonState.SPINNING
            || spinningDelayedState == ResellTextButtonState.SPINNING
        ) {
            delay(200)
        }
        spinningDelayedState = state
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .widthIn(min = 200.dp)
            .alpha(if (state == ResellTextButtonState.ENABLED) 1f else 0.4f),
        enabled = state == ResellTextButtonState.ENABLED,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerType.color,
            disabledContainerColor = containerType.color,
        ),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 28.dp),
        border = containerType.borderStroke,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedContent(
                targetState = spinningDelayedState,
                label = "Spinner"
            ) { state ->
                if (state == ResellTextButtonState.SPINNING) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(20.dp),
                        color = containerType.textColor,
                        strokeWidth = 2.dp,
                    )
                }
            }
            Text(
                text = text,
                style = Style.title1,
                color = containerType.textColor,
            )
        }
    }
}

@Composable
private fun ResellTextButtonPreview(
    containerType: ResellTextButtonContainer = ResellTextButtonContainer.PRIMARY,
) {
    var spinning by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Text(text = containerType.name, style = Style.resellLogo)

        Spacer(modifier = Modifier.height(16.dp))

        ResellTextButton(text = "Button", onClick = {}, containerType = containerType)

        Spacer(modifier = Modifier.height(16.dp))

        ResellTextButton(
            text = "Really really really long text wow!",
            onClick = {},
            containerType = containerType
        )

        Spacer(modifier = Modifier.height(16.dp))

        ResellTextButton(
            text = "Disabled",
            onClick = {},
            state = ResellTextButtonState.DISABLED,
            containerType = containerType
        )

        Spacer(modifier = Modifier.height(16.dp))

        ResellTextButton(
            text = "Spinner",
            onClick = {},
            state = ResellTextButtonState.SPINNING,
            containerType = containerType
        )

        Spacer(modifier = Modifier.height(16.dp))

        ResellTextButton(
            text = "Spinner (Press me!)",
            containerType = containerType,
            onClick = {
                spinning = true
                enabled = false
                coroutineScope.launch {
                    delay(2000)
                    spinning = false
                    enabled = true
                }
            },
            state = if (spinning) ResellTextButtonState.SPINNING else ResellTextButtonState.ENABLED
        )
    }
}

@Preview
@Composable
private fun ResellPrimaryTextButtonPreview() {
    ResellTextButtonPreview()
}

@Preview
@Composable
private fun ResellSecondaryTextButtonPreview() {
    ResellTextButtonPreview(containerType = ResellTextButtonContainer.SECONDARY)
}

@Preview
@Composable
private fun ResellPrimaryRedTextButtonPreview() {
    ResellTextButtonPreview(containerType = ResellTextButtonContainer.PRIMARY_RED)
}

@Preview
@Composable
private fun ResellSecondaryRedTextButtonPreview() {
    ResellTextButtonPreview(containerType = ResellTextButtonContainer.SECONDARY_RED)
}

@Preview
@Composable
private fun ResellSecondaryNakedButtonPreview() {
    ResellTextButtonPreview(containerType = ResellTextButtonContainer.NAKED)
}

@Preview
@Composable
private fun ResellSecondaryNakedRedTextButtonPreview() {
    ResellTextButtonPreview(containerType = ResellTextButtonContainer.NAKED_RED)
}

@Preview
@Composable
private fun ResellPrimaryNakedButtonPreview() {
    ResellTextButtonPreview(containerType = ResellTextButtonContainer.NAKED_PRIMARY)
}
