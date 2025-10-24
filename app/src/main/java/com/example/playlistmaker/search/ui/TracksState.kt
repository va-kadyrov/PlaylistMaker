package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.Track

data class TracksState(var isLoading: Boolean, var isEmpty: Boolean, var isError: Boolean, var ErrorDescription: String, val content:MutableList<Track>)