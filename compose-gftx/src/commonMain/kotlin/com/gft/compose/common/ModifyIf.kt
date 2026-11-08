package com.gft.compose.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Applies [transformation] to this [Modifier] only when [condition] is `true`.
 *
 * ```
 * Modifier
 *     .fillMaxWidth()
 *     .modifyIf(isSelected) { border(2.dp, Color.Blue) }
 * ```
 *
 * * [transformation] runs with this [Modifier] as its receiver. Chain onto the receiver
 *   (e.g. `{ padding(8.dp) }`) instead of starting from a new [Modifier].
 * * [transformation] is [Composable] and runs only while [condition] is `true`. Toggling
 *   [condition] adds it to or removes it from the composition, which resets any state it
 *   remembers.
 *
 * @param condition whether to apply [transformation]
 * @param transformation modifiers to append to this [Modifier] when [condition] is `true`
 * @return the transformed [Modifier] if [condition] is `true`, otherwise this [Modifier] unchanged
 */
@Composable
inline fun Modifier.modifyIf(
    condition: Boolean,
    crossinline transformation: @Composable Modifier.() -> Modifier,
) = if (condition) transformation() else this
