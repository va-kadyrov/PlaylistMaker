package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.media.data.PlaylistDbConverter
import com.example.playlistmaker.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.player.data.FavoriteTrackDbConverter
import com.example.playlistmaker.player.data.TrackDbConverter
import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.player.data.db.TracksRepositoryImpl
import com.example.playlistmaker.player.domain.db.TracksRepository
import com.example.playlistmaker.search.data.TracksHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksNtRepositoryImpl
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksNtRepository
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

    single<TracksNtRepository> {
        TracksNtRepositoryImpl(get(), get())
    }

    single<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { Gson() }

    factory { TrackDbConverter() }

    factory { FavoriteTrackDbConverter() }

    factory { PlaylistDbConverter() }

}