package com.example.playlist_maker.search.data.repository

import android.content.SharedPreferences
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SearchHistoryRepository {


    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    override fun getSearchHistory(): List<Track> {
        val historyJson = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        return if (!historyJson.isNullOrBlank()) {
            val type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(historyJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun addSearchTrack(track: Track) {
        val currentHistory = getSearchHistory().toMutableList()

        val existingIndex = currentHistory.indexOfFirst { it.trackId == track.trackId }

        if (existingIndex != -1) {
            currentHistory.removeAt(existingIndex)
        }

        currentHistory.add(0, track)

        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeLast()
        }

        saveSearchHistory(currentHistory)
    }

    override fun clearSearchHistory() {
        saveSearchHistory(emptyList())
    }

    private fun saveSearchHistory(history: List<Track>) {
        val historyJson = Gson().toJson(history)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, historyJson).apply()
    }
}