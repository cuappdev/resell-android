package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.api.CreateUserBody
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.ResellAuthRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonState
import com.cornellappdev.resell.android.ui.screens.onboarding.ResellOnboardingScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VenmoFieldViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val rootNavigationRepository: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val resellAuthRepository: ResellAuthRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    savedStateHandle: SavedStateHandle
) : ResellViewModel<VenmoFieldViewModel.VenmoFieldUiState>(
    initialUiState = VenmoFieldUiState()
) {

    data class VenmoFieldUiState(
        val handle: String = "",
        val loading: Boolean = false,
        val proceedMain: UIEvent<Unit>? = null,
        val skipLoading: Boolean = false,
        val username: String = "",
        val bio: String = "",
    ) {
        // Continue button.
        val continueButtonState: ResellTextButtonState
            get() = if (loading) {
                ResellTextButtonState.SPINNING
            } else if (handle.isNotEmpty() && !skipLoading) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }

        // Skip button.
        val skipButtonState: ResellTextButtonState
            get() = if (skipLoading) {
                ResellTextButtonState.SPINNING
            } else if (!loading) {
                ResellTextButtonState.ENABLED
            } else {
                ResellTextButtonState.DISABLED
            }
    }

    init {
        val navArgs = savedStateHandle.toRoute<ResellOnboardingScreen.Venmo>()

        applyMutation {
            copy(
                username = navArgs.username,
                bio = navArgs.bio
            )
        }
    }

    fun onContinueClick() {
        // TODO
        viewModelScope.launch {

        }

        // Test
        viewModelScope.launch {
            applyMutation {
                copy(loading = true)
            }
            delay(500)
            makeNewUser()
        }
    }

    fun onSkipClick() {
        viewModelScope.launch {
            applyMutation {
                copy(skipLoading = true)
            }
            delay(500)
            makeNewUser()
        }
    }

    /**
     * Makes a new user and navigates out if successful.
     */
    private fun makeNewUser() {
        viewModelScope.launch {
            val googleUser = googleAuthRepository.accountOrNull()!!
            try {
                val response = resellAuthRepository.createUser(
                    CreateUserBody(
                        username = stateValue().username,
                        netid = googleUser.email!!.split("@")[0],
                        givenName = googleUser.givenName!!,
                        familyName = googleUser.familyName!!,
                        email = googleUser.email!!,
                        // TODO How to determine photo url from setup? This is not right.
                        photoUrl = googleUser.photoUrl.toString(),
                        googleId = googleUser.id!!,
                        bio = stateValue().bio,
                    )
                )

                rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                rootNavigationSheetRepository.showBottomSheet(RootSheet.Welcome)
            } catch (e: Exception) {
                rootConfirmationRepository.showError()
                applyMutation {
                    copy(loading = false, skipLoading = false)
                }
                e.printStackTrace()
            }
        }
    }

    fun onHandleChanged(handle: String) {
        applyMutation {
            copy(handle = handle)
        }
    }
}
