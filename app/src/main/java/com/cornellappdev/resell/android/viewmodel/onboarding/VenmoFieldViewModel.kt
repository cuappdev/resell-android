package com.cornellappdev.resell.android.viewmodel.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.api.CreateUserBody
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
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
    private val fireStoreRepository: FireStoreRepository,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val userInfoRepository: UserInfoRepository,
    savedStateHandle: SavedStateHandle
) : ResellViewModel<VenmoFieldViewModel.VenmoFieldUiState>(
    initialUiState = VenmoFieldUiState()
) {
    val navArgs = savedStateHandle.toRoute<ResellOnboardingScreen.Venmo>()

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
        applyMutation {
            copy(
                username = navArgs.username,
                bio = navArgs.bio
            )
        }
    }

    fun onContinueClick() {
        viewModelScope.launch {
            applyMutation {
                copy(loading = true)
            }
            delay(500)
            makeNewUser(false)
        }
    }

    fun onSkipClick() {
        viewModelScope.launch {
            applyMutation {
                copy(skipLoading = true)
            }
            delay(500)
            makeNewUser(true)
        }
    }

    /**
     * Makes a new user and navigates out if successful.
     */
    private fun makeNewUser(skipped: Boolean) {
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
                        photoUrl = navArgs.pfpUrl.ifEmpty {
                            googleUser.photoUrl.toString()
                        },
                        googleId = googleUser.id!!,
                        bio = stateValue().bio,
                        fcmToken = firebaseMessagingRepository.getDeviceFCMToken(),
                        venmoHandle = if (skipped) "" else stateValue().handle
                    )
                )

                userInfoRepository.storeUserFromUserObject(response.user)
                rootNavigationRepository.navigate(ResellRootRoute.MAIN)
                rootNavigationSheetRepository.showBottomSheet(RootSheet.Welcome)
            } catch (e: Exception) {
                rootConfirmationRepository.showError()
                Log.e("VenmoFieldViewModel", "Error creating user: ", e)
                applyMutation {
                    copy(loading = false, skipLoading = false)
                }
            }
        }
    }

    fun onHandleChanged(handle: String) {
        applyMutation {
            copy(handle = handle)
        }
    }
}
