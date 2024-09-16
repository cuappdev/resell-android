package com.cornellappdev.resell.android.viewmodel

import com.cornellappdev.resell.android.util.UIEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootNavigationSheetRepository @Inject constructor() {

    private val _rootSheetFlow: MutableStateFlow<UIEvent<RootSheet>?> = MutableStateFlow(null)

    /**
     * A flow emitting the current sheet to show. An emission should be handled
     * as a UI event.
     */
    val rootSheetFlow: StateFlow<UIEvent<RootSheet>?> = _rootSheetFlow.asStateFlow()

    fun showBottomSheet(sheet: RootSheet) {
        _rootSheetFlow.value = UIEvent(sheet)
    }
}

enum class RootSheet {
    LOGIN_FAILED,
    LOGIN_CORNELL_EMAIL,
    WELCOME
}

@Module
@InstallIn(SingletonComponent::class)
object RootNavigationSheetModule {

    @Provides
    @Singleton
    fun provideMySingleton(): RootNavigationSheetRepository {
        return RootNavigationSheetRepository()
    }
}
