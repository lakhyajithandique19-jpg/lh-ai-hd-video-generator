package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationDao {
    @Query("SELECT * FROM generations ORDER BY timestamp DESC")
    fun getAllGenerations(): Flow<List<GenerationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneration(generation: GenerationEntity): Long

    @Query("DELETE FROM generations WHERE id = :id")
    suspend fun deleteGeneration(id: Int)

    @Query("UPDATE generations SET localVideoPath = :localPath WHERE id = :id")
    suspend fun updateLocalVideoPath(id: Int, localPath: String)
}
