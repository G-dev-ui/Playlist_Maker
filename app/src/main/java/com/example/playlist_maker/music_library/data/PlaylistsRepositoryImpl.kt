package com.example.playlist_maker.music_library.data


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlist_maker.db.AppDatabase
import com.example.playlist_maker.db.PlaylistEntity
import com.example.playlist_maker.db.converters.PlaylistsDbConverter
import com.example.playlist_maker.db.converters.TracksToPlaylistConverter
import com.example.playlist_maker.music_library.domain.Playlist
import com.example.playlist_maker.music_library.domain.PlaylistsRepository
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.util.ILLEGAL_ARGUMENT_TRACK_ID
import com.example.playlist_maker.util.NULL_ARGUMENT_TRACK_ID
import com.example.playlist_maker.util.STORAGE_DIR_NAME
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.UUID


class PlaylistsRepositoryImpl(
    private val appDatabasePlaylists: AppDatabase,
    private val playlistsDbConverter: PlaylistsDbConverter,
    private val tracksToPlaylistConverter: TracksToPlaylistConverter
) : PlaylistsRepository {


    override suspend fun getPlaylists(): List<Playlist> {
        val playlistsEntities = appDatabasePlaylists.playlistsDao().getAllPlayLists()
        return converterForEntity(playlistsEntities)
    }
    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabasePlaylists.playlistsDao().insertPlayList(playlistsDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabasePlaylists.playlistsDao().updatePlayList(playlistsDbConverter.map(playlist))
    }

    override suspend fun addTrackToPlaylist(playList: Playlist, track: Track) {
        val trackId = track.trackId?.toLong() ?: throw IllegalArgumentException(
            ILLEGAL_ARGUMENT_TRACK_ID
        )
        if (trackId == 0L) {
            throw IllegalArgumentException(NULL_ARGUMENT_TRACK_ID)
        }
        addPlaylist(playlist = playList)
        val addTime = Date().time
        appDatabasePlaylists.playlistsDao()
            .addTrackToPlaylist(tracksToPlaylistConverter.map(track, addTime))
    }

    override suspend fun saveCoverToPrivateStorage(previewUri: Uri, context: Context): Uri? {
        val path =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), STORAGE_DIR_NAME)
        if (!path.exists()) {
            path.mkdirs()
        }

        val file = File(path, "playlist_cover_${System.currentTimeMillis()}.jpg")
        val input = context.contentResolver.openInputStream(previewUri)
        val output = FileOutputStream(file)
        BitmapFactory
            .decodeStream(input)
            .compress(Bitmap.CompressFormat.JPEG, 30, output)
        val previewUri = Uri.fromFile(file)
        return previewUri
    }
    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return converterForPlaylistEntity(
            appDatabasePlaylists.playlistsDao().getPlaylistById(playlistId)
        )
    }
    override suspend fun deletePlaylist(playlistId: Int) {
        val playlist = getPlaylistById(playlistId)
        appDatabasePlaylists.playlistsDao().deletePlaylist(playlistsDbConverter.map(playlist))
    }

    override suspend fun newPlaylist(
        playlistName: String,
        playlistDescription: String,
        coverUri: Uri?
    ) {
        val coverPath = getCover()
        val playlist = Playlist(
            id = 0,
            name = playlistName,
            description = playlistDescription,
            coverPath = coverPath,
            tracksIds = "",
            tracksAmount = 0,
            imageUri = coverUri?.toString() ?: ""
        )
        addPlaylist(playlist)
    }

    override suspend fun getCover(): String {
        return "cover_${UUID.randomUUID()}.jpg"
    }

    private fun converterForEntity(playlist: List<PlaylistEntity>): List<Playlist> {
        return playlist.map { playlist -> playlistsDbConverter.map(playlist) }
    }


    private fun converterForPlaylistEntity(playlist: PlaylistEntity): Playlist {
        return playlistsDbConverter.map(playlist)
    }
}