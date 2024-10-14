package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.newpost.ResellNewPostScreen
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPostNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ResellNewPostScreen>()
