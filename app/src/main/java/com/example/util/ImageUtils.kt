package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageUtils {

    private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
        val scheme = uri.scheme
        return try {
            when {
                scheme == "http" || scheme == "https" -> {
                    val client = okhttp3.OkHttpClient.Builder()
                        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
                    val request = okhttp3.Request.Builder().url(uri.toString()).build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.bytes()
                        } else {
                            null
                        }
                    }
                }
                scheme == "file" -> {
                    val file = File(uri.path ?: "")
                    file.readBytes()
                }
                scheme == "content" -> {
                    context.contentResolver.openInputStream(uri)?.use { its ->
                        its.readBytes()
                    }
                }
                else -> {
                    // Try as direct file path
                    val file = File(uri.toString())
                    if (file.exists()) {
                        file.readBytes()
                    } else {
                        // Fallback to content resolver
                        context.contentResolver.openInputStream(uri)?.use { its ->
                            its.readBytes()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Resizes and compresses a source image Uri, saves it locally to files directory,
     * and returns the local file path and base64 encoded string.
     */
    fun processAndCacheImage(context: Context, uri: Uri): Pair<String, String>? {
        return try {
            val bytes = readBytesFromUri(context, uri) ?: return null
            
            // Step 1: Decode dimensions first to avoid OOM
            var inputStream: InputStream = java.io.ByteArrayInputStream(bytes)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // Calculate scale down factor
            val maxDimension = 1024
            var scale = 1
            if (options.outWidth > maxDimension || options.outHeight > maxDimension) {
                val largest = maxOf(options.outWidth, options.outHeight)
                scale = Math.round(largest.toFloat() / maxDimension.toFloat())
            }

            // Decode fully with sampling rate
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            inputStream = java.io.ByteArrayInputStream(bytes)
            val decodedBitmap = BitmapFactory.decodeStream(inputStream, null, finalOptions)
            inputStream.close()

            if (decodedBitmap == null) return null

            // Scale to exact bounds if needed
            val finalBitmap = if (decodedBitmap.width > maxDimension || decodedBitmap.height > maxDimension) {
                val ratio = decodedBitmap.width.toFloat() / decodedBitmap.height.toFloat()
                val targetWidth: Int
                val targetHeight: Int
                if (decodedBitmap.width > decodedBitmap.height) {
                    targetWidth = maxDimension
                    targetHeight = (maxDimension / ratio).toInt()
                } else {
                    targetHeight = maxDimension
                    targetWidth = (maxDimension * ratio).toInt()
                }
                Bitmap.createScaledBitmap(decodedBitmap, targetWidth, targetHeight, true)
            } else {
                decodedBitmap
            }

            // Save the resized bitmap to internal storage
            val fileName = "hd_src_${UUID.randomUUID()}.png"
            val file = File(context.filesDir, fileName)
            val fileOutputStream = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 95, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            // Convert to Base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            
            // Recycle bitmap
            if (finalBitmap != decodedBitmap) {
                finalBitmap.recycle()
            }
            decodedBitmap.recycle()

            Pair(file.absolutePath, "data:image/png;base64,$base64String")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
