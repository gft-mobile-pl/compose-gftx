package com.gft.compose.interaction

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Blocks user input to [content] while the lifecycle is below [minActiveState].
 *
 * ```
 * InteractionFilter(minActiveState = Lifecycle.State.RESUMED) {
 *     Button(onClick = ::submit) { Text("Submit") }
 * }
 * ```
 *
 * * Reads the lifecycle from [androidx.lifecycle.compose.LocalLifecycleOwner].
 * * Checks pointer and key input against the current lifecycle state on every event.
 * * Updates focusability on each [Lifecycle.Event].
 * * See the `interactionEnabled` overload for the exact blocking behavior.
 *
 * @param minActiveState lowest [Lifecycle.State] at which [content] accepts input
 * @param content content to filter, laid out in a [Box]
 */
@Composable
fun InteractionFilter(
    minActiveState: Lifecycle.State,
    content: @Composable BoxScope.() -> Unit,
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

/**
 * Blocks user input to [content] while [interactionEnabled] is `false`.
 *
 * ```
 * InteractionFilter(interactionEnabled = !isLoading) {
 *     LoginForm()
 * }
 * ```
 *
 * While disabled:
 * * Consumes pointer events in [PointerEventPass.Initial], before [content] sees them, so child
 *   gesture handlers ignore them.
 * * Consumes hardware key events before they reach the software keyboard or [content].
 * * Makes [content] unfocusable through [androidx.compose.ui.focus.FocusProperties.canFocus].
 *
 * @param interactionEnabled whether [content] accepts pointer, key, and focus input
 * @param content content to filter, laid out in a [Box]
 * @see interactionFilter
 */
@Composable
fun InteractionFilter(
    interactionEnabled: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionEnabledState = rememberUpdatedState(interactionEnabled)
    InteractionFilter(
        inputEnabled = { interactionEnabledState.value },
        focusEnabled = interactionEnabled,
        content = content
    )
}

@Composable
private fun InteractionFilter(
    inputEnabled: () -> Boolean,
    focusEnabled: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier.interactionFilter(inputEnabled, focusEnabled),
        content = content
    )
}

/**
 * Blocks user input to this element and its children while [interactionEnabled] is `false`.
 *
 * ```
 * Column(Modifier.interactionFilter(interactionEnabled = !isLoading)) { ... }
 * ```
 *
 * * Applies the same pointer, key, and focus blocking as [InteractionFilter], without adding a
 *   [Box].
 *
 * @param interactionEnabled whether this element accepts pointer, key, and focus input
 * @return this [Modifier] chained with the input filter
 */
@Composable
fun Modifier.interactionFilter(
    interactionEnabled: Boolean,
): Modifier {
    val interactionEnabledState = rememberUpdatedState(interactionEnabled)
    return this then Modifier.interactionFilter(
        inputEnabled = { interactionEnabledState.value },
        focusEnabled = interactionEnabled
    )
}

private fun Modifier.interactionFilter(
    inputEnabled: () -> Boolean,
    focusEnabled: Boolean,
) = this then Modifier
    .focusProperties {
        canFocus = focusEnabled
    }
    .pointerInput(inputEnabled) {
        awaitEachGesture {
            var isAnyPointerPressed: Boolean
            do {
                isAnyPointerPressed = awaitPointerEvent(pass = PointerEventPass.Initial)
                    .changes
                    .onEach { change ->
                        if (!inputEnabled()) {
                            change.consume()
                        }
                    }
                    .any(PointerInputChange::pressed)
            } while (isAnyPointerPressed)
        }
    }
    .onPreInterceptKeyBeforeSoftKeyboard { !inputEnabled() }
