package com.example.playlist_maker.player.ui


import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.music_library.domain.FavoriteTracksInteractor
import com.example.playlist_maker.player.domain.MediaPlayerState
import com.example.playlist_maker.player.domain.MediaRepository
import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MediaPlayerViewModel(
    private val mediaPlayerRepository: MediaRepository,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {
    companion object {
        private const val UPDATE_POSITION_DELAY = 300L
    }

    private var playerState = MediaPlayerState.PREPARED
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private var updateJob: Job? = null


    private var isFavoriteTrack: Boolean = false
    private val _favoriteState = MutableLiveData<FavoriteState>()
    val favoriteState: LiveData<FavoriteState> = _favoriteState

    fun isFavorite(track: Track) {
        viewModelScope.launch {
            favoriteTracksInteractor
                .isFavoriteTrack(track.trackId ?: 0)
                .collect { isFavorite ->
                    isFavoriteTrack = isFavorite
                    _favoriteState.postValue(FavoriteState(isFavorite))

                }
        }
    }
    fun playbackControl() {
        when (playerState) {
            MediaPlayerState.PLAYING -> {
                mediaPlayerRepository.pauseMediaPlayer()
                stopUpdatingTrackPosition()
                playerState = MediaPlayerState.PAUSED
                renderState(PlayerState.Pause)
            }
            MediaPlayerState.PREPARED, MediaPlayerState.PAUSED -> {
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
        stateLiveData.postValue(state)
    }

    fun addToFavorite(track: Track) {
        viewModelScope.launch {
            isFavoriteTrack = if (isFavoriteTrack) {
                favoriteTracksInteractor.deleteTrack(track.trackId?.toInt() ?: 0)
                _favoriteState.postValue(FavoriteState(false))
                false
            } else {
                favoriteTracksInteractor.insertTrack(track)
                _favoriteState.postValue(FavoriteState(true))
                true
            }
        }
    }

    private fun Long.convertLongToTimeMillis(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
    }
}

