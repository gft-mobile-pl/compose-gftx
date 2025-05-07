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
