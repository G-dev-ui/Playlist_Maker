package com.example.playlist_maker.search.domain

import com.example.playlist_maker.player.domain.Track

interface TracksInteractor {
    fun searchTracks(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundMovies: List<Track>?, errorMessage: String?)
    }
}