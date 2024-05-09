package com.example.playlist_maker.search.ui


import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.search.domain.TracksInteractor


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

    private var lastSearchText: String? = null


    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {

        this.lastSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { performSearch(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun performSearch(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()

                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                TracksState.ConnectionError(
                                    errorMessage = errorMessage
                                )
                            )

                        }

                        tracks.isEmpty() -> {
                            renderState(
                                TracksState.NothingFound(
                                    message = String()
                                )
                            )

                        }

                        else -> {

                            renderState(
                                TracksState.Content(
                                    tracks = tracks
                                )
                            )

                        }
                    }

                }
            })
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