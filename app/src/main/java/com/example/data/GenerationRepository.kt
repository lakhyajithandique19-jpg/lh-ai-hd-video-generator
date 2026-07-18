package com.example.data

import kotlinx.coroutines.flow.Flow

class GenerationRepository(private val generationDao: GenerationDao) {
    val allGenerations: Flow<List<GenerationEntity>> = generationDao.getAllGenerations()

    suspend fun insert(generation: GenerationEntity): Long {
        return generationDao.insertGeneration(generation)
    }

    suspend fun delete(id: Int) {
        generationDao.deleteGeneration(id)
    }

    suspend fun updateLocalVideoPath(id: Int, localPath: String) {
        generationDao.updateLocalVideoPath(id, localPath)
    }
}
