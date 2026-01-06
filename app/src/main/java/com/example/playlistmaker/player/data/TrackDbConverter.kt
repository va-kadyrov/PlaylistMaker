package com.example.playlistmaker.player.data

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.player.data.db.TrackEntity
import com.example.playlistmaker.search.domain.Track
import java.util.Locale

class TrackDbConverter {
    private val dateFormat by lazy { SimpleDateFormat("YYYY-MM-DD", Locale.getDefault()) }
    fun map(track: Track): TrackEntity {
        return TrackEntity(track.trackId, track.trackName, track.collectionName,
            track.artistName, track.trackTimeMillis, dateFormat.format(track.releaseDate),
            track.primaryGenreName, track.country, track.artworkUrl100,
            track.previewUrl, System.currentTimeMillis())
    }
    fun map(track: TrackEntity): Track {
        return Track(track.trackName, track.collectionName,
            track.artistName, track.trackTimeMillis, dateFormat.parse(track.releaseDate),
            track.primaryGenreName, track.country, track.artworkUrl100,
            track.trackId, track.previewUrl)
    }
}