package com.cornellappdev.resell.android.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.components.onboarding.BlurBlob
import com.cornellappdev.resell.android.ui.components.onboarding.Corner
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.LoginBlurBrushEnd
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.closeApp
import com.cornellappdev.resell.android.viewmodel.onboarding.LandingViewModel
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(
    landingViewModel: LandingViewModel = hiltViewModel(),
) {
    val state = landingViewModel.collectUiStateValue()
    val resultLauncher = landingViewModel.makeSignInLauncher()
    val context = LocalContext.current

    BackHandler {
        closeApp(context)
    }

    LaunchedEffect(Unit) {
        landingViewModel.navigateIfLoggedIn()
        delay(1000)
        landingViewModel.showButton()
    }

    BlobbedContent(
        showButton = state.showButton,
        buttonState = state.buttonState,
        onSignInPressed = {
            resultLauncher.launch(1)
            landingViewModel.onSignInClick()
        },
    )
}

@Preview(apiLevel = 34)
@Composable
private fun BlobbedContent(
    showButton: Boolean = false,
    buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
    onSignInPressed: () -> Unit = {},
) {
    Box(modifier = Modifier.background(Color.White)) {
        BlurBlob(
            modifier = Modifier
                .align(Alignment.BottomStart),
            corner = Corner.BOTTOM_LEFT,
        )
        BlurBlob(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            corner = Corner.BOTTOM_RIGHT,
            brush = LoginBlurBrushEnd,
        )
        LandingContent(
            showButton = showButton,
            buttonState = buttonState,
            onSignInPressed = onSignInPressed,
        )
    }
}

@Composable
private fun LandingContent(
    showButton: Boolean = false,
    buttonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
    onSignInPressed: () -> Unit,
) {
    val buttonAlpha = animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        label = "button",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(2f))
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

        Box(
            modifier = Modifier
                .padding(bottom = 46.dp)
                .heightIn(min = 48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(1 - buttonAlpha.value)
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
                )
            }

            if (showButton) {
                ResellTextButton(
                    text = "Login with NetID",
                    onClick = { onSignInPressed() },
                    modifier = Modifier
                        .alpha(buttonAlpha.value)
                        .align(Alignment.Center),
                    state = buttonState,
                )
            }
        }
    }
}
