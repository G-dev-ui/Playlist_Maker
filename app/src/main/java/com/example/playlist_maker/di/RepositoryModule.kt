package com.example.playlist_maker.di

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

}