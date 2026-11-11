package com.cornellappdev.resell.android.viewmodel.root

import com.cornellappdev.resell.android.model.core.DeepLinkRepository
import com.cornellappdev.resell.android.util.UIEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Requests that the system share sheet be shown with some text. The root UI launches it.
 */
@Singleton
class RootShareRepository @Inject constructor() {

    private val _shareEvent = MutableStateFlow<UIEvent<String>?>(null)
    val shareEvent = _shareEvent.asStateFlow()

    fun sharePost(postId: String, title: String) {
        val item = title.ifBlank { "item" }
        _shareEvent.value = UIEvent(
            "Check out this AWESOME $item on Resell!\n${DeepLinkRepository.productUrl(postId)}"
        )
    }
}
