package com.example.playlist_maker.search.domain.api

import com.example.playlist_maker.resource.Resource
import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
   suspend fun searchTracks(text: String): Flow<Resource<List<Track>>>
}