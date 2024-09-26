package com.cornellappdev.resell.android.ui.screens.onboarding

import android.graphics.Bitmap
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.util.singlePhotoPicker
import com.cornellappdev.resell.android.viewmodel.onboarding.SetupViewModel

@Composable
fun SetupScreen(
    setupViewModel: SetupViewModel = hiltViewModel(),
) {
    val uiState = setupViewModel.collectUiStateValue()
    val singlePhotoPicker = singlePhotoPicker {
        if (it != null) {
            setupViewModel.onImageSelected(it)
        } else {
            setupViewModel.onImageLoadFail()
        }
    }

    Box(
        modifier = Modifier
            .background(
                color = Color.White
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            PhotoHeader(
                imageBitmap = uiState.imageBitmap,
                onImageTapped = {
                    singlePhotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
            TextEntry(
                username = uiState.username,
                bio = uiState.bio,
                onUsernameChanged = setupViewModel::onUsernameChanged,
                onBioChanged = setupViewModel::onBioChanged,
                errors = uiState.errors,
            )
            EULARadio(
                selected = uiState.checkedEULA,
                onEULATapped = setupViewModel::onEULAChanged
            )
        }

        ResellTextButton(
            text = "Next", onClick = setupViewModel::onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(46.dp),
            state = uiState.buttonState,
        )
    }
}

@Preview
@Composable
private fun PhotoHeader(
    imageBitmap: Bitmap? = null,
    onImageTapped: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set up your profile",
            style = Style.heading3,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Box(modifier = Modifier.padding(top = 16.dp, bottom = 40.dp)) {
            AnimatedContent(
                targetState = imageBitmap,
                label = "pfp"
            ) { bitmap ->
                if (bitmap != null) {
                    AsyncImage(
                        model = bitmap,
                        contentDescription = "profile picture",
                        modifier = Modifier
                            .size(132.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_empty_pfp),
                        contentDescription = "profile picture",
                        modifier = Modifier
                            .size(132.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) {
                                onImageTapped()
                            },
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_edit_pfp),
                contentDescription = "camera",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onImageTapped()
                    }
                    .shadow(1.dp, CircleShape)
            )
        }
    }
}

@Preview
@Composable
private fun TextEntry(
    username: String = "",
    bio: String = "",
    errors: List<String> = emptyList(),
    onUsernameChanged: (String) -> Unit = {},
    onBioChanged: (String) -> Unit = {},
) {

    Column(
        modifier = Modifier
            .defaultHorizontalPadding()
            .fillMaxWidth()
    ) {

        ResellTextEntry(
            label = "Username*",
            text = username,
            onTextChange = onUsernameChanged,
            inlineLabel = false,
        )

        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = errors,
            label = "errors"
        ) { errors ->
            Column {
                errors.forEach { error ->
                    Text(
                        text = error,
                        style = Style.subtitle1,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        ResellTextEntry(
            label = "Bio",
            text = bio,
            onTextChange = onBioChanged,
            inlineLabel = false,
            singleLine = false,
            maxLines = 3,
        )

        Spacer(Modifier.height(24.dp))

    }
}

@Composable
private fun EULARadio(
    selected: Boolean = false,
    onEULATapped: (Boolean) -> Unit = {},
) {
    val radiusAnimated = animateFloatAsState(
        targetValue = if (selected) 20f else 0f,
        label = "radio animate"
    )

    val annotatedText = buildAnnotatedString {
        append("I agree to Resell's ")

        // Annotating the clickable part
        pushLink(
            LinkAnnotation.Url(
                url = "https://www.cornellappdev.com/license/resell"
            )
        )
        withStyle(style = SpanStyle(color = ResellPurple)) {
            append("End User License Agreement")
        }
        pop() // End of the clickable part
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Surface(
                shape = CircleShape,
                modifier = Modifier
                    .size(radiusAnimated.value.dp)
                    .align(Alignment.Center),
                color = ResellPurple,
            ) {}

            Surface(
                shape = CircleShape,
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onEULATapped(!selected)
                    },
                border = BorderStroke(2.5.dp, ResellPurple),
                color = Color.Transparent
            ) {}
        }

        Spacer(Modifier.widthIn(min = 16.dp))

        Text(
            text = annotatedText,
            style = Style.title4,
        )
    }
}

@Preview
@Composable
private fun EULARadioPreview() {
    var selected by remember { mutableStateOf(false) }
    EULARadio(
        selected = selected,
        onEULATapped = {
            selected = !selected
        },
    )
}
