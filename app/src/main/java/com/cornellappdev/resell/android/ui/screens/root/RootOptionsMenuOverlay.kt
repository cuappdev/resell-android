package com.cornellappdev.resell.android.ui.screens.root

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ripple
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.Overlay
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.util.clickableNoIndication
import com.cornellappdev.resell.android.util.defaultHorizontalPadding
import com.cornellappdev.resell.android.viewmodel.root.RootOptionsMenuViewModel

@Composable
fun RootOptionsMenuOverlay(
    rootOptionsMenuViewModel: RootOptionsMenuViewModel = hiltViewModel()
) {
    val uiState = rootOptionsMenuViewModel.collectUiStateValue()

    val showPercent by animateFloatAsState(
        if (uiState.showing) 1f else 0f,
        label = "dialog shade"
    )

    if (uiState.options.isEmpty() || showPercent == 0f) {
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(showPercent)
            .clickableNoIndication {
                rootOptionsMenuViewModel.onDismiss()
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(showPercent)
                .background(Color.Black.copy(alpha = showPercent * 0.3f))
        )

        Surface(
            modifier = Modifier
                .padding(top = uiState.fromTop)
                .align(uiState.alignment)
                .defaultHorizontalPadding()
                .fillMaxWidth(.6f),
            shape = RoundedCornerShape(12.dp),
            color = Overlay,
            shadowElevation = 12.dp
        ) {

            Column {
                uiState.options.forEach { option ->
                    OptionRow(
                        text = option.title,
                        icon = painterResource(option.icon),
                        onClick = { rootOptionsMenuViewModel.onOptionClicked(option) },
                        contentColor = option.color
                    )

                    if (option != uiState.options.last()) {
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(AppDev)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionRow(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    contentColor: Color,
) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 11.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = Style.overlay,
            color = contentColor
        )

        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp),
            tint = contentColor
        )

    }
}
