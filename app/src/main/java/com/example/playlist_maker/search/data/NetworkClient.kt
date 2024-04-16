package com.example.playlist_maker.search.data

interface NetworkClient {
    fun doRequest(dto: Any): Response
}