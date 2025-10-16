package com.cornellappdev.resell.android.ui.components.submitted

import androidx.compose.animation.core.LinearEasing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ConfettiOverlay(
    modifier: Modifier = Modifier,
    particleCount: Int = 40,
    colors: List<Color> = listOf(
        Color(0xFFAD68E3),
        Color(0xFFDE6CD3),
        Color(0xFFDF9856)
    )
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                size = Random.nextInt(10, 25),
                color = colors.random(),
                shape = if (Random.nextBoolean()) ConfettiShape.CIRCLE else ConfettiShape.RECTANGLE,
                rotation = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 150f + 100f
            )
        }
    }

    var startFall by remember { mutableStateOf(false) }
    var isAnimationFinished by remember { mutableStateOf(false) }

    val MAX_DURATION_MS = 5_000

    LaunchedEffect(Unit) {
        startFall = true
        delay(MAX_DURATION_MS.toLong())
        isAnimationFinished = true
    }

    if (isAnimationFinished) {
        return
    }

    Box(modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yOffset by animateFloatAsState(
                targetValue = if (startFall) screenHeight.toFloat() + 50f else -50f,
                animationSpec = tween(
                    durationMillis = (particle.speed * 5).toInt(),
                    easing = LinearEasing
                )
            )

            val xPos = particle.x * screenWidth

            // Define dimensions based on shape
            val confettiSizeModifier = when (particle.shape) {
                ConfettiShape.CIRCLE -> Modifier.size(particle.size.dp)
                ConfettiShape.RECTANGLE -> Modifier
                    .width((particle.size / 4f).coerceAtLeast(2f).dp)
                    .height(particle.size.dp)
            }

            Box(
                Modifier
                    .offset(x = xPos.dp, y = yOffset.dp)
                    .then(confettiSizeModifier)
                    .graphicsLayer {
                        rotationZ = if (particle.shape == ConfettiShape.RECTANGLE) particle.rotation else 0f
                    }
                    .background(
                        color = particle.color,
                        shape = if (particle.shape == ConfettiShape.CIRCLE) CircleShape else RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Preview
@Composable
fun ConfettiPreview() {
    ConfettiOverlay(
        modifier = Modifier.fillMaxSize()
    )
}