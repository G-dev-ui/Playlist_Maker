package com.example.playlist_maker.player.data

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.playlist_maker.player.domain.MediaRepository

class MediaRepositoryImpl(context: Context) : MediaRepository {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    override fun prepareMediaPlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        val mediaItem = MediaItem.fromUri(trackUrl)
        exoPlayer.setMediaItem(mediaItem)

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> onPrepared.invoke()
                    Player.STATE_ENDED -> onCompletion.invoke()
                }
            }
        })
        exoPlayer.prepare()
    }

    override fun startMediaPlayer() {
        exoPlayer.play()
    }

    override fun pauseMediaPlayer() {
        exoPlayer.pause()
    }

    override fun releaseMediaPlayer() {
        exoPlayer.release()
    }
    override fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return exoPlayer.currentPosition.toInt()
    }

    override fun seekTo(position: Int) {
        exoPlayer.seekTo(position.toLong())
    }
}