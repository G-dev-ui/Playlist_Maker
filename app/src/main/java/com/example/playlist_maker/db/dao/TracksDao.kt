package com.example.playlist_maker.db.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlist_maker.db.TrackEntity



@Dao
interface TracksDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(tracks: TrackEntity)

    @Query("DELETE FROM favorite_tracks_tablet WHERE trackId = :trackId")
    suspend fun deleteTrackEntity(trackId: Int)

    @Query("SELECT * FROM favorite_tracks_tablet ORDER BY insertTime DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM favorite_tracks_tablet  WHERE trackId = :trackId)")
    suspend fun isFavoriteTrack(trackId: kotlin.Int): Boolean

}