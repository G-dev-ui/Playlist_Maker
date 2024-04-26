package com.example.playlist_maker.search.ui


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.player.ui.MediaActivity


class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var textWatcher: TextWatcher
    private lateinit var viewModel: TracksSearchViewModel
    private lateinit var emptyResultPlaceholder: View
    private lateinit var errorPlaceholder: View
    private lateinit var refreshButton: Button
    private lateinit var resetButton: ImageView
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var tracksList: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

   private val historyadapter = TrackAdapter(mutableListOf()){track ->
       if (clickDebounce()){
           viewModel.moveSearchHistoryToTop(track)
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
   }

    private val adapter = TrackAdapter(mutableListOf()) {track ->

        if (clickDebounce()) {
            viewModel.addToSearchHistory(track)
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
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, TracksSearchViewModel.getViewModelFactory())[TracksSearchViewModel::class.java]
        viewModel.observeState().observe(this) {
            render(it)
        }

        resetButton = findViewById(R.id.reset_button)
        searchBar = findViewById(R.id.search_bar)
        emptyResultPlaceholder = findViewById(R.id.empty_result_placeholder)
        errorPlaceholder = findViewById(R.id.error_placeholder)
        refreshButton = findViewById(R.id.refresh_button)
        searchHistoryLayout = findViewById(R.id.search_history)
        clearHistoryButton = findViewById(R.id.clearing_history)
        searchHistoryRecyclerView = findViewById(R.id.track_history)
        progressBar = findViewById(R.id.progress_Bar)
        tracksList = findViewById(R.id.rv_track)

        val backButton = findViewById<ImageView>(R.id.image)
        backButton.setOnClickListener {
            finish()
        }

        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = adapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
               viewModel.showHideResetButton(text = String())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let {searchBar.addTextChangedListener(it) }

        searchHistoryRecyclerView.adapter = historyadapter
        searchHistoryRecyclerView.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.showSearchHostory()
        clearHistoryButton.setOnClickListener{
            viewModel.ClearHistory()
        }

        resetButton.setOnClickListener {
            searchBar.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)

            viewModel.showSearchHostory()
            errorPlaceholder.visibility = View.GONE
        }

       refreshButton.setOnClickListener {
           val lastEnteredText = searchBar.text.toString()
               viewModel.searchDebounce(changedText =  lastEnteredText)
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { searchBar.removeTextChangedListener(it) }
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.NothingFound -> showEmpty(state.message)
            is TracksState.ConnectionError -> showError(state.errorMessage)
            is TracksState.History -> showHistory(state.searchHistory)
            is TracksState.ClearedSearchBar -> showHideResetButton(state.text)
            is TracksState.Empty -> showEmptyState()
            is TracksState.Loading -> showLoading()
        }
    }

    private fun showHideResetButton(text: String){
        if (text.isEmpty()){
            resetButton.visibility = View.VISIBLE
            tracksList.visibility = View.GONE
        } else {
            resetButton.visibility =View.GONE

        }
    }
    private fun showEmptyState(){
        searchHistoryLayout.visibility = View.GONE
        tracksList.visibility = View.GONE
    }
    private fun showHistory(searchHistoryList: List<Track>){

        searchHistoryLayout.visibility = View.VISIBLE
        emptyResultPlaceholder.visibility = View.GONE
        tracksList.visibility = View.GONE
        progressBar.visibility = View.GONE

        historyadapter.tracks.clear()
        historyadapter.tracks.addAll(searchHistoryList)
        historyadapter.notifyDataSetChanged()

    }
    private fun showEmpty(emptyMessage: String) {

        emptyResultPlaceholder.visibility = View.VISIBLE

        progressBar.visibility = View.GONE
        tracksList.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }
    private fun showError(errorMessage: String) {
        tracksList.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        searchHistoryLayout.visibility = View.GONE
    }
    private fun showContent(tracks: List<Track>) {
       tracksList.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }
    private fun showLoading() {
        tracksList.visibility = View.GONE
        emptyResultPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
        progressBar.visibility =  View.VISIBLE
        searchHistoryLayout.visibility = View.GONE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}


