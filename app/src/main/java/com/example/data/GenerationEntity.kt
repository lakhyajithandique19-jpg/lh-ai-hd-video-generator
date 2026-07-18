package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generations")
data class GenerationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceImagePath: String, // Path to the saved source image in internal storage
    val videoUrl: String,       // Remote URL of the generated video
    val localVideoPath: String?, // Local file path if downloaded
    val motionBucket: Int,       // Motion intensity slider value
    val targetStructure: String, // Target Video Frame Structure
    val timestamp: Long = System.currentTimeMillis()
)
