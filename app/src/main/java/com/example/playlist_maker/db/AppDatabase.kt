package com.example.playlist_maker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlist_maker.db.converters.TracksIdsConverter
import com.example.playlist_maker.db.dao.PlaylistsDao
import com.example.playlist_maker.db.dao.TracksDao

@Database(
    version = 2,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackToPlaylistEntity::class]
)
@TypeConverters(TracksIdsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
    abstract fun playlistsDao(): PlaylistsDao
}