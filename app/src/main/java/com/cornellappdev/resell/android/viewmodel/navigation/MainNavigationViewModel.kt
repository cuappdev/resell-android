package com.cornellappdev.resell.android.viewmodel.navigation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cornellappdev.resell.android.model.api.NotificationData
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
import com.cornellappdev.resell.android.model.login.GoogleAuthRepository
import com.cornellappdev.resell.android.model.login.ResellAuthRepository
import com.cornellappdev.resell.android.model.posts.ResellPostRepository
import com.cornellappdev.resell.android.model.settings.NotificationsRepository
import com.cornellappdev.resell.android.ui.screens.main.ResellMainScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    private val fireStoreRepository: FireStoreRepository,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
    private val notificationsRepository: NotificationsRepository,
    mainNavigationRepository: MainNavigationRepository,
) : ResellViewModel<MainNavigationViewModel.MainNavigationUiState>(
    initialUiState = MainNavigationUiState(
        newPostExpanded = false
    )
) {

    data class MainNavigationUiState(
        val newPostExpanded: Boolean,
        val navEvent: UIEvent<ResellMainScreen>? = null,
        val bottomBarEnabled: Boolean = false,
        val notificationData: NotificationData? = null
    )

    init {
        asyncCollect(mainNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    navEvent = route
                )
            }
        }

        asyncCollect(notificationsRepository.notificationData) { event ->
            event?.consume { data ->
                applyMutation {
                    copy(notificationData = data)
                }

                if (stateValue().bottomBarEnabled) {
                    parseNotification(data)
                }
            }
        }

        // Start networking if applicable
        viewModelScope.launch {
            validateAccessToken()

            addFCMToken()

            // Enable bottom bar
            applyMutation {
                copy(bottomBarEnabled = true)
            }

            if (stateValue().notificationData != null) {
                parseNotification(stateValue().notificationData!!)
            }

            // User was locked onto the main screen, so we can fetch posts now and it will load.
            resellPostRepository.fetchPosts()
        }
    }

    private fun parseNotification(notificationData: NotificationData) {
        when (notificationData) {
            is NotificationData.ChatNotification -> {
                val post = Json.decodeFromString<Post>(notificationData.postJson)

                rootNavigationRepository.navigate(ResellRootRoute.CHAT(
                    email = notificationData.email,
                    name = notificationData.name,
                    pfp = notificationData.pfp,
                    isBuyer = notificationData.isBuyer == "true",
                    postJson = Json.encodeToString(post.toListing()),
                ))
            }
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

    /**
     * Attempts to publish the current user's FCM token, if it isn't already added.
     *
     * Catches edge cases in testing where we did not add the FCM token.
     */
    private suspend fun addFCMToken() {
        try {
            // Extra step: add FCM token if not already added
            val email = userInfoRepository.getEmail()!!
            val fcmToken = fireStoreRepository.getUserFCMToken(email)
            if (fcmToken == null || fcmToken != firebaseMessagingRepository.getDeviceFCMToken()) {
                fireStoreRepository.saveDeviceToken(
                    email,
                    firebaseMessagingRepository.getDeviceFCMToken()!!
                )
                Log.d("MainNavigationViewModel", "FCM token added successfully.")
            } else {
                Log.d(
                    "MainNavigationViewModel",
                    "FCM token already added: $fcmToken\n(This should match to: ${firebaseMessagingRepository.getDeviceFCMToken()})"
                )
            }
        } catch (e: Exception) {
            Log.e("MainNavigationViewModel", "Error adding FCM token: ", e)
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
