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
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
import com.cornellappdev.resell.android.model.pdp.ImageBitmapLoader
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.richieUrl
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.OptionType
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.root.RootOptionsMenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val rootOptionsMenuRepository: RootOptionsMenuRepository,
    private val imageBitmapLoader: ImageBitmapLoader,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val retrofitInstance: RetrofitInstance,
    private val userInfoRepository: UserInfoRepository,
    private val postsRepository: ResellPostRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val profileRepository: ProfileRepository,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val fireStoreRepository: FireStoreRepository,
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
        val contactButtonState: ResellTextButtonState = ResellTextButtonState.ENABLED,
        val showContact: Boolean = false,
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
//                val response = retrofitInstance.postsApi.getFilteredPosts(
//                    CategoryRequest(category)
//                )
                val response = retrofitInstance.postsApi.getSimilarPosts(
                    id
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
                Log.e("PostDetailViewModel", "Error fetching similar posts: ", e)
                applyMutation {
                    copy(
                        similarItems = ResellApiResponse.Error
                    )
                }
            }
        }
    }

    private fun fetchSaved(id: String) {
        applyMutation {
            copy(bookmarked = false)
        }

        viewModelScope.launch {
            try {
                val saved = postsRepository.isPostSaved(id)
                applyMutation {
                    copy(bookmarked = saved)
                }
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Error fetching saved: ", e)
            }
        }
    }

    fun onEllipseClick() {
        viewModelScope.launch {
            rootOptionsMenuRepository.showOptionsMenu(
                options = listOf(
                    OptionType.SHARE,
                    OptionType.REPORT
                ).plus(
                    if (stateValue().uid == userInfoRepository.getUserId()) {
                        listOf(OptionType.DELETE)
                    } else {
                        listOf()
                    }
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
                                userId = stateValue().uid,
                            )
                        )
                    }

                    OptionType.DELETE -> {
                        onDelete()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun onDelete() {
        rootDialogRepository.showDialog(
            event = RootDialogContent.TwoButtonDialog(
                title = "Delete listing?",
                description = "Do you want to archive or PERMANENTLY delete this listing?",
                primaryButtonText = "Delete",
                secondaryButtonText = "Archive",
                primaryButtonContainer = ResellTextButtonContainer.PRIMARY_RED,
                secondaryButtonContainer = ResellTextButtonContainer.SECONDARY,
                onPrimaryButtonClick = {
                    viewModelScope.launch {
                        try {
                            rootDialogRepository.setPrimaryButtonState(ResellTextButtonState.SPINNING)
                            postsRepository.deletePost(stateValue().postId)
                            rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                            rootConfirmationRepository.showSuccess(
                                message = "Your listing has been deleted successfully.",
                            )
                            rootDialogRepository.dismissDialog()
                        } catch (e: Exception) {
                            Log.e("PostDetailViewModel", "Error deleting post: ", e)
                            rootConfirmationRepository.showError()
                            rootDialogRepository.dismissDialog()
                        }
                    }
                },
                onSecondaryButtonClick = {
                    viewModelScope.launch {
                        try {
                            rootDialogRepository.setPrimaryButtonState(ResellTextButtonState.SPINNING)
                            postsRepository.archivePost(stateValue().postId)
                            rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                            rootConfirmationRepository.showSuccess(
                                message = "Your listing has been archived successfully.",
                            )
                            rootDialogRepository.dismissDialog()
                        } catch (e: Exception) {
                            Log.e("PostDetailViewModel", "Error archiving post: ", e)
                            rootConfirmationRepository.showError()
                            rootDialogRepository.dismissDialog()
                        }
                    }
                },
                exitButton = true
            )
        )
    }

    fun onContactClick() {
        val uid = stateValue().uid
        val postId = stateValue().postId

        viewModelScope.launch {
            try {
                val userInfo = profileRepository.getUserById(uid).user.toUserInfo()
                applyMutation {
                    copy(
                        contactButtonState = ResellTextButtonState.SPINNING
                    )
                }
                contactSeller(
                    onSuccess = {
                        applyMutation {
                            copy(
                                contactButtonState = ResellTextButtonState.ENABLED
                            )
                        }
                    },
                    onError = {
                        applyMutation {
                            copy(
                                contactButtonState = ResellTextButtonState.ENABLED
                            )
                        }
                    },
                    postsRepository = postsRepository,
                    rootConfirmationRepository = rootConfirmationRepository,
                    rootNavigationRepository = rootNavigationRepository,
                    fireStoreRepository = fireStoreRepository,
                    isBuyer = true,
                    pfp = userInfo.imageUrl,
                    name = userInfo.name,
                    myId = userInfo.id,
                    listingId = postId
                )

            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Error fetching user info: ", e)
                applyMutation {
                    copy(
                        contactButtonState = ResellTextButtonState.ENABLED
                    )
                }
                rootConfirmationRepository.showError()
            }
        }
    }

    fun onBookmarkClick() {
        if (stateValue().bookmarked) {
            applyMutation {
                copy(bookmarked = false)
            }

            viewModelScope.launch {
                try {
                    postsRepository.unsavePost(stateValue().postId)
                } catch (e: Exception) {
                    Log.e("PostDetailViewModel", "Error unsaving post: ", e)
                    rootConfirmationRepository.showError()
                    applyMutation {
                        copy(bookmarked = true)
                    }
                }
            }
        } else {
            applyMutation {
                copy(bookmarked = true)
            }

            viewModelScope.launch {
                try {
                    postsRepository.savePost(stateValue().postId)
                } catch (e: Exception) {
                    Log.e("PostDetailViewModel", "Error saving post: ", e)
                    rootConfirmationRepository.showError()
                    applyMutation {
                        copy(bookmarked = false)
                    }
                }
            }
        }

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

        loadPost(
            id = listing.id,
            title = listing.title,
            price = listing.price,
            description = listing.description,
            userImageUrl = listing.user.imageUrl,
            userHumanName = listing.user.name,
            userId = listing.user.id,
            images = listing.images,
            categories = listing.categories
        )
    }

    private fun loadPost(
        id: String,
        title: String,
        price: String,
        description: String,
        userImageUrl: String,
        userHumanName: String,
        userId: String,
        images: List<String>,
        categories: List<String>
    ) {
        applyMutation {
            copy(
                postId = id,
                title = title,
                price = price,
                description = description,
                profileImageUrl = userImageUrl,
                username = userHumanName,
                uid = userId
            )
        }
        onNeedLoadImages(
            urls = images,
            currentPostId = id
        )
        fetchSimilarPosts(
            id = id,
            category = categories.firstOrNull() ?: ""
        )
        fetchSaved(id)

        // Hide "Contact Seller" if the current user is the same as the post owner.
        viewModelScope.launch {
            val myId = userInfoRepository.getUserId()!!
            applyMutation {
                copy(
                    showContact = myId != userId
                )
            }
        }
    }

    init {
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.PDP>()
        loadPost(
            id = navArgs.id,
            title = navArgs.title,
            price = navArgs.price,
            description = navArgs.description,
            userImageUrl = navArgs.userImageUrl,
            userHumanName = navArgs.userHumanName,
            userId = navArgs.userId,
            images = navArgs.images,
            categories = navArgs.categories
        )
    }
}
