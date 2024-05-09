package com.example.playlist_maker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.databinding.FragmentSearchBinding
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.player.ui.MediaActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var searchBar: EditText
    private lateinit var textWatcher: TextWatcher
    private lateinit var emptyResultPlaceholder: View
    private lateinit var errorPlaceholder: View
    private lateinit var refreshButton: Button
    private lateinit var resetButton: ImageView
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var tracksList: RecyclerView
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModel<TracksSearchViewModel>()

    private val historyadapter = TrackAdapter(mutableListOf()) { track ->
        if (clickDebounce()) {
            viewModel.moveSearchHistoryToTop(track)
            val intent = Intent(requireActivity(), MediaActivity::class.java)
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

    private val adapter = TrackAdapter(mutableListOf()) { track ->
        if (clickDebounce()) {
            viewModel.addToSearchHistory(track)
            val intent = Intent(requireActivity(), MediaActivity::class.java)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateHistory.observe(viewLifecycleOwner) {
            showHistory(it)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        searchBar = binding.searchBar
        emptyResultPlaceholder = binding.emptyResultPlaceholder
        errorPlaceholder = binding.errorPlaceholder
        refreshButton = binding.refreshButton
        resetButton = binding.resetButton
        searchHistoryLayout = binding.searchHistory
        clearHistoryButton = binding.clearingHistory
        searchHistoryRecyclerView = binding.trackHistory
        progressBar = binding.progressBar
        tracksList = binding.rvTrack



        tracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = adapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.resetButton.visibility = clearButtonVisibility(s)
                viewModel.searchDebounce(changedText = s?.toString() ?: "")

            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { searchBar.addTextChangedListener(it) }

        searchHistoryRecyclerView.adapter = historyadapter
        searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)




        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()

        }


        resetButton.setOnClickListener {
            searchBar.setText("")
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)

            tracksList.isVisible = false
            viewModel.showSearchHistory()
            searchHistoryLayout.isVisible = true
            errorPlaceholder.visibility = View.GONE
        }

        refreshButton.setOnClickListener {
            val lastEnteredText = searchBar.text.toString()
            viewModel.searchDebounce(changedText = lastEnteredText)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: TracksState) {
        Log.d("SearchFragment", "Rendering state: $state")
        when (state) {
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.NothingFound -> showEmpty()
            is TracksState.ConnectionError -> showError()
            is TracksState.History -> showHistory(state.searchHistory)
            is TracksState.Empty -> showEmptyState()
            is TracksState.Loading -> showLoading()
        }
    }


    private fun showEmptyState() {
        searchBar.text.clear()
        searchHistoryLayout.isVisible = false
        tracksList.isVisible = false
    }

    private fun showHistory(searchHistoryList: List<Track>) {
        emptyResultPlaceholder.isVisible = false
        errorPlaceholder.isVisible = false
        progressBar.isVisible = false
        tracksList.isVisible = false


        if (searchHistoryList.isNotEmpty()) {
            searchHistoryLayout.isVisible = true

            historyadapter.tracks.clear()
            historyadapter.tracks.addAll(searchHistoryList)
            historyadapter.notifyDataSetChanged()
        }


    }

    private fun showEmpty() {
        emptyResultPlaceholder.isVisible = true
        progressBar.isVisible = false
        tracksList.isVisible = false
        searchHistoryLayout.isVisible = false
    }

    private fun showError() {
        tracksList.isVisible = false
        errorPlaceholder.isVisible = true
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
    }

    private fun showContent(tracks: List<Track>) {
        searchHistoryLayout.isVisible = false
        progressBar.isVisible = false
        tracksList.isVisible = true
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()

    }


    private fun showLoading() {
        tracksList.isVisible = false
        emptyResultPlaceholder.isVisible = false
        errorPlaceholder.isVisible = false
        progressBar.isVisible = true
        searchHistoryLayout.isVisible = false
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()
        searchBar.text.clear()
    }
}