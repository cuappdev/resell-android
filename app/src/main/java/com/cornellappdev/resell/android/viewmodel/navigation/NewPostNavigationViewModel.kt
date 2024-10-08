package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.newpost.ResellNewPostScreen
import com.cornellappdev.resell.android.util.UIEvent
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewPostNavigationViewModel @Inject constructor(
    val newPostNavigationRepository: NewPostNavigationRepository,
) : ResellViewModel<NewPostNavigationViewModel.NewPostNavigationUiState>(
    initialUiState = NewPostNavigationUiState()
) {

    data class NewPostNavigationUiState(
        val navigationEvent: UIEvent<ResellNewPostScreen>? = null
    )

    init {
        asyncCollect(newPostNavigationRepository.routeFlow) {
            applyMutation { copy(navigationEvent = it) }
        }
    }
}
