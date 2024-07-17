package com.example.playlist_maker.di

import com.example.playlist_maker.db.converters.PlaylistsDbConverter
import com.example.playlist_maker.db.converters.TrackDbConverter
import com.example.playlist_maker.db.converters.TracksToPlaylistConverter
import com.example.playlist_maker.music_library.data.FavoriteTracksRepositoryImpl
import com.example.playlist_maker.music_library.data.PlaylistsRepositoryImpl
import com.example.playlist_maker.music_library.domain.FavoriteTracksRepository
import com.example.playlist_maker.music_library.domain.PlaylistsRepository
import com.example.playlist_maker.player.data.MediaRepositoryImpl
import com.example.playlist_maker.player.domain.MediaRepository
import com.example.playlist_maker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlist_maker.search.data.repository.TracksRepositoryImpl
import com.example.playlist_maker.search.domain.api.TracksRepository
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.settings.data.SettingsRepository
import com.example.playlist_maker.settings.data.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module{

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }

    single<MediaRepository> { MediaRepositoryImpl(get()) }

    single<TracksRepository> { TracksRepositoryImpl(get()) }

    factory { TrackDbConverter() }

    factory { PlaylistsDbConverter() }

    factory { TracksToPlaylistConverter() }

    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get (), get()) }

    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get(), get(), get()) }

}