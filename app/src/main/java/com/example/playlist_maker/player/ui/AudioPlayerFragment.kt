package com.example.playlist_maker.player.ui


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentAudioPlayerBinding
import com.example.playlist_maker.main.ui.MainActivity
import com.example.playlist_maker.music_library.domain.Playlist
import com.example.playlist_maker.music_library.domain.PlaylistState
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.player.domain.getCoverArtwork
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AudioPlayerFragment : Fragment(), AudioPlayerViewHolder.ClickListener {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var track: Track

    private lateinit var playButton: AppCompatImageView
    private lateinit var durationTextView: TextView
    private lateinit var likeButton: ImageView

    private val mediaViewModel by viewModel<MediaPlayerViewModel>()
    private lateinit var adapter: AudioPlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playButton = view.findViewById(R.id.playButton1)
        likeButton = view.findViewById(R.id.likeButton)
        durationTextView = view.findViewById(R.id.durationTextView1)

        mediaViewModel.observeState().observe(viewLifecycleOwner) { render(it) }

        mediaViewModel.playlistsState.observe(viewLifecycleOwner) { state ->
            playlistStateManage(state)
        }

        mediaViewModel.favoriteState.observe(viewLifecycleOwner, Observer { isFavorite ->
            like(isFavorite)
        })

        (activity as? MainActivity)?.hideNavBar()



        val backButton = view.findViewById<Toolbar>(R.id.back_button_playerActivity1)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        track = arguments?.getParcelable<Track>(SEARCH_QUERY_HISTORY) as Track

        val coverImageView = view.findViewById<ImageView>(R.id.album_cover)

        Glide.with(this)
            .load(track.getCoverArtwork())
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder2)
                    .error(R.drawable.placeholder2)
            )
            .into(coverImageView)

        binding.trackNamePlayerActivity.text = track.trackName
        binding.artistNamePlayerActivity1.text = track.artistName
        binding.durationValue1.text = track.trackTimeMillis.toString()
        binding.albumValue1.text = track.collectionName ?: ""
        binding.yearValue1.text = LocalDateTime.parse(
            track.releaseDate,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        ).year.toString()
        binding.genreValue1.text = track.primaryGenreName
        binding.countryValue1.text = track.country

        if (track.previewUrl?.isNotEmpty() == true) {
            track.previewUrl?.let { mediaViewModel.preparePlayer(it) }
        }

        playButton.setOnClickListener {
            mediaViewModel.playbackControl()
        }
        mediaViewModel.isFavorite(track)
        likeButton.setOnClickListener {
            mediaViewModel.addToFavorite(track)
        }
        adapter = AudioPlayerAdapter(this)

        binding.bottomSheetRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.bottomSheetRecyclerView.adapter = adapter

        val bottomSheetState = BottomSheetBehavior.from(binding.bottomSheetAudioPlayer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetState.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.bottomSheetOverlay.visibility = View.GONE
                    }

                    else -> {
                        adapter.notifyDataSetChanged()
                        binding.bottomSheetOverlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.addToCollectionButton1.setOnClickListener {
            bottomSheetState.state = BottomSheetBehavior.STATE_COLLAPSED
            adapter.notifyDataSetChanged()
        }

        binding.bottomSheetAddPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
            mediaViewModel.getPlaylists()
        }

        mediaViewModel.getPlaylists()
    }

    override fun onPause() {
        super.onPause()
        playButton.setImageResource(R.drawable.play_buttom)
        mediaViewModel.releaseMediaPlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaViewModel.releaseMediaPlayer()
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.ChangePosition -> TrackPositionChanged(state.position)
            is PlayerState.Prepared -> PreparedPlayer()
            is PlayerState.Playing -> PlayerStart()
            is PlayerState.Pause -> PlayerPaused()
            is PlayerState.Complete -> TrackComplete()
        }
    }

    private fun like(isFavoriteState: FavoriteState) {
        when (isFavoriteState) {
            FavoriteState(true) -> likeButton.setImageResource(R.drawable.button_like)
            FavoriteState(false) -> likeButton.setImageResource(R.drawable.like_buttom)
        }
    }

    private fun PlayerStart() {
        playButton.setImageResource(R.drawable.paus_buttom)
    }

    private fun PlayerPaused() {
        playButton.setImageResource(R.drawable.play_buttom)
    }

    private fun TrackPositionChanged(position: String) {
        durationTextView.text = position
    }

    private fun PreparedPlayer() {
        playButton.isClickable = true
        playButton.setImageResource(R.drawable.play_buttom)
    }

    private fun TrackComplete() {
        playButton.setImageResource(R.drawable.play_buttom)
        durationTextView.setText(R.string.durationSample2)
    }

    private fun playlistStateManage(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> {
                binding.bottomSheetRecyclerView.visibility = View.GONE
            }

            is PlaylistState.Data -> {
                val playlists = state.tracks
                binding.bottomSheetRecyclerView.visibility = View.VISIBLE
                adapter.playlists = playlists as ArrayList<Playlist>
                adapter.notifyDataSetChanged()
            }

            else -> {}
        }
    }

    override fun onClick(playlist: Playlist) {
        if (!mediaViewModel.inPlaylist(
                playlist = playlist,
                trackId = track.trackId?.toLong() ?: 0
            )
        ) {
            mediaViewModel.clickOnAddtoPlaylist(playlist = playlist, track = track)
            Toast.makeText(
                requireContext().applicationContext,
                "${getString(R.string.added_to_playlist)} ${playlist.name}",
                Toast.LENGTH_SHORT
            )
                .show()

            playlist.tracksAmount = playlist.tracksIds.split(",").size

            BottomSheetBehavior.from(binding.bottomSheetAudioPlayer).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        } else {
            Toast.makeText(
                requireContext().applicationContext,
                "${getString(R.string.already_in_playlist)} ${playlist.name}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        BottomSheetBehavior.from(binding.bottomSheetAudioPlayer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    companion object {
        private const val SEARCH_QUERY_HISTORY = "SEARCH_QUERY_HISTORY"
    }
}
