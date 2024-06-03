package com.gft.compose.interaction

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * An effect for handling presses of the system back button.
 *
 * This method is similar to [BackHandler] however, it adds the possibility to choose the required minimum lifecycle state
 * to propagate the back pressed event to the handler.
 *
 * Note that regardless of the value of `minActiveStateForEventsPropagation`, this composable intercepts the back pressed events
 * whenever the current state is at least `Lifecycle.State.STARTED` (and `enabled` is set to `true`), but it simply does not pass the events to the handler
 * if the current state is not at least `minActiveStateForEventsPropagation`.
 */
@Composable
fun LifecycleAwareBackHandler(
    enabled: Boolean = true,
    minActiveStateForEventsPropagation: Lifecycle.State = Lifecycle.State.RESUMED,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBack: () -> Unit,
) {
    check(minActiveStateForEventsPropagation.isAtLeast(Lifecycle.State.STARTED)) {
        "minActiveState has to be at least Lifecycle.State.STARTED"
    }

    BackHandler(enabled) {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(minActiveStateForEventsPropagation)) {
            onBack()
        }
    }
}
