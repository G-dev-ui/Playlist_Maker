package com.example.playlist_maker.search.ui

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.player.domain.Track

class TrackAdapter(var tracks: MutableList<Track>, private val onItemClick: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null
    private var lastClickTime: Long = 0

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_viwe, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()
            onItemClick(track)
        }
    }
}