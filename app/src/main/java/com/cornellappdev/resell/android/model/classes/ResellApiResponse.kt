package com.cornellappdev.resell.android.model.classes

import androidx.compose.runtime.Composable
import com.cornellappdev.resell.android.model.classes.ResellApiResponse.Error
import com.cornellappdev.resell.android.model.classes.ResellApiResponse.Pending
import com.cornellappdev.resell.android.model.classes.ResellApiResponse.Success

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

    fun <K : Any> combine(other: ResellApiResponse<K>): ResellApiResponse<Pair<T, K>> {
        return when (this) {
            is Pending -> {
                when (other) {
                    is Pending -> Pending
                    is Error -> Error
                    is Success -> Pending
                }
            }

            is Error -> Error
            is Success -> when (other) {
                is Pending -> Pending
                is Error -> Error
                is Success -> Success(Pair(data, other.data))
            }
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
    fun ComposableIfSuccess(block: @Composable (T) -> Unit) {
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
        is Success -> ResellApiState.Success
        is Error -> ResellApiState.Error
        is Pending -> ResellApiState.Loading
    }
}
