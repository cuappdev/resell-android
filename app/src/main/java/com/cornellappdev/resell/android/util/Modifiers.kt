package com.cornellappdev.resell.android.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.cornellappdev.resell.android.ui.theme.Padding

/**
 * 24.dp padding.
 */
fun Modifier.defaultHorizontalPadding(scale: Float = 1f) =
    this.padding(horizontal = Padding.leftRight * scale)

@Composable
fun Modifier.clickableNoIndication(onClick: () -> Unit) = this.clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = onClick,
)
