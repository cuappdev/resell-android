package com.cornellappdev.resell.android.ui.components.submitted

import androidx.compose.animation.core.FastOutSlowInEasing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.resell.android.viewmodel.submitted.ConfettiViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@Composable
fun ConfettiOverlay(
    confettiViewModel: ConfettiViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    particleCount: Int = 30,
    colors: List<Color> = listOf(
        Color(0xFFAD68E3),
        Color(0xFFDE6CD3),
        Color(0xFFDF9856)
    )
) {
    val uiState = confettiViewModel.collectUiStateValue()

    if (!uiState.showing) {
        return
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Generate random particles with varied properties for visual diversity
    val particles = remember((uiState.showing)) {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                size = Random.nextInt(30, 40),
                color = colors.random(),
                // 25% particles are circles and 75% particles are rectangles
                shape = if (Random.nextFloat() < 0.25f) ConfettiShape.CIRCLE else ConfettiShape.RECTANGLE,
                rotation = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 150f + 100f
            )
        }
    }

    val maxDurationMs : Duration = 10_000.milliseconds

    // Hide confetti after animation completes
    LaunchedEffect(uiState.showing) {
        if (uiState.showing) {
            delay(maxDurationMs.toLong(DurationUnit.MILLISECONDS))
            confettiViewModel.onAnimationFinished()
        }
    }

    // Use a started flag to trigger animation
    var startFall by remember(uiState.showing) { mutableStateOf(false) }

    LaunchedEffect(uiState.showing) {
        if (uiState.showing) {
            startFall = true
        }
    }

    // Animate each particle from above the screen to below it
    // Multiplies the speed by 5 to convert from pixels to reasonable animation duration
    val animatedYOffsets = particles.map { particle ->
        animateFloatAsState(
            targetValue = if (startFall) screenHeightPx + 200f else -50f,
            animationSpec = tween(
                durationMillis = (particle.speed * 5).toInt(),
                easing = FastOutSlowInEasing
            ),
            label = ""
        ).value
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val widthPx = size.width

        particles.forEachIndexed { index, particle ->
            val yOffset = animatedYOffsets[index]
            val xPos = particle.x * widthPx

            val particleRadius = particle.size.toFloat()

            // Create a gradient brush per particle
            val brush = Brush.linearGradient(
                colors = colors,
                // We're extending the gradient by 0.8x the radius to make sure all three colors are visible in the particles
                start = Offset(xPos - particleRadius * 0.8f, yOffset - particleRadius * 0.8f),
                end = Offset(xPos + particleRadius * 0.8f, yOffset + particleRadius * 0.8f)
            )

            // Create rectangle widths for visual diversity
            val thicknessType = Random.nextFloat()
            val rectWidth = when {
                thicknessType < 0.33f -> particle.size * 0.25f // for thinner rectangles
                thicknessType < 0.66f -> particle.size * 0.5f// for thicker rectangles
                else -> particle.size * 0.8f// for THICK rectangles
            }

            // Make rectangles taller to resemble more like confetti strips
            val rectHeight = (particle.size * 1.5f + particle.size * Random.nextFloat() * 0.8f)

            // Where particles are actually drawn on the screen and rendered
            when (particle.shape) {
                ConfettiShape.CIRCLE -> {
                    drawCircle(
                        brush = brush,
                        radius = particle.size.toFloat(),
                        center = Offset(xPos, yOffset)
                    )
                }

                ConfettiShape.RECTANGLE -> {
                    withTransform({
                        rotate(degrees = particle.rotation, pivot = Offset(xPos, yOffset))
                    }) {
                        // Offset by half dimensions to center the rectangle on its position
                        drawRoundRect(
                            brush = brush,
                            topLeft = Offset(
                                xPos - (particle.size / 8f),
                                yOffset - (particle.size / 2f)
                            ),
                            size = Size(
                                width = rectWidth,
                                height = rectHeight
                            ),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                    }
                }
            }
        }
    }
}

// Note: This preview won't show animation since it requires a ViewModel.
@Preview(showBackground = true)
@Composable
fun ConfettiOverlayPreview() {
    ConfettiOverlay()
}