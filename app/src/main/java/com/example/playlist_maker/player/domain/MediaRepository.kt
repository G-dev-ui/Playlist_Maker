package com.example.playlist_maker.player.domain

    interface MediaRepository {
        fun prepareMediaPlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
        fun startMediaPlayer()
        fun pauseMediaPlayer()
        fun releaseMediaPlayer()
        fun isPlaying(): Boolean
        fun getCurrentPosition(): Int
    }