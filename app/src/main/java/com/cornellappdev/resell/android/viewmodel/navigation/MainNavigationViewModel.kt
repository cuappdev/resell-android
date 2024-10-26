package com.cornellappdev.resell.android.viewmodel.navigation

import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.ResellAuthRepository
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val userInfoRepository: UserInfoRepository,
    private val resellAuthRepository: ResellAuthRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    mainNavigationRepository: MainNavigationRepository,
) : ResellViewModel<MainNavigationViewModel.MainNavigationUiState>(
    initialUiState = MainNavigationUiState(
        newPostExpanded = false
    )
) {

    data class MainNavigationUiState(
        val newPostExpanded: Boolean,
        val navEvent: UIEvent<ResellMainScreen>? = null
    )

    init {
        asyncCollect(mainNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    navEvent = route
                )
            }
        }
    }

    /**
     * When the main screen is opened, we need to validate the access token.
     *
     * If the access token is invalid, we revalidate and proceed. If revalidation fails for any reason,
     * we will force logout the user and navigate to the login screen.
     *
     * Afterwards, assuming the token is valid, we can then network on the main screen.
     */
    fun validateAccessToken() {

        try {
            viewModelScope.launch {
                // First step: if `UserInfoRepository` doesn't have a user ID and username, we should store:
                if (userInfoRepository.getUserId() == null ||
                    userInfoRepository.getUsername() == null
                ) {
                    val user = resellAuthRepository.getGoogleUser(
                        id = googleAuthRepository.accountOrNull()!!.id!!
                    )

                    userInfoRepository.storeUserId(user.id)
                    userInfoRepository.storeUsername(user.name)
                }

                // Now `UserInfoRepository` should have a user ID and username.
                // Second step: Authenticate. If any exception is thrown, the catch will handle.
                resellAuthRepository.authenticate()


            }
        } catch (e: Exception) {
            // TODO: Handle this better
            e.printStackTrace()
        }
    }

    fun onNewPostExpandClick() {
        applyMutation {
            copy(
                newPostExpanded = !newPostExpanded
            )
        }
    }

    fun onShadeTapped() {
        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }

    fun onNewRequestClick() {
        rootNavigationRepository.navigate(ResellRootRoute.NEW_REQUEST)

        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }

    fun onNewPostClick() {
        rootNavigationRepository.navigate(ResellRootRoute.NEW_POST)

        applyMutation {
            copy(
                newPostExpanded = false
            )
        }
    }
}

@Singleton
class MainNavigationRepository @Inject constructor() : BaseNavigationRepository<ResellMainScreen>()
