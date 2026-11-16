package com.cornellappdev.resell.android.viewmodel.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.util.loadBitmapFromUri
import com.cornellappdev.resell.android.util.toNetworkingString
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootDialogContent
import com.cornellappdev.resell.android.viewmodel.root.RootDialogRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val userInfoRepository: UserInfoRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle,
    private val rootConfirmationRepository: RootConfirmationRepository,
    firebaseMessagingRepository: FirebaseMessagingRepository,
    private val rootDialogRepository: RootDialogRepository,
    private val rootNavigationRepository: RootNavigationRepository,
    @ApplicationContext private val context: Context
) :
    ResellViewModel<ChatViewModel.MessagesUiState>(
        initialUiState = MessagesUiState(
            chatType = ChatType.Purchases,
            currentChat = ResellApiResponse.Pending,
        )
    ) {
        companion object {
            private const val TAG = "ChatViewModel"
        }
    data class MessagesUiState(
        val chatType: ChatType,
        val currentChat: ResellApiResponse<Chat>,
        val otherName: String = "Unknown",
        val title: String = "Unknown",
        val typedMessage: String = "",
        val scrollBottom: UIEvent<Unit>? = null,
    ) {
        val showNegotiate
            get() = true

        val showPayWithVenmo
            get() = chatType == ChatType.Purchases
    }

    val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
    val listing = Json.decodeFromString<Listing>(navArgs.listingJson)

    enum class ChatType {
        Purchases, Offers
    }

    fun onBackPressed() {
        navController.popBackStack()
    }

    fun onSendMessage(message: String) {
        applyMutation {
            copy(
                typedMessage = ""
            )
        }
        viewModelScope.launch {
            val myInfo = userInfoRepository.getUserInfo()
            try {
                chatRepository.sendTextMessage(
                    text = message,
                    selfIsBuyer = navArgs.isBuyer,
                    listingId = listing.id,
                    myId = myInfo.id,
                    otherId = navArgs.otherUserId,
                    chatId = navArgs.chatId
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error sending message: ", e)
                rootConfirmationRepository.showError(
                    "Something went wrong while sending your message."
                )
            }
        }
    }

    fun onPostClicked(post: Post) {
        rootNavigationRepository.navigateToPdp(
            post.toListing()
        )
    }

    fun onTyped(message: String) = applyMutation { copy(typedMessage = message) }

    fun onNegotiatePressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.ProposalSheet(
                confirmString = "Propose",
                title = "What price do you want to propose?",
                defaultPrice = listing.price.replace("$", ""),
                callback = { price ->
                    if (stateValue().chatType == ChatType.Purchases) {
                        onTyped(
                            message = "Hi! I'm interested in buying your ${listing.title}, but would you be open to selling it for $$price?"
                        )
                    } else {
                        onTyped(
                            message = "The lowest I can accept for this item would be $$price."
                        )
                    }
                }
            )
        )
    }

    fun payWithVenmoPressed() = viewModelScope.launch {
        try {
            val theirVenmo = navArgs.otherVenmo
            if (theirVenmo.isNotBlank()) {
                // Open Venmo URL
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://account.venmo.com/u/${theirVenmo}".toUri()
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                throw Exception("No Venmo handle found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Venmo handle: ", e)
            rootDialogRepository.showDialog(
                RootDialogContent.TwoButtonDialog(
                    title = "Venmo Not Set Up",
                    description = "${stateValue().otherName} has not set up their venmo yet.\nPlease contact them directly.",
                    primaryButtonText = "Dismiss",
                    secondaryButtonText = null,
                    onPrimaryButtonClick = {
                        rootDialogRepository.dismissDialog()
                    },
                    onSecondaryButtonClick = {},
                    exitButton = true
                )
            )
        }
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(context, uri)?.asImageBitmap()
            if (bitmap != null) {
                val myInfo = userInfoRepository.getUserInfo()
                try {
                    chatRepository.sendImageMessage(
                        selfIsBuyer = navArgs.isBuyer,
                        listingId = listing.id,
                        myId = myInfo.id,
                        otherId = navArgs.otherUserId,
                        imageBase64 = bitmap.toNetworkingString(),
                        chatId = navArgs.chatId
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending message: ", e)
                    rootConfirmationRepository.showError(
                        "Something went wrong while sending your image."
                    )
                }
            } else {
                rootConfirmationRepository.showError(
                    "Could not load image. Please try again."
                )
            }
        }
    }

    init {
        firebaseMessagingRepository.requestNotificationsPermission()

        val title = listing.title
        applyMutation {
            copy(
                otherName = navArgs.name,
                title = title,
                chatType = if (navArgs.isBuyer) ChatType.Purchases else ChatType.Offers,
            )
        }

        viewModelScope.launch {
            val myId = userInfoRepository.getUserId() ?: ""
            chatRepository.subscribeToChat(
                myName = userInfoRepository.getFirstName()!!,
                otherName = navArgs.name,
                chatId = navArgs.chatId,
                myId = myId,
                otherPfp = navArgs.pfp
            )

            chatRepository.markChatRead(
                chatId = navArgs.chatId,
                myId = myId
            )
        }

        asyncCollect(chatRepository.subscribedChatFlow(navArgs.chatId)) { response ->
            applyMutation {
                copy(
                    currentChat = response,
                    scrollBottom = UIEvent(Unit)
                )
            }
        }
    }
}
