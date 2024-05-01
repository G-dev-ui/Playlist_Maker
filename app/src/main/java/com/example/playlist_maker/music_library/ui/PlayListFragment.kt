package com.example.playlist_maker.music_library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlist_maker.databinding.FragmentPlayListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayListFragment : Fragment() {

    companion object {
            fun newInstance() = PlayListFragment()
    }

    private var _binding: FragmentPlayListBinding? = null
    private val binding get() = _binding!!
    private val plViewModel by viewModel<PlayListViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }


}