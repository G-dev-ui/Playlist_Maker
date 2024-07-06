package com.example.playlist_maker.di

import com.example.playlist_maker.music_library.ui.favorite.FavoritesTracksViewModel
import com.example.playlist_maker.music_library.ui.playlist.NewPlaylistViewModel
import com.example.playlist_maker.music_library.ui.playlist.PlayListViewModel
import com.example.playlist_maker.player.ui.MediaPlayerViewModel
import com.example.playlist_maker.search.ui.TracksSearchViewModel
import com.example.playlist_maker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel{
        MediaPlayerViewModel( mediaPlayerRepository = get(), get(), get())
    }

    viewModel {
        TracksSearchViewModel(tracksInteractor = get(), searchHistoryRepository = get())
    }

    viewModel {
        SettingsViewModel(settingsInteractor = get(), sharingInteractor = get())
    }
    viewModel{
       FavoritesTracksViewModel(favoriteTracksInteractor = get())
    }

    viewModel {
        PlayListViewModel(get())
    }

    viewModel{
        NewPlaylistViewModel(get())
    }
}