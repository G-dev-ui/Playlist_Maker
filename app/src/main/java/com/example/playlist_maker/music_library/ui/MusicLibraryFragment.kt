package com.example.playlist_maker.music_library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentMusicLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator


class MusicLibraryFragment : Fragment() {


    private  var _binding: FragmentMusicLibraryBinding? = null
    private  var tabMediator: TabLayoutMediator? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMusicLibraryBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.viewPager.adapter = LibraryViewPagerAdapter(childFragmentManager, lifecycle)


        tabMediator = TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.selected_tracks)
                else -> tab.text = resources.getString(R.string.playlists)
            }
        }
        tabMediator!!.attach()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator?.detach()
        _binding = null
    }
}