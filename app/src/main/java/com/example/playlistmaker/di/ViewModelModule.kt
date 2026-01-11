package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.EditPlaylistViewModel
import com.example.playlistmaker.media.ui.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.NewPlaylistViewModel
import com.example.playlistmaker.media.ui.PlaylistInfoViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(get(), get())
    }
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }
    viewModel {
        PlaylistsViewModel(get())
    }
    viewModel {
        NewPlaylistViewModel(get())
    }
    viewModel {
        PlaylistInfoViewModel(get(), get())
    }
    viewModel {
        EditPlaylistViewModel(get())
    }
}
