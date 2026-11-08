package com.gft.compose.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Converts this [StateFlow] to a [State] that updates based on [lifecycle].
 *
 * ```
 * val uiState by dataFlow.map<UIState> { ... }.toState(minActiveState = Lifecycle.State.STARTED)
 * ```
 *
 * Updates [State.value]:
 * - once, when the [State] is created,
 * - continuously, while [lifecycle] is at least [minActiveState].
 *
 * Similar to [androidx.lifecycle.compose.collectAsStateWithLifecycle], but skips reading
 * [StateFlow.value] for the initial value when [lifecycle] is already at least
 * [minActiveState], because collection provides that value anyway.
 *
 * Notes:
 * - Stops collecting when [lifecycle] drops below [minActiveState] and restarts when it returns.
 * - Stops collecting and removes the lifecycle observer when this call leaves the composition.
 *
 * @param minActiveState minimum [Lifecycle.State] in which to collect; must be at least
 *   [Lifecycle.State.CREATED]
 * @param lifecycle lifecycle that controls collection; defaults to the lifecycle of
 *   [LocalLifecycleOwner]
 * @return [State] holding the latest value of this [StateFlow]
 * @throws IllegalArgumentException if [minActiveState] is [Lifecycle.State.INITIALIZED]
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
