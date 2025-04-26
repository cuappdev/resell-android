package com.cornellappdev.resell.android.viewmodel.newpost

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.profile.ProfileRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.isLeqMoney
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestDetailsEntryViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val profileRepository: ProfileRepository,
    private val userInfoRepository: UserInfoRepository
) : ResellViewModel<RequestDetailsEntryViewModel.RequestDetailsEntryViewState>(
    initialUiState = RequestDetailsEntryViewState()
) {

    data class RequestDetailsEntryViewState(
        val title: String = "",
        val description: String = "",
        val minPrice: String = "",
        val maxPrice: String = "",
        val loadingPost: Boolean = false,
    ) {
        private val priceValid
            get() = minPrice.isNotEmpty() && maxPrice.isNotEmpty() &&
                    minPrice.isLeqMoney(maxPrice) || minPrice.isEmpty() || maxPrice.isEmpty()

        private val canConfirm
            get() = title.isNotBlank() && priceValid

        val buttonState: ResellTextButtonState
            get() = if (loadingPost) {
                ResellTextButtonState.SPINNING
            } else if (canConfirm) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    fun onMinPricePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                callback = {
                    applyMutation {
                        copy(minPrice = it)
                    }
                },
                confirmString = "Set Min Price",
                title = "What minimum price do you want to propose?",
                defaultPrice = stateValue().minPrice
            )
        )
    }

    fun onMaxPricePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                callback = {
                    applyMutation {
                        copy(maxPrice = it)
                    }
                },
                confirmString = "Set Max Price",
                title = "What maximum price do you want to propose?",
                defaultPrice = stateValue().maxPrice
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

    fun onConfirmPost() {
        viewModelScope.launch {
            try {
                applyMutation {
                    copy(loadingPost = true)
                }

                profileRepository.createRequestListing(
                    title = stateValue().title,
                    description = stateValue().description,
                    userId = userInfoRepository.getUserId() ?: "lol"
                )

                applyMutation {
                    copy(loadingPost = false)
                }
                rootConfirmationRepository.showSuccess(
                    message = "Your request has been submitted successfully!"
                )
                rootNavigationRepository.navigate(ResellRootRoute.MAIN)
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
