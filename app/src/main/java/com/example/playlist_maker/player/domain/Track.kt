package com.example.playlist_maker.player.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String? = null,
    val insertTime: Long?
): Parcelable

fun Track.getCoverArtwork(): String? {
    return try {
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    } catch (e: StringIndexOutOfBoundsException) {

        artworkUrl100
    }
}