package com.example.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PredictionRequest(
    val version: String,
    val input: Map<String, Any>
)

@JsonClass(generateAdapter = true)
data class PredictionResponse(
    val id: String,
    val version: String?,
    val status: String, // starting, processing, succeeded, failed, canceled
    val output: Any?,   // can be List<String> or String URL
    val error: String?,
    val urls: PredictionUrls?
)

@JsonClass(generateAdapter = true)
data class PredictionUrls(
    val get: String?,
    val cancel: String?
)
