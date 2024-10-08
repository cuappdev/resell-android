package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.newpost.ResellNewPostScreen
import com.cornellappdev.resell.android.util.UIEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPostNavigationRepository @Inject constructor() {

    private val _routeFlow: MutableStateFlow<UIEvent<ResellNewPostScreen>?>
        = MutableStateFlow(null)
    val routeFlow: StateFlow<UIEvent<ResellNewPostScreen>?> = _routeFlow.asStateFlow()

    fun navigate(route: ResellNewPostScreen) {
        _routeFlow.value = UIEvent(route)
    }
}
