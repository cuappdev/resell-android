package com.cornellappdev.resell.android.viewmodel.navigation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.ResellAuthRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val rootNavigationRepository: RootNavigationRepository,
    private val userInfoRepository: UserInfoRepository,
    private val resellAuthRepository: ResellAuthRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val resellPostRepository: ResellPostRepository,
    mainNavigationRepository: MainNavigationRepository,
) : ResellViewModel<MainNavigationViewModel.MainNavigationUiState>(
    initialUiState = MainNavigationUiState(
        newPostExpanded = false
    )
) {

    data class MainNavigationUiState(
        val newPostExpanded: Boolean,
        val navEvent: UIEvent<ResellMainScreen>? = null,
        val bottomBarEnabled: Boolean = false
    )

    init {
        asyncCollect(mainNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    navEvent = route
                )
            }
        }

        // Start networking if applicable
        viewModelScope.launch {
            validateAccessToken()

            // Enable bottom bar
            applyMutation {
                copy(bottomBarEnabled = true)
            }

            // User was locked onto the main screen, so we can fetch posts now and it will load.
            resellPostRepository.fetchPosts()
        }
    }

    /**
     * When the main screen is opened, we need to validate the access token.
     *
     * If the access token is invalid, we revalidate and proceed. If revalidation fails for any reason,
     * we will force logout the user and navigate to the login screen.
     *
     * Afterwards, assuming the token is valid, we can then network on the main screen. Thus, when
     * this function call terminates, networking can proceed.
     */
    private suspend fun validateAccessToken() {
        try {
            // First step: if `UserInfoRepository` doesn't have a user ID and username, we should store:
            try {
                val user = resellAuthRepository.getGoogleUser(
                    id = googleAuthRepository.accountOrNull()!!.id!!
                )
                userInfoRepository.storeUserId(user.id)
                userInfoRepository.storeBio(user.bio)
                userInfoRepository.storeNetId(user.netid)
                userInfoRepository.storeEmail(user.email)
                userInfoRepository.storeUsername(user.username)
                userInfoRepository.storeFirstName(user.givenName)
                userInfoRepository.storeLastName(user.familyName)
                userInfoRepository.storeProfilePicUrl(user.photoUrl)
                userInfoRepository.storeIdToken(googleAuthRepository.accountOrNull()!!.idToken!!)

                Log.d(
                    "MainNavigationViewModel",
                    "User ID and username stored: id: ${user.id}, username: ${user.username}, idToken: ${googleAuthRepository.accountOrNull()!!.idToken!!}"
                )
            } catch (e: HttpException) {
                // Edge case: If for some reason the user doesn't exist,
                // we should move to onboarding instead. This handles the case in which
                // a DEV user logs in with an onboarded PROD user.
                rootNavigationRepository.navigate(ResellRootRoute.ONBOARDING)
            }

//            resellAuthRepository.loginToResell(
//                idToken = userInfoRepository.getUserId()!!,
//                user = userInfoRepository.getUsername()!!,
//            )

            Log.d("MainNavigationViewModel", "Logged in!")

            // Now `UserInfoRepository` should have a user ID and username.
            // Second step: Authenticate. If any exception is thrown, the catch will handle.
            resellAuthRepository.authenticate()

            Log.d("MainNavigationViewModel", "Authenticated!")
        } catch (e: Exception) {
            // TODO: Handle this better
            rootConfirmationRepository.showError(
                message = "Something went wrong authenticating. Please try again."
            )
            Log.e("MainNavigationViewModel", "Error authenticating: ", e)
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
