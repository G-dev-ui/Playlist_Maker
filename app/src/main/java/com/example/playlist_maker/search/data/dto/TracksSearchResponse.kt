package com.example.playlist_maker.search.data.dto




class TracksSearchResponse(
    val text: String,
    val results: List<TrackDto>) : Response()