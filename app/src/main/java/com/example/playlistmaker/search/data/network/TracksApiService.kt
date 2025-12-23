package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksApiService {
    @GET("search")
    suspend fun getTracks(
        @Query("entity") song: String,
        @Query("term", encoded = false) searchString: String,
    ): TracksSearchResponse
}