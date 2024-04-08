package com.example.playlist_maker.data.network

import android.media.MediaPlayer
import com.example.playlist_maker.domain.api.MediaRepository

class MediaRepositoryImpl : MediaRepository {
    private var mediaPlayer = MediaPlayer()

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
        mediaPlayer.release()
    }
    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}