package com.example.playlistmaker.player.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun delete(track: TrackEntity)

    @Query("SELECT * FROM tracks ORDER BY timeStamp DESC")
    suspend fun getAll(): List<TrackEntity>

    @Query("SELECT trackId FROM tracks")
    fun getIds(): List<Long>

    @Query("SELECT * FROM tracks WHERE  trackId = :id")
    suspend fun getInfo(id: Long): TrackEntity
}