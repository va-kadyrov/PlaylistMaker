package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder().baseUrl(I_TUNES_URL).addConverterFactory(
        GsonConverterFactory.create()).build()

    private val tracksApiService = retrofit.create(TracksApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            val resp = tracksApiService.getTracks("song", dto.expression).execute()
            val body = resp.body() ?: Response()
            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400}
        }
    }

    companion object{
        const val I_TUNES_URL = "https://itunes.apple.com"
    }
}