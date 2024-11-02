package com.cornellappdev.resell.android.viewmodel.pdp

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.api.CategoryRequest
import com.cornellappdev.resell.android.model.api.RetrofitInstance
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.pdp.ImageBitmapLoader
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
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
        val similarItems: ResellApiResponse<List<Listing>> = ResellApiResponse.Pending,
        val hideSheetEvent: UIEvent<Unit>? = null,
        val uid: String = "",
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
    private fun fetchSimilarPosts(id: String, category: String) {
        applyMutation {
            copy(
                similarItems = ResellApiResponse.Pending
            )
        }

        // Start networking
        viewModelScope.launch {
            try {
                // TODO: Backend be mf tweaking breh
                //  Replace with `getSimilarPosts` when that endpoint is back up running.
                val response = retrofitInstance.postsApi.getFilteredPosts(
                    CategoryRequest(category)
                )

                val posts = response.posts.filter {
                    it.id != stateValue().postId
                }.take(4)

                applyMutation {
                    copy(
                        similarItems = ResellApiResponse.Success(
                            posts.map { it.toListing() }
                        )
                    )
                }
            } catch (e: Exception) {
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

    fun onUserClick() {
        rootNavigationRepository.navigate(
            ResellRootRoute.EXTERNAL_PROFILE(
                id = stateValue().uid
            )
        )
    }

    fun onSimilarPressed(index: Int) {
        val listing = stateValue().similarItems.asSuccess().data[index]
        applyMutation {
            copy(
                postId = listing.id,
                title = listing.title,
                price = listing.price,
                description = listing.description,
                hideSheetEvent = UIEvent(Unit),
                profileImageUrl = listing.user.imageUrl,
                username = listing.user.name,
                uid = listing.user.id
            )
        }

        onNeedLoadImages(
            urls = listing.images,
            currentPostId = listing.id
        )

        fetchSimilarPosts(
            id = listing.id,
            category = listing.categories.firstOrNull() ?: ""
        )
    }

    init {
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.PDP>()
        applyMutation {
            copy(
                postId = navArgs.id,
                title = navArgs.title,
                price = navArgs.price,
                description = navArgs.description,
                profileImageUrl = navArgs.userImageUrl,
                username = navArgs.userHumanName,
                uid = navArgs.userId
            )
        }
        onNeedLoadImages(
            urls = navArgs.images,
            currentPostId = navArgs.id
        )
        fetchSimilarPosts(
            id = navArgs.id,
            category = navArgs.categories.firstOrNull() ?: ""
        )
    }
}
