package com.example.playlist_maker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlist_maker.domain.models.Track


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.name_track)
    private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTimeView: TextView = itemView.findViewById(R.id.time_track)
    private val artworkUrlView: ImageView = itemView.findViewById(R.id.image_track)

    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = model.trackTime

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(artworkUrlView)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}

