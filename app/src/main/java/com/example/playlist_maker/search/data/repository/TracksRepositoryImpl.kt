package com.example.playlist_maker.search.data.repository


import com.example.playlist_maker.resource.Resource
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.search.data.dto.TracksSearchRequest
import com.example.playlist_maker.search.data.dto.TracksSearchResponse
import com.example.playlist_maker.search.data.network.NetworkClient
import com.example.playlist_maker.search.domain.api.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(text: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(text))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    Track(it.trackId, it.trackName, it.artistName,formatTrackTime(it.trackTimeMillis), it.artworkUrl100, it.collectionName, it.releaseDate, it.primaryGenreName,it.country,it.previewUrl) })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }




    private fun formatTrackTime(trackTimeStr: String): String {
        return try {
            val milliseconds = trackTimeStr.toLong()
            val seconds = (milliseconds / 1000) % 60
            val minutes = (milliseconds / (1000 * 60)) % 60
            String.format("%02d:%02d", minutes, seconds)
        } catch (e: NumberFormatException) {
            "Unknown"
        }
    }
}