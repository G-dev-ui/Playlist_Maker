package com.example.playlist_maker.player.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val viewModel = ServiceLocator.mediaPlayerViewModel

        when (action) {
            "ACTION_PLAY" -> {
                viewModel.startMediaPlayer()
                MediaNotificationManager(context, viewModel).showNotification(true)
            }
            "ACTION_PAUSE" -> {
                viewModel.pauseMediaPlayer()
                MediaNotificationManager(context, viewModel).showNotification(false)
            }
        }
    }

}
