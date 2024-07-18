package com.example.playlist_maker.db.converters

import com.example.playlist_maker.db.PlaylistEntity
import com.example.playlist_maker.music_library.domain.Playlist

class PlaylistsDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverPath,
            playlist.tracksIds,
            playlist.tracks,
            playlist.tracksAmount,
            playlist.imageUri
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverPath,
            playlist.tracksIds,
            playlist.tracks,
            playlist.tracksAmount,
            playlist.imageUri
        )
    }
}