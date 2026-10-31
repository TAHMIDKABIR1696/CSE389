package com.example.assignment2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

const val CUSTOM_ACTION = "com.example.assignment2.CUSTOM_MESSAGE"

// ---------- SCREEN 1: dropdown (spinner) + Proceed ----------
@Composable
fun BroadcastSelectScreen(onProceed: (String) -> Unit) {
    val options = listOf("Custom", "System battery")
    var selected by remember { mutableStateOf(options[0]) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select a broadcast type", fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(selected, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selected = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        FilledTonalButton(onClick = { onProceed(selected) }) {
            Text("Proceed")
        }
    }
}

// ---------- SCREEN 2 (option 1): user types plain text ----------
@Composable
fun CustomInputScreen(onNext: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter the message to broadcast", fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))
        FilledTonalButton(
            onClick = { onNext(text) },
            enabled = text.isNotBlank()
        ) {
            Text("Next")
        }
    }
}

// ---------- SCREEN 3 (option 1): custom broadcast receiver ----------
@Composable
fun CustomReceiveScreen(message: String) {
    val context = LocalContext.current
    var received by remember { mutableStateOf("Waiting for broadcast...") }

    DisposableEffect(message) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                received = intent?.getStringExtra("message") ?: "No message"
            }
        }
        // Register the receiver (only this app can send to it)
        ContextCompat.registerReceiver(
            context, receiver, IntentFilter(CUSTOM_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        // Send the text we got from the previous screen
        sendCustomBroadcast(context, message)

        onDispose { context.unregisterReceiver(receiver) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Custom Broadcast Receiver", fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Text("Received message:", fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Text(received, fontSize = 22.sp)
        Spacer(Modifier.height(24.dp))
        FilledTonalButton(onClick = { sendCustomBroadcast(context, message) }) {
            Text("Send again")
        }
    }
}

private fun sendCustomBroadcast(context: Context, message: String) {
    val intent = Intent(CUSTOM_ACTION)
        .setPackage(context.packageName)
        .putExtra("message", message)
    context.sendBroadcast(intent)
}

// ---------- SCREEN 2 (option 2): battery percentage ----------
@Composable
fun BatteryScreen() {
    val context = LocalContext.current
    var percent by remember { mutableStateOf<Int?>(null) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (level >= 0 && scale > 0) percent = level * 100 / scale
            }
        }
        val stickyIntent = ContextCompat.registerReceiver(
            context, receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            ContextCompat.RECEIVER_EXPORTED
        )
        stickyIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) percent = level * 100 / scale
        }

        onDispose { context.unregisterReceiver(receiver) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("System Battery Receiver", fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Text("Battery level:", fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Text(percent?.let { "$it%" } ?: "Waiting...", fontSize = 40.sp)
    }
}
