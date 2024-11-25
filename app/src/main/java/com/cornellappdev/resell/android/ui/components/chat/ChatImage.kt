package com.cornellappdev.resell.android.ui.components.chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.ui.theme.Secondary
import com.cornellappdev.resell.android.ui.theme.Stroke
import com.cornellappdev.resell.android.ui.theme.Wash
import com.cornellappdev.resell.android.ui.theme.interpolateColorHSV
import com.cornellappdev.resell.android.util.LocalInfiniteLoading
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun MessageImage(
    imageUrl: String,
    messageImageViewModel: MessageImageViewModel = hiltViewModel()
) {
    // Screen Width
    val width = LocalConfiguration.current.screenWidthDp.dp

    val image by messageImageViewModel.getImageUrlState(imageUrl)

    Box(
        modifier = Modifier
            .heightIn(min = 130.dp, max = 220.dp)
            .widthIn(min = 130.dp, max = width / 2f)
    ) {
        AnimatedContent(targetState = image, label = "image loading") { response ->
            when (response) {
                ResellApiResponse.Pending -> {
                    Box(
                        modifier = Modifier
                            .height(175.dp)
                            .background(
                                interpolateColorHSV(
                                    Wash,
                                    Stroke,
                                    LocalInfiniteLoading.current
                                )
                            )
                            .fillMaxWidth()
                    )
                }

                ResellApiResponse.Error -> {
                    Box(
                        modifier = Modifier
                            .height(175.dp)
                            .background(Secondary)
                            .fillMaxWidth()
                    )
                }

                is ResellApiResponse.Success -> {
                    Image(
                        bitmap = response.data,
                        contentDescription = null,
                        modifier = Modifier
                            .heightIn(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .widthIn(max = width / 2f),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@HiltViewModel
class MessageImageViewModel @Inject constructor(
    private val coilRepository: CoilRepository
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {

    fun getImageUrlState(imageUrl: String) = coilRepository.getUrlState(imageUrl)
}
