package com.gft.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Converts [StateFlow] to [State].
 * The [State.value] is updated:
 * - one time once the [State] is created,
 * - constantly when the state of the provided [lifecycle] is at least [minActiveState].
 *
 * This method is similar to [androidx.lifecycle.compose.collectAsStateWithLifecycle]
 * but it does not use [StateFlow.value] to obtain the initial value if current state
 * of the provided [lifecycle] is at least [minActiveState] as the state will be provided
 * anyway via collection.
 */
@Composable
fun <T> StateFlow<T>.toState(
    minActiveState: Lifecycle.State,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
): State<T> {
    require(minActiveState !== Lifecycle.State.INITIALIZED) {
        "minActiveState must be at least Lifecycle.State.CREATED."
    }

    val state = remember { mutableStateOf<MutableState<T>?>(null) }
    remember(this, lifecycle, minActiveState) {
        var flowCollectionJob: Job? = null
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event.targetState.isAtLeast(minActiveState)) {
                if (flowCollectionJob == null) {
                    flowCollectionJob = CoroutineScope(Dispatchers.Main.immediate).launch {
                        collect { value ->
                            if (state.value == null) {
                                state.value = mutableStateOf(value)
                            } else {
                                state.value!!.value = value
                            }
                        }
                    }
                }
            } else {
                flowCollectionJob?.apply {
                    flowCollectionJob = null
                    cancel()
                }
            }
        }

        lifecycle.addObserver(lifecycleObserver)
        if (state.value == null) {
            state.value = mutableStateOf(this.value)
        }

        object : RememberObserver {
            override fun onAbandoned() = Unit
            override fun onRemembered() = Unit
            override fun onForgotten() {
                lifecycle.removeObserver(lifecycleObserver)
                flowCollectionJob?.cancel()
            }
        }
    }

    return state.value!!
}

