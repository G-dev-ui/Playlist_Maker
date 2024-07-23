package com.example.playlist_maker.music_library.ui.playlist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.music_library.domain.Playlist
import com.example.playlist_maker.music_library.domain.PlaylistsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyPlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor) :
    NewPlaylistViewModel(playlistsInteractor) {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> get() = _playlist

    fun getPlaylist(playlist: Playlist) {
        _playlist.postValue(playlist)
    }

    fun getCover() {
        viewModelScope.launch(Dispatchers.IO) {
            val playlists = playlistsInteractor.getPlaylists()
            val updatedPlaylists = playlists.map { playlist ->
                playlist.copy(
                    imageUri = Uri.parse(playlist.imageUri).toString()
                )
            }
        }
    }

    fun modifyData(
        name: String,
        description: String,
        cover: String,
        coverUri: Uri?,
        originalPlayList: Playlist
    ) {
        viewModelScope.launch {
            playlistsInteractor.modifyData(
                name,
                description,
                cover,
                coverUri,
                originalPlayList
            )
        }
    }
}