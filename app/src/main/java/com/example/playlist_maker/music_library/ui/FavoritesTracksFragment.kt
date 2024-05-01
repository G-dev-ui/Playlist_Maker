package com.example.playlist_maker.music_library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.playlist_maker.databinding.FragmentFavoritesTracksBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {
    companion object {
        fun newInstance() = FavoritesTracksFragment()
    }

    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding get() = _binding!!
    private val ltViewModel by viewModel<FavoritesTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
}