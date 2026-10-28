package com.cornellappdev.resell.android.util

import com.cornellappdev.resell.android.model.api.Post
import com.cornellappdev.resell.android.model.classes.Listing
import kotlinx.coroutines.CancellationException

fun List<Listing>.excludingIds(seen: List<Listing>): List<Listing> {
    val seenIds = seen.map { it.id }.toSet()
    return filterNot { it.id in seenIds }
}

sealed class ScrollPageResult {
    data class Success(
        val newListings: List<Listing>,
        val nextPage: Int,
        val endReached: Boolean
    ) : ScrollPageResult()

    data class Error(val exception: Exception) : ScrollPageResult()
}

suspend fun fetchNextScrollPage(
    currentPage: Int,
    currentListings: List<Listing>,
    fetch: suspend (nextPage: Int) -> List<Post>,
): ScrollPageResult {
    return try {
        val nextPage = currentPage + 1
        val newListings = fetch(nextPage).map { it.toListing() }.excludingIds(currentListings)
        ScrollPageResult.Success(newListings, nextPage, newListings.isEmpty())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ScrollPageResult.Error(e)
    }
}
