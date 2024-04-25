package com.example.playlist_maker.player.ui

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.player.domain.MediaPlayerState
import java.util.Locale

class MediaPlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val UPDATE_POSITION_DELAY = 250L
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { MediaPlayerViewModel(this[APPLICATION_KEY] as Application) }
        }
    }

    private var playerState = MediaPlayerState.PREPARED
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val mediaPlayerRepository = Creator.provideMediaPlayerRepository()
    private val handler = Handler(Looper.getMainLooper())

    private val updateProgressAction = object : Runnable{
        override fun run() {
           if (mediaPlayerRepository.isPlaying()){
               updateTrackTimer()
               handler.postDelayed(this, UPDATE_POSITION_DELAY)
           }
        }
    }

    fun playbackControl() {
        this.playerState = when (playerState) {
            MediaPlayerState.PLAYING -> {
                mediaPlayerRepository.pauseMediaPlayer()
                renderState(PlayerState.Playing)
                updateProgressAction.let { handler.removeCallbacks(it) }
                MediaPlayerState.PAUSED
            }
            MediaPlayerState.PREPARED , MediaPlayerState.PAUSED -> {
                mediaPlayerRepository.startMediaPlayer()
                renderState(PlayerState.Pause)
                updateProgressAction.let { handler.post(it) }
                MediaPlayerState.PLAYING
            }
            else -> {

                return
            }
        }
    }

    fun preparePlayer(trackUrl: String): MediaPlayerState {
        mediaPlayerRepository.prepareMediaPlayer(trackUrl, onPrepared = { renderState(PlayerState.Prepared) }, onCompletion = {renderState(PlayerState.Complete)})
        return MediaPlayerState.PREPARED
    }

    fun releaseMediaPlayer() {
        mediaPlayerRepository.releaseMediaPlayer()
    }

    private fun updateTrackTimer(){
            if (mediaPlayerRepository.getCurrentPosition() == -1){
                return
            }
        val  trackPosition = mediaPlayerRepository.getCurrentPosition().toLong().convertLongToTimeMillis()
        renderState(PlayerState.ChangePosition(trackPosition))
    }

    private fun renderState (state: PlayerState){
        stateLiveData.postValue(state)
    }


}

private fun Long.convertLongToTimeMillis(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}
