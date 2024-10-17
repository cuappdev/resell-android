package com.cornellappdev.resell.android.viewmodel.pdp

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.pdp.ImageBitmapLoader
import com.cornellappdev.resell.android.util.longUrl
import com.cornellappdev.resell.android.util.richieUrl
import com.cornellappdev.resell.android.util.tallUrl
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.OptionType
import com.cornellappdev.resell.android.viewmodel.root.RootOptionsMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val rootOptionsMenuRepository: RootOptionsMenuRepository,
    private val imageBitmapLoader: ImageBitmapLoader,
) : ResellViewModel<PostDetailViewModel.UiState>(
    initialUiState = UiState()
) {

    data class UiState(
        val title: String = "",
        val description: String = "",
        val profileImageUrl: String = richieUrl,
        val username: String = "",
        val price: String = "",
        val detailsLoading: Boolean = false,
        val imageLoading: Boolean = false,
        val images: List<ImageBitmap> = listOf(),
        val postId: String = "",
        val bookmarked: Boolean = false,
        private val similarItemIds: List<Int> = emptyList()
    ) {
        val minAspectRatio
            get() = images.minOfOrNull { it.width.toFloat() / it.height.toFloat() } ?: 1f
    }

    /**
     * Initiates a load of new images. After some time, the images will be loaded and pipelined
     * down the UiState as bitmaps.
     */
    fun onNeedLoadImages(urls: List<String>, currentPostId: String) {
        viewModelScope.launch {
            applyMutation { copy(imageLoading = true) }

            val images = urls.mapNotNull {
                imageBitmapLoader.getBitmap(it)
            }

            // If a request takes super long and we started looking at a different post,
            // don't load images
            if (stateValue().postId != currentPostId) {
                return@launch
            }

            applyMutation { copy(images = images) }

            applyMutation { copy(imageLoading = false) }
        }
    }

    fun onEllipseClick() {
        rootOptionsMenuRepository.showOptionsMenu(
            options = listOf(
                OptionType.SHARE,
                OptionType.REPORT
            ),
            alignment = Alignment.TopEnd,
        ) {
            when (it) {
                OptionType.SHARE -> {

                }

                OptionType.REPORT -> {

                }

                else -> {}
            }
        }
    }

    fun onContactClick() {

    }

    fun onBookmarkClick() {

    }

    init {
        // TODO: Replace with the packaged data, whenever I get around to that
        applyMutation {
            copy(
                postId = "1",
                title = "my soul",
                price = "-$10.00",
                description = "I need to get rid of this."
            )
        }
        onNeedLoadImages(
            urls = listOf(
                richieUrl,
                tallUrl,
                longUrl,
            ),
            currentPostId = "1"
        )
    }
}
