package com.gft.compose.ui.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gft.compose.interaction.clearFocusOnClick

@Composable
fun ClearFocusOnClickTestScreen() {
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .clearFocusOnClick()
            .padding(64.dp)
    ) {
        OutlinedTextField(value = "Test", onValueChange = {})
    }
}