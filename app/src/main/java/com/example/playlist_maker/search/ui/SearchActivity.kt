package com.example.playlist_maker.search.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.player.ui.MediaActivity
import com.example.playlist_maker.search.data.SearchHistoryRepositoryImpl
import com.example.playlist_maker.search.domain.SearchHistoryRepository
import com.example.playlist_maker.search.domain.TracksInteractor


class SearchActivity : ComponentActivity() {

    private var editTextValue = ""

    private lateinit var searchBar: EditText
    private lateinit var textWatcher: TextWatcher
    private lateinit var viewModel: TracksSearchViewModel
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var emptyResultPlaceholder: View
    private lateinit var errorPlaceholder: View
    private lateinit var refreshButton: Button
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var tracksList: RecyclerView
    private lateinit var progressBar: ProgressBar






    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val EDIT_TEXT_VALUE_KEY = "editTextValue"
        private const val MEDIA_ACTIVITY_REQUEST_CODE = 1001
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, TracksSearchViewModel.getViewModelFactory())[TracksSearchViewModel::class.java]

        searchBar = findViewById(R.id.search_bar)
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        errorPlaceholder = findViewById(R.id.error_placeholder)
        refreshButton = findViewById(R.id.refresh_button)
        searchHistoryLayout = findViewById(R.id.search_history)
        clearHistoryButton = findViewById(R.id.clearing_history)
        searchHistoryRecyclerView = findViewById(R.id.track_history)
        progressBar = findViewById(R.id.progress_Bar)
        tracksList = findViewById(R.id.rv_track)

        hidePlaceholders()

        refreshButton.setOnClickListener {
            //performSearch(editTextValue)
        }

        val backButton = findViewById<ImageView>(R.id.image)
        backButton.setOnClickListener {
            finish()
        }

        val searchBar = findViewById<EditText>(R.id.search_bar)
        val resetButton = findViewById<ImageView>(R.id.reset_button)

        trackAdapter = TrackAdapter(mutableListOf())
        tracksList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = trackAdapter

        searchHistoryRepository = SearchHistoryRepositoryImpl(getSharedPreferences("SearchHistory", Context.MODE_PRIVATE))

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let {searchBar.addTextChangedListener(it) }

        viewModel.observeState().observe(this) {
            render(it)
        }





        resetButton.setOnClickListener {
            if (editTextValue.isNotEmpty()) {
                editTextValue = ""
                searchBar.setText("")

                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)

                resetButton.isVisible = false

                hidePlaceholders()
                trackAdapter.updateTracks(emptyList())

                showSearchHistory()
            }
        }

        editTextValue = savedInstanceState?.getString(EDIT_TEXT_VALUE_KEY, "") ?: ""
        searchBar.setText(editTextValue)

        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (searchHistoryRepository.getSearchHistory().isNotEmpty() && searchBar.text.isEmpty()) {
                    searchHistoryLayout.visibility = View.VISIBLE
                } else {
                    searchHistoryLayout.visibility = View.GONE
                }
            } else {
                searchHistoryLayout.visibility = View.GONE
            }
        }
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editTextValue = s.toString()
                resetButton.isVisible = s.isNotEmpty()
               //searchDebounce()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        showSearchHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { searchBar.removeTextChangedListener(it) }
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showEmpty(state.message)
            is TracksState.Error -> showError(state.errorMessage)
            is TracksState.Loading -> showLoading()
        }
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
        emptyResultPlaceholder.visibility = View.VISIBLE
    }
    fun showError(errorMessage: String) {
        tracksList.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        showError(errorMessage)
    }
    private fun showContent(track: List<Track>) {
       tracksList.visibility = View.VISIBLE
        hidePlaceholders()
        progressBar.visibility = View.GONE

        trackAdapter.updateTracks(emptyList())
    }
    private fun showLoading() {
        tracksList.visibility = View.GONE
        hidePlaceholders()
        progressBar.visibility =  View.VISIBLE
    }
    private fun hidePlaceholders() {
        emptyResultPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showEmptyResultPlaceholder() {
        hidePlaceholders()
        emptyResultPlaceholder.visibility = View.VISIBLE

        trackAdapter.updateTracks(emptyList())
        hideTrackList()
    }

    private fun showErrorPlaceholder() {
        hidePlaceholders()
        errorPlaceholder.visibility = View.VISIBLE
        trackAdapter.updateTracks(emptyList())
        hideTrackList()
    }

    private fun hideTrackList() {
        val rvTrack = findViewById<RecyclerView>(R.id.rv_track)
        rvTrack.visibility = View.GONE
    }






    private fun redirectToAudioPlayer(track: Track) {
        val intent = Intent(this, MediaActivity::class.java)
        intent.putExtra("trackId", track.trackId)
        intent.putExtra("trackName", track.trackName)
        intent.putExtra("artistName", track.artistName)
        intent.putExtra("trackTime", track.trackTimeMillis)
        intent.putExtra("artworkUrl100", track.artworkUrl100)
        intent.putExtra("collectionName", track.collectionName)
        intent.putExtra("releaseDate", track.releaseDate)
        intent.putExtra("primaryGenreName", track.primaryGenreName)
        intent.putExtra("country", track.country)
        intent.putExtra("previewUrl", track.previewUrl)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEDIA_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

        }
    }


    private fun showSearchHistory() {
        val searchHistoryList = searchHistoryRepository.getSearchHistory().toMutableList()

        if (searchHistoryList.isNotEmpty()) {
            searchHistoryLayout.visibility = View.VISIBLE

            val historyAdapter = TrackAdapter(searchHistoryList)
            searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
            searchHistoryRecyclerView.adapter = historyAdapter

            historyAdapter.setOnItemClickListener(object : TrackAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val selectedTrack = searchHistoryList[position]
                    searchHistoryRepository.addSearchTrack(selectedTrack)
                    redirectToAudioPlayer(selectedTrack)
                    searchHistoryList.removeAt(position)
                    searchHistoryList.add(0, selectedTrack)
                    historyAdapter.notifyDataSetChanged()
                }
            })

            clearHistoryButton.setOnClickListener {
                searchHistoryRepository.clearSearchHistory()
                searchHistoryLayout.visibility = View.GONE
            }
        } else {
            searchHistoryLayout.visibility = View.GONE
        }
    }
}


