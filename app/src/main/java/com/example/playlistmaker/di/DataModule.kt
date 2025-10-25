package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.PropBooleanRepositoryImpl
import com.example.playlistmaker.settings.domain.api.PropBooleanRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences("PM_SHARED_PREFERENCES", Context.MODE_PRIVATE)
    }

    single<PropBooleanRepository> {
        PropBooleanRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

}