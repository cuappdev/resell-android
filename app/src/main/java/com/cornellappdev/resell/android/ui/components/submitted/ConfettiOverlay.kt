package com.cornellappdev.resell.android.ui.components.submitted

import androidx.compose.animation.core.LinearEasing

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
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.viewmodel.submitted.ConfettiViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ConfettiOverlay(
    confettiViewModel: ConfettiViewModel,
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

    val maxDurationMs = 10_000

    LaunchedEffect(uiState.showing) {
        if (uiState.showing) {
            delay(maxDurationMs.toLong())
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

    val animatedYOffsets = particles.map { particle ->
        animateFloatAsState(
            targetValue = if (startFall) screenHeightPx + 200f else -50f,
            animationSpec = tween(
                durationMillis = (particle.speed * 5).toInt(),
                easing = LinearEasing
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
                start = Offset(xPos - particleRadius * 0.8f, yOffset - particleRadius * 0.8f),
                end = Offset(xPos + particleRadius * 0.8f, yOffset + particleRadius * 0.8f)
            )

            // dimensions for rectangular particles
            val thicknessType = Random.nextFloat()
            val rectWidth = when {
                thicknessType < 0.33f -> particle.size * 0.25f
                thicknessType < 0.66f -> particle.size * 0.5f
                else -> particle.size * 0.8f
            }
            val rectHeight = (particle.size * 1.5f + particle.size * Random.nextFloat() * 0.8f)

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