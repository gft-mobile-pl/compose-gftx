package com.gft.compose.ui.interactionfilter

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gft.compose.interaction.LifecycleAwareBackHandler
import com.gft.compose.ui.common.CenteredColumn
import com.gft.compose.ui.common.Screen

private const val ScreenADestination = "screenA"
private const val ScreenBDestination = "screenB"

@Composable
fun LifecycleAwareBackHandlerTestScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "screenA"
    ) {
        composable(
            route = ScreenADestination,
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(durationMillis = 2500, easing = EaseIn),
                    targetOffsetX = { -it }
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = 2500, easing = EaseIn),
                    initialOffsetX = { -it }
                )
            }
        ) {
            LifecycleAwareBackHandler {
                println("#Test Screen A: Back clicked")
            }

            Screen(
                modifier = Modifier.background(Color(0xfff0fff0)), title = "Screen A"
            ) {
                CenteredColumn {
                    Card {
                        CenteredColumn {
                            Button(onClick = {
                                navController.navigate(ScreenBDestination)
                            }) {
                                Text(text = "Go to Screen B")
                            }
                        }
                    }

                }

            }
        }

        composable(route = ScreenBDestination, enterTransition = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = 2500, easing = EaseIn),
                initialOffsetX = { it }
            )
        }, popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(durationMillis = 2500, easing = EaseIn),
                targetOffsetX = { it }
            )
        }) {
            Screen(
                modifier = Modifier.background(Color(0xfffff0f0)), title = "Screen B"
            ) {
                Card {
                    CenteredColumn {
                        Button(onClick = {
                            navController.popBackStack()
                        }) {
                            Text(text = "Go back")
                        }
                    }
                }
            }
        }
    }
}
