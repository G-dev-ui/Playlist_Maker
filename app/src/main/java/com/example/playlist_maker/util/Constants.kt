package com.example.playlist_maker.util

import java.text.SimpleDateFormat
import java.util.Locale

const val ILLEGAL_ARGUMENT_TRACK_ID = "Track ID must not be null or  0"
const val NULL_ARGUMENT_TRACK_ID = "Track ID must not be 0"
const val PLAYLISTS_TABLE = "playlists_tablet"
const val TRACKS_IN_PLAYLIST = "tracks_in_playlist"
const val SEARCH_QUERY_HISTORY = "SEARCH_QUERY_HISTORY"
const val STORAGE_DIR_NAME = "myPics"
const val CURRENT_PLAYLIST = "current_playlist"
const val MODIFY_PLAYLIST = "modify_playlist"
fun Long.convertLongToTimeMillis(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}
fun String.convertTimeStringToMillis(): Long {
    val parts = this.split(":")
    if (parts.size != 2) throw IllegalArgumentException("Invalid time format")
    val minutes = parts[0].toLong()
    val seconds = parts[1].toLong()
    return (minutes * 60000) + (seconds * 1000)
}