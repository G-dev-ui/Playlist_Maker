package com.example.playlist_maker.player.ui



import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.player.data.MediaRepositoryImpl
import com.example.playlist_maker.player.domain.MediaRepository
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.player.domain.getCoverArtwork
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private lateinit var playButton: ImageButton
    private lateinit var durationTextView: TextView
    private lateinit var mediaInteractor: MediaRepository
    private var handler = Handler(Looper.getMainLooper())

    private val updateProgressAction = object : Runnable {
        override fun run() {
            if (mediaInteractor.isPlaying()) {
                val currentPosition = mediaInteractor.getCurrentPosition()
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                durationTextView.text = formattedTime
                handler.postDelayed(this, 500)
            }
        }
    }

    private fun startUpdatingProgress() {
        handler.post(updateProgressAction)
    }

    private fun stopUpdatingProgress() {
        handler.removeCallbacks(updateProgressAction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        durationTextView = findViewById(R.id.durationTextView1)
        val backButton = findViewById<ImageButton>(R.id.back_button_playerActivity1)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val previewUrl = intent.getStringExtra("previewUrl") ?: ""
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
        val track = Track(trackId, trackName, artistName, trackTime, artworkUrl100, collectionName, formattedReleaseDate, primaryGenreName, country, previewUrl)

        mediaInteractor = MediaRepositoryImpl()

        track.previewUrl?.let { preparePlayer(it) }

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
        playButton = findViewById(R.id.playButton1)

        playButton.setOnClickListener {
            playbackControl()
            if (playerState == STATE_PLAYING) {
                startUpdatingProgress()
            } else {
                stopUpdatingProgress()
            }
        }

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.trackTimeMillis
        if (!track.collectionName.isNullOrBlank()) {
            collectionNameTextView.visibility = View.VISIBLE
            collectionNameTextView.text = track.collectionName
        } else {
            collectionNameTextView.visibility = View.GONE
        }

        releaseDateTextView.text = track.releaseDate
        primaryGenreNameTextView.text = track.primaryGenreName
        countryTextView.text = track.country
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaInteractor.releaseMediaPlayer()
        stopUpdatingProgress()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
        updatePlayButtonImage()
    }

    private fun updatePlayButtonImage() {
        val playButtonDrawableRes = when (playerState) {
            STATE_PLAYING -> R.drawable.paus_buttom
            STATE_PREPARED, STATE_PAUSED -> R.drawable.play_buttom
            else -> R.drawable.play_buttom
        }
        playButton.setImageResource(playButtonDrawableRes)
    }

    private fun preparePlayer(trackUrl: String) {
        mediaInteractor.prepareMediaPlayer(trackUrl, {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }, {
            playerState = STATE_PREPARED
            updatePlayButtonImage()
            stopUpdatingProgress()
            durationTextView.text = "00:00"
        })
    }

    private fun startPlayer() {
        mediaInteractor.startMediaPlayer()
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaInteractor.pauseMediaPlayer()
        playerState = STATE_PAUSED
    }
}