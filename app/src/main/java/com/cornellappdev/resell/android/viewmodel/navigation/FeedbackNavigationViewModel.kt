package com.cornellappdev.resell.android.viewmodel.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.cornellappdev.resell.android.ui.screens.feedback.FeedbackScreen
import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class FeedbackNavigationViewModel @Inject constructor(
    feedbackNavigationRepository: FeedbackNavigationRepository,
    savedStateHandle: SavedStateHandle
) :
    ResellViewModel<FeedbackNavigationViewModel.UiState>(
        initialUiState = UiState(
            route = null
        ),
    ) {
    data class UiState(
        val route: UIEvent<FeedbackScreen>?,
        val popBackStack: UIEvent<Unit>? = null,
        val initialPage: FeedbackScreen? = null,
    )

    init {
        asyncCollect(feedbackNavigationRepository.routeFlow) { route ->
            applyMutation {
                copy(
                    route = route
                )
            }
        }

        asyncCollect(feedbackNavigationRepository.popBackStackFlow) { pop ->
            applyMutation {
                copy(
                    popBackStack = pop
                )
            }
        }

        val navArgs = savedStateHandle.toRoute<ResellRootRoute.FEEDBACK>()
        applyMutation {
            copy(
                initialPage = FeedbackScreen.Reason(
                    postId = navArgs.postId,
                    userId = navArgs.userId,
                    userName = navArgs.userName,
                )
            )
        }
    }
}

@Singleton
class FeedbackNavigationRepository @Inject constructor() :
    BaseNavigationRepository<FeedbackScreen>()
