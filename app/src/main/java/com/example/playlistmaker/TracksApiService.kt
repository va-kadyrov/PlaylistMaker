package com.example.playlistmaker

import TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksApiService {
    @GET("search")
    fun getTracks(
        @Query("entity") song: String,
        @Query("term", encoded = false) searchString: String,
    ): Call<TrackResponse>
}
