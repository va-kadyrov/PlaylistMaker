package com.example.playlistmaker.di

import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.media.domain.PlaylistInteractorImpl
import com.example.playlistmaker.player.domain.TracksInteractor
import com.example.playlistmaker.player.domain.TracksInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksNtInteractor
import com.example.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksNtInteractorImpl
import com.example.playlistmaker.settings.domain.api.DarkThemeInteractor
import com.example.playlistmaker.settings.domain.impl.DarkThemeInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<DarkThemeInteractor> {
        DarkThemeInteractorImpl(get())
    }

    single<TracksNtInteractor> {
        TracksNtInteractorImpl(get())
    }

    single<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

}