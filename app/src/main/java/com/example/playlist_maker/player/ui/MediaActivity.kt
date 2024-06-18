package com.example.playlist_maker.player.ui



import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlist_maker.R
import com.example.playlist_maker.player.domain.Track
import com.example.playlist_maker.player.domain.getCoverArtwork
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale



class MediaActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var durationTextView: TextView
    private lateinit var likeButton: ImageButton

    private val mediaViewModel by viewModel<MediaPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        playButton = findViewById(R.id.playButton1)
        likeButton = findViewById(R.id.likeButton)
        durationTextView = findViewById(R.id.durationTextView1)

        mediaViewModel.observeState().observe(this) { render(it) }
        mediaViewModel.favoriteState.observe(this@MediaActivity, Observer { isFavorite ->
            like(isFavorite)
        })

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
            val date = yearFormat.parse(releaseDate)
            yearFormat.format(date)
        } catch (e: ParseException) {
            ""
        }
        val primaryGenreName = intent.getStringExtra("primaryGenreName") ?: ""
        val country = intent.getStringExtra("country") ?: ""
        val track = Track(trackId, trackName, artistName, trackTime, artworkUrl100, collectionName,
            formattedReleaseDate as String, primaryGenreName, country, previewUrl, insertTime = null)

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

        if (previewUrl.isNotEmpty()) {
            mediaViewModel.preparePlayer(previewUrl)
        }

        playButton.setOnClickListener {
            mediaViewModel.playbackControl()
        }
        mediaViewModel.isFavorite(track)
        likeButton.setOnClickListener {
            mediaViewModel.addToFavorite(track)
        }
    }

    override fun onPause() {
        super.onPause()
        playButton.setImageResource(R.drawable.play_buttom)
        mediaViewModel.releaseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
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
}