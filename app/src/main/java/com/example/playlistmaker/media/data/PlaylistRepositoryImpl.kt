package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.player.data.TrackDbConverter
import com.example.playlistmaker.player.data.db.AppDatabase

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
): PlaylistRepository {
    override suspend fun add(playlist: Playlist) {
        appDatabase.playlistDao().insert(playlistDbConverter.map(playlist))
    }
}