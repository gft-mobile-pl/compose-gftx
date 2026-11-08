package com.gft.compose.interaction

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

/**
 * Consumes back events and calls [onBack] when the lifecycle reaches the required state.
 *
 * ```
 * LifecycleAwareBackHandler { closeScreen() }
 * ```
 *
 * - Intercepts back events while [enabled] and registered with the navigation event dispatcher,
 *   even before [lifecycleOwner] reaches [minActiveStateForEventsPropagation].
 * - Calls [onBack] only when [lifecycleOwner] is at least [minActiveStateForEventsPropagation]
 *   when the back event completes. Otherwise, consumes the event without calling [onBack].
 * - Requires [minActiveStateForEventsPropagation] to be at least [Lifecycle.State.STARTED].
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

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = enabled,
        onBackCompleted = {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(minActiveStateForEventsPropagation)) {
                onBack()
            }
        },
    )
}
