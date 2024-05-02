package com.example.playlist_maker.music_library.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ActivityMusicLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MusicLibrary : AppCompatActivity() {


    private var binding: ActivityMusicLibraryBinding? = null
    private var tabMediator: TabLayoutMediator? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMusicLibraryBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.viewPager.adapter = LibraryViewPagerAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager) { tab, position ->
            when(position) {
                0-> tab.text = resources.getString(R.string.selected_tracks)
                else -> tab.text = resources.getString((R.string.playlists))
            }
        }
        tabMediator!!.attach()
        binding!!.image.setOnClickListener {
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        tabMediator?.detach()
    }
}