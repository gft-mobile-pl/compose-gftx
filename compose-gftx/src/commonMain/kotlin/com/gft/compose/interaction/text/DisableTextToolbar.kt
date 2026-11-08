package com.gft.compose.interaction.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

@Stable
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

/**
 * Hides the text toolbar (copy, paste, cut, select all) for [content].
 *
 * ```
 * DisableTextToolbar {
 *     TextField(value = text, onValueChange = { text = it })
 * }
 * ```
 *
 * * Provides a [TextToolbar] that never shows a menu through [LocalTextToolbar].
 * * Text selection and keyboard shortcuts still work; only the floating menu stays hidden.
 * * Applies to every text component inside [content], including nested ones.
 *
 * @param content composable content that shows no text toolbar
 */
@Composable
fun DisableTextToolbar(
    content: @Composable () -> Unit,
) = CompositionLocalProvider(LocalTextToolbar provides AlwaysHiddenTextToolbar(), content = content)
