package com.example.playlist_maker.search.domain.impl

import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.util.Resource
import com.example.playlist_maker.search.domain.TracksInteractor
import com.example.playlist_maker.search.domain.api.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override suspend fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(text).map  {resource ->
            when(resource) {
                is Resource.Success ->  resource.data to  null
                is Resource.Error -> null to resource.message
            }
        }
    }
}