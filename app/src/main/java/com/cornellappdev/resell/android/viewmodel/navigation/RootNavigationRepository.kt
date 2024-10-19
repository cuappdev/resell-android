package com.cornellappdev.resell.android.viewmodel.navigation

import com.cornellappdev.resell.android.ui.screens.root.ResellRootRoute
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootNavigationRepository @Inject constructor() :
    BaseNavigationRepository<ResellRootRoute>()
