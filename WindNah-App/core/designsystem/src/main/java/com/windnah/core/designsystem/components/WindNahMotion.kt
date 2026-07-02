package com.windnah.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp

object WindNahMotion {
    const val DurationShort2 = 150
    const val DurationMedium2 = 300

    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
}

@Composable
fun WindNahAnimatedDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    enter: EnterTransition = fadeIn(
        animationSpec = tween(
            durationMillis = WindNahMotion.DurationMedium2,
            easing = WindNahMotion.EmphasizedDecelerate,
        ),
    ) + scaleIn(
        initialScale = 0.96f,
        animationSpec = tween(
            durationMillis = WindNahMotion.DurationMedium2,
            easing = WindNahMotion.EmphasizedDecelerate,
        ),
    ),
    exit: ExitTransition = fadeOut(
        animationSpec = tween(
            durationMillis = WindNahMotion.DurationShort2,
            easing = WindNahMotion.EmphasizedAccelerate,
        ),
    ) + scaleOut(
        targetScale = 0.96f,
        animationSpec = tween(
            durationMillis = WindNahMotion.DurationShort2,
            easing = WindNahMotion.EmphasizedAccelerate,
        ),
    ),
    content: @Composable BoxScope.(dismiss: (afterDismiss: (() -> Unit)?) -> Unit) -> Unit,
) {
    val visibleState = remember { MutableTransitionState(false) }
    var pendingAfterDismiss by remember { mutableStateOf<(() -> Unit)?>(null) }

    fun dismiss(afterDismiss: (() -> Unit)? = null) {
        pendingAfterDismiss = afterDismiss
        visibleState.targetState = false
    }

    LaunchedEffect(Unit) {
        visibleState.targetState = true
    }

    LaunchedEffect(visibleState.isIdle, visibleState.currentState, visibleState.targetState) {
        if (visibleState.isIdle && !visibleState.currentState && !visibleState.targetState) {
            val afterDismiss = pendingAfterDismiss
            pendingAfterDismiss = null
            onDismissRequest()
            afterDismiss?.invoke()
        }
    }

    Dialog(
        onDismissRequest = { dismiss() },
        properties = properties,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = enter,
                exit = exit,
                modifier = modifier,
            ) {
                Box {
                    content(::dismiss)
                }
            }
        }
    }
}

@Composable
fun WindNahDialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        content = { content() },
    )
}
