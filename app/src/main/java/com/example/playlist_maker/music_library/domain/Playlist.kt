package com.example.playlist_maker.music_library.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverPath: String,
    var tracksIds: String,
    val tracks: ArrayList<Long>,
    var tracksAmount: Int,
    val imageUri: String?
) : Parcelable