package com.cornellappdev.resell.android.ui.components.availability

import android.util.Log
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
internal fun InteractionSurface(
    onInteraction: FlowCollector<PointerEvent>,
    modifier: Modifier = Modifier,
    filter: PointerInputFilter? = null,
    content: @Composable () -> Unit
) {
    val pointerEventChannel = remember { Channel<PointerEvent>() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        pointerEventChannel.receiveAsFlow().collect(onInteraction)
    }

    Surface(modifier = modifier.pointerInput(filter) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                coroutineScope.launch {
                    pointerEventChannel.send(event)
                }
            }
        }
    }) {
        content()
    }
}

@Composable
@Preview
private fun InteractionSurface_RUN_ME_Preview() = ResellPreview {
    InteractionSurface(onInteraction = {
        Log.d("TAG", "InteractionSurfacePreview:${it.type} ${it.calculateCentroid()} ")
    }) {
        Box(modifier = Modifier.requiredSize(800.dp))
    }
}