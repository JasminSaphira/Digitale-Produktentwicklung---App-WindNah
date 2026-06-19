package com.windnah.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Branding splash shown while [AppViewModel.startDestination] is null
 * (i.e. DataStore has not yet resolved the first-launch state).
 *
 * Layout derived from Figma node 120:2155 – "Launch" frame.
 *
 * Measurements (all relative to content area below status bar, frame = 412 × 865 dp):
 *   Logo      : top = 256 dp, size = 200 × 200 dp, horizontally centred
 *   App name  : top = 470 dp (container), inner padding 16 dp → Roboto Regular 32 sp / 40 lh
 *   Loader    : top = 618 dp, size = 48 × 48 dp, horizontally centred
 */
@Composable
fun LaunchScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 256.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo – 200 × 200 dp (Figma node 101:4513 "WindNah Logo")
            Image(
                painter = painterResource(R.drawable.ic_windnah_logo),
                contentDescription = "WindNah Logo",
                modifier = Modifier.size(200.dp),
            )

            // Gap: logo bottom (456 dp) → text container top (470 dp) = 14 dp
            Spacer(modifier = Modifier.height(14.dp))

            // App name (Figma node 120:2164) – headlineLarge matches WindNah/headline/large spec:
            // Roboto Regular, 32 sp, lineHeight 40 sp, letter-spacing 0
            Text(
                text = "WindNah",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // Gap: text container bottom (542 dp) → loading indicator top (618 dp) = 76 dp
            Spacer(modifier = Modifier.height(76.dp))

            // Loading indicator (Figma node 120:3016) – 48 × 48 dp
            // trackColor = primaryContainer (#C0EFB0), active color = primary (#3F6836)
            WindNahLoadingIndicator(
                modifier = Modifier.size(48.dp),
            )
        }
    }
}

@Composable
private fun WindNahLoadingIndicator(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WindNahLoadingIndicator")

    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "LoadingRotation",
    )

    val color = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier.rotate(rotation.value),
    ) {
        drawRoundedStar(color = color)
    }
}

private fun DrawScope.drawRoundedStar(
    color: androidx.compose.ui.graphics.Color,
) {
    val spikes = 12
    val outerRadius = size.minDimension / 2f
    val innerRadius = outerRadius * 0.78f
    val centerX = size.width / 2f
    val centerY = size.height / 2f

    val path = Path()

    for (i in 0 until spikes * 2) {
        val angle = -PI / 2 + i * PI / spikes
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = centerX + cos(angle).toFloat() * radius
        val y = centerY + sin(angle).toFloat() * radius

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    path.close()

    drawPath(
        path = path,
        color = color,
    )
}
