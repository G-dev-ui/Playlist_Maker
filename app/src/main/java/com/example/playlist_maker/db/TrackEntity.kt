package com.example.playlist_maker.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="favorite_tracks_tablet")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: String?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val insertTime: Long?

)