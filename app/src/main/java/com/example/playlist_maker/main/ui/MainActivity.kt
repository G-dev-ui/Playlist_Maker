package com.example.playlist_maker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlist_maker.music_library.ui.Music_Library
import com.example.playlist_maker.R
import com.example.playlist_maker.search.ui.SearchActivity
import com.example.playlist_maker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonSearch = findViewById<Button>(R.id.Search)
        buttonSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


        val buttonMedia = findViewById<Button>(R.id.music_library)
        buttonMedia.setOnClickListener {
            val intent = Intent(this, Music_Library::class.java)
            startActivity(intent)
        }


        val buttonSettings = findViewById<Button>(R.id.Settings)
        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}