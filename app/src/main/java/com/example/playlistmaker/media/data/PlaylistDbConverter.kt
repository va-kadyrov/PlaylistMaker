package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.google.gson.Gson

class PlaylistDbConverter {
    val gson = Gson()

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(playlist.id, playlist.name, playlist.description,
            playlist.filePath, gson.toJson(playlist.tracks), System.currentTimeMillis())
    }
    fun map(playlist: PlaylistEntity): Playlist {
        val _tracks = gson.fromJson(playlist.tracks, Array<Long>::class.java)
        val tracks = _tracks.toMutableList()
        return Playlist(playlist.id, playlist.name, playlist.description,
            playlist.filePath, tracks, tracks.count())
    }

}