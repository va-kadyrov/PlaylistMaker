package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.search.data.network.Response

data class TracksSearchResponse(
    val resultCount: Int,
    val results:List<TrackDTO>) : Response()