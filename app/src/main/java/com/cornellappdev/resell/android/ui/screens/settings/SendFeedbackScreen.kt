package com.cornellappdev.resell.android.ui.screens.settings

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.ui.components.global.ResellHeaderCore
import com.cornellappdev.resell.android.ui.components.global.ResellTextEntry
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.Style.body1
import com.cornellappdev.resell.android.ui.theme.Style.title1
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.util.singlePhotoPicker
import com.cornellappdev.resell.android.viewmodel.settings.SendFeedbackViewModel

@Composable
fun SendFeedbackScreen(
    sendFeedbackViewModel: SendFeedbackViewModel = hiltViewModel(),
) {
    val uiState = sendFeedbackViewModel.collectUiStateValue()

    Content(
        description = uiState.feedback,
        onDescriptionChanged = sendFeedbackViewModel::onDescriptionChanged,
        onImageUploaded = sendFeedbackViewModel::onImageAdded,
        onImageFailed = sendFeedbackViewModel::onImageFailed,
        onImageDelete = sendFeedbackViewModel::onImageDelete,
        onSubmit = sendFeedbackViewModel::onFeedbackSubmit,
        bitmaps = uiState.images,
        canSubmit = uiState.canSubmit
    )
}

@Preview
@Composable
private fun Content(
    description: String = "",
    canSubmit: Boolean = false,
    onDescriptionChanged: (String) -> Unit = {},
    onImageUploaded: (Uri) -> Unit = {},
    onImageFailed: () -> Unit = {},
    onImageDelete: (Int) -> Unit = {},
    onSubmit: () -> Unit = {},
    bitmaps: List<ImageBitmap> = emptyList()
) {
    val singlePhotoPicker = singlePhotoPicker {
        if (it != null) {
            onImageUploaded(it)
        } else {
            onImageFailed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeaderCore(
            title = "Send Feedback",
            rightContent = {
                Text(
                    text = "Submit",
                    color = ResellPurple.copy(
                        alpha = if (canSubmit) 1f else 0.4f
                    ),
                    style = title1
                )
            },
            onRightClick = {
                if (canSubmit) {
                    onSubmit()
                }
            }
        )

        Spacer(Modifier.height(40.dp))

        Text(
            text = "Thanks for using Resell! We appreciate any feedback to improve your experience.",
            style = body1,
            textAlign = TextAlign.Center,
            modifier = Modifier.defaultHorizontalPadding()
        )

        Spacer(Modifier.height(8.dp))

        ResellTextEntry(
            text = description,
            onTextChange = onDescriptionChanged,
            inlineLabel = false,
            multiLineHeight = 255.dp,
            singleLine = false,
            // TODO: probably wrong max lines
            maxLines = 20,
            modifier = Modifier.defaultHorizontalPadding(),
            placeholder = "enter feedback details..."
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Image Upload",
            style = title1,
            modifier = Modifier
                .defaultHorizontalPadding()
                .fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.defaultHorizontalPadding(),
            verticalAlignment = Alignment.Bottom,
        ) {
            bitmaps.forEachIndexed { i, bitmap ->
                ImageUploadedCard(
                    bitmap = bitmap,
                    onDelete = { onImageDelete(bitmaps.indexOf(bitmap)) },
                    modifier = Modifier.weight(1f)
                )

                if (i != 2) {
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }

            if (bitmaps.size < 3) {
                AddImageCard(
                    modifier = Modifier.weight(1f),
                    onAddPressed = {
                        singlePhotoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )

                if (bitmaps.size != 2) {
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }

            for (i in 0 until 2 - bitmaps.size) {
                Box(modifier = Modifier.weight(1f)) {}

                if (i != 1 - bitmaps.size) {
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }
        }
    }
}

@Composable
private fun ImageUploadedCard(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap,
    onDelete: () -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        Column {
            Spacer(Modifier.height(8.dp))

            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .shadow(10.dp, CircleShape)
                .background(Wash)
                .clickableNoIndication {
                    onDelete()
                },
        ) {
            Text(
                text = "-",
                style = title1,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun AddImageCard(
    modifier: Modifier = Modifier,
    onAddPressed: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Wash)
            .clickableNoIndication(onClick = onAddPressed)
            .padding(16.dp)
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .shadow(10.dp, CircleShape)
                .background(Color.White),
        ) {
            Text(
                text = "+",
                style = title1,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
