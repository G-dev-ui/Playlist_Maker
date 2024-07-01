package com.example.playlist_maker.music_library.domain

import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun getTracks(): Flow<List<Track>>

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(trackId: Int)

    suspend fun isFavoriteTrack(trackId: Long): Flow<Boolean>
}