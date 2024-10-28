package com.cornellappdev.resell.android.model.classes

/**
 * Represents the state of an api response fetching data of type [T].
 * Can be: [Pending], which represents the call still loading in, [Error], which represents the
 * API call failing, and [Success], which contains a `data` field containing the [T] data.
 */
sealed class ResellApiResponse<out T : Any> {
    data object Pending : ResellApiResponse<Nothing>()
    data object Error : ResellApiResponse<Nothing>()
    data class Success<out T : Any>(val data: T) : ResellApiResponse<T>()
}

sealed class ResellApiState {
    data object Success : ResellApiState()
    data object Error : ResellApiState()
    data object Loading : ResellApiState()
}

fun <T : Any> ResellApiResponse<T>.toResellApiState(): ResellApiState {
    return when (this) {
        is ResellApiResponse.Success -> ResellApiState.Success
        is ResellApiResponse.Error -> ResellApiState.Error
        is ResellApiResponse.Pending -> ResellApiState.Loading
    }
}
