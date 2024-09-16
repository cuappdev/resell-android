package com.cornellappdev.resell.android.util

/**
 * A simple class that represents a UI event.
 *
 * Distinct from the payload alone since different events will still
 * trigger LaunchedEffect.
 */
class UIEvent<T>(
    val payload: T,
)
