package com.gft.compose.interaction

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

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
