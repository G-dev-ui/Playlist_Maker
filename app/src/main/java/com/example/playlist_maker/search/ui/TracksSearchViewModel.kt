package com.example.playlist_maker.search.ui


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.search.domain.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TracksSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())
    private var historyTrackList: List<Track> = searchHistoryRepository.getSearchHistory()

    private val stateHistoryLD = MutableLiveData(searchHistoryRepository.getSearchHistory())
    val stateHistory: LiveData<List<Track>> = stateHistoryLD
    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    var lastSearchText: String? = null
    private var searchJob: Job? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private var forceSearch: Boolean = false

    fun searchDebounce(changedText: String, force: Boolean = false) {
        if (!force && lastSearchText == changedText) {
            return
        }
        lastSearchText = changedText
        forceSearch = force

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(changedText)
        }
    }

    private fun performSearch(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        val (foundTracks, errorMessage) = pair

                        when {
                            errorMessage != null -> {
                                renderState(TracksState.ConnectionError(errorMessage = errorMessage))
                            }
                            foundTracks.isNullOrEmpty() -> {
                                renderState(TracksState.NothingFound(message = "No tracks found"))
                            }
                            else -> {
                                renderState(TracksState.Content(tracks = foundTracks))
                            }
                        }
                    }
            }
        }
    }

    fun clearHistory() {
        historyTrackList = emptyList()
        searchHistoryRepository.clearSearchHistory()
        renderState(TracksState.Empty)
    }

    fun addToSearchHistory(track: Track) {
        searchHistoryRepository.addSearchTrack(track)
        historyTrackList = searchHistoryRepository.getSearchHistory()
    }

    fun moveSearchHistoryToTop(track: Track) {
        historyTrackList = historyTrackList.filterNot { it == track }.toMutableList()
        (historyTrackList as MutableList).add(0, track)
        renderState(TracksState.History(historyTrackList))
    }

    fun showSearchHistory() {
        if (historyTrackList.isEmpty()) {
            renderState(TracksState.Empty)
        } else {
            renderState(TracksState.History(historyTrackList))
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }
}
