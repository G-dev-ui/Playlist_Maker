package com.example.playlist_maker.domain.api

interface MediaInteractor {
    fun prepareMediaPlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startMediaPlayer()
    fun pauseMediaPlayer()
    fun releaseMediaPlayer()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}