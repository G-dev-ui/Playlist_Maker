package com.example.playlist_maker.music_library.ui.favorite



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentFavoritesTracksBinding
import com.example.playlist_maker.main.ui.MainActivity
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.search.ui.TrackAdapter
import com.example.playlist_maker.util.SEARCH_QUERY_HISTORY
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {

    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding get() = _binding!!
    private var favoriteAdapter = TrackAdapter()


    private val vm by viewModel<FavoritesTracksViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getFavoriteList()
        binding.libraryEmpty.visibility = View.GONE
        binding.favoriteRecyclerView.adapter = favoriteAdapter

        (activity as? MainActivity)?.showNavBar()

        favoriteAdapter.itemClickListener = {
            openAudioPlayer(track = it)
        }

        vm.favoriteState.observe(viewLifecycleOwner) {
            execute(it)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.getFavoriteList()
    }

    private fun execute(favoriteState: FavoriteState) {
        when (favoriteState) {
            is FavoriteState.Content -> showFavoritesList(favoriteState.tracks)
            is FavoriteState.NoEntry -> showBlank()
            is FavoriteState.Load -> showLoading()
        }
    }


    private fun showFavoritesList(tracks: List<Track>) {
        binding.libraryEmpty.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.favoriteRecyclerView.visibility = View.VISIBLE

        favoriteAdapter.updateTracks(tracks)

    }

    private fun showBlank() {
        binding.libraryEmpty.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.favoriteRecyclerView.visibility = View.GONE
    }

    private fun showLoading() {
        binding.libraryEmpty.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.favoriteRecyclerView.visibility = View.GONE
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openAudioPlayer(track: Track) {
        val bundle = Bundle()
        bundle.putParcelable(SEARCH_QUERY_HISTORY, track)
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_audioPlayerFragment,
            bundle
        )
    }

    companion object {
        fun newInstance() = FavoritesTracksFragment()
    }
}