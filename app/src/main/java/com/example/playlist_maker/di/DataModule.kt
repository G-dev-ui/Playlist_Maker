package com.example.playlist_maker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlist_maker.search.data.network.ItunesApiService
import com.example.playlist_maker.search.data.network.NetworkClient
import com.example.playlist_maker.search.data.network.RetrofitNetworkClient
import com.example.playlist_maker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlist_maker.search.domain.repository.SearchHistoryRepository
import com.example.playlist_maker.settings.data.SettingsRepository
import com.example.playlist_maker.settings.data.SettingsRepositoryImpl
import com.example.playlist_maker.sharing.data.ExternalNavigator
import com.example.playlist_maker.sharing.data.ExternalNavigatorImpl


import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModel = module {

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory { MediaPlayer() }



    factory<NetworkClient> { RetrofitNetworkClient (context = get(), itunesApiService = get())}


    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl (get())}

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single<ExternalNavigator> { ExternalNavigatorImpl(get()) }

}