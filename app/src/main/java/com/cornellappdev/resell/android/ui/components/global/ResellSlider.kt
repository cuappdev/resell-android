package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.ResellPurple
import com.cornellappdev.resell.android.ui.theme.ResellPurpleTransparent
import com.cornellappdev.resell.android.ui.theme.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResellSlider(range: IntRange, lowestValue: Int, highestValue: Int, onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit) {
    RangeSlider(
    value = range.first.toFloat()..range.last.toFloat(),
    onValueChange = onRangeChanged,
    valueRange = lowestValue.toFloat()..highestValue.toFloat(),
    colors = SliderDefaults.colors(
    thumbColor = Color.White,
    activeTrackColor = ResellPurple,
    inactiveTrackColor = ResellPurpleTransparent,
    activeTickColor = ResellPurpleTransparent,
    inactiveTickColor = ResellPurpleTransparent
    ),
    steps = 0,
    startThumb = { ThumbComposable() },
    endThumb = { ThumbComposable() },
    track = {
        SliderDefaults.Track(
            rangeSliderState = it, colors = SliderColors(
                thumbColor = Color.White,
                activeTrackColor = ResellPurple,
                activeTickColor = ResellPurpleTransparent,
                inactiveTrackColor = ResellPurpleTransparent,
                inactiveTickColor = ResellPurpleTransparent,
                disabledThumbColor = Color.White,
                disabledActiveTrackColor = ResellPurple,
                disabledActiveTickColor = ResellPurpleTransparent,
                disabledInactiveTrackColor = ResellPurpleTransparent,
                disabledInactiveTickColor = ResellPurpleTransparent,
            ),
            modifier = Modifier.height(8.dp),
            thumbTrackGapSize = 0.dp,
            drawStopIndicator = {}
        )
    }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThumbComposable() {
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(color = Color.White, shape = CircleShape)
            .border(1.dp, Stroke, CircleShape)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PreviewResellSlider(){
    // Use interactive mode
    ResellSlider(range = 20..80, lowestValue = 0, highestValue = 100, onRangeChanged = {})
}