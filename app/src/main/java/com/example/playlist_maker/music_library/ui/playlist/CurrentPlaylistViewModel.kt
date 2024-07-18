package com.example.playlist_maker.music_library.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.music_library.domain.Playlist
import com.example.playlist_maker.music_library.domain.PlaylistsInteractor
import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentPlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private lateinit var tracks: List<Track>


    private val _playlistId = MutableLiveData<Playlist>()
    fun observePlaylistId(): LiveData<Playlist> = _playlistId

    private val _playlistTracks = MutableLiveData<List<Track>>()
    fun observePlaylistTracks(): LiveData<List<Track>> = _playlistTracks

    private val _trackCount = MutableLiveData<Int>()
    fun observeTrackCount(): LiveData<Int> = _trackCount

    private val _playlistTime = MutableLiveData<Long>()
    fun observePlaylistAllTime(): LiveData<Long> = _playlistTime


    fun playlistAllTime() {
        _playlistTracks.value?.let { tracks ->
            val totalTime: Long = tracks.sumOf { track ->
                track.trackTimeMillis?.let {
                    val parts = it.split(":")
                    val minutes = parts[0].toLong()
                    val seconds = parts[1].toLong()
                    minutes * 60 * 1000 + seconds * 1000
                } ?: 0L
            }
            _playlistTime.postValue(totalTime)
        }
    }

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            _playlistId.postValue(playlistsInteractor.getPlaylistById(playlistId))
        }
    }

    fun getAllTracks(playlistId: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            tracks = playlistsInteractor.getAllTracks(playlistId)
            _playlistTracks.postValue(tracks)
        }
    }

    fun deleteTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        val playlistId = _playlistId.value?.id
        viewModelScope.launch(Dispatchers.IO) {
            playlist.tracksAmount = playlist.tracks.size - 1
            if (playlistId != null) {
                playlistsInteractor.deleteTrackFromPlaylist(playlistId, trackId)
                trackCountDecrease(playlistId)
                updatePlaylist(playlistId)
            }
        }
    }

    fun deletePlaylist() {
        val playlistId = _playlistId.value?.id
        viewModelScope.launch(Dispatchers.IO) {
            if (playlistId != null) {
                playlistsInteractor.deletePlaylist(playlistId)
            }
        }
    }

    private fun updatePlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val update = playlistsInteractor.getPlaylistById(playlistId)
            _playlistId.postValue(update)
            val updateTracks = playlistsInteractor.getAllTracks(update.tracks)
            _playlistTracks.postValue(updateTracks)
            _trackCount.postValue(updateTracks.size)
        }
    }

    private suspend fun trackCountDecrease(playlistId: Int) {
        playlistsInteractor.trackCountDecrease(playlistId)
    }
}




