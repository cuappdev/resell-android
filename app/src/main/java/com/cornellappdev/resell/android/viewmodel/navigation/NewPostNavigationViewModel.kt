package com.cornellappdev.resell.android.viewmodel.navigation

import androidx.navigation.NavHostController
import com.cornellappdev.resell.android.model.NewPostNav
import com.cornellappdev.resell.android.viewmodel.ResellViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewPostNavigationViewModel @Inject constructor(
    @NewPostNav val navHostController: NavHostController,
) : ResellViewModel<Unit>(
    initialUiState = Unit
)
