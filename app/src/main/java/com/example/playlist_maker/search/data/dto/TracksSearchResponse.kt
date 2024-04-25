package com.example.playlist_maker.search.data.dto

import com.example.playlist_maker.search.data.dto.Response
import com.example.playlist_maker.search.data.dto.TrackDto


class TracksSearchResponse(
    val text: String,
    val results: List<TrackDto>) : Response()