package com.cornellappdev.resell.android.viewmodel.pdp

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.pdp.ImageBitmapLoader
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.longUrl
import com.cornellappdev.resell.android.util.richieUrl
import com.cornellappdev.resell.android.util.tallUrl
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.OptionType
import com.cornellappdev.resell.android.viewmodel.root.RootOptionsMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val rootOptionsMenuRepository: RootOptionsMenuRepository,
    private val imageBitmapLoader: ImageBitmapLoader,
    private val rootNavigationRepository: RootNavigationRepository,
    savedStateHandle: SavedStateHandle
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
                    rootNavigationRepository.navigate(
                        ResellRootRoute.REPORT(
                            reportPost = true,
                            postId = stateValue().postId,
                            userId = ""
                        )
                    )
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
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.PDP>()
        applyMutation {
            copy(
                postId = navArgs.id,
                title = navArgs.title,
                price = navArgs.price,
                description = navArgs.description
            )
        }
        onNeedLoadImages(
            urls = navArgs.images,
            currentPostId = navArgs.id
        )
    }
}
