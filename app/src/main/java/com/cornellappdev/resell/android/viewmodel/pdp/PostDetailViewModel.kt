package com.cornellappdev.resell.android.viewmodel.pdp

import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.pdp.ImageBitmapLoader
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.richieUrl
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
    private val retrofitInstance: RetrofitInstance,
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
        val similarItems: ResellApiResponse<List<Listing>> = ResellApiResponse.Pending
    ) {
        val minAspectRatio
            get() = images.minOfOrNull { it.width.toFloat() / it.height.toFloat() } ?: 1f

        val similarImageUrls
            get() = similarItems.map {
                it.mapNotNull { listing ->
                    listing.images.firstOrNull()
                }
            }
    }

    /**
     * Initiates a load of new images. After some time, the images will be loaded and pipelined
     * down the UiState as bitmaps.
     */
    private fun onNeedLoadImages(urls: List<String>, currentPostId: String) {
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

    /**
     * Invalidates current similar posts, then fetches new similar posts. Once loaded, these similar
     * posts will populate the bottom of the screen.
     */
    private fun fetchSimilarPosts(id: String) {
        applyMutation {
            copy(
                similarItems = ResellApiResponse.Pending
            )
        }

        // Start networking
        viewModelScope.launch {
            try {
                val response = retrofitInstance.postsApi.getSimilarPosts(id)

                Log.d("helpme", response.posts.size.toString())

                applyMutation {
                    copy(
                        similarItems = ResellApiResponse.Success(
                            response.posts.map { it.toListing() })
                    )
                }
            } catch (e: Exception) {
                Log.e("helpme", "Error fetching similar posts: ", e)
                applyMutation {
                    copy(
                        similarItems = ResellApiResponse.Error
                    )
                }
            }
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

    fun onSimilarPressed(index: Int) {
        val listing = stateValue().similarItems.asSuccess().data[index]
        applyMutation {
            copy(
                postId = listing.id,
                title = listing.title,
                price = listing.price,
                description = listing.description
            )
        }

        onNeedLoadImages(
            urls = listing.images,
            currentPostId = listing.id
        )

        fetchSimilarPosts(
            id = listing.id
        )
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
        fetchSimilarPosts(
            id = navArgs.id
        )
    }
}
