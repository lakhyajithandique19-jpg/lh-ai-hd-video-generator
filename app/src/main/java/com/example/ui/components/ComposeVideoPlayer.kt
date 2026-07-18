package com.example.ui.components

import android.net.Uri
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Check
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun ComposeVideoPlayer(
    videoUrlOrPath: String,
    modifier: Modifier = Modifier,
    filterType: String = "None",
    textOverlay: String = "",
    textPosition: String = "Bottom",
    textColor: Color = Color.White,
    textSizeSp: Float = 20f,
    startTimeMs: Int = 0,
    endTimeMs: Int = 0,
    onDurationLoaded: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    var activePlayUrl by remember(videoUrlOrPath) { mutableStateOf(videoUrlOrPath) }
    var isLoading by remember(activePlayUrl) { mutableStateOf(true) }
    var hasError by remember(activePlayUrl) { mutableStateOf(false) }
    var errorMessage by remember(activePlayUrl) { mutableStateOf("") }
    var retryTrigger by remember { mutableStateOf(0) }
    var duration by remember(activePlayUrl) { mutableStateOf(0) }

    // Re-use a single ExoPlayer instance over videoUrlOrPath changes to avoid heavy instantiation
    val exoPlayer = remember {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000) // 15 seconds connection timeout
            .setReadTimeoutMs(15000)
            .setUserAgent("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
        
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
        
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = true
            }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Reactively update seek position when start time changes
    LaunchedEffect(exoPlayer, startTimeMs) {
        exoPlayer.seekTo(startTimeMs.toLong())
    }

    // Periodic check to enforce trim loop limits
    LaunchedEffect(exoPlayer, startTimeMs, endTimeMs) {
        while (true) {
            if (exoPlayer.isPlaying) {
                val current = exoPlayer.currentPosition.toInt()
                if (current < startTimeMs) {
                    exoPlayer.seekTo(startTimeMs.toLong())
                } else if (endTimeMs > startTimeMs && current >= endTimeMs) {
                    exoPlayer.seekTo(startTimeMs.toLong())
                }
            }
            kotlinx.coroutines.delay(100)
        }
    }

    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var localFallbackPath by remember(activePlayUrl) { mutableStateOf<String?>(null) }

    // Set up media, listeners and player preparation
    LaunchedEffect(exoPlayer, activePlayUrl, retryTrigger, localFallbackPath) {
        isLoading = true
        hasError = false
        errorMessage = ""

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        isLoading = false
                        hasError = false
                        val mediaDuration = exoPlayer.duration.toInt()
                        duration = mediaDuration
                        onDurationLoaded(mediaDuration)
                    }
                    Player.STATE_BUFFERING -> {
                        isLoading = true
                    }
                    Player.STATE_ENDED -> {
                        isLoading = false
                    }
                    Player.STATE_IDLE -> {
                        // Keep loading state consistent
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                error.printStackTrace()
                if (activePlayUrl.startsWith("http") && localFallbackPath == null) {
                    isLoading = true
                    hasError = false
                    errorMessage = "Optimizing playback (downloading video container)..."
                    scope.launch {
                        try {
                            val cachedFile = downloadVideoToCache(context, activePlayUrl)
                            if (cachedFile != null && cachedFile.exists()) {
                                localFallbackPath = cachedFile.absolutePath
                            } else {
                                hasError = true
                                errorMessage = error.localizedMessage ?: "Unable to stream online video."
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            hasError = true
                            errorMessage = e.localizedMessage ?: "Failed to download cache container."
                            isLoading = false
                        }
                    }
                } else {
                    hasError = true
                    errorMessage = error.localizedMessage ?: "Playback failed."
                    isLoading = false
                }
            }
        }
        exoPlayer.addListener(listener)

        try {
            val urlOrPathToPlay = localFallbackPath ?: activePlayUrl
            if (urlOrPathToPlay.isNotEmpty()) {
                val mediaItem = if (urlOrPathToPlay.startsWith("http")) {
                    MediaItem.fromUri(Uri.parse(urlOrPathToPlay))
                } else {
                    val localFile = java.io.File(urlOrPathToPlay)
                    if (localFile.exists()) {
                        MediaItem.fromUri(Uri.fromFile(localFile))
                    } else {
                        hasError = true
                        errorMessage = "Local file source not found."
                        isLoading = false
                        null
                    }
                }

                mediaItem?.let {
                    exoPlayer.setMediaItem(it)
                    exoPlayer.prepare()
                }
            } else {
                isLoading = false
            }
            
            // Keep coroutine active to preserve the listener until videoUrlOrPath or retry changes
            kotlinx.coroutines.awaitCancellation()
        } catch (e: Exception) {
            e.printStackTrace()
            hasError = true
            errorMessage = e.localizedMessage ?: "Failed to prepare video player."
            isLoading = false
        } finally {
            exoPlayer.removeListener(listener)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // 1. AndroidX Media3 PlayerView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    player = exoPlayer
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Cinematic Live Color Filters Layer
        if (filterType != "None") {
            val filterModifier = when (filterType) {
                "Vintage Sepia" -> Modifier.background(Color(0x3B8B4513)) // Translucent brown tone
                "Noir Black & White" -> Modifier.background(Color(0x55444444)) // Charcoal monochrome shading
                "Golden Hour Warm" -> Modifier.background(Color(0x40FF9100)) // Golden amber overlay
                "Cool Slate" -> Modifier.background(Color(0x3000E5FF)) // Cool cyan overlay
                "Glitch Sci-Fi" -> Modifier.background(Color(0x2500E676)) // Matrix green tint
                "Cyberpunk Neon" -> Modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x35FF007F), Color(0x357B00FF))
                    )
                ) // Pink & Purple cinematic gradient
                else -> Modifier
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(filterModifier)
            )
        }

        // 3. Real-time Text Overlay rendering Layer
        if (textOverlay.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val textAlignment = when (textPosition) {
                    "Top" -> Alignment.TopCenter
                    "Center" -> Alignment.Center
                    else -> Alignment.BottomCenter
                }
                
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(textAlignment)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = textOverlay,
                        color = textColor,
                        fontSize = textSizeSp.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 4. Loading indicator
        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                CircularProgressIndicator(color = Color(0xFFFFB300))
                if (errorMessage.isNotEmpty() && errorMessage.contains("Optimizing")) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 5. Custom Error & Retry overlay with Source Add & Alternate Switchers
        if (hasError) {
            var customUrlInput by remember { mutableStateOf("") }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Playback Error",
                        tint = Color(0xFFFF4D4D),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Unable to stream video (Source Error)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (errorMessage.isNotEmpty()) errorMessage else "Connection error or codec issue.",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Predefined high-availability online mirrors
                    Text(
                        text = "⚡ Select Highly Available CDN Mirror Source:",
                        color = Color(0xFFFFB300),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            Triple("Ocean HD", "https://vjs.zencdn.net/v/oceans.mp4", "🌊"),
                            Triple("Sintel HD", "https://media.w3.org/2010/05/sintel/trailer_hd.mp4", "🎬"),
                            Triple("Bunny SD", "https://www.w3schools.com/html/mov_bbb.mp4", "🐰")
                        ).forEach { (label, url, emoji) ->
                            Button(
                                onClick = {
                                    activePlayUrl = url
                                    hasError = false
                                    isLoading = true
                                    retryTrigger++
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activePlayUrl == url) Color(0xFFFFB300) else Color(0xFF1E2638),
                                    contentColor = if (activePlayUrl == url) Color(0xFF0C0F14) else Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(text = "$emoji $label", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Paste/Add custom source input field
                    Text(
                        text = "🔗 Add Custom Online Stream Source (.mp4 URL):",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = customUrlInput,
                            onValueChange = { customUrlInput = it },
                            placeholder = { Text("https://example.com/video.mp4", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f)) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF111622),
                                unfocusedContainerColor = Color(0xFF111622),
                                focusedBorderColor = Color(0xFFFFB300),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedLabelColor = Color(0xFFFFB300)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                        
                        Button(
                            onClick = {
                                if (customUrlInput.trim().isNotEmpty()) {
                                    activePlayUrl = customUrlInput.trim()
                                    hasError = false
                                    isLoading = true
                                    retryTrigger++
                                }
                            },
                            enabled = customUrlInput.trim().isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300),
                                disabledContainerColor = Color.White.copy(alpha = 0.1f),
                                contentColor = Color(0xFF0C0F14),
                                disabledContentColor = Color.White.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(46.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Apply", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Simple original retry stream action
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                hasError = false
                                isLoading = true
                                retryTrigger++
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(30.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Retry Original",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun downloadVideoToCache(context: android.content.Context, videoUrl: String): java.io.File? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
    try {
        val hash = videoUrl.hashCode().toString()
        val cacheFile = java.io.File(context.cacheDir, "cached_video_$hash.mp4")
        if (cacheFile.exists() && cacheFile.length() > 10240) {
            return@withContext cacheFile
        }
        
        val client = okhttp3.OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
        val request = okhttp3.Request.Builder().url(videoUrl).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val body = response.body
                if (body != null) {
                    val tempFile = java.io.File(context.cacheDir, "cached_video_$hash.tmp")
                    if (tempFile.exists()) tempFile.delete()
                    
                    body.byteStream().use { input ->
                        java.io.FileOutputStream(tempFile).use { output ->
                            val buffer = ByteArray(8 * 1024)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                            output.flush()
                        }
                    }
                    if (tempFile.exists() && tempFile.length() > 10240) {
                        if (cacheFile.exists()) cacheFile.delete()
                        tempFile.renameTo(cacheFile)
                        return@withContext cacheFile
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    null
}
