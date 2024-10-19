package com.cornellappdev.resell.android.util

fun Pair<Int, Int>.toSortedPair() = if (first <= second) this else second to first