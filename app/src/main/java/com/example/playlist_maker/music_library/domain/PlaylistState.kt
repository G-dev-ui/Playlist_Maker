package com.example.playlist_maker.music_library.domain

sealed interface PlaylistState {
    data class Data(val playlists: List<Playlist>, val status: String = "", val playlistName: String = "") : PlaylistState

    data object Empty : PlaylistState

    data object Load : PlaylistState
}