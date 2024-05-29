package com.gft.compose.ui.interactionfilter

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gft.compose.interaction.InteractionFilter
import com.gft.compose.ui.common.CenteredColumn
import com.gft.compose.ui.common.LargeText
import com.gft.compose.ui.common.Screen
import kotlinx.coroutines.delay

private const val ScreenADestination = "screenA"
private const val ScreenBDestination = "screenB"

@Composable
fun InteractionFilterTestScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "screenA"
    ) {
        composable(ScreenADestination) {
            Screen(
                modifier = Modifier.background(Color(0xfff0fff0)), title = "Screen A"
            ) {
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
                InteractionFilter(
                    interactionEnabled = interactionEnabled.value
                ) {
                    CenteredColumn {
                        Card(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            val counter = remember { mutableIntStateOf(0) }
                            val editTextValue = remember { mutableStateOf("") }
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

        composable(route = ScreenBDestination, enterTransition = {
            fadeIn(
                animationSpec = tween(durationMillis = 500, easing = EaseIn)
            ) + scaleIn(
                animationSpec = tween(durationMillis = 3000, easing = EaseIn), initialScale = 0.9f
            )
        }, popExitTransition = {
            fadeOut(
                animationSpec = tween(delayMillis = 2500, durationMillis = 500, easing = EaseIn)
            ) + scaleOut(
                animationSpec = tween(durationMillis = 3000, easing = EaseIn), targetScale = 0.9f
            )
        }) {
            Screen(
                modifier = Modifier.background(Color(0xfffff0f0)), title = "Screen B"
            ) {
                val minActiveState = remember {
                    mutableStateOf(Lifecycle.State.RESUMED)
                }
                LaunchedEffect(Unit) {
                    delay(1500)
                    minActiveState.value = Lifecycle.State.STARTED
                }
                LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
                    minActiveState.value = Lifecycle.State.RESUMED
                }

                InteractionFilter(minActiveState = minActiveState.value) {
                    val counter = remember { mutableIntStateOf(0) }
                    val editTextValue = remember { mutableStateOf("") }
                    CenteredColumn {
                        Card(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            CenteredColumn {
                                LargeText("Counter: ${counter.intValue}")
                                Button(onClick = {
                                    counter.intValue++
                                }) {
                                    Text(text = "Increase counter")
                                }
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(value = editTextValue.value, onValueChange = { text ->
                                    editTextValue.value = text
                                })
                                Spacer(Modifier.height(16.dp))
                                Text("minActiveState = ${minActiveState.value}")
                            }
                        }
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp), text = "💡 As long as animation is in progress current state is STARTED"
                        )
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = {
                            navController.popBackStack()
                        }) {
                            Text(text = "Back")
                        }
                    }
                }
            }
        }
    }
}
