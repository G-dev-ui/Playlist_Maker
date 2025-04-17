package com.example.playlist_maker.player.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.player.domain.getCoverArtwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaNotificationManager(
    private val context: Context,
    private val mediaPlayerViewModel: MediaPlayerViewModel
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notificationId = 1

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "media_channel",
            "Media Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Media playback controls"
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(isPlaying: Boolean) {
        val playPauseIntent = Intent(context, MediaNotificationReceiver::class.java).apply {
            action = if (isPlaying) "ACTION_PAUSE" else "ACTION_PLAY"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, playPauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val track = mediaPlayerViewModel.currentTrack
        val coverArtworkUrl = track?.getCoverArtwork()
        val trackName = track?.trackName ?: "Unknown Track"
        val artistName = track?.artistName ?: "Unknown Artist"

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = try {
                coverArtworkUrl?.let {
                    Glide.with(context)
                        .asBitmap()
                        .load(it)
                        .submit()
                        .get()
                }
            } catch (_: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                val mediaSession = MediaSessionCompat(context, "MediaSessionTag")

                val builder = NotificationCompat.Builder(context, "media_channel")
                    .setSmallIcon(R.drawable.placeholder2)
                    .setContentTitle(trackName)
                    .setContentText(artistName)
                    .setLargeIcon(bitmap)
                    .addAction(
                        if (isPlaying) R.drawable.ic_pause_24 else R.drawable.ic_play_24_1,
                        if (isPlaying) "Pause" else "Play",
                        pendingIntent
                    )
                    .setStyle(
                        androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.sessionToken)
                            .setShowActionsInCompactView(0)
                    )
                    .setOnlyAlertOnce(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false)
                    .setOngoing(true)

                notificationManager.notify(notificationId, builder.build())
            }
        }
    }

    fun cancelNotification() {
        notificationManager.cancel(notificationId)
    }
}
