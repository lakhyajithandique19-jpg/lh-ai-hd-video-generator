package com.example.api

import android.content.Context
import android.util.Log
import com.example.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object MultiProviderClient {
    private const val TAG = "MultiProviderClient"

    // OkHttpClient with optimized timeouts and retry capabilities
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Executes post request to backend with automatic retries and exponential backoff
     */
    private suspend fun postToBackend(
        backendUrl: String,
        endpoint: String,
        payload: JSONObject,
        retries: Int = 2
    ): JSONObject = withContext(Dispatchers.IO) {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = payload.toString().toRequestBody(mediaType)
        
        // Clean and construct absolute URL
        val baseUrl = backendUrl.trim().removeSuffix("/")
        val url = "$baseUrl$endpoint"

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        var lastException: Exception? = null
        var delayMs = 1500L

        for (attempt in 0..retries) {
            try {
                client.newCall(request).execute().use { response ->
                    val responseString = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        return@withContext JSONObject(responseString)
                    } else {
                        val errMsg = try {
                            JSONObject(responseString).optString("error", "HTTP error ${response.code}")
                        } catch (ignored: Exception) {
                            "HTTP error ${response.code}: $responseString"
                        }
                        throw IOException(errMsg)
                    }
                }
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "Backend attempt ${attempt + 1} failed on $url: ${e.message}")
                if (attempt < retries) {
                    delay(delayMs)
                    delayMs *= 2
                }
            }
        }
        throw lastException ?: IOException("Failed to connect to backend after multiple attempts")
    }

    /**
     * Call text generation with fallback chain (Gemini -> OpenRouter -> Hugging Face)
     */
    suspend fun generateText(context: Context, backendUrl: String, prompt: String): Pair<String, String> {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            throw IOException("No active internet connection. Please enable mobile data or Wi-Fi.")
        }
        if (backendUrl.isEmpty()) {
            throw IllegalArgumentException("Backend URL is not configured. Please set it in Settings.")
        }

        val payload = JSONObject().apply {
            put("prompt", prompt)
        }

        val resultJson = postToBackend(backendUrl, "/api/generate/text", payload)
        val text = resultJson.getString("text")
        val provider = resultJson.getString("provider")
        return Pair(text, provider)
    }

    /**
     * Generate high-quality image using Replicate via backend
     */
    suspend fun generateImage(context: Context, backendUrl: String, prompt: String): String {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            throw IOException("No active internet connection. Please check your network and retry.")
        }
        if (backendUrl.isEmpty()) {
            throw IllegalArgumentException("Backend URL is not configured.")
        }

        val payload = JSONObject().apply {
            put("prompt", prompt)
        }

        val resultJson = postToBackend(backendUrl, "/api/generate/image", payload)
        return resultJson.getString("imageUrl")
    }

    /**
     * Generate video sequences using Replicate via backend
     */
    suspend fun generateVideo(
        context: Context,
        backendUrl: String,
        prompt: String,
        inputImageBase64: String? = null
    ): String {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            throw IOException("No active internet connection to submit the video job.")
        }
        if (backendUrl.isEmpty()) {
            throw IllegalArgumentException("Backend URL is not configured in Settings.")
        }

        val payload = JSONObject().apply {
            put("prompt", prompt)
            if (!inputImageBase64.isNullOrEmpty()) {
                put("inputImage", inputImageBase64)
            }
        }

        val resultJson = postToBackend(backendUrl, "/api/generate/video", payload)
        return resultJson.getString("videoUrl")
    }
}
