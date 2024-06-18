package com.example.playlist_maker.search.data.dto

data class TrackDto (
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String,
    val previewUrl: String? = null,
    var insertTime: Long?
        )