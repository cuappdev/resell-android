package com.cornellappdev.android.resell.util

import android.view.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.cornellappdev.android.resell.ui.theme.Padding

fun Modifier.defaultHorizontalPadding() = this.padding(horizontal = Padding.leftRight)
