package com.example.playlist_maker.search.domain.repository

import com.example.playlist_maker.player.domain.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addSearchTrack(track: Track)
    fun clearSearchHistory()
}