package com.example.playlist_maker.search.ui

import com.example.playlist_maker.player.domain.Track

sealed interface TracksState {

object Loading : TracksState

    data class Content (
        val tracks: List<Track>
            ) : TracksState
    data class Error(
        val errorMessage: String
    ): TracksState

    data class Empty(
        val message: String
    ): TracksState

}