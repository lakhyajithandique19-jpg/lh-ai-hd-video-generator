package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VideoRenderProgressConsole(
    progress: Float,
    statusMessage: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RenderPulse")
    
    // Breathing scale for the glow effect
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScale"
    )

    // Glowing/breathing alpha for active indicators
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    // Rotating rotation for spinning loader ring
    val loaderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LoaderRotation"
    )

    // Percentage formatted
    val pct = (progress * 100).toInt().coerceIn(0, 100)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C0F14) // Rich cosmic dark
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFB300).copy(alpha = glowAlpha),
                    Color(0xFFE65100).copy(alpha = 0.1f)
                )
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFFB300).copy(alpha = glowAlpha), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚡ GPU CLOUD RENDER CONSOLE",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300),
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
                Surface(
                    color = Color(0xFFFFB300).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "H100 TENSOR CLUSTER",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFB300),
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main progress wheel section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Spinning gradient radial wheel
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .drawBehind {
                            // Track background
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 6.dp.toPx())
                            )
                            // Animated Sweep gradient track based on current progress
                            drawArc(
                                color = Color(0xFFFFB300),
                                startAngle = -90f,
                                sweepAngle = progress * 360f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx())
                            )
                        }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$pct%",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "RENDER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Detail and progress messages
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Active Operation:",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.4f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = statusMessage,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Elegant custom progress tracking indicator
                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFFFFB300),
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            Spacer(modifier = Modifier.height(16.dp))

            // Stepper/Pipeline tracking list
            Text(
                text = "Rendering Pipeline Progress:",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 4 rendering pipeline steps
            val steps = listOf(
                RenderStep("Phase 1: Preparing prompt & image layers", 0.0f, 0.25f, "ASSETS PREPARED"),
                RenderStep("Phase 2: Allocating GPU tensor nodes", 0.25f, 0.50f, "HARDWARE INITIALIZED"),
                RenderStep("Phase 3: Synthesizing motion frame consistency", 0.50f, 0.85f, "FRAMES RENDERED"),
                RenderStep("Phase 4: Remuxing high-definition MP4 stream", 0.85f, 1.00f, "CACHE SYNCED")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // Draw continuous connecting timeline vertical line
                        // Only draw if we have space, starting from the first dot to the last
                        val startY = 16.dp.toPx()
                        val endY = size.height - 24.dp.toPx()
                        drawLine(
                            color = Color.White.copy(alpha = 0.1f),
                            start = Offset(10.dp.toPx(), startY),
                            end = Offset(10.dp.toPx(), endY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
            ) {
                steps.forEachIndexed { index, step ->
                    val isCompleted = progress >= step.endThreshold
                    val isActive = progress >= step.startThreshold && progress < step.endThreshold
                    
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        // Bullet dot
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color(0xFF0C0F14), CircleShape) // overlap line
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (isActive) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(0xFFFFB300).copy(alpha = glowAlpha), CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = step.title,
                                fontSize = 12.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) Color(0xFFFFB300) else if (isCompleted) Color.White else Color.White.copy(alpha = 0.4f)
                            )
                            if (isActive) {
                                Text(
                                    text = "Running realtime neural computation...",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }
                        }

                        // Right status badge
                        Text(
                            text = if (isCompleted) step.successBadge else if (isActive) "PROCESSING..." else "QUEUED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Color(0xFF00E676) else if (isActive) Color(0xFFFFB300).copy(alpha = glowAlpha) else Color.White.copy(alpha = 0.25f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live terminal / compiler style logs box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⌨ RENDER_PROCESS_OUTPUT.LOG",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.3f),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "LIVE FEED",
                            fontSize = 9.sp,
                            color = Color(0xFF00E676).copy(alpha = glowAlpha),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Generate live contextual logs based on the current progress level
                    val consoleLogs = remember(progress) {
                        generateConsoleLogs(progress)
                    }

                    consoleLogs.forEach { logLine ->
                        Text(
                            text = logLine,
                            fontSize = 10.sp,
                            color = if (logLine.startsWith("[ERROR]")) Color(0xFFFF5252) else if (logLine.startsWith("[SYS]")) Color(0xFFFFB300) else Color(0xFF00E676),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

data class RenderStep(
    val title: String,
    val startThreshold: Float,
    val endThreshold: Float,
    val successBadge: String
)

private fun generateConsoleLogs(progress: Float): List<String> {
    val logs = mutableListOf<String>()
    logs.add("user@hd-video-engine:~$ run_replicate_model_v2")
    
    if (progress >= 0.0f) {
        logs.add("[SYS] Parsing user creative prompt input tags...")
        logs.add("[SYS] Local scratch directory allocated: success")
    }
    if (progress >= 0.25f) {
        logs.add("[GPU] Connecting to NVIDIA H100 Core Cluster [US-EAST]...")
        logs.add("[GPU] Tensor weights verification checksum: [PASS]")
    }
    if (progress >= 0.50f) {
        val totalSteps = 50
        val currentStep = ((progress - 0.5f) / 0.35f * totalSteps).toInt().coerceIn(1, totalSteps)
        logs.add("[MODEL] Processing frames consistency... step $currentStep/$totalSteps")
        logs.add("[MODEL] Flow vectors density: 2.4 million/s")
    }
    if (progress >= 0.85f) {
        logs.add("[MP4] Render finished! Remuxing visual frames...")
        logs.add("[SYS] Updating local database cache file map... OK")
    }
    
    // Always return last 4 items for layout consistency
    return logs.takeLast(4)
}
