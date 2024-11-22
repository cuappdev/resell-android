package com.cornellappdev.resell.android.viewmodel.main

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.api.ChatRepository
import com.cornellappdev.resell.android.model.classes.Listing
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FirebaseMessagingRepository
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootConfirmationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
    private val userInfoRepository: UserInfoRepository,
    private val chatRepository: ChatRepository,
    private val savedStateHandle: SavedStateHandle,
    private val rootConfirmationRepository: RootConfirmationRepository,
    private val firebaseMessagingRepository: FirebaseMessagingRepository,
) :
    ResellViewModel<ChatViewModel.MessagesUiState>(
        initialUiState = MessagesUiState(
            chatType = ChatType.Purchases,
            currentChat = ResellApiResponse.Pending
        )
    ) {
    data class MessagesUiState(
        val chatType: ChatType,
        val currentChat: ResellApiResponse<Chat>,
        val sellerName: String = "Unknown",
        val title: String = "Unknown",
        val typedMessage: String = "",
    )

    enum class ChatType {
        Purchases, Offers
    }

    fun onBackPressed() {
        navController.popBackStack()
    }

    fun onMeetingDetailsPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Cancel Meeting",
                title = "Meeting Details",
                closeString = "Close",
                confirmColor = ResellTextButtonContainer.PRIMARY_RED
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Meeting with Lia for Blue Pants confirmed for",
                    style = Style.title4
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Time",
                    style = Style.title3
                )
                Text(
                    text = "Friday, October 23 - 1:30-2:00 PM",
                    style = Style.title4
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "For safety, make sure to meet up in a public space on campus",
                    style = Style.title4
                )

                Spacer(Modifier.height(32.dp))
            }
        )
    }

    fun onProposalDetailsPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Edit Proposal",
                title = "Proposal Details",
                closeString = "Cancel",
                confirmColor = ResellTextButtonContainer.PRIMARY
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "You have proposed the following meeting:",
                    style = Style.body1
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Time",
                    style = Style.title3
                )
                Text(
                    text = "Friday, October 23 - 1:30-2:00 PM",
                    style = Style.body1
                )

                Spacer(Modifier.height(32.dp))
            }
        )
    }

    fun onSyncToCalendarPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Sync",
                title = "Sync to Google Calendar?",
                closeString = "Close",
                confirmColor = ResellTextButtonContainer.PRIMARY
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "A new meeting has been detected would you like to sync your calendar?",
                    style = Style.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))
            }
        )
    }

    fun onNewProposalPressed() {
        rootNavigationSheetRepository.showBottomSheet(
            RootSheet.MeetingDetails(
                callback = {

                },
                confirmString = "Reschedule",
                title = "New Proposal",
                closeString = "Cancel",
                confirmColor = ResellTextButtonContainer.PRIMARY
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "This proposal replaces your previous one scheduled for [Original Time]. Are you sure you want to reschedule?",
                    style = Style.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))
            }
        )
    }

    fun onSendMessage(message: String) {
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
        applyMutation {
            copy(
                typedMessage = ""
            )
        }
        viewModelScope.launch {
            try {
                chatRepository.sendTextMessage(
                    myEmail = userInfoRepository.getEmail()!!,
                    otherEmail = navArgs.email,
                    text = message,
                    selfIsBuyer = navArgs.isBuyer
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ", e)
                rootConfirmationRepository.showError(
                    "Could not send your text message. Please try again."
                )
            }
        }
    }

    fun onTyped(message: String) = applyMutation { copy(typedMessage = message) }

    init {
        val navArgs = savedStateHandle.toRoute<ResellRootRoute.CHAT>()
        val listing = Json.decodeFromString<Listing>(navArgs.postJson)

        firebaseMessagingRepository.requestNotificationsPermission()

        applyMutation {
            copy(
                sellerName = navArgs.name,
                title = listing.title,
                chatType = if (navArgs.isBuyer) ChatType.Purchases else ChatType.Offers,
            )
        }

        viewModelScope.launch {
            val myEmail = userInfoRepository.getEmail()!!
            val theirEmail = navArgs.email
            val myId = userInfoRepository.getUserId()!!

            chatRepository.subscribeToChat(myEmail, theirEmail, myId)
        }

        asyncCollect(chatRepository.subscribedChatFlow) { response ->
            applyMutation {
                copy(
                    currentChat = response
                )
            }
        }
    }
}
