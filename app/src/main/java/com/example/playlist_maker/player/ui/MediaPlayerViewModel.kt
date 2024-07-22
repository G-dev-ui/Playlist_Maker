package com.example.playlist_maker.player.ui


import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.music_library.domain.FavoriteTracksInteractor
import com.example.playlist_maker.music_library.domain.Playlist
import com.example.playlist_maker.music_library.domain.PlaylistState
import com.example.playlist_maker.music_library.domain.PlaylistsInteractor
import com.example.playlist_maker.player.domain.MediaPlayerState
import com.example.playlist_maker.player.domain.MediaRepository
import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MediaPlayerViewModel(
    private val mediaPlayerRepository: MediaRepository,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    companion object {
        private const val UPDATE_POSITION_DELAY = 300L
    }
    var currentTrackPosition: Long = 0L
    var isPlaying: Boolean = false
    private var playerState = MediaPlayerState.PREPARED
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private var updateJob: Job? = null

    private var isFavoriteTrack: Boolean = false
    private val _favoriteState = MutableLiveData<FavoriteState>()
    val favoriteState: LiveData<FavoriteState> = _favoriteState

    private val _playlistsState = MutableLiveData<PlaylistState>()
    val playlistsState: LiveData<PlaylistState> = _playlistsState



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

    fun saveCurrentPosition() {
        currentTrackPosition = mediaPlayerRepository.getCurrentPosition().toLong()
    }

    fun seekTo(position: Long) {
        currentTrackPosition = position
        mediaPlayerRepository.seekTo(position.toInt())
    }

    fun playbackControl() {
        when (playerState) {
            MediaPlayerState.PLAYING -> {
                saveCurrentPosition()
                mediaPlayerRepository.pauseMediaPlayer()
                stopUpdatingTrackPosition()
                playerState = MediaPlayerState.PAUSED
                renderState(PlayerState.Pause)
                isPlaying = false
            }
            MediaPlayerState.PREPARED, MediaPlayerState.PAUSED -> {
                mediaPlayerRepository.seekTo(currentTrackPosition.toInt())
                mediaPlayerRepository.startMediaPlayer()
                startUpdatingTrackPosition()
                playerState = MediaPlayerState.PLAYING
                renderState(PlayerState.Playing)
                isPlaying = true
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

    fun inPlaylist(playlist: Playlist, trackId: Long): Boolean {
        var data = false
        for (track in playlist.tracks) {
            if (track == trackId) data = true
        }
        return data
    }
    fun clickOnAddtoPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            playlist.tracksAmount = playlist.tracks.size + 1
            playlistsInteractor.addTrackToPlaylist(playlist, track)
            _playlistsState.postValue(PlaylistState.Data(playlistsInteractor.getPlaylists()))
        }
    }

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val playlists = playlistsInteractor.getPlaylists()
                if (playlists.isEmpty()) {
                    _playlistsState.postValue(PlaylistState.Empty)
                } else {
                    _playlistsState.postValue(PlaylistState.Data(playlists))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isPrepared(): Boolean {
        return playerState == MediaPlayerState.PREPARED || playerState == MediaPlayerState.PAUSED
    }

     fun Long.convertLongToTimeMillis(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
    }
}