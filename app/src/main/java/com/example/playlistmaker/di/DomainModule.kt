package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.db.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.player.domain.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.FavoriteTracksInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.impl.DarkThemeInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<DarkThemeInteractor> {
        DarkThemeInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

}