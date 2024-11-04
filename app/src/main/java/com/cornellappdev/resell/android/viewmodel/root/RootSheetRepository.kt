package com.cornellappdev.resell.android.viewmodel.root

import androidx.compose.runtime.Composable
import com.cornellappdev.resell.android.ui.components.global.ResellTextButtonContainer
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

    private val _hideFlow = MutableStateFlow<UIEvent<Unit>?>(null)

    /**
     * A flow emitting an event saying that the sheet should hide.
     */
    val hideFlow = _hideFlow.asStateFlow()

    fun showBottomSheet(sheet: RootSheet) {
        _rootSheetFlow.value = UIEvent(sheet)
    }

    /**
     * Hides the price proposal sheet.
     */
    fun hideSheet() {
        _hideFlow.value = UIEvent(Unit)
    }
}

sealed class RootSheet {
    data object LoginFailed : RootSheet()
    data object LoginCornellEmail : RootSheet()
    data class ProposalSheet(
        val confirmString: String,
        val defaultPrice: String,
        val callback: (String) -> Unit,
        val title: String,
    ) : RootSheet()

    data class WebViewSheet(
        val url: String
    ) : RootSheet()

    data object LogOut : RootSheet()
    data object Welcome : RootSheet()

    data class MeetingCancel (
        val confirmString: String,
        val closeString: String,
        val callback : () -> Unit,
        val title: String
    ) : RootSheet()

    data class MeetingDetails (
        val confirmString: String,
        val closeString: String,
        val confirmColor: ResellTextButtonContainer,
        val callback : () -> Unit,
        val title: String,
        // TODO: We should just make several more sheets (or some other multiplex) instead of this.
        //  Because this demands the VM to make UI which is poor abstraction.
        val content: @Composable () -> Unit
    ) : RootSheet()

    data class MeetingGCalSync (
        val confirmString: String,
        val closeString: String,
        val callback : () -> Unit,
        val title: String
    )
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
