package com.gft.compose.interaction.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

private class AlwaysHiddenTextToolbar : TextToolbar {
    override val status = TextToolbarStatus.Hidden

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) = Unit

    override fun hide() = Unit
}

@Composable
fun DisableTextToolbar(
    content: @Composable () -> Unit,
) = CompositionLocalProvider(LocalTextToolbar provides AlwaysHiddenTextToolbar(), content = content)
