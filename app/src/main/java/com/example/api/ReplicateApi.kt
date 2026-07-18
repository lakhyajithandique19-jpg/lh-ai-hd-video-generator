package com.example.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReplicateApi {
    @POST("v1/predictions")
    suspend fun createPrediction(
        @Header("Authorization") authorization: String,
        @Body request: PredictionRequest
    ): PredictionResponse

    @GET("v1/predictions/{id}")
    suspend fun getPrediction(
        @Header("Authorization") authorization: String,
        @Path("id") predictionId: String
    ): PredictionResponse
}
