package com.example.playlist_maker.player.ui

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.ViewBottomSheetPlaylistBinding
import com.example.playlist_maker.music_library.domain.Playlist


class AudioPlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ViewBottomSheetPlaylistBinding.bind(view)

    fun bind(model: Playlist, clickListener: ClickListener) {
        binding.bottomSheetNamePlaylist.text = model.name
        binding.bottomSheetDecsPlaylist.text = setTracksAmount(itemView.context, model.tracksAmount)
        val previewUri = model.imageUri.let { Uri.parse(it) }

        Glide.with(itemView)
            .load(previewUri)
            .placeholder(R.drawable.placeholder)
            .into(binding.bottomSheetCoverPlaylist)
        itemView.setOnClickListener {
            clickListener.onClick(model)
        }
    }

    private fun setTracksAmount(context: Context, count: Int): String {
        val countTrack = context.resources.getQuantityString(R.plurals.track_count, count)
        return "$count $countTrack"
    }

    fun interface ClickListener {
        fun onClick(playlist: Playlist)
    }

}