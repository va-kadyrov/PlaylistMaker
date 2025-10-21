package com.example.playlistmaker.data.dto

import com.example.playlistmaker.data.Response

data class TracksSearchResponse(
    val resultCount: Int,
    val results:List<TrackDTO>) : Response()