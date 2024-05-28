package com.gft.compose.interaction

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun InteractionFilter(
    interactionEnabled: Boolean,
    content: @Composable () -> Unit,
) {
    val interactionEnabledState = rememberUpdatedState(interactionEnabled)
    InteractionFilter(
        interactionEnabled = interactionEnabledState,
        content = content
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InteractionFilter(
    interactionEnabled: State<Boolean>,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .pointerInput(interactionEnabled.value) {
                if (!interactionEnabled.value) {
                    awaitEachGesture {
                        awaitPointerEvent(pass = PointerEventPass.Initial)
                            .changes
                            .forEach(PointerInputChange::consume)
                    }
                }
            }
            .onPreInterceptKeyBeforeSoftKeyboard { !interactionEnabled.value }
    ) {
        content()
    }
    KeyboardVisibilityController(interactionEnabled)
}

@Composable
private fun KeyboardVisibilityController(
    interactionEnabled: State<Boolean>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(interactionEnabled.value) {
        if (!interactionEnabled.value) {
            keyboardController?.hide()
        }
        onDispose { }
    }
}

