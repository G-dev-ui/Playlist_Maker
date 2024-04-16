package com.example.playlist_maker.creator

import android.content.Context
import com.example.playlist_maker.search.data.RetrofitNetworkClient
import com.example.playlist_maker.search.data.TracksRepositoryImpl
import com.example.playlist_maker.search.domain.TracksInteractor
import com.example.playlist_maker.search.domain.TracksInteractorImpl
import com.example.playlist_maker.search.domain.TracksRepository

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }
}