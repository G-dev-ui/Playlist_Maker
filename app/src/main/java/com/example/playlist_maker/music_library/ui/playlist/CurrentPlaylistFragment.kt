package com.example.playlist_maker.music_library.ui.playlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentCurrentPlaylistBinding
import com.example.playlist_maker.main.ui.MainActivity
import com.example.playlist_maker.music_library.domain.Playlist
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.search.ui.TrackAdapter
import com.example.playlist_maker.util.CURRENT_PLAYLIST
import com.example.playlist_maker.util.MODIFY_PLAYLIST
import com.example.playlist_maker.util.SEARCH_QUERY_HISTORY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class CurrentPlaylistFragment : Fragment() {
    private var _binding: FragmentCurrentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModel<CurrentPlaylistViewModel>()
    private lateinit var playlist: Playlist
    private lateinit var callback: OnBackPressedCallback
    private val adapter = TrackAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlist = (arguments?.getParcelable(CURRENT_PLAYLIST) as? Playlist)!!

        adapter.itemClickListener = {
            openAudioPlayer(it)
        }
        adapter.itemLongClickListener = {
            showDialog(playlist, it.trackId?.toLong() ?: 0)
        }

        (activity as? MainActivity)?.hideNavBar()

        binding.rvCurrentPlaylist.adapter = adapter

        renderCurrentPlaylist()

        val playlistId = playlist.id
        val screenHeight = resources.displayMetrics.heightPixels
        val allowableHeight = (screenHeight * 0.25).toInt()
        val bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomMenuCurrentPlaylist).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        val bottomSheetBehaviorPlaylist =
            BottomSheetBehavior.from(binding.playlistBottomMenuTracks).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }

        bottomSheetBehaviorPlaylist.peekHeight = allowableHeight

        binding.rvCurrentPlaylist.visibility = View.VISIBLE
        binding.bottomMenuCurrentPlaylist.visibility = View.VISIBLE

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBehaviorPlaylist.isHideable = false
                        binding.menuBottomSheetOverlay.visibility = View.GONE
                    }

                    else -> {
                        bottomSheetBehaviorPlaylist.isHideable = true
                        bottomSheetBehaviorPlaylist.state = BottomSheetBehavior.STATE_HIDDEN
                        binding.menuBottomSheetOverlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheetBehaviorPlaylist.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
        val bundle = Bundle().apply {
            putParcelable(MODIFY_PLAYLIST, playlist)
        }
        binding.editMenuCurrentPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_currentPlaylistFragment_to_modifyPlaylistFragment,
                bundle
            )
        }

        binding.toolbarCurrentPlaylist.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.shareCurrentPlaylist.setOnClickListener {
            toShare()
        }

        binding.shareMenuCurrentPlaylist.setOnClickListener {
            toShare()
        }

        binding.deleteMenuCurrentPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            toDelete()
        }

        binding.menuDotsCurrentPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.titleBottomCurrentPlaylist.text = playlist.name
            binding.trackAmountBottomCurrentPlaylist.text = countTracks(playlist.tracksAmount)
        }

        vm.getPlaylistById(playlistId)

        vm.observeTrackCount().observe(viewLifecycleOwner) { trackCount ->
            binding.trackAmountCurrentPlaylist.text = countTracks(trackCount)
        }

        vm.observePlaylistAllTime().observe(viewLifecycleOwner) {
            vm.playlistAllTime()
            if (it != null) {
                renderDuration(it)
            } else {
                Log.d("no tracks to count", "there is no track time to count")
            }
        }

        vm.observePlaylistId().observe(viewLifecycleOwner) {
            playlist = it
            vm.playlistAllTime()
            vm.getAllTracks(playlist.tracks)
        }

        vm.observePlaylistTracks().observe(viewLifecycleOwner) {
            if (it != null) {
                showContent(it)
                vm.playlistAllTime()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (playlist.tracksAmount == 0) {
            binding.noTracksInPlaylist.visibility = View.VISIBLE
        } else {
            binding.noTracksInPlaylist.visibility = View.GONE
        }

        val playlistId = playlist.id
        vm.getPlaylistById(playlistId)
        vm.observePlaylistId().observe(viewLifecycleOwner) {
            playlist = it
            vm.getAllTracks(playlist.tracks)
            vm.playlistAllTime()
            renderCurrentPlaylist()
        }
    }

    private fun renderDuration(time: Long) {
        val duration = SimpleDateFormat("mm", Locale.getDefault()).format(time).toInt()
        val formattedDuration =
            resources.getQuantityString(R.plurals.minutes, duration, duration)
        binding.fullTimeCurrentPlaylist.text = formattedDuration
    }

    private fun toDelete() {
        MaterialAlertDialogBuilder(requireContext(), R.style.custom_alert_dialog_theme)
            .setMessage(getString(R.string.delete_playlist_dialog))
            .setNegativeButton(getString(R.string.answer_no)) { dialog, witch -> }
            .setPositiveButton(getString(R.string.answer_yes)) { dialog, witch ->
                vm.deletePlaylist()
                findNavController().navigateUp()
            }.show()
    }

    private fun toShare() {
        val tracks = vm.observePlaylistTracks().value ?: emptyList()
        if (tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.on_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val message = formMessage()
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun formMessage(): String {
        val name = playlist.name
        val descript = playlist.description
        val tracks = vm.observePlaylistTracks().value ?: emptyList()
        val stringBuilder = StringBuilder()
        with(stringBuilder) {
            append("$name\n")
            append("$descript\n")
            append("${countTracks(tracks.size)}\n")
        }
        for ((index, track) in tracks.withIndex()) {
            val trackTimeMillis = track.trackTimeMillis.toLongOrNull()
            val formattedTime = if (trackTimeMillis != null) {
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
            } else {
                "00:00"
            }
            stringBuilder.append(
                "${index + 1}. ${track.artistName} - ${track.trackName} ($formattedTime)\n"
            )
        }
        return stringBuilder.toString()
    }

    private fun renderCurrentPlaylist() {
        binding.trackAmountCurrentPlaylist.text = countTracks(playlist.tracksAmount)
        binding.titleCurrentPlaylist.text = playlist.name
        binding.descriptionTextviewCurrentPlaylist.text = playlist.description

        val imageUri = playlist.imageUri.let { Uri.parse(it) }
        Glide.with(requireContext())
            .load(imageUri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.imageNewPlaylistImage)

        Glide.with(requireContext())
            .load(imageUri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.coverBottomCurrentPlaylist)
    }

    private fun countTracks(count: Int): String {
        val countTrack = context?.resources?.getQuantityString(R.plurals.track_count, count)
        return "$count $countTrack"

    }

    private fun showContent(tracks: List<Track>) {
        binding.rvCurrentPlaylist.visibility = View.VISIBLE
        with(adapter) {
            updateTracks(tracks)
            notifyItemRangeChanged(0, tracks.size)
        }
    }

    private fun openAudioPlayer(track: Track) {
        val bundle = Bundle()
        bundle.putParcelable(SEARCH_QUERY_HISTORY, track)
        findNavController().navigate(
            R.id.action_currentPlaylistFragment_to_audioPlayerFragment,
            bundle
        )
    }

    private fun showDialog(playlist: Playlist, trackId: Long) {
        MaterialAlertDialogBuilder(requireContext(), R.style.custom_alert_dialog_theme)
            .setMessage(getString(R.string.delete_track_dialog))
            .setNegativeButton(getString(R.string.answer_no)) { dialog, witch -> }
            .setPositiveButton(getString(R.string.answer_yes)) { dialog, witch ->
                vm.deleteTrackFromPlaylist(playlist, trackId)
                binding.trackAmountCurrentPlaylist.text =
                    countTracks(vm.observePlaylistTracks().value?.size ?: 0)
                val position = adapter.tracks.indexOfFirst { it.trackId?.toLong() == trackId }
                if (position != -1) {
                    adapter.tracks.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::callback.isInitialized) {
            callback.remove()
        }
    }
}