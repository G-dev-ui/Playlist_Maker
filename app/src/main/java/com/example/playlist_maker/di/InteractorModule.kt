package com.example.playlist_maker.di

import com.example.playlist_maker.music_library.domain.FavoriteTracksInteractor
import com.example.playlist_maker.music_library.domain.FavoriteTracksInteractorImpl
import com.example.playlist_maker.search.domain.TracksInteractor
import com.example.playlist_maker.search.domain.impl.TracksInteractorImpl
import com.example.playlist_maker.settings.domain.SettingsInteractor
import com.example.playlist_maker.settings.domain.SettingsInteractorImpl
import com.example.playlist_maker.sharing.domain.SharingInteractor
import com.example.playlist_maker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<SharingInteractor> { SharingInteractorImpl(get()) }

    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }

    factory<TracksInteractor> { TracksInteractorImpl(get()) }

    factory<FavoriteTracksInteractor> { FavoriteTracksInteractorImpl(get()) }

}