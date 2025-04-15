package com.cornellappdev.resell.android.ui.screens.pdp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.BlurredEdgeTreatment.Companion.Rectangle
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cornellappdev.resell.android.R
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.components.global.ResellTextButton
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.components.main.ProfilePictureView
import com.cornellappdev.resell.android.ui.components.newpost.WhichPage
import com.cornellappdev.resell.android.ui.components.pdp.BookmarkFAB
import com.cornellappdev.resell.android.ui.theme.IconInactive
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.pdp.PostDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailPage(
    postDetailViewModel: PostDetailViewModel = hiltViewModel(),
) {
    val uiState = postDetailViewModel.collectUiStateValue()

    // Image will take up at most this proportion of the screen
    val imageProp = .75f
    val maxImageHeight = LocalConfiguration.current.screenHeightDp.dp * imageProp
    val minAspectRatio = uiState.minAspectRatio
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Preferred height of the tallest image, given the aspect ratio
    val aspectRatioPreferredHeight = screenWidth / minAspectRatio

    // Cap at the max image height.
    val imageHeight = if (aspectRatioPreferredHeight > maxImageHeight) {
        maxImageHeight
    } else {
        aspectRatioPreferredHeight
    }

    LaunchedEffect(uiState.hideSheetEvent) {
        uiState.hideSheetEvent?.consumeSuspend {
            // TODO: Making the bottom sheet hide is complicated... and sometimes it hides
            //  automatically anyways.
        }
    }

    Content(
        onContactClick = postDetailViewModel::onContactClick,
        onEllipseClick = postDetailViewModel::onEllipseClick,
        images = uiState.images,
        imageHeight = imageHeight,
        userPfp = uiState.profileImageUrl,
        username = uiState.username,
        title = uiState.title,
        price = uiState.price,
        description = uiState.description,
        onBookmarkClick = postDetailViewModel::onBookmarkClick,
        bookmarked = uiState.bookmarked,
        similarImageUrls = uiState.similarImageUrls,
        onSimilarClick = {
            postDetailViewModel.onSimilarPressed(it)
        },
        onUserClick = postDetailViewModel::onUserClick,
        contactButtonState = uiState.contactButtonState,
        showContact = uiState.showContact
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Content(
    imageHeight: Dp = 500.dp,
    images: List<ImageBitmap> = emptyList(),
    similarImageUrls: ResellApiResponse<List<String>> = ResellApiResponse.Pending,
    onContactClick: () -> Unit = {},
    onEllipseClick: () -> Unit = {},
    contactButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
    userPfp: String = "",
    username: String = "",
    title: String = "",
    price: String = "",
    description: String = "",
    bookmarked: Boolean = false,
    onBookmarkClick: () -> Unit = {},
    onSimilarClick: (Int) -> Unit = {},
    onUserClick: () -> Unit = {},
    showContact: Boolean = false,
) {
    var sheetHeightFromBottom by remember { mutableStateOf(0.dp) }
    val pagerState = rememberPagerState(pageCount = { images.size })

    // Derive peekHeight as screen height minus image height:
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // TODO the plus at the end seems wrong. Test on other devices.
    val peekHeight = screenHeight - imageHeight + 96.dp

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        BottomSheetScaffold(
            sheetContent = {
                BottomSheetContent(
                    profilePictureUrl = userPfp,
                    username = username,
                    title = title,
                    price = price,
                    description = description,
                    onHeightChanged = {
                        sheetHeightFromBottom = it
                    },
                    onSimilarClick = onSimilarClick,
                    similarImageUrls = similarImageUrls,
                    onUserClick = onUserClick
                )
            },
            sheetPeekHeight = peekHeight,
            sheetContainerColor = Color.White,
            sheetShadowElevation = 12.dp,
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(IconInactive),
            ) {
                Column(modifier = Modifier.fillMaxHeight()) {
                    PdpImageBlurredBackground(
                        imageHeight = imageHeight,
                        bitmap = images[it]
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Header {
            onEllipseClick()
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.White)))
                .height(64.dp)
        )

        if (showContact) {
            ResellTextButton(
                text = "Contact Seller",
                onClick = onContactClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 46.dp)
                    .navigationBarsPadding(),
                state = contactButtonState
            )
        }

        WhichPage(
            pagerState = pagerState,
            modifier = Modifier
                .padding(bottom = sheetHeightFromBottom)
                .align(Alignment.BottomCenter)
        )

        BookmarkFAB(
            selected = bookmarked,
            onClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .defaultHorizontalPadding()
                .padding(bottom = sheetHeightFromBottom)
        )
    }
}

@Composable
private fun PdpImageBlurredBackground(
    imageHeight: Dp,
    bitmap: ImageBitmap,
) {
    Box {
        Image(
            bitmap = bitmap,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .blur(
                    radius = 15.dp,
                    edgeTreatment = Rectangle
                ),
            contentScale = ContentScale.Crop
        )

        Image(
            bitmap = bitmap,
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
private fun Header(
    onEllipseClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .defaultHorizontalPadding()
            .systemBarsPadding()
    ) {
        Box {}

        Box(
            modifier = Modifier
                .size(24.dp)
                .clickableNoIndication {
                    onEllipseClick()
                },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ellipse),
                contentDescription = "Ellipsis",
                modifier = Modifier.align(Alignment.Center),
                tint = Color.White
            )
        }

    }
}

@Composable
private fun BottomSheetContent(
    title: String,
    price: String,
    description: String,
    profilePictureUrl: String,
    username: String,
    paddingTop: Dp = 116.dp,
    similarImageUrls: ResellApiResponse<List<String>>,
    onHeightChanged: (Dp) -> Unit,
    onSimilarClick: (Int) -> Unit,
    onUserClick: () -> Unit,
) {

    // Get screen height
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    // Calculate maximum height for the sheet content based on padding from top
    val maxSheetHeight = screenHeight - paddingTop

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(maxSheetHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultHorizontalPadding()
                .onGloballyPositioned { layoutCoordinates ->
                    val textPosition = layoutCoordinates.positionInRoot().y
                    val screenHeightPx = with(density) { screenHeight.toPx() }

                    // Calculate distance from bottom in px and convert to dp
                    val distanceFromBottomPx = screenHeightPx - textPosition
                    val textDistanceFromBottom = with(density) { distanceFromBottomPx.toDp() }

                    // Tell the parent that the height has changed.
                    onHeightChanged(textDistanceFromBottom + 120.dp)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = Style.heading2,
                modifier = Modifier.weight(.7f)
            )
            Text(
                text = price,
                style = Style.heading2,
                modifier = Modifier.weight(.3f),
                textAlign = TextAlign.End
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .defaultHorizontalPadding()
                .clickableNoIndication {
                    onUserClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePictureView(
                imageUrl = profilePictureUrl,
                modifier = Modifier
                    .size(31.dp)
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = username,
                style = Style.body2,
                color = Secondary
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = description,
            style = Style.body2,
            modifier = Modifier.defaultHorizontalPadding()
        )

        Spacer(Modifier.height(28.dp))

        similarImageUrls.ComposableIfSuccess {
            Text(
                text = "Similar Items",
                style = Style.body2,
                modifier = Modifier.defaultHorizontalPadding()
            )

            Spacer(Modifier.height(8.dp))

            SimilarItemsRow(
                images = it,
                onListingClick = { ind ->
                    onSimilarClick(ind)
                }
            )
        }
    }
}

@Composable
private fun SimilarItemsRow(
    images: List<String>,
    onListingClick: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .defaultHorizontalPadding()
    ) {
        images.forEachIndexed { index, imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickableNoIndication { onListingClick(index) }
                    .clip(RoundedCornerShape(15.dp))
                    .weight(1f)
                    .background(IconInactive),
                contentScale = ContentScale.Crop,
            )
        }

        // Fill in to reach 4 elements
        repeat(4 - images.size) {
            Spacer(Modifier.weight(1f))
        }
    }
}
