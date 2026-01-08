package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.player.domain.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(private val repository: PlaylistRepository): PlaylistInteractor {
    override suspend fun add(playlist: Playlist) {
        withContext(Dispatchers.IO) { repository.add(playlist) }
    }
    override suspend fun loadAll(): Flow<List<Playlist>>{
        return withContext(Dispatchers.IO) { repository.loadAll() }

    }
}