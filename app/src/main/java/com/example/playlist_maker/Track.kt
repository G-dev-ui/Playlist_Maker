package com.example.playlist_maker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
){
    companion object {
        private const val MAX_TRACK_NAME_LENGTH = 20
        private const val MAX_ARTIST_NAME_LENGTH = 15
        private const val ELLIPSIS = "..."

        fun getShortenedTrackName(trackName: String): String {
            return if (trackName.length > MAX_TRACK_NAME_LENGTH) {
                trackName.substring(0, MAX_TRACK_NAME_LENGTH) + ELLIPSIS
            } else {
                trackName
            }
        }

        fun getShortenedArtistName(artistName: String): String {
            return if (artistName.length > MAX_ARTIST_NAME_LENGTH) {
                artistName.substring(0, MAX_ARTIST_NAME_LENGTH) + ELLIPSIS
            } else {
                artistName
            }
        }
    }
}

