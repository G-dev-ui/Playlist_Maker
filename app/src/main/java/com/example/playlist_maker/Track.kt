package com.example.playlist_maker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
){
    companion object {

        private const val MAX_ARTIST_NAME_LENGTH = 15
        private const val ELLIPSIS = "..."

        fun getShortenedArtistName(artistName: String): String {
            return if (artistName.length > MAX_ARTIST_NAME_LENGTH) {
                artistName.substring(0, MAX_ARTIST_NAME_LENGTH) + ELLIPSIS
            } else {
                artistName
            }
        }
    }
}

