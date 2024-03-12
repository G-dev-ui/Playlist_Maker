package com.example.playlist_maker

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private var editTextValue = ""

    private lateinit var itunesApiService: ItunesApiService
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var emptyResultPlaceholder: View
    private lateinit var errorPlaceholder: View
    private lateinit var refreshButton: Button
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar
    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var currentTracks: List<Track> = emptyList()
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        performSearch(editTextValue)
        searchHistoryLayout.visibility = View.GONE
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val EDIT_TEXT_VALUE_KEY = "editTextValue"
        private const val BASE_URL = "https://itunes.apple.com"
        private const val MEDIA_ACTIVITY_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        errorPlaceholder = findViewById(R.id.error_placeholder)
        refreshButton = findViewById(R.id.refresh_button)
        searchHistoryLayout = findViewById(R.id.search_history)
        clearHistoryButton = findViewById(R.id.clearing_history)
        searchHistoryRecyclerView = findViewById(R.id.track_history)
        progressBar = findViewById(R.id.progress_Bar)

        hidePlaceholders()

        refreshButton.setOnClickListener {
            performSearch(editTextValue)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        itunesApiService = retrofit.create(ItunesApiService::class.java)

        val backButton = findViewById<ImageView>(R.id.image)
        backButton.setOnClickListener {
            finish()
        }
        val rvTrack = findViewById<RecyclerView>(R.id.rv_track)
        val searchBar = findViewById<EditText>(R.id.search_bar)
        val resetButton = findViewById<ImageView>(R.id.reset_button)

        trackAdapter = TrackAdapter(mutableListOf())
        rvTrack.layoutManager = LinearLayoutManager(this)
        rvTrack.adapter = trackAdapter

        searchHistory = SearchHistory(getSharedPreferences("SearchHistory", Context.MODE_PRIVATE))

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editTextValue = s.toString()
                resetButton.isVisible = s.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })

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

                if (searchHistory.getSearchHistory().isNotEmpty()) {
                    showSearchHistory()
                } else {
                    searchHistoryLayout.visibility = View.GONE
                }
            }
        }

        editTextValue = savedInstanceState?.getString(EDIT_TEXT_VALUE_KEY, "") ?: ""
        searchBar.setText(editTextValue)

        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (searchHistory.getSearchHistory().isNotEmpty() && searchBar.text.isEmpty()) {
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
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                performSearch(searchBar.text.toString())

                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)

                true
            } else {
                false
            }
        }

        showSearchHistory()
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

    private fun showTrackList() {
        val rvTrack = findViewById<RecyclerView>(R.id.rv_track)
        rvTrack.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE_KEY, editTextValue)
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            val call = itunesApiService.search(query)
            progressBar.visibility = View.VISIBLE
            trackAdapter.updateTracks(emptyList())
            hidePlaceholders()

            call.enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (query == editTextValue) {
                        if (response.isSuccessful) {
                            val searchResponse = response.body()
                            if (searchResponse != null) {
                                val tracks = searchResponse.results.map { result ->
                                    Track(
                                        result.trackId,
                                        result.trackName,
                                        result.artistName,
                                        timeFormatter.format(result.trackTimeMillis),
                                        result.artworkUrl100,
                                        result.collectionName,
                                        result.releaseDate,
                                        result.primaryGenreName,
                                        result.country,
                                        result.previewUrl
                                    )
                                }

                                if (tracks.isEmpty() && query.isNotEmpty()) {
                                    showEmptyResultPlaceholder()
                                    hideTrackList()
                                } else {
                                    hidePlaceholders()
                                    trackAdapter.updateTracks(tracks)
                                    currentTracks = tracks
                                    showTrackList()

                                    trackAdapter.setOnItemClickListener(object : TrackAdapter.OnItemClickListener {
                                        override fun onItemClick(position: Int) {
                                            val selectedTrack = if (searchHistoryLayout.visibility == View.VISIBLE) {
                                                searchHistory.getSearchHistory()[position]
                                            } else {
                                                currentTracks[position]
                                            }
                                            redirectToAudioPlayer(selectedTrack)
                                            searchHistory.addSearchTrack(selectedTrack)
                                        }
                                    })
                                }
                            } else {
                                showErrorPlaceholder()
                            }
                        } else {
                            showErrorPlaceholder()
                        }
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    if (query == editTextValue) {
                        showErrorPlaceholder()
                    }
                }
            })
        } else {
            progressBar.visibility = View.GONE
            hidePlaceholders()
            trackAdapter.updateTracks(emptyList())
        }
    }

    private fun redirectToAudioPlayer(track: Track) {
        val intent = Intent(this, MediaActivity::class.java)
        intent.putExtra("trackId", track.trackId)
        intent.putExtra("trackName", track.trackName)
        intent.putExtra("artistName", track.artistName)
        intent.putExtra("trackTime", track.trackTime)
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
        val searchHistoryList = searchHistory.getSearchHistory()

        if (searchHistoryList.isNotEmpty()) {
            searchHistoryLayout.visibility = View.VISIBLE

            val historyAdapter = TrackAdapter(searchHistoryList.toMutableList())
            searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
            searchHistoryRecyclerView.adapter = historyAdapter

            historyAdapter.setOnItemClickListener(object : TrackAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val selectedTrack = searchHistoryList[position]
                    redirectToAudioPlayer(selectedTrack)
                }
            })

            clearHistoryButton.setOnClickListener {
                searchHistory.clearSearchHistory()
                searchHistoryLayout.visibility = View.GONE
            }
        } else {
            searchHistoryLayout.visibility = View.GONE
        }
    }
}


