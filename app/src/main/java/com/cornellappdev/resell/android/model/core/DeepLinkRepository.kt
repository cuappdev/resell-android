package com.cornellappdev.resell.android.model.core

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds an incoming `resell://product/<id>` deep link until the app is ready to act on it.
 */
@Singleton
class DeepLinkRepository @Inject constructor() {

    private val _pendingPostId = MutableStateFlow<String?>(null)
    val pendingPostId = _pendingPostId.asStateFlow()

    /**
     * Records the post in [uri] if it is a product link. Anything else is ignored.
     */
    fun onLinkReceived(uri: Uri?) {
        if (uri?.scheme != SCHEME || uri.host != PRODUCT_HOST) return

        val postId = uri.lastPathSegment
        if (!postId.isNullOrBlank()) {
            _pendingPostId.value = postId
        }
    }

    /**
     * Clears the pending link, but only if it is still [postId] so a newer link isn't dropped.
     */
    fun onLinkHandled(postId: String) {
        _pendingPostId.compareAndSet(postId, null)
    }

    companion object {
        private const val SCHEME = "resell"
        private const val PRODUCT_HOST = "product"

        fun productUrl(postId: String) = "$SCHEME://$PRODUCT_HOST/$postId"
    }
}
