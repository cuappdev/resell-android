package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.model.CoilRepository
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun ResellSavedCard(
    imageUrl: String,
    onClick: () -> Unit,
    viewModel: ResellSavedCardViewModel = hiltViewModel()
) {
    val img by viewModel.getImageUrlState(imageUrl)
    AnimatedClampedAsyncImage(
        image = img,
        modifier = Modifier
            .height(112.dp)
            .width(112.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
    )
}

@HiltViewModel
class ResellSavedCardViewModel @Inject constructor(
    private val coilRepository: CoilRepository
) : ResellViewModel<Unit>(
    initialUiState = Unit
) {
    fun getImageUrlState(imageUrl: String) = coilRepository.getUrlState(imageUrl)
}
