package com.example.playlist_maker.music_library.ui.favorite

import com.example.playlist_maker.player.domain.Track

sealed interface FavoriteState {

    data class Content(val tracks: List<Track>) : FavoriteState

    data object NoEntry : FavoriteState

    data object Load : FavoriteState

}