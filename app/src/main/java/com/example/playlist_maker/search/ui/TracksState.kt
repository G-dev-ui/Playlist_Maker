package com.example.playlist_maker.search.ui

import com.example.playlist_maker.player.domain.Track

sealed interface TracksState {

    object Loading : TracksState
    object Empty : TracksState
    data class Content (
        val tracks: List<Track>
            ) : TracksState
    data class  ConnectionError(
        val errorMessage: String
    ): TracksState

    data class NothingFound(
        val message: String
    ): TracksState

    data class History(
        val searchHistory: List<Track>
        ) : TracksState


}