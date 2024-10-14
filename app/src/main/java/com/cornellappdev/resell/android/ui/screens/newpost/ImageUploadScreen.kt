package com.cornellappdev.resell.android.ui.screens.newpost

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.ui.components.global.ResellHeader
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.main.PostFloatingActionButton
import com.cornellappdev.resell.android.ui.components.newpost.WhichPage
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.util.isBottomLeftMoreBlack
import com.cornellappdev.resell.android.util.singlePhotoPicker
import com.cornellappdev.resell.android.viewmodel.newpost.ImageUploadViewModel

@Composable
fun ImageUploadScreen(
    imageUploadViewModel: ImageUploadViewModel = hiltViewModel()
) {
    val uiState = imageUploadViewModel.collectUiStateValue()
    val bitmaps = uiState.images

    val singlePhotoPicker = singlePhotoPicker {
        if (it != null) {
            imageUploadViewModel.onImageSelected(it)
        } else {
            imageUploadViewModel.onImageLoadFail()
        }
    }

    LaunchedEffect(uiState.launchPhotoPicker) {
        uiState.launchPhotoPicker?.consumeSuspend {
            singlePhotoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResellHeader(
            title = "New Listing",
            rightPainter = if (uiState.canAddImages) R.drawable.ic_plus else null,
            onRightClick = {
                imageUploadViewModel.onAddPressed()
            }
        )

        Spacer(modifier = Modifier.weight(if (bitmaps.isNotEmpty()) 0.3f else 1f))

        SelectionBody(
            pagerState = rememberPagerState {
                (bitmaps.size + 1).coerceAtMost(maximumValue = 9)
            },
            bitmaps = bitmaps,
            onDelete = { imageUploadViewModel.onDelete(it) },
            onAdd = { imageUploadViewModel.onAddPressed() }
        )

        Spacer(modifier = Modifier.weight(1f))

        ResellTextButton(
            text = uiState.buttonText,
            onClick = { imageUploadViewModel.onButtonPressed() },
            modifier = Modifier.padding(bottom = 48.dp)
        )
    }
}

@Composable
private fun SelectionBody(
    pagerState: PagerState,
    bitmaps: List<ImageBitmap>,
    onDelete: (Int) -> Unit,
    onAdd: () -> Unit,
) {
    // Screen width in dp

    if (bitmaps.isNotEmpty()) {
        Column {
            Text(
                text = "Image Upload",
                style = Style.title1,
                modifier = Modifier.defaultHorizontalPadding(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                snapPosition = SnapPosition.Center
            ) { page ->
                if (page < bitmaps.size) {
                    ImageDisplay(bitmap = bitmaps[page],
                        onDelete = { onDelete(page) }
                    )
                } else {
                    AddPage(
                        onAddPressed = onAdd
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            WhichPage(
                pagerState = pagerState,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Image Upload",
                style = Style.heading2,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(19.dp))

            Text(
                text = "Add images of your item to get started with a new listing",
                style = Style.body1,
                color = Secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.defaultHorizontalPadding()
            )
        }
    }
}

@Composable
private fun ImageDisplay(
    bitmap: ImageBitmap,
    onDelete: () -> Unit,
) {
    // TODO
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Box {
        Row {
            Spacer(modifier = Modifier.width(24.dp))
            Image(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .width(screenWidth - 48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentDescription = null,
                bitmap = bitmap,
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_trash),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 46.dp, bottom = 22.dp)
                .size(30.dp)
                .clickableNoIndication { onDelete() },
            contentDescription = "trash",
            // This... kinda works... sometimes.
            tint = if (isBottomLeftMoreBlack(bitmap)) Color.White else Color.Black
        )
    }
}

@Composable
private fun AddPage(
    onAddPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PostFloatingActionButton(
            onClick = {
                onAddPressed()
            },
            expanded = false,
        )
    }
}
