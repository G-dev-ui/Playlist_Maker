package com.example.playlist_maker.search.ui

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlist_maker.R
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.search.domain.TracksInteractor



class TracksSearchViewModel (application: Application): AndroidViewModel(application) {


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TracksSearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val searchHistoryRepository: SearchHistoryRepository = SearchHistoryRepositoryImpl(
        application.getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
    )
    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())
    private var historyTrackList: List<Track> = searchHistoryRepository.getSearchHistory()

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
                                       errorMessage = getApplication<Application>().getString(R.string.something_went_wrong),
                                    )
                                )

                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.NothingFound(
                                        message = getApplication<Application>().getString(R.string.nothing_found),
                                    )
                                )

                            }

                            else -> {
                                renderState(
                                    TracksState.Content(
                                        tracks = tracks,
                                    )
                                )

                            }
                        }
                }
            })
        }
    }

    fun showHideResetButton(text: String){
        renderState(TracksState.ClearedSearchBar(text))
    }
    fun ClearHistory(){
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
    fun showSearchHostory(){
        if (historyTrackList.isEmpty()){
            renderState(TracksState.Empty)
        } else {
            renderState(TracksState.History(historyTrackList))
        }
    }


    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }
}