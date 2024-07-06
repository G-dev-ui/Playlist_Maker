package com.example.playlist_maker.music_library.domain

sealed interface PlaylistState {
    data class Data(val tracks: List<Playlist>) : PlaylistState

    data object Empty : PlaylistState

    data object Load : PlaylistState
}