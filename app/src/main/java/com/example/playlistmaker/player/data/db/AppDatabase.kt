package com.example.playlistmaker.player.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.PlaylistDao
import com.example.playlistmaker.media.data.db.PlaylistEntity

@Database(version = 4, entities = [TrackEntity::class,PlaylistEntity::class,FavoriteTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
}