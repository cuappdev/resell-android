package com.cornellappdev.resell.android.viewmodel.newpost

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.main.HomeViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsEntryViewModel @Inject constructor(
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    private val postRepository: ResellPostRepository,
    private val userInfoRepository: UserInfoRepository,
    private val rootConfirmationRepository: RootConfirmationRepository
) : ResellViewModel<PostDetailsEntryViewModel.PostEntryUiState>(
    initialUiState = PostEntryUiState()
) {

    data class PostEntryUiState(
        val title: String = "",
        val description: String = "",
        val price: String = "",
        val activeFilter: HomeViewModel.HomeFilter = HomeViewModel.HomeFilter.CLOTHING,
        val loadingPost: Boolean = false,
    ) {
        private val canConfirm
            get() = title.isNotBlank() && price.isNotBlank()

        val buttonState: ResellTextButtonState
            get() = if (loadingPost) {
                ResellTextButtonState.SPINNING
            } else if (canConfirm) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    fun onPricePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                callback = {
                    applyMutation {
                        copy(price = it)
                    }
                },
                confirmString = "Set Price",
                title = "What price do you want to propose?",
                defaultPrice = stateValue().price
            )
        )
    }

    fun onTitleChanged(title: String) {
        applyMutation {
            copy(title = title)
        }
    }

    fun onDescriptionChanged(description: String) {
        applyMutation {
            copy(description = description)
        }
    }

    fun onFilterPressed(filter: HomeViewModel.HomeFilter) {
        applyMutation {
            copy(activeFilter = filter)
        }
    }

    fun onConfirmPost() {
        // TODO: Placeholder
        viewModelScope.launch {
            try {
                applyMutation {
                    copy(loadingPost = true)
                }

                Log.d("helpme", stateValue().price.toDouble().toString())
                postRepository.uploadPost(
                    title = stateValue().title,
                    description = stateValue().description,
                    originalPrice = stateValue().price.toDouble(),
                    images = postRepository.getRecentBitmaps()!!,
                    categories = listOf(stateValue().activeFilter.name),
                    userId = userInfoRepository.getUserId() ?: "lol"
                )
                applyMutation {
                    copy(loadingPost = false)
                }
                rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                rootConfirmationRepository.showSuccess(
                    message = "Your listing has been posted successfully!"
                )
            } catch (e: Exception) {
                Log.e("ProfileRepository", "Error creating listing: ", e)
                applyMutation {
                    copy(loadingPost = false)
                }
                rootConfirmationRepository.showError()
            }
        }
    }
}
