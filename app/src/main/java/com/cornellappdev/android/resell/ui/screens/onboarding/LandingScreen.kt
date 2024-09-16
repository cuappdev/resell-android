package com.cornellappdev.android.resell.ui.screens.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.resell.R
import com.cornellappdev.android.resell.ui.components.global.ResellTextButton
import com.cornellappdev.android.resell.ui.components.global.ResellTextButtonState
import com.cornellappdev.android.resell.ui.components.onboarding.BlurBlob
import com.cornellappdev.android.resell.ui.components.onboarding.Corner
import com.cornellappdev.android.resell.ui.theme.AppDev
import com.cornellappdev.android.resell.ui.theme.Style
import kotlinx.coroutines.delay

@Composable
fun LandingScreen() {
    var showButton by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1000)
        showButton = true
    }

    BlobbedContent(showButton)
}

@Preview(apiLevel = 34)
@Composable
fun BlobbedContent(
    showButton: Boolean = false,
    buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
) {
    Box {
        LandingContent(
            showButton = showButton,
            buttonState = buttonState,
        )
        BlurBlob(
            modifier = Modifier
                .align(Alignment.BottomStart),
            corner = Corner.BOTTOM_LEFT,
        )
        BlurBlob(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            corner = Corner.BOTTOM_RIGHT
        )
    }
}

@Composable
private fun LandingContent(
    showButton: Boolean = false,
    buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
) {
    val buttonAlpha = animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        label = "button",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_resell),
                contentDescription = null,
                modifier = Modifier.size(width = 96.dp, height = 130.dp)
            )

            Text(
                text = "resell",
                fontSize = 48.sp,
                style = Style.resellLogo,
            )
        }
        Spacer(modifier = Modifier.weight(3f))

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_appdev),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 6.5.dp)
                        .size(24.dp),
                    tint = AppDev,
                )

                Text(
                    text = "CornellAppDev",
                    style = Style.appDev,
                    modifier = Modifier.alpha(1 - buttonAlpha.value),
                )
            }

            ResellTextButton(
                text = "Login with NetID",
                onClick = { /*TODO*/ },
                modifier = Modifier.alpha(buttonAlpha.value),
                state = buttonState,
            )

        }

        Spacer(modifier = Modifier.weight(.5f))
    }
}
