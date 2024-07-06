package com.example.playlist_maker.search.ui


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlist_maker.R
import com.example.playlist_maker.player.domain.Track

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: ArrayList<Track> = ArrayList()
    lateinit var itemClickListener: ((Track) -> Unit)

    lateinit var itemLongClickListener: ((Track) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_viwe, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(tracks[position])
            this.notifyItemRangeChanged(0, tracks.size)
        }
        holder.itemView.setOnLongClickListener {
            itemLongClickListener.invoke(tracks[position])
            true
        }
    }

}