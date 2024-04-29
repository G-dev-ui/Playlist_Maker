package com.example.playlist_maker.player.data

import android.media.MediaPlayer
import com.example.playlist_maker.player.domain.MediaRepository

class MediaRepositoryImpl( private val mediaPlayer : MediaPlayer) : MediaRepository {


    override fun prepareMediaPlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.apply {
            reset()
            setDataSource(trackUrl)
            setOnPreparedListener {
                onPrepared.invoke()
            }
            setOnCompletionListener {
                onCompletion.invoke()
            }
            prepareAsync()
        }
    }

    override fun startMediaPlayer() {
        mediaPlayer.start()
    }

    override fun pauseMediaPlayer() {
        mediaPlayer.pause()
    }

    override fun releaseMediaPlayer() {
        mediaPlayer.reset()
    }
    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}