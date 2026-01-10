package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.domain.PlaylistRepository
import com.example.playlistmaker.player.data.db.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
): PlaylistRepository {
    override suspend fun add(playlist: Playlist) {
        appDatabase.playlistDao().insert(playlistDbConverter.map(playlist))
    }

    override suspend fun loadAll(): Flow<List<Playlist>> = flow {
        emit(convert(appDatabase.playlistDao().getAll()))
    }

    override suspend fun loadInfo(id: Long): Flow<Playlist> = flow {
        emit(playlistDbConverter.map(appDatabase.playlistDao().getInfo(id)))
    }

    private fun convert(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {playlist -> playlistDbConverter.map(playlist)}
    }


}