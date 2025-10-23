package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.PropBooleanRepositoryImpl
import com.example.playlistmaker.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.DarkThemeInteractor
import com.example.playlistmaker.domain.api.PropBooleanRepository
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.DarkThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getTracksHistoryRepository(sharedPreferences: SharedPreferences): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(sharedPreferences)
    }

    private fun getPropBooleanRepository(sharedPreferences: SharedPreferences): PropBooleanRepository {
        return PropBooleanRepositoryImpl(sharedPreferences)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideTracksHistoryInteractor(sharedPreferences: SharedPreferences): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(sharedPreferences))
    }

    fun provideDarkThemeInteractor(sharedPreferences: SharedPreferences): DarkThemeInteractor {
        return DarkThemeInteractorImpl(getPropBooleanRepository(sharedPreferences))
    }

}