package com.gft.compose.interaction

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntSize

/**
 * Clears focus when the user taps this element.
 *
 * Apply it to a screen's root container to hide the keyboard when the user taps outside a
 * text field:
 * ```
 * Column(Modifier.fillMaxSize().clearFocusOnClick()) {
 *     TextField(state = rememberTextFieldState())
 * }
 * ```
 *
 * * Calls [androidx.compose.ui.focus.FocusManager.clearFocus] on the current [LocalFocusManager].
 * * Detects taps with [detectTapGestures]. Fires on pointer up, not on pointer down.
 * * Ignores taps that a child already consumed (e.g. clickable children or text fields), so
 *   tapping them keeps the current focus.
 *
 * @return this [Modifier] chained with the clear-focus tap handler
 */
fun Modifier.clearFocusOnClick() = this then ClearFocusOnClickElement

private data object ClearFocusOnClickElement : ModifierNodeElement<ClearFocusOnClickNode>() {
    override fun create() = ClearFocusOnClickNode()

    override fun update(node: ClearFocusOnClickNode) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "Clear focus on click"
    }
}

private class ClearFocusOnClickNode : CompositionLocalConsumerModifierNode, PointerInputModifierNode, DelegatingNode() {

    private val pointerInputNode = delegate(SuspendingPointerInputModifierNode { pointerInput() })

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    private suspend fun PointerInputScope.pointerInput() {
        detectTapGestures(
            onTap = {
                currentValueOf(LocalFocusManager).clearFocus()
            }
        )
    }
}
