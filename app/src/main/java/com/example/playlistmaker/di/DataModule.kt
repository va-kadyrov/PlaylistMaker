package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.player.data.TrackDbConverter
import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.player.data.db.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.PropBooleanRepositoryImpl
import com.example.playlistmaker.settings.domain.api.PropBooleanRepository
import com.google.gson.Gson
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
        TracksRepositoryImpl(get(), get())
    }

    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(), get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    factory { Gson() }

    factory { TrackDbConverter() }

}