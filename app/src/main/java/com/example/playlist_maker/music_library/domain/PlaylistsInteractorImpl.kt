package com.example.playlist_maker.music_library.domain

import android.content.Context
import android.net.Uri
import com.example.playlist_maker.player.domain.Track


class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {
    override suspend fun getPlaylists(): List<Playlist> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playList: Playlist, track: Track) {
        playlistsRepository.addTrackToPlaylist(playList, track)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsRepository.updatePlaylist(playlist)
    }

    override suspend fun saveCoverToPrivateStorage(previewUri: Uri, context: Context): Uri? {
        return playlistsRepository.saveCoverToPrivateStorage(previewUri, context)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        return playlistsRepository.deletePlaylist(playlistId)
    }
    override suspend fun newPlaylist(
        playlistName: String,
        playlistDescription: String,
        coverUri: Uri?
    ) {
        return playlistsRepository.newPlaylist(playlistName, playlistDescription, coverUri)
    }

    override suspend fun getCover(): String {
        return playlistsRepository.getCover()
    }

    override suspend fun getAllTracks(tracksIdsList: List<Long>): List<Track> {
        return playlistsRepository.getAllTracks(tracksIdsList)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Long) {
        return playlistsRepository.deleteTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun trackCountDecrease(playlistId: Int) {
        return playlistsRepository.trackCountDecrease(playlistId)
    }

    override suspend fun modifyData(
        name: String,
        description: String,
        cover: String,
        coverUri: Uri?,
        originalPlayList: Playlist
    ) {
        playlistsRepository.modifyData(
            name,
            description,
            cover,
            coverUri,
            originalPlayList
        )
    }

}