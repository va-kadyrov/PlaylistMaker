package com.example.playlistmaker.creator

import android.content.Context
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

    private fun getTracksHistoryRepository(context: Context): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(context)
    }

    private fun getPropBooleanRepository(context: Context): PropBooleanRepository {
        return PropBooleanRepositoryImpl(context)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideTracksHistoryInteractor(context: Context): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getTracksHistoryRepository(context))
    }

    fun provideDarkThemeInteractor(context: Context): DarkThemeInteractor {
        return DarkThemeInteractorImpl(getPropBooleanRepository(context))
    }

}