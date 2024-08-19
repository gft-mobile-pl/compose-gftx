package com.gft.compose.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
inline fun Modifier.modifyIf(
    condition: Boolean,
    crossinline transformation: @Composable Modifier.() -> Modifier,
) = if (condition) transformation() else this
