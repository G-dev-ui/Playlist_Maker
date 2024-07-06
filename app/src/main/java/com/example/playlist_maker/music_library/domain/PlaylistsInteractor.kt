package com.example.playlist_maker.music_library.domain

import android.content.Context
import android.net.Uri
import com.example.playlist_maker.player.domain.Track


interface PlaylistsInteractor {
    suspend fun getPlaylists(): List<Playlist>

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(playList: Playlist, track: Track)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun saveCoverToPrivateStorage(previewUri: Uri, context: Context): Uri?

    suspend fun getPlaylistById(playlistId: Int): Playlist

    suspend fun deletePlaylist(playlistId: Int)

    suspend fun newPlaylist(playlistName: String, playlistDescription: String, coverUri: Uri?)

    suspend fun getCover(): String

}