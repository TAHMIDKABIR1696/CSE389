package com.example.assignment2

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Drawer menu items: label to screen name
    val menuItems = listOf(
        "Broadcast Receiver" to "broadcast_select",
        "Image Scale" to "image_scale",
        "Video" to "video",
        "Audio" to "audio",
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen, // so pinch gestures don't open the drawer
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                menuItems.forEach { (label, route) ->
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(route) {
                                popUpTo("broadcast_select") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Assignment 2") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "broadcast_select",
                modifier = Modifier.padding(padding)
            ) {
                // Screen 1: spinner + Proceed
                composable("broadcast_select") {
                    BroadcastSelectScreen(onProceed = { choice ->
                        if (choice == "Custom") {
                            navController.navigate("custom_input")
                        } else {
                            navController.navigate("battery")
                        }
                    })
                }
                // Screen 2 (option 1): type a message
                composable("custom_input") {
                    CustomInputScreen(onNext = { msg ->
                        navController.navigate("custom_receive/${Uri.encode(msg)}")
                    })
                }
                // Screen 3 (option 1): custom receiver
                composable("custom_receive/{msg}") { entry ->
                    CustomReceiveScreen(entry.arguments?.getString("msg") ?: "")
                }
                // Screen 2 (option 2): battery receiver
                composable("battery") { BatteryScreen() }

                composable("image_scale") { ImageScaleScreen() }
                composable("video") { VideoScreen() }
                composable("audio") { AudioScreen() }
            }
        }
    }
}
