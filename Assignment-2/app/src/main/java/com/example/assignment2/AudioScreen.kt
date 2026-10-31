package com.example.assignment2

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val AUDIO_URL = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

@Composable
fun AudioScreen() {
    var status by remember { mutableStateOf("Loading audio...") }
    var ready by remember { mutableStateOf(false) }
    val player = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            player.setDataSource(AUDIO_URL)
            player.setOnPreparedListener {
                ready = true
                status = "Ready"
            }
            player.setOnCompletionListener { status = "Finished" }
            player.setOnErrorListener { _, _, _ ->
                status = "Error loading audio"
                true
            }
            player.prepareAsync()
        } catch (_: Exception) {
            status = "Error initializing audio"
        }

        onDispose {
            try {
                player.release()
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Audio Player", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(status, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledTonalButton(
                enabled = ready,
                onClick = {
                    player.start()
                    status = "Playing"
                }
            ) {
                Text("Play")
            }

            FilledTonalButton(
                enabled = ready,
                onClick = {
                    if (player.isPlaying) player.pause()
                    status = "Paused"
                }
            ) {
                Text("Pause")
            }

            FilledTonalButton(
                enabled = ready,
                onClick = {
                    if (player.isPlaying) player.pause()
                    player.seekTo(0)
                    status = "Stopped"
                }
            ) {
                Text("Stop")
            }
        }
    }
}
