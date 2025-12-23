package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder().baseUrl(I_TUNES_URL).addConverterFactory(
        GsonConverterFactory.create()).build()

    private val tracksApiService = retrofit.create(TracksApiService::class.java)

    override suspend fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            return withContext(Dispatchers.IO) {
                try{
                    val resp = tracksApiService.getTracks("song", dto.expression)
                    resp.apply { resultCode = 200 }}
                catch (e: Throwable) {
                    Response().apply { resultCode = 500 }
                }
            }

        } else {
            return Response().apply { resultCode = 400}
        }
    }

    companion object{
        const val I_TUNES_URL = "https://itunes.apple.com"
    }
}