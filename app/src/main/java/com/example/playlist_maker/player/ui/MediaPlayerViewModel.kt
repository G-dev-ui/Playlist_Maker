package com.example.playlist_maker.player.ui


import android.icu.text.SimpleDateFormat
import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.example.playlist_maker.player.domain.MediaPlayerState
import com.example.playlist_maker.player.domain.MediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MediaPlayerViewModel(private val mediaPlayerRepository: MediaRepository) : ViewModel() {
    companion object {
        private const val UPDATE_POSITION_DELAY = 300L
    }

    private var playerState = MediaPlayerState.PREPARED
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private var updateJob: Job? = null

    fun playbackControl() {
        when (playerState) {
            MediaPlayerState.PLAYING -> {
                Log.d("MediaPlayer", "Pausing media player")
                mediaPlayerRepository.pauseMediaPlayer()
                stopUpdatingTrackPosition()
                playerState = MediaPlayerState.PAUSED
                renderState(PlayerState.Pause)
            }
            MediaPlayerState.PREPARED, MediaPlayerState.PAUSED -> {
                Log.d("MediaPlayer", "Starting media player")
                mediaPlayerRepository.startMediaPlayer()
                startUpdatingTrackPosition()
                playerState = MediaPlayerState.PLAYING
                renderState(PlayerState.Playing)
            }
            else -> {
                return
            }
        }
    }

    fun preparePlayer(trackUrl: String): MediaPlayerState {
        mediaPlayerRepository.prepareMediaPlayer(trackUrl, onPrepared = {
            playerState = MediaPlayerState.PREPARED
            renderState(PlayerState.Prepared)
        }, onCompletion = {
            playerState = MediaPlayerState.PREPARED
            renderState(PlayerState.Complete)
        })
        playerState = MediaPlayerState.PREPARED
        renderState(PlayerState.Prepared)
        return MediaPlayerState.PREPARED
    }

    fun releaseMediaPlayer() {
        mediaPlayerRepository.releaseMediaPlayer()
        stopUpdatingTrackPosition()
    }

    private fun startUpdatingTrackPosition() {
        updateJob = viewModelScope.launch {
            while (mediaPlayerRepository.isPlaying()) {
                updateTrackTimer()
                delay(UPDATE_POSITION_DELAY)
            }
        }
    }

    private fun stopUpdatingTrackPosition() {
        updateJob?.cancel()
    }

    private fun updateTrackTimer() {
        val currentPosition = mediaPlayerRepository.getCurrentPosition()
        if (currentPosition != -1) {
            val trackPosition = currentPosition.toLong().convertLongToTimeMillis()
            renderState(PlayerState.ChangePosition(trackPosition))
        }
    }

    private fun renderState(state: PlayerState) {
        Log.d("MediaPlayer", "Rendering state: $state")
        stateLiveData.postValue(state)
    }
}

private fun Long.convertLongToTimeMillis(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}
