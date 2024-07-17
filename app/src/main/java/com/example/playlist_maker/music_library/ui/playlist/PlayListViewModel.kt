package com.example.playlist_maker.music_library.ui.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.music_library.domain.PlaylistState
import com.example.playlist_maker.music_library.domain.PlaylistsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayListViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val _playlistsState = MutableLiveData<PlaylistState>(PlaylistState.Load)
    val playlistState: MutableLiveData<PlaylistState> = _playlistsState

    private fun setState(playlistState: PlaylistState) {
        _playlistsState.postValue(playlistState)
    }

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val playlists = playlistsInteractor.getPlaylists()
                if (playlists.isEmpty()) {
                    setState(PlaylistState.Empty)
                } else {
                    setState(PlaylistState.Data(playlists))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}