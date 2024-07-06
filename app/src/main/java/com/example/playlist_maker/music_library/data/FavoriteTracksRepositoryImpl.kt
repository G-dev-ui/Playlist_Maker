package com.example.playlist_maker.music_library.data

import com.example.playlist_maker.db.AppDatabase
import com.example.playlist_maker.db.TrackEntity
import com.example.playlist_maker.db.converters.TrackDbConverter
import com.example.playlist_maker.music_library.domain.FavoriteTracksRepository
import com.example.playlist_maker.player.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoriteTracksRepository {
    override suspend fun getTracks(): Flow<List<Track>> = flow {
        val favoriteTracks = appDatabase.tracksDao().getTracks()
        emit(converterForEntity(favoriteTracks))
    }

    override suspend fun insertTrack(track: Track) {
        appDatabase.tracksDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrack(trackId: Int) {
        appDatabase.tracksDao().deleteTrackEntity(trackId)
    }

    override suspend fun isFavoriteTrack(trackId: Int): Flow<Boolean> = flow {
        val isFavorite = appDatabase.tracksDao().isFavoriteTrack(trackId)
        emit(isFavorite)
    }

    private fun converterForEntity(track: List<TrackEntity>): List<Track> {
        return track.map { track -> trackDbConverter.map(track) }
    }
}