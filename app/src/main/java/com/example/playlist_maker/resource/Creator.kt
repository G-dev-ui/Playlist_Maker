package com.example.playlist_maker.resource

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlist_maker.player.data.MediaRepositoryImpl
import com.example.playlist_maker.player.domain.MediaRepository
import com.example.playlist_maker.search.data.network.RetrofitNetworkClient
import com.example.playlist_maker.search.data.repository.TracksRepositoryImpl
import com.example.playlist_maker.search.domain.TracksInteractor
import com.example.playlist_maker.search.domain.impl.TracksInteractorImpl
import com.example.playlist_maker.search.domain.api.TracksRepository
import com.example.playlist_maker.settings.data.SettingsRepository
import com.example.playlist_maker.settings.data.SettingsRepositoryImpl
import com.example.playlist_maker.settings.domain.SettingsInteractor
import com.example.playlist_maker.settings.domain.SettingsInteractorImpl
import com.example.playlist_maker.sharing.data.ExternalNavigator
import com.example.playlist_maker.sharing.data.ExternalNavigatorImpl
import com.example.playlist_maker.sharing.domain.SharingInteractor
import com.example.playlist_maker.sharing.domain.SharingInteractorImpl

//object Creator {
//    private fun getTracksRepository(context: Context): TracksRepository {
//        return TracksRepositoryImpl(RetrofitNetworkClient(context))
//    }
//
//    fun provideTracksInteractor(context: Context): TracksInteractor {
//        return TracksInteractorImpl(getTracksRepository(context))
//    }
//
//    fun provideMediaPlayerRepository() : MediaRepository{
//        return MediaRepositoryImpl()
//    }
//
//    fun providesettingsInteractor(context: Context): SettingsInteractor {
//        return SettingsInteractorImpl(getSettingsRepository(context))
//    }
//
//    const val THEME_PREFERENCES = "playlist_maker_theme_preferences"
//
//    fun getSettingsRepository(context: Context): SettingsRepository {
//        return SettingsRepositoryImpl(
//            context.getSharedPreferences(
//                THEME_PREFERENCES,
//                MODE_PRIVATE
//            )
//        )
//    }
//
//    fun provideSharingInteractor(context: Context): SharingInteractor {
//        return SharingInteractorImpl(getExternalNavigator(context))
//    }
//
//    private fun getExternalNavigator(context: Context): ExternalNavigator {
//        return ExternalNavigatorImpl(context)
//    }
//}