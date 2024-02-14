package com.example.playlist_maker




data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
)

fun Track.getCoverArtwork(): String {
    return try {
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    } catch (e: StringIndexOutOfBoundsException) {

        artworkUrl100
    }
}