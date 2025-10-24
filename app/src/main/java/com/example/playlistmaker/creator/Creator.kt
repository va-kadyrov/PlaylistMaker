package com.example.playlistmaker.creator

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.data.DarkThemeInteractor
import com.example.playlistmaker.settings.data.DarkThemeInteractorImpl
import com.example.playlistmaker.settings.data.PropBooleanRepositoryImpl
import com.example.playlistmaker.settings.domain.api.PropBooleanRepository

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