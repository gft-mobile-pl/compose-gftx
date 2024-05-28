package com.gft.compose.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun VeryLargeText(text: String) {
    Text(
        text = text,
        fontSize = 32.sp
    )
}

@Composable
fun LargeText(text: String) {
    Text(
        text = text,
        fontSize = 24.sp
    )
}
