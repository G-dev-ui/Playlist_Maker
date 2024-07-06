package com.example.playlist_maker.db.converters

import com.example.playlist_maker.db.TrackToPlaylistEntity
import com.example.playlist_maker.player.domain.Track

class TracksToPlaylistConverter {
    fun map(track: Track, addTime: Long): TrackToPlaylistEntity{
        return TrackToPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            addTime
        )
    }

    fun map(track: TrackToPlaylistEntity, addTime: Long): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            addTime
        )
    }
}