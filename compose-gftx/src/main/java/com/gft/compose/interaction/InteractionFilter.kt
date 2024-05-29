package com.gft.compose.interaction

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun InteractionFilter(
    interactionEnabled: Boolean,
    content: @Composable () -> Unit,
) {
    val interactionEnabledState = rememberUpdatedState(newValue = interactionEnabled)
    FilterInputEvents(
        interactionEnabled = { interactionEnabledState.value },
        content = content
    )
    UpdateKeyboardVisibility(
        interactionEnabled = interactionEnabled
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FilterInputEvents(
    interactionEnabled: () -> Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach { change ->
                            if (!interactionEnabled()) {
                                change.consume()
                            }
                        }
                }
            }
            .onPreInterceptKeyBeforeSoftKeyboard { !interactionEnabled() }
    ) {
        content()
    }
}

@Composable
fun UpdateKeyboardVisibility(
    interactionEnabled: Boolean,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    SideEffect {
        if (!interactionEnabled) {
            keyboardController?.hide()
        }
    }
}
