package com.example.playlist_maker.search.data



class TracksSearchResponse(
    val text: String,
    val results: List<TrackDto>) : Response()