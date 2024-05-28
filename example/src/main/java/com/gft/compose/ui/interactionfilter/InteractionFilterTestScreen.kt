package com.gft.compose.ui.interactionfilter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gft.compose.interaction.InteractionFilter
import com.gft.compose.ui.common.CenteredColumn
import com.gft.compose.ui.common.LargeText
import com.gft.compose.ui.common.Screen

private const val ScreenADestination = "screenA"
private const val ScreenBDestination = "screenB"

@Composable
fun InteractionFilterTestScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "screenA"
    ) {
        composable(ScreenADestination) {
            Screen("Screen A") {
                val interactionEnabled = remember {
                    mutableStateOf(true)
                }

                Card(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = interactionEnabled.value, onCheckedChange = { checked ->
                            interactionEnabled.value = checked
                        })
                        Text(text = "Interaction enabled")
                    }
                }

                Card(
                    modifier = Modifier.padding(12.dp)
                ) {
                    val counter = remember { mutableIntStateOf(0) }
                    val editTextValue = remember { mutableStateOf("") }


                    InteractionFilter(
                        interactionEnabled = interactionEnabled.value
                    ) {
                        CenteredColumn {
                            LargeText("Counter: ${counter.intValue}")
                            Button(onClick = {
                                counter.intValue++
                            }) {
                                Text(text = "Increase counter")
                            }
                            OutlinedTextField(value = editTextValue.value, onValueChange = { text ->
                                editTextValue.value = text
                            })
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                Button(onClick = {
                    navController.navigate(ScreenBDestination)
                }) {
                    Text(text = "Go to next screen")
                }
            }
        }
    }
}
