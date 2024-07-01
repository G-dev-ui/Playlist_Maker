package com.example.playlist_maker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlist_maker.db.dao.TracksDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
}