package com.example.playlistmaker.data

interface NetworkClient {
    fun doRequest(dto: Any): Response
}