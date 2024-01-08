package com.example.playlist_maker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    companion object {
        private const val EDIT_TEXT_VALUE_KEY = "editTextValue"
    }

    private var editTextValue = ""

    private lateinit var itunesApiService: ItunesApiService
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var emptyResultPlaceholder: View
    private lateinit var errorPlaceholder: View
    private lateinit var refreshButton: Button
    private var currentTracks: List<Track> = emptyList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        errorPlaceholder = findViewById(R.id.error_placeholder)
        refreshButton = findViewById(R.id.refresh_button)

        hidePlaceholders()

        refreshButton.setOnClickListener {
            performSearch(editTextValue)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
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

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)

                resetButton.isVisible = false

                hidePlaceholders()
                trackAdapter.updateTracks(emptyList())
            }
        }

        editTextValue = savedInstanceState?.getString(EDIT_TEXT_VALUE_KEY, "") ?: ""
        searchBar.setText(editTextValue)

        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                performSearch(searchBar.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun hidePlaceholders() {
        emptyResultPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showEmptyResultPlaceholder() {
        hidePlaceholders()
        emptyResultPlaceholder.visibility = View.VISIBLE


    }

    private fun showErrorPlaceholder() {
        hidePlaceholders()
        errorPlaceholder.visibility = View.VISIBLE

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_VALUE_KEY, editTextValue)
    }

    private fun performSearch(query: String) {
        val call = itunesApiService.search(query)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null) {
                        val tracks = searchResponse.results.map { result ->
                            Track(
                                result.trackName,
                                result.artistName,
                                SimpleDateFormat("mm:ss", Locale.getDefault()).format(result.trackTimeMillis),
                                result.artworkUrl100
                            )
                        }

                        if (tracks.isEmpty() && query.isNotEmpty()) {
                            showEmptyResultPlaceholder()
                            trackAdapter.updateTracks(emptyList())
                        } else {
                            hidePlaceholders()
                            trackAdapter.updateTracks(tracks)
                            currentTracks = tracks
                        }
                    } else {
                        showErrorPlaceholder()
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showErrorPlaceholder()
            }
        })
    }

}




