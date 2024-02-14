package com.example.playlist_maker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val backButton = findViewById<ImageButton>(R.id.back_button_playerActivity1)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val trackId = intent.getLongExtra("trackId", 0)
        val trackName = intent.getStringExtra("trackName") ?: ""
        val artistName = intent.getStringExtra("artistName") ?: ""
        val trackTime = intent.getStringExtra("trackTime") ?: ""
        val artworkUrl100 = intent.getStringExtra("artworkUrl100") ?: ""
        val collectionName = intent.getStringExtra("collectionName") ?: ""
        val releaseDate = intent.getStringExtra("releaseDate") ?: ""
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val formattedReleaseDate = try {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(releaseDate)
            yearFormat.format(date)
        } catch (e: ParseException) {
            ""
        }
        val primaryGenreName = intent.getStringExtra("primaryGenreName") ?: ""
        val country = intent.getStringExtra("country") ?: ""

        val track = Track(trackId, trackName, artistName, trackTime, artworkUrl100, collectionName, formattedReleaseDate, primaryGenreName, country)

        val coverImageView = findViewById<ImageView>(R.id.album_cover)

        Glide.with(this)
            .load(track.getCoverArtwork())
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder2)
                    .error(R.drawable.placeholder2)
            )
            .into(coverImageView)

        val trackNameTextView = findViewById<TextView>(R.id.trackNamePlayerActivity)
        val artistNameTextView = findViewById<TextView>(R.id.artistNamePlayerActivity1)
        val trackTimeTextView = findViewById<TextView>(R.id.durationValue1)
        val collectionNameTextView = findViewById<TextView>(R.id.albumValue1)
        val releaseDateTextView = findViewById<TextView>(R.id.yearValue1)
        val primaryGenreNameTextView = findViewById<TextView>(R.id.genreValue1)
        val countryTextView = findViewById<TextView>(R.id.countryValue1)

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.trackTime
        if (!collectionName.isNullOrBlank()) {
            collectionNameTextView.visibility = View.VISIBLE
            collectionNameTextView.text = collectionName
        } else {
            collectionNameTextView.visibility = View.GONE
        }

        releaseDateTextView.text = track.releaseDate
        primaryGenreNameTextView.text = primaryGenreName
        countryTextView.text = country
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}