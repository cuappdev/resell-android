package com.cornellappdev.resell.android.viewmodel.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.model.Chat
import com.cornellappdev.resell.android.model.classes.ResellApiState
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.justinChats
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import com.cornellappdev.resell.android.viewmodel.navigation.RootNavigationRepository
import com.cornellappdev.resell.android.viewmodel.root.RootNavigationSheetRepository
import com.cornellappdev.resell.android.viewmodel.root.RootSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navController: RootNavigationRepository,
    private val rootNavigationSheetRepository: RootNavigationSheetRepository,
) :
    ResellViewModel<ChatViewModel.MessagesUiState>(
        initialUiState = MessagesUiState(
            chats = justinChats(40),
            loadedState = ResellApiState.Success,
            chatType = ChatType.Purchases,
            currentChat = null
        )
    ) {
    data class MessagesUiState(
        val loadedState: ResellApiState,
        val chats: List<Chat>,
        val chatType: ChatType,
        val currentChat: Chat?,
    ) {
        val filteredChats = chats.filter { it.chatType == chatType }
    }


    enum class ChatType {
        Purchases, Offers
    }

    fun onMessagePressed(chat: Chat) {
        applyMutation {
            copy(currentChat = chat)
        }
        navController.navigate(ResellRootRoute.CHAT)
    }

    fun onChangeChatType(chatType: ChatType) {
        applyMutation {
            copy(chatType = chatType)
        }
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
}
