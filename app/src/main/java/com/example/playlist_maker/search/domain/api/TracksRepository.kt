package com.example.playlist_maker.search.domain.api

import com.example.playlist_maker.creator.Resource
import com.example.playlist_maker.player.domain.Track

interface TracksRepository {
    fun searchTracks(text: String): Resource<List<Track>>
}