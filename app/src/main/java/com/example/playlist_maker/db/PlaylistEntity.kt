package com.example.playlist_maker.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlist_maker.util.PLAYLISTS_TABLE


@Entity(tableName =  PLAYLISTS_TABLE)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val coverPath: String,
    val tracksIds: String,
    val tracks: ArrayList<Long>,
    val tracksAmount: Int,
    val imageUri: String?
)