package com.example.playlist_maker.music_library.domain

import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(private val favoriteTracksRepository: FavoriteTracksRepository) :
    FavoriteTracksInteractor {
    override suspend fun getTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getTracks()
    }

    override suspend fun insertTrack(track: Track) {
        return favoriteTracksRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(trackId: Int) {
        return favoriteTracksRepository.deleteTrack(trackId = trackId)
    }

    override suspend fun isFavoriteTrack(trackId: Long): Flow<Boolean> {
        return favoriteTracksRepository.isFavoriteTrack(trackId)
    }
}