package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.data.Playlist
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
    override suspend fun loadInfo(id: Long): Flow<Playlist>{
        return withContext(Dispatchers.IO) { repository.loadInfo(id) }
    }
    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) { repository.delete(id) }
    }

    override suspend fun trackIsIdle(idTrack: Long): Boolean{
        var res = true
        repository.loadAll().collect{
            playlists -> playlists.forEach {
                if (it.tracks.contains(idTrack)) res = false
            }
        }
        return res
    }
}