package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.theme.Padding
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.shimmer
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @param price The price of the listing, as $###.##.
 */
@Composable
fun ResellCard(
    imageUrl: String,
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    viewModel: ResellCardViewModel = hiltViewModel(),
    onClick: () -> Unit,
) {
    val image by viewModel.getImageUrlState(imageUrl)

    // Separated into a different function to allow `@Preview` despite the viewModel.
    ResellCardContent(
        modifier = modifier,
        onClick = onClick,
        image = image,
        title = title,
        price = price,
    )
}

@Composable
fun ResellCardContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    image: ResellApiResponse<ImageBitmap>,
    title: String,
    price: String,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = modifier
            .widthIn(max = 0.5f * screenWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .background(Color.White)
            .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        AnimatedClampedAsyncImage(
            image = image
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.small),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                style = Style.title3,
                text = title,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )
            Text(
                style = Style.title4,
                text = price
            )
        }
    }
}

@Composable
fun AnimatedClampedAsyncImage(
    image: ResellApiResponse<ImageBitmap>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.heightIn(min = 130.dp, max = 220.dp)) {
        AnimatedContent(targetState = image, label = "image loading") { response ->
            when (response) {
                ResellApiResponse.Pending -> {
                    Box(
                        modifier = Modifier
                            .height(175.dp)
                            .shimmer()
                            .fillMaxWidth()
                    )
                }

                ResellApiResponse.Error -> {
                    Box(
                        modifier = Modifier
                            .height(175.dp)
                            .background(
                                Secondary
                            )
                            .fillMaxWidth()
                    )
                }

                is ResellApiResponse.Success -> {
                    Image(
                        bitmap = response.data,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ResellCardPreview() = ResellPreview(
    backgroundColor = Color.Transparent
) {
    ResellCardContent(
        image = ResellApiResponse.Pending,
        title = "Title",
        price = "$10.00",
        onClick = {},
    )

    Spacer(modifier = Modifier.padding(Padding.large))

    ResellCardContent(
        image = ResellApiResponse.Error,
        title = "Richie man",
        price = "$999.99",
        onClick = {},
    )

    Spacer(modifier = Modifier.padding(Padding.large))

    ResellCardContent(
        image = ResellApiResponse.Success(ImageBitmap(1, 1)),
        title = "Richie man with a damn long listing name",
        price = "$999.99",
        onClick = {},
    )
}

// Semantics to be able to call this function in the composable...

@HiltViewModel
class ResellCardViewModel @Inject constructor(
    private val coilRepository: CoilRepository
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {

    fun getImageUrlState(imageUrl: String) = coilRepository.getUrlState(imageUrl)
}
