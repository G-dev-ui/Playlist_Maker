package com.example.playlist_maker.search.domain

import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
   suspend fun searchTracks(text: String) : Flow<Pair<List<Track>?, String?>>

}