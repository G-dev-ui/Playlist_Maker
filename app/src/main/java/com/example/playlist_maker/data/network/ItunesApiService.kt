package com.example.playlist_maker.data.network

import com.example.playlist_maker.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}