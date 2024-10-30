package com.cornellappdev.resell.android.model.classes

import androidx.compose.runtime.Composable
import com.cornellappdev.resell.android.model.classes.ResellApiResponse.Error
import com.cornellappdev.resell.android.model.classes.ResellApiResponse.Pending
import com.cornellappdev.resell.android.model.classes.ResellApiResponse.Success
import kotlin.Any
import kotlin.IllegalStateException
import kotlin.Nothing
import kotlin.Unit

/**
 * Represents the state of an api response fetching data of type [T].
 * Can be: [Pending], which represents the call still loading in, [Error], which represents the
 * API call failing, and [Success], which contains a `data` field containing the [T] data.
 */
sealed class ResellApiResponse<out T : Any> {
    data object Pending : ResellApiResponse<Nothing>()
    data object Error : ResellApiResponse<Nothing>()
    data class Success<out T : Any>(val data: T) : ResellApiResponse<T>()


    fun <K : Any> map(transform: (T) -> K): ResellApiResponse<K> {
        return when (this) {
            is Pending -> Pending
            is Error -> Error
            is Success -> Success(transform(data))
        }
    }

    /**
     * Basically a force `!!`, but for [Success] responses.
     */
    fun asSuccess(): Success<T> {
        if (this is Success) return this
        throw IllegalStateException("Response is not a success: $this")
    }

    /**
     * Runs the block if the response is a [Success]
     */
    fun ifSuccess(block: (T) -> Unit) {
        if (this is Success) {
            block(data)
        }
    }

    @Composable
    fun composableIfSuccess(block: @Composable (T) -> Unit) {
        if (this is Success) {
            block(data)
        }
    }

    fun asSuccessOrNull(): Success<T>? {
        if (this is Success) return this
        return null
    }
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
