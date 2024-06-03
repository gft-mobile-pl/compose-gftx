package com.gft.compose.interaction

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun InteractionFilter(
    minActiveState: Lifecycle.State,
    content: @Composable () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentMinActiveState = rememberUpdatedState(newValue = minActiveState)
    val interactionEnabled = rememberUpdatedState(newValue = lifecycleOwner.isStateAtLeast(minActiveState))

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, _ ->
            (interactionEnabled as MutableState).value = lifecycleOwner.isStateAtLeast(currentMinActiveState.value)
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    InteractionFilter(
        inputEnabled = remember(lifecycleOwner) {
            { lifecycleOwner.isStateAtLeast(currentMinActiveState.value) }
        },
        focusEnabled = interactionEnabled.value,
        content = content
    )
}

private fun LifecycleOwner.isStateAtLeast(state: Lifecycle.State) = lifecycle.currentState.isAtLeast(state)

@Composable
fun InteractionFilter(
    interactionEnabled: Boolean,
    content: @Composable () -> Unit,
) {
    val interactionEnabledState = rememberUpdatedState(interactionEnabled)
    InteractionFilter(
        inputEnabled = { interactionEnabledState.value },
        focusEnabled = interactionEnabled,
        content = content
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InteractionFilter(
    inputEnabled: () -> Boolean,
    focusEnabled: Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .focusProperties {
                canFocus = focusEnabled
            }
            .pointerInput(inputEnabled) {
                awaitEachGesture {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach { change ->
                            if (!inputEnabled()) {
                                change.consume()
                            }
                        }
                }
            }
            .onPreInterceptKeyBeforeSoftKeyboard { !inputEnabled() }
    ) {
        content()
    }
}
