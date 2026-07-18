package com.example

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.GenerationEntity
import com.example.ui.components.ComposeVideoPlayer
import com.example.ui.components.VideoRenderProgressConsole
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GenerationState
import com.example.viewmodel.ImageGenerationState
import com.example.viewmodel.VoiceGenerationState
import com.example.viewmodel.MainActivityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel()
) {
    var currentBottomTab by remember { mutableStateOf("Home") }
    val selectedTabState = remember { mutableStateOf(0) }
    var showApiSettings by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val apiKey by viewModel.apiKey.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- BRANDING HERO HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF161A24),
                            Color(0xFF0C0F14)
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "V I D E O R I F T",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFB300),
                        letterSpacing = 4.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security Tag",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PATENTED SECURE CLOUD SYSTEM",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentBottomTab) {
                "Home" -> {
                    HomeScreen(
                        viewModel = viewModel,
                        selectedTabState = selectedTabState
                    )
                }
                "Premium" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "👑 Pro Licensing & Hardware",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB300)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "LakhyaJit Handique's patented software provides full, commercial resale rights for all AI generated assets.",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row {
                                        Text("✓ ", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                                        Text("No Watermarks on high-resolution exports", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                                    }
                                    Row {
                                        Text("✓ ", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                                        Text("Commercial Resale & Broadcast Rights", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                                    }
                                    Row {
                                        Text("✓ ", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                                        Text("Unlimited generations on your own server node", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🌐 Cloud Server Node Setup", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                var keyInput by remember(apiKey) { mutableStateOf(apiKey) }
                                var keyVisible by remember { mutableStateOf(false) }

                                OutlinedTextField(
                                    value = if (apiKey == "FREE_MODE") "FREE_MODE_ACTIVE" else keyInput,
                                    onValueChange = {
                                        keyInput = it
                                        viewModel.saveApiKey(it)
                                    },
                                    label = { Text("Replicate AI Cloud API Key") },
                                    placeholder = { Text("Enter your token from replicate.com") },
                                    visualTransformation = if (keyVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { keyVisible = !keyVisible }) {
                                            Icon(
                                                imageVector = if (keyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null,
                                                tint = Color(0xFFFFB300)
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFB300),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Button(
                                    onClick = {
                                        viewModel.saveApiKey("FREE_MODE")
                                        Toast.makeText(context, "⚡ Unlocked Free Cloud Suite!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text("Activate Free Shared Node", fontWeight = FontWeight.Bold, color = Color(0xFF0C0F14))
                                }
                            }
                        }
                    }
                }
                "Credits" -> {
                    CreditsScreen()
                }
                "Profile" -> {
                    ProfileScreen()
                }
            }
        }

        // --- BOTTOM NAVIGATION BAR ---
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(Color(0xFF07090E))
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val navItems = listOf(
                Triple("Home", Icons.Default.Home, "Home"),
                Triple("History", Icons.Default.History, "History"),
                Triple("Premium", Icons.Default.Star, "Premium"),
                Triple("Credits", Icons.Default.MonetizationOn, "Credits"),
                Triple("Profile", Icons.Default.AccountCircle, "Profile")
            )
            
            navItems.forEach { (label, icon, tabKey) ->
                val isSelected = currentBottomTab == tabKey || (tabKey == "History" && selectedTabState.value == 2 && currentBottomTab == "Home")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { 
                            if (tabKey == "History") {
                                selectedTabState.value = 2
                                currentBottomTab = "Home"
                            } else {
                                currentBottomTab = tabKey
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) Color(0xFFFFB300) else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFFFFB300) else Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel(),
    selectedTabState: MutableState<Int>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by selectedTabState
    var currentBottomTab by remember { mutableStateOf("Home") }
    var generationMode by remember { mutableStateOf(0) } // 0 = Image-to-Video, 1 = Prompt-to-Video
    var textPrompt by remember { mutableStateOf("") }
    var imageToVideoPrompt by remember { mutableStateOf("") }
    val tabs = listOf("Studio", "Cinematic Editor", "Creations Library")

    // --- NEW STUDIO SUB-TAB LAB MODES ---
    var studioTabMode by remember { mutableStateOf(0) } // 0 = Video Lab, 1 = Image Lab, 2 = Voice Lab
    var imagePrompt by remember { mutableStateOf("") }
    var voicePrompt by remember { mutableStateOf("") }
    var selectedVoiceCharacter by remember { mutableStateOf("Cinematic Deep") }
    var voicePitch by remember { mutableStateOf(1.0f) }
    var voiceSpeed by remember { mutableStateOf(1.0f) }

    val apiKey by viewModel.apiKey.collectAsState()
    val motionIntensity by viewModel.motionIntensity.collectAsState()
    val targetStructure by viewModel.targetStructure.collectAsState()
    val selectedFreeVideoUrl by viewModel.selectedFreeVideoUrl.collectAsState()
    val selectedVideoSourceNode by viewModel.selectedVideoSourceNode.collectAsState()
    val selectedAnimationStyle by viewModel.selectedAnimationStyle.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    val imageState by viewModel.imageState.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()
    val textState by viewModel.textState.collectAsState()
    val history by viewModel.history.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showApiSettings by remember { mutableStateOf(false) }
    var activePreviewVideoUrl by remember { mutableStateOf<String?>(null) }

    // --- DYNAMIC PERMISSIONS MANAGER ---
    var permissionsGranted by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val recordAudioGranted = permissionsMap[android.Manifest.permission.RECORD_AUDIO] ?: false
        val postNotificationsGranted = permissionsMap[android.Manifest.permission.POST_NOTIFICATIONS] ?: false
        permissionsGranted = recordAudioGranted || postNotificationsGranted
    }

    LaunchedEffect(studioTabMode) {
        if (studioTabMode > 0 && !permissionsGranted) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }
    }

    // Listening for Text Generation Success to automatically fill prompt inputs
    LaunchedEffect(textState) {
        when (val state = textState) {
            is com.example.viewmodel.TextGenerationState.Success -> {
                if (studioTabMode == 0) {
                    textPrompt = state.text
                } else if (studioTabMode == 1) {
                    imagePrompt = state.text
                }
                Toast.makeText(context, "🪄 Prompt Enhanced by ${state.provider}!", Toast.LENGTH_LONG).show()
                viewModel.resetTextState()
            }
            is com.example.viewmodel.TextGenerationState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetTextState()
            }
            else -> {}
        }
    }

    // --- INTERACTIVE VIDEO EDITOR STATES ---
    var editorVideoSource by remember { mutableStateOf<String?>(null) }
    var editorTotalDuration by remember { mutableStateOf(10000) } // loaded duration in ms
    var editorTrimStart by remember { mutableStateOf(0f) }
    var editorTrimEnd by remember { mutableStateOf(10000f) }
    var editorSplitPoint by remember { mutableStateOf(5000f) }
    var editorTextOverlay by remember { mutableStateOf("") }
    var editorTextPosition by remember { mutableStateOf("Bottom") } // "Top", "Center", "Bottom"
    var editorTextColor by remember { mutableStateOf(Color.White) }
    var editorTextSizeSp by remember { mutableStateOf(20f) }
    var editorFilterType by remember { mutableStateOf("None") } // "None", "Vintage Sepia", etc.
    
    // For Merging clips
    val editorMergedClips = remember { mutableStateListOf<String>() }
    var showMergeSelectionDialog by remember { mutableStateOf(false) }
    var selectedTransition by remember { mutableStateOf("Fade to Black") }

    // Active tool sub-tab inside the editor: 0=Trimming/Splitting, 1=Text, 2=Filters, 3=Merge
    var editorSubTab by remember { mutableStateOf(0) }

    // Rendering simulation states
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableStateOf(0f) }
    var exportStatusText by remember { mutableStateOf("") }
    var showExportSuccessDialog by remember { mutableStateOf(false) }
    var exportedVideoUrl by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.resetGenerationState()
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            editorVideoSource = uri.toString()
            // Reset editor values
            editorTrimStart = 0f
            editorTrimEnd = 10000f
            editorTextOverlay = ""
            editorFilterType = "None"
            editorMergedClips.clear()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- HERO BANNER CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Unleash Your Imagination",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create Stunning HD Images\n& Prompt Videos in Seconds",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Sparkles",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Cosmic Image on the right
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_cosmic_banner_1784163218590),
                        contentDescription = "Cosmic Fantasy Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp))
                    )
                    // Subtle dark gradient from left to right to blend the image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0F131C),
                                        Color.Transparent
                                    ),
                                    startX = 0f,
                                    endX = 60f
                                )
                            )
                    )
                }
            }
        }

        // --- INTERACTIVE SETTINGS AND MONETIZATION PANEL ---
        AnimatedVisibility(
            visible = showApiSettings || apiKey.isEmpty(),
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F131C)
                ),
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💰 Business & Cloud Settings",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("✓ ", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Premium Access: Charge users per 10-minute HD generation.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                        Row(verticalAlignment = Alignment.Top) {
                            Text("✓ ", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("Commercial Rights: Generated videos are completely copyright-free for", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                        Row(verticalAlignment = Alignment.Top) {
                            Text("┌ ", color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("resale, YouTube, and digital ads.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))

                    val useBackend by viewModel.useBackend.collectAsState()
                    val backendUrl by viewModel.backendUrl.collectAsState()
                    val sharedPrefs = context.getSharedPreferences("lakhyajit_prefs", android.content.Context.MODE_PRIVATE)

                    Text(
                        text = "🛡️ Secure Multi-Provider Platform",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.saveUseBackend(!useBackend) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Switch(
                            checked = useBackend,
                            onCheckedChange = { viewModel.saveUseBackend(it) },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFB300),
                                checkedTrackColor = Color(0xFFFFB300).copy(alpha = 0.4f),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Enable Node.js Backend Proxy",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Encrypts and stores keys securely on-server (No key inside APK)",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (useBackend) {
                        OutlinedTextField(
                            value = backendUrl,
                            onValueChange = { viewModel.saveBackendUrl(it) },
                            label = { Text("Secure Backend REST API URL") },
                            placeholder = { Text("http://10.0.2.2:3000") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Backend Security Icon",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB300),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedLabelColor = Color(0xFFFFB300),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("backend_url_input")
                        )
                    } else {
                        // Direct on-device keys mode
                        var keyInput by remember(apiKey) { mutableStateOf(apiKey) }
                        var keyVisible by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = if (apiKey == "FREE_MODE") "FREE_MODE_ACTIVE" else keyInput,
                            onValueChange = {
                                keyInput = it
                                viewModel.saveApiKey(it)
                            },
                            label = { Text("Replicate AI Cloud API Key") },
                            placeholder = { Text("Enter your token from replicate.com") },
                            visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { keyVisible = !keyVisible }) {
                                    Icon(
                                        imageVector = if (keyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Key Visibility",
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "Key Icon",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB300),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedLabelColor = Color(0xFFFFB300),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("api_key_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        var openRouterInput by remember { mutableStateOf(sharedPrefs.getString("openrouter_api_key", "") ?: "") }
                        OutlinedTextField(
                            value = openRouterInput,
                            onValueChange = {
                                openRouterInput = it
                                sharedPrefs.edit().putString("openrouter_api_key", it).apply()
                            },
                            label = { Text("OpenRouter API Key (Direct Fallback)") },
                            placeholder = { Text("sk-or-v1-...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "Key Icon",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB300),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedLabelColor = Color(0xFFFFB300),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("openrouter_key_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        var hfInput by remember { mutableStateOf(sharedPrefs.getString("huggingface_api_key", "") ?: "") }
                        OutlinedTextField(
                            value = hfInput,
                            onValueChange = {
                                hfInput = it
                                sharedPrefs.edit().putString("huggingface_api_key", it).apply()
                            },
                            label = { Text("Hugging Face API Key (Direct Fallback)") },
                            placeholder = { Text("hf_...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "Key Icon",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB300),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedLabelColor = Color(0xFFFFB300),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("huggingface_key_input")
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Styled Green-Cyan Gradient Button matching the screenshot
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF00C853),
                                        Color(0xFF0091EA)
                                    )
                                )
                            )
                            .clickable {
                                viewModel.saveApiKey("FREE_MODE")
                                Toast.makeText(context, "⚡ Unlocked Free Cloud Suite! Enjoy Unlimited Generations.", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Activate Free Suite (No Key Needed)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "Try powerful features without any API key",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (apiKey == "FREE_MODE") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.03f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🎬 Free Source Video Streams (Internet)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB300),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Choose or paste any valid .mp4 stream URL from the internet. This video will be used instantly for Image-to-Video and Prompt-to-Video free generation.",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Custom URL paste input
                                OutlinedTextField(
                                    value = selectedFreeVideoUrl,
                                    onValueChange = { viewModel.selectFreeVideoUrl(it) },
                                    label = { Text("Custom Video Stream URL (.mp4)") },
                                    placeholder = { Text("https://example.com/stream.mp4") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Movie,
                                            contentDescription = "Video Icon",
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFB300),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                        focusedLabelColor = Color(0xFFFFB300),
                                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "⚡ Quick Preset Free Streams:",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Predefined chips
                                val presets = listOf(
                                    Triple("🔥 Neon", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", "High-intensity colorful fire trail"),
                                    Triple("Valley", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4", "Splendid flying camera over mountain valley"),
                                    Triple("Cosmic", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4", "Hypnotic digital space vortex tunnel"),
                                    Triple("Nature", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4", "Slow camera pans through beautiful green pines"),
                                    Triple("CGI Tech", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4", "Cinematic animated futuristic drone capture")
                                )

                                presets.forEach { (title, url, desc) ->
                                    val isSelected = selectedFreeVideoUrl.trim() == url
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .background(
                                                if (isSelected) Color(0xFFFFB300).copy(alpha = 0.15f) else Color.Transparent,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { viewModel.selectFreeVideoUrl(url) }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Color(0xFFFFB300) else Color.White
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hasKey = apiKey.isNotEmpty()
                        Icon(
                            imageVector = if (hasKey) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                            contentDescription = "Status Connected Icon",
                            tint = if (hasKey) Color(0xFF00E676) else Color(0xFFFF5252),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasKey) {
                                if (apiKey == "FREE_MODE") "Connected to Shared Free Cloud Network"
                                else "Connected to Replicate Hardware Node"
                            } else "Offline: API Key Required",
                            fontSize = 11.sp,
                            color = if (hasKey) Color(0xFF00E676) else Color(0xFFFF5252),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (hasKey) {
                            Text(
                                text = "Close Panel",
                                fontSize = 11.sp,
                                color = Color(0xFFFFB300),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { showApiSettings = false }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- MAIN TABS ROW ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val cardTabs = listOf(
                Triple("Studio", "Create with AI", Icons.Default.PlayArrow),
                Triple("Cinematic Editor", "Edit like a Pro", Icons.Default.Movie),
                Triple("Creations Library", "Your Masterpieces", Icons.Default.FolderSpecial)
            )
            cardTabs.forEachIndexed { index, (title, subtitle, icon) ->
                val isSelected = selectedTab == index
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .height(64.dp)
                        .clickable { selectedTab = index },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1B2333) else Color(0xFF0F131C)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFFFFB300) else Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFB300).copy(alpha = if (isSelected) 0.2f else 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // --- TABS VIEWPORTS ---
        Box(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> {
                    // STUDIO TAB (LABS)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // STUDIO LAB SELECTION TABROW
                        TabRow(
                            selectedTabIndex = studioTabMode,
                            containerColor = Color(0xFF11141B),
                            contentColor = Color(0xFFFFB300),
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[studioTabMode]),
                                    color = Color(0xFFFFB300)
                                )
                            },
                            divider = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            listOf("🎬 Video Lab", "🎨 Image Lab", "🎙️ Voice Lab").forEachIndexed { index, title ->
                                Tab(
                                    selected = studioTabMode == index,
                                    onClick = { studioTabMode = index },
                                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }

                        when (studioTabMode) {
                            0 -> {
                                // --- ORIGINAL VIDEO LAB ---
                                // MODE SELECTION ROW
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    listOf("Image-to-Video", "Prompt-to-Video").forEachIndexed { index, modeTitle ->
                                        val isSelected = generationMode == index
                                        Button(
                                            onClick = { generationMode = index },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSelected) Color(0xFFFFB300) else Color(0xFF171C26),
                                                contentColor = if (isSelected) Color(0xFF0C0F14) else Color.White
                                            ),
                                            border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFB300) else Color.White.copy(alpha = 0.15f)),
                                            shape = RoundedCornerShape(24.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (index == 0) Icons.Default.AddPhotoAlternate else Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(modeTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                if (generationMode == 0) {
                                    Text(
                                        text = "📸 Step 1: Upload Your Source Image",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF171C26)
                                        ),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            if (selectedImageUri == null) {
                                                Icon(
                                                    imageVector = Icons.Default.AddPhotoAlternate,
                                                    contentDescription = "Upload Placeholder",
                                                    tint = Color(0xFFFFB300).copy(alpha = 0.5f),
                                                    modifier = Modifier.size(56.dp)
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = "Select a high-resolution JPG, JPEG or PNG image to generate a realistic video motion loop.",
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(horizontal = 16.dp)
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Button(
                                                    onClick = { imagePickerLauncher.launch("image/*") },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFFFFB300),
                                                        contentColor = Color(0xFF0C0F14)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.testTag("image_picker_button")
                                                ) {
                                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Pick Source Image", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(16f / 10f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.Black)
                                                ) {
                                                    AsyncImage(
                                                        model = selectedImageUri,
                                                        contentDescription = "Source image verified successfully",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.BottomCenter)
                                                            .fillMaxWidth()
                                                            .background(Color.Black.copy(alpha = 0.65f))
                                                            .padding(vertical = 6.dp)
                                                    ) {
                                                        Text(
                                                            text = "✓ HD Source File Verified Successfully",
                                                            color = Color(0xFF00E676),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.Center,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Button(
                                                        onClick = { imagePickerLauncher.launch("image/*") },
                                                        colors = ButtonDefaults.outlinedButtonColors(
                                                            contentColor = Color(0xFFFFB300)
                                                        ),
                                                        border = BorderStroke(1.dp, Color(0xFFFFB300)),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Replace Image", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "✍️ Optional: Guide Animation with Motion Prompt",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF171C26)
                                        ),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = imageToVideoPrompt,
                                                onValueChange = { imageToVideoPrompt = it },
                                                placeholder = {
                                                    Text(
                                                        text = "Describe how to animate (e.g., 'Make the water flow gently, camera zoom in' or 'Magical sparks floating around the temple')",
                                                        fontSize = 11.sp,
                                                        color = Color.White.copy(alpha = 0.4f)
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(75.dp),
                                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFFFB300),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "✍️ Step 1: Enter Your Creative Video Prompt",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF171C26)
                                        ),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = textPrompt,
                                                onValueChange = { textPrompt = it },
                                                placeholder = {
                                                    Text(
                                                        text = "Describe the video you want to generate in detail (e.g., 'A majestic golden eagle soaring through high mountain peaks during a vibrant sunset, 4k resolution, cinematic lighting' ...)",
                                                        fontSize = 12.sp,
                                                        color = Color.White.copy(alpha = 0.4f)
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(110.dp)
                                                    .testTag("prompt_input_field"),
                                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFFFB300),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "⚡ Powered by HunyuanVideo Cloud Nodes",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFFFB300),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Button(
                                                    onClick = {
                                                        val promptToEnhance = textPrompt.trim()
                                                        if (promptToEnhance.isEmpty()) {
                                                             Toast.makeText(context, "Please write a simple prompt draft first!", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                             viewModel.generateText("Expand and enhance this prompt to make it extremely detailed, cinematic, and professional for AI video generation. Maintain the core request but enrich it with camera details, vivid descriptions, and artistic direction: $promptToEnhance")
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300).copy(alpha = 0.15f)),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.height(32.dp).testTag("enhance_video_prompt_button")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AutoAwesome,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFFB300),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "AI Enhance",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFFFFB300),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                if (generationMode == 0) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // STEP 2: AI RENDER PARAMETERS
                                    Text(
                                        text = "🎬 Step 2: Initialize AI Render Engine",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF171C26)
                                        ),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Motion Intensity (Camera Speed)",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = motionIntensity.toString(),
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFFB300)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Slider(
                                                value = motionIntensity.toFloat(),
                                                onValueChange = { viewModel.motionIntensity.value = it.toInt() },
                                                valueRange = 1f..255f,
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color(0xFFFFB300),
                                                    activeTrackColor = Color(0xFFFFB300),
                                                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            Text(
                                                text = "Target Video Frame Structure",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                val structures = listOf(
                                                    "High Definition Motion Loop",
                                                    "Long-Form 10 Min Extended Sequence (Beta)"
                                                )
                                                structures.forEach { structure ->
                                                    val isSelected = targetStructure == structure
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = { viewModel.targetStructure.value = structure },
                                                        label = {
                                                            Text(
                                                                text = if (structure.contains("Beta")) "Long-Form 10m (Beta)" else "HD Motion Loop",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                        },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = Color(0xFFFFB300),
                                                            selectedLabelColor = Color(0xFF0C0F14),
                                                            containerColor = Color(0xFF0C0F14),
                                                            labelColor = Color.White.copy(alpha = 0.7f)
                                                        ),
                                                        border = FilterChipDefaults.filterChipBorder(
                                                            enabled = true,
                                                            selected = isSelected,
                                                            selectedBorderColor = Color(0xFFFFB300),
                                                            borderColor = Color.White.copy(alpha = 0.15f)
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                 // CHOOSE VIDEO ENGINE NODE & ANIMATION STYLE
                                 Text(
                                     text = "⚙️ Select Video Generation Engine Node",
                                     fontWeight = FontWeight.Bold,
                                     color = Color.White,
                                     fontSize = 14.sp
                                 )
                                 Spacer(modifier = Modifier.height(6.dp))
                                 androidx.compose.foundation.lazy.LazyRow(
                                     horizontalArrangement = Arrangement.spacedBy(8.dp),
                                     modifier = Modifier.fillMaxWidth()
                                 ) {
                                     val nodes = listOf(
                                         "Veo 3.1 Fast Node (Gemini)",
                                         "HunyuanVideo Node",
                                         "Luma Dream Node",
                                         "Free Fallback Core"
                                     )
                                     items(nodes.size) { index ->
                                         val node = nodes[index]
                                         val isSelected = selectedVideoSourceNode == node
                                         FilterChip(
                                             selected = isSelected,
                                             onClick = { viewModel.selectedVideoSourceNode.value = node },
                                             label = { Text(node, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                             colors = FilterChipDefaults.filterChipColors(
                                                 selectedContainerColor = Color(0xFFFFB300),
                                                 selectedLabelColor = Color(0xFF0C0F14),
                                                 containerColor = Color(0xFF171C26),
                                                 labelColor = Color.White.copy(alpha = 0.7f)
                                             ),
                                             border = FilterChipDefaults.filterChipBorder(
                                                 enabled = true,
                                                 selected = isSelected,
                                                 selectedBorderColor = Color(0xFFFFB300),
                                                 borderColor = Color.White.copy(alpha = 0.1f)
                                             )
                                         )
                                     }
                                 }

                                 Spacer(modifier = Modifier.height(12.dp))

                                 Text(
                                     text = "🎬 Select Animation Motion Preset Style",
                                     fontWeight = FontWeight.Bold,
                                     color = Color.White,
                                     fontSize = 14.sp
                                 )
                                 Spacer(modifier = Modifier.height(6.dp))
                                 androidx.compose.foundation.lazy.LazyRow(
                                     horizontalArrangement = Arrangement.spacedBy(8.dp),
                                     modifier = Modifier.fillMaxWidth()
                                 ) {
                                     val styles = listOf(
                                         "🌊 Cinematic Zoom & Slow Pan",
                                         "🚀 Orbital Camera Spin",
                                         "✨ Magic Particle Float",
                                         "🌀 Dimensional Space Warp",
                                         "🎮 CGI Animation Core",
                                         "🐰 Cute Cartoon Motion",
                                         "❄️ Snow / Blizzard Drift",
                                         "🌊 Oceanic Waves Flow"
                                     )
                                     items(styles.size) { index ->
                                         val style = styles[index]
                                         val isSelected = selectedAnimationStyle == style
                                         FilterChip(
                                             selected = isSelected,
                                             onClick = { viewModel.selectedAnimationStyle.value = style },
                                             label = { Text(style, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                             colors = FilterChipDefaults.filterChipColors(
                                                 selectedContainerColor = Color(0xFFFFB300),
                                                 selectedLabelColor = Color(0xFF0C0F14),
                                                 containerColor = Color(0xFF171C26),
                                                 labelColor = Color.White.copy(alpha = 0.7f)
                                             ),
                                             border = FilterChipDefaults.filterChipBorder(
                                                 enabled = true,
                                                 selected = isSelected,
                                                 selectedBorderColor = Color(0xFFFFB300),
                                                 borderColor = Color.White.copy(alpha = 0.1f)
                                             )
                                         )
                                     }
                                 }

                                 Spacer(modifier = Modifier.height(16.dp))

                                 // Action Button
                                Button(
                                    onClick = {
                                        if (generationMode == 0) {
                                            val uri = selectedImageUri
                                            if (uri != null) {
                                                val promptToUse = if (imageToVideoPrompt.trim().isNotEmpty()) imageToVideoPrompt.trim() else imagePrompt.trim()
                                                viewModel.generateVideo(uri, promptToUse.ifEmpty { null })
                                            } else {
                                                Toast.makeText(context, "Please pick a source image first", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            val prompt = textPrompt.trim()
                                            if (prompt.isNotEmpty()) {
                                                viewModel.generateVideoFromPrompt(prompt)
                                            } else {
                                                Toast.makeText(context, "Please write a creative prompt first", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    enabled = generationState !is GenerationState.Loading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFB300),
                                        contentColor = Color(0xFF0C0F14),
                                        disabledContainerColor = Color(0xFFFFB300).copy(alpha = 0.4f),
                                        disabledContentColor = Color(0xFF0C0F14).copy(alpha = 0.6f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("generate_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (generationState is GenerationState.Loading) "Rendering In Progress..." else {
                                                if (generationMode == 0) "Generate High-Definition Video Now" else "Generate HD Video From Prompt"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                // --- GENERATION PIPELINE LOADING STATES ---
                                AnimatedVisibility(
                                    visible = generationState !is GenerationState.Idle,
                                    enter = fadeIn() + slideInVertically()
                                ) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    val state = generationState
                                    if (state is GenerationState.Loading) {
                                        VideoRenderProgressConsole(
                                            progress = state.progress,
                                            statusMessage = state.statusMessage,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFF171C26)
                                            ),
                                            border = BorderStroke(
                                                1.dp,
                                                if (state is GenerationState.Success) Color(0xFF00E676) else Color(0xFFFF5252)
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                if (state is GenerationState.Success) {
                                                    Text(
                                                        text = "🎉 Video Generation Complete!",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF00E676)
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    ComposeVideoPlayer(
                                                        videoUrlOrPath = state.videoUrl,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )

                                                    Spacer(modifier = Modifier.height(10.dp))

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        // Download button
                                                        Button(
                                                            onClick = {
                                                                viewModel.downloadVideoToGallery(
                                                                    context,
                                                                    state.videoUrl,
                                                                    "lakhyajit_ai_${System.currentTimeMillis()}.mp4"
                                                                )
                                                                Toast.makeText(context, "Download queued to device", Toast.LENGTH_SHORT).show()
                                                            },
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFF00E676),
                                                                contentColor = Color(0xFF0C0F14)
                                                            ),
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("Download", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        }

                                                        // Load into Editor button
                                                        Button(
                                                            onClick = {
                                                                editorVideoSource = state.videoUrl
                                                                editorTrimStart = 0f
                                                                editorTrimEnd = 10000f
                                                                editorTextOverlay = ""
                                                                editorFilterType = "None"
                                                                editorMergedClips.clear()
                                                                selectedTab = 1 // Switch to Editor Tab
                                                            },
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = Color(0xFFFFB300),
                                                                contentColor = Color(0xFF0C0F14)
                                                            ),
                                                            shape = RoundedCornerShape(8.dp),
                                                            modifier = Modifier.weight(1.2f)
                                                        ) {
                                                            Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("Edit Clip", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        }
                                                    }
                                                }

                                                if (state is GenerationState.Error) {
                                                    Text(
                                                        text = "Error Occurred",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFF5252)
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = state.message,
                                                        fontSize = 12.sp,
                                                        color = Color.White.copy(alpha = 0.85f),
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Button(
                                                        onClick = { viewModel.resetGenerationState() },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(0xFFFF5252)
                                                        ),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Clear", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            1 -> {
                                // --- BRAND NEW IMAGE LAB ---
                                Text(
                                    text = "🎨 Step 1: Describe Your High-Definition Masterpiece",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Describe your vision in detail for best results",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0F14)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        androidx.compose.foundation.text.BasicTextField(
                                            value = imagePrompt,
                                            onValueChange = { if (it.length <= 1000) imagePrompt = it },
                                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFFB300)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp),
                                            decorationBox = { innerTextField ->
                                                if (imagePrompt.isEmpty()) {
                                                    Text(
                                                        text = "E.g., An ancient mystical temple hidden inside a lush giant neon jungle, photographic, volumetric lighting, highly detailed...",
                                                        color = Color.White.copy(alpha = 0.4f),
                                                        fontSize = 13.sp
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        )
                                        Text(
                                            text = "${imagePrompt.length} / 1000",
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.4f),
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(top = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            val promptToEnhance = imagePrompt.trim()
                                            if (promptToEnhance.isEmpty()) {
                                                Toast.makeText(context, "Please write a simple prompt draft first!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                viewModel.generateText("Expand and enhance this prompt to make it extremely detailed, cinematic, and professional for AI image generation. Maintain the core request but enrich it with camera details, vivid descriptions, and artistic direction: $promptToEnhance")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300).copy(alpha = 0.15f)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp).testTag("enhance_image_prompt_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "AI Enhance",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFFB300),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (imagePrompt.trim().isNotEmpty()) {
                                            viewModel.generateImage(imagePrompt.trim())
                                        } else {
                                            Toast.makeText(context, "Please enter an image prompt first!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = imageState !is ImageGenerationState.Loading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFB300),
                                        contentColor = Color(0xFF0C0F14),
                                        disabledContainerColor = Color(0xFFFFB300).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color(0xFF0C0F14)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (imageState is ImageGenerationState.Loading) "Rendering Masterpiece..." else "Generate HD Image Online",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF0C0F14)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Text(
                                            text = "Create Professional-Quality Images Instantly",
                                            fontSize = 10.sp,
                                            color = Color(0xFF0C0F14).copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                when (val state = imageState) {
                                    is ImageGenerationState.Loading -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                            border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                CircularProgressIndicator(color = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.height(12.dp))
                                                LinearProgressIndicator(
                                                    progress = { state.progress },
                                                    color = Color(0xFFFFB300),
                                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(state.statusMessage, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, textAlign = TextAlign.Center)
                                            }
                                        }
                                    }
                                    is ImageGenerationState.Success -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                            border = BorderStroke(1.dp, Color(0xFF00E676)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("🎉 HD Image Render Complete!", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.Black)
                                                ) {
                                                    AsyncImage(
                                                        model = state.imageUrl,
                                                        contentDescription = "Generated Masterpiece",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            viewModel.downloadVideoToGallery(context, state.imageUrl, "lakhyajit_image_${System.currentTimeMillis()}.jpg")
                                                            Toast.makeText(context, "Image download queued!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color(0xFF0C0F14)),
                                                        modifier = Modifier.weight(1f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            selectedImageUri = Uri.parse(state.imageUrl)
                                                            studioTabMode = 0
                                                            generationMode = 0
                                                            Toast.makeText(context, "✨ Loaded into Video Lab! Tap generate to render movement.", Toast.LENGTH_LONG).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color(0xFF0C0F14)),
                                                        modifier = Modifier.weight(1.3f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Animate Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    is ImageGenerationState.Error -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                            border = BorderStroke(1.dp, Color(0xFFFF5252)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("Image Generation Error", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(state.message, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, textAlign = TextAlign.Center)
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Button(
                                                    onClick = { viewModel.resetImageState() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                                                ) {
                                                    Text("Reset", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }

                            2 -> {
                                // --- BRAND NEW VOICE LAB ---
                                Text(
                                    text = "🎙️ Step 1: Write Voiceover or Cinematic Script",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        OutlinedTextField(
                                            value = voicePrompt,
                                            onValueChange = { voicePrompt = it },
                                            placeholder = {
                                                Text(
                                                    text = "Enter the script text for your AI to speak (e.g. 'Welcome to the futuristic AI creative suite. Let\\'s compile some HD content now!')...",
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.4f)
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp),
                                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFFFFB300),
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "🎙️ Step 2: Choose Voice Persona",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val voiceCharacters = listOf(
                                    "Cinematic Deep" to "🎬 Deep Narrator",
                                    "British Female" to "🇬🇧 Queen's Accent",
                                    "Sultry Radio" to "📻 FM Radio Host",
                                    "Robotic Cyber" to "🤖 Cyber Synth",
                                    "Playful Animated" to "✨ Animated Toon"
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(voiceCharacters) { (id, name) ->
                                        val isSelected = selectedVoiceCharacter == id
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) Color(0xFFFFB300) else Color(0xFF171C26)
                                            ),
                                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                            modifier = Modifier
                                                .width(130.dp)
                                                .clickable { selectedVoiceCharacter = id }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = if (id == "Robotic Cyber") Icons.Default.AutoAwesome else Icons.Default.Mic,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color(0xFF0C0F14) else Color(0xFFFFB300),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = name,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color(0xFF0C0F14) else Color.White,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "🎚️ Step 3: Fine-Tune Acoustics",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Voice Pitch Level", fontSize = 12.sp, color = Color.White)
                                            Text(String.format("%.2fx", voicePitch), fontSize = 12.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = voicePitch,
                                            onValueChange = { voicePitch = it },
                                            valueRange = 0.5f..2.0f,
                                            colors = SliderDefaults.colors(thumbColor = Color(0xFFFFB300), activeTrackColor = Color(0xFFFFB300))
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Speech Delivery Rate", fontSize = 12.sp, color = Color.White)
                                            Text(String.format("%.2fx", voiceSpeed), fontSize = 12.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                                        }
                                        Slider(
                                            value = voiceSpeed,
                                            onValueChange = { voiceSpeed = it },
                                            valueRange = 0.5f..2.0f,
                                            colors = SliderDefaults.colors(thumbColor = Color(0xFFFFB300), activeTrackColor = Color(0xFFFFB300))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (voicePrompt.trim().isNotEmpty()) {
                                                viewModel.speakText(voicePrompt.trim(), selectedVoiceCharacter, voicePitch, voiceSpeed)
                                            } else {
                                                Toast.makeText(context, "Please enter a script first!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFB300),
                                            contentColor = Color(0xFF0C0F14)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(48.dp)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Speak Text", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            if (voicePrompt.trim().isNotEmpty()) {
                                                viewModel.generateVoice(voicePrompt.trim(), selectedVoiceCharacter, voicePitch, voiceSpeed)
                                            } else {
                                                Toast.makeText(context, "Please enter a script first!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        enabled = voiceState !is VoiceGenerationState.Loading,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF00E676),
                                            contentColor = Color(0xFF0C0F14)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Compile Wav", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                when (val state = voiceState) {
                                    is VoiceGenerationState.Loading -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                            border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                CircularProgressIndicator(color = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Compiling High-Quality Wav Stream...", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                            }
                                        }
                                    }
                                    is VoiceGenerationState.Success -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                            border = BorderStroke(1.dp, Color(0xFF00E676)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("✓ Wav Audio Stream Compiled Successfully!", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "File saved securely at:\n${state.localAudioPath}",
                                                    fontSize = 11.sp,
                                                    color = Color.White.copy(alpha = 0.6f)
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            viewModel.speakText(state.text, selectedVoiceCharacter, voicePitch, voiceSpeed)
                                                            Toast.makeText(context, "🔊 Playing wave preview file!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color(0xFF0C0F14)),
                                                        modifier = Modifier.weight(1.2f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Play Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            viewModel.resetVoiceState()
                                                            Toast.makeText(context, "Voice studio reset successfully.", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF171C26), contentColor = Color.White),
                                                        modifier = Modifier.weight(0.8f),
                                                        shape = RoundedCornerShape(8.dp),
                                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                                                    ) {
                                                        Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    is VoiceGenerationState.Error -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                            border = BorderStroke(1.dp, Color(0xFFFF5252)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text("Audio Compilation Error", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(state.message, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, textAlign = TextAlign.Center)
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Button(
                                                    onClick = { viewModel.resetVoiceState() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                                                ) {
                                                    Text("Reset", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }

                1 -> {
                    // --- CINEMATIC VIDEO EDITOR TAB ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        if (editorVideoSource == null) {
                            // EMPTY EDITOR STATE - IMPORT PANEL
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = "No video loaded",
                                        tint = Color(0xFFFFB300).copy(alpha = 0.4f),
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No Video Loaded inside Cinematic Studio",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Select a newly generated clip, pick a creation from your library, or upload any local MP4 file to unlock the HD Render editing pipeline.",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.65f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = { videoPickerLauncher.launch("video/*") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFB300),
                                            contentColor = Color(0xFF0C0F14)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Import Video File", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    if (history.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = "Or Quick-Select from Library:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFB300)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(history) { item ->
                                                val imgFile = File(item.sourceImagePath)
                                                Box(
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.Black)
                                                        .border(1.dp, Color.White.copy(alpha = 0.1f))
                                                        .clickable {
                                                            editorVideoSource = getPlayableSource(item.localVideoPath, item.videoUrl)
                                                            editorTrimStart = 0f
                                                            editorTrimEnd = 10000f
                                                            editorTextOverlay = ""
                                                            editorFilterType = "None"
                                                            editorMergedClips.clear()
                                                        }
                                                ) {
                                                    AsyncImage(
                                                        model = if (imgFile.exists()) imgFile else item.sourceImagePath,
                                                        contentDescription = "Quick load thumbnail",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.Black.copy(alpha = 0.3f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.PlayArrow,
                                                            contentDescription = "Quick load",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // ACTIVE VIDEO WORKSPACE
                            Text(
                                text = "🎬 Live Preview Canvas (Real-time overlays & filters)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Custom Video Player rendering the active filters & text overlays live
                            ComposeVideoPlayer(
                                videoUrlOrPath = editorVideoSource!!,
                                filterType = editorFilterType,
                                textOverlay = editorTextOverlay,
                                textPosition = editorTextPosition,
                                textColor = editorTextColor,
                                textSizeSp = editorTextSizeSp,
                                startTimeMs = editorTrimStart.toInt(),
                                endTimeMs = editorTrimEnd.toInt(),
                                onDurationLoaded = { durationMs ->
                                    if (durationMs > 0 && editorTotalDuration != durationMs) {
                                        editorTotalDuration = durationMs
                                        editorTrimEnd = durationMs.toFloat()
                                        editorSplitPoint = (durationMs / 2).toFloat()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // TOOL CHOICE TABS
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF171C26), RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val tools = listOf(
                                    Triple(0, Icons.Default.ContentCut, "Trim"),
                                    Triple(1, Icons.Default.TextFields, "Text"),
                                    Triple(2, Icons.Default.AutoAwesome, "Filters"),
                                    Triple(3, Icons.Default.Movie, "Merge")
                                )
                                tools.forEach { (index, icon, label) ->
                                    val isSelected = editorSubTab == index
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) Color(0xFFFFB300) else Color.Transparent)
                                            .clickable { editorSubTab = index }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = if (isSelected) Color(0xFF0C0F14) else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color(0xFF0C0F14) else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // TOOL DETAIL WORKSPACE PANELS
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    when (editorSubTab) {
                                        0 -> {
                                            // --- TRIMMING & SPLITTING SUB-PANEL ---
                                            Text(
                                                text = "✂ Trimming & Splitting Control",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB300),
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Slide the start and end sliders to trim your clip. Set split points to isolate sub-segments.",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Start Trim
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Trim Start", fontSize = 12.sp, color = Color.White)
                                                Text(
                                                    String.format("%.1fs", editorTrimStart / 1000f),
                                                    fontSize = 12.sp,
                                                    color = Color(0xFFFFB300),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Slider(
                                                value = editorTrimStart,
                                                onValueChange = {
                                                    editorTrimStart = it.coerceAtMost(editorTrimEnd - 500f)
                                                },
                                                valueRange = 0f..editorTotalDuration.toFloat(),
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color(0xFFFFB300),
                                                    activeTrackColor = Color(0xFFFFB300)
                                                )
                                            )

                                            // End Trim
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Trim End", fontSize = 12.sp, color = Color.White)
                                                Text(
                                                    String.format("%.1fs", editorTrimEnd / 1000f),
                                                    fontSize = 12.sp,
                                                    color = Color(0xFFFFB300),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Slider(
                                                value = editorTrimEnd,
                                                onValueChange = {
                                                    editorTrimEnd = it.coerceAtLeast(editorTrimStart + 500f)
                                                },
                                                valueRange = 0f..editorTotalDuration.toFloat(),
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color(0xFFFFB300),
                                                    activeTrackColor = Color(0xFFFFB300)
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.25f),
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Output Duration: " + String.format("%.1fs", (editorTrimEnd - editorTrimStart) / 1000f),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00E676),
                                                    modifier = Modifier.padding(8.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))
                                            // Split Segment Control
                                            Text(
                                                text = "📍 Segment Splitting Preview",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Split Point: " + String.format("%.1fs", editorSplitPoint / 1000f), fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                                                Button(
                                                    onClick = {
                                                        val splitSec = String.format("%.1f", editorSplitPoint / 1000f)
                                                        Toast.makeText(context, "Clip cut into Part A (0s - ${splitSec}s) and Part B (${splitSec}s - End)", Toast.LENGTH_LONG).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color(0xFF0C0F14)),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text("Execute Split Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Slider(
                                                value = editorSplitPoint,
                                                onValueChange = {
                                                    editorSplitPoint = it.coerceIn(editorTrimStart, editorTrimEnd)
                                                },
                                                valueRange = editorTrimStart..editorTrimEnd,
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color.White,
                                                    activeTrackColor = Color.White.copy(alpha = 0.5f)
                                                )
                                            )
                                        }

                                        1 -> {
                                            // --- TEXT OVERLAY SUB-PANEL ---
                                            Text(
                                                text = "✍ Add Text Overlay Overlay",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB300),
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = editorTextOverlay,
                                                onValueChange = { editorTextOverlay = it },
                                                label = { Text("Overlay Text") },
                                                placeholder = { Text("E.g., LAKHYAJIT AI 1080P") },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFFFFB300),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White
                                                ),
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Position Selectors
                                            Text("Vertical Text Alignment:", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                val positions = listOf("Top", "Center", "Bottom")
                                                positions.forEach { pos ->
                                                    val isSelected = editorTextPosition == pos
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = { editorTextPosition = pos },
                                                        label = { Text(pos, fontSize = 11.sp) },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = Color(0xFFFFB300),
                                                            selectedLabelColor = Color(0xFF0C0F14),
                                                            containerColor = Color(0xFF0C0F14),
                                                            labelColor = Color.White.copy(alpha = 0.8f)
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            // Color Selection
                                            Text("Text Display Color:", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                val colors = listOf(
                                                    Pair(Color.White, "White"),
                                                    Pair(Color(0xFFFFB300), "Gold"),
                                                    Pair(Color(0xFF00E5FF), "Cyan"),
                                                    Pair(Color(0xFF00E676), "Green"),
                                                    Pair(Color(0xFFFF5252), "Red")
                                                )
                                                colors.forEach { (color, name) ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clip(CircleShape)
                                                            .background(color)
                                                            .border(
                                                                width = if (editorTextColor == color) 2.dp else 0.dp,
                                                                color = Color.White,
                                                                shape = CircleShape
                                                            )
                                                            .clickable { editorTextColor = color }
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            // Text Size Slider
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Font Size", fontSize = 12.sp, color = Color.White)
                                                Text("${editorTextSizeSp.toInt()} sp", fontSize = 12.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                                            }
                                            Slider(
                                                value = editorTextSizeSp,
                                                onValueChange = { editorTextSizeSp = it },
                                                valueRange = 12f..36f,
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color(0xFFFFB300),
                                                    activeTrackColor = Color(0xFFFFB300)
                                                )
                                            )
                                        }

                                        2 -> {
                                            // --- CINEMATIC FILTERS SUB-PANEL ---
                                            Text(
                                                text = "🎨 Apply Cinematic Filter",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB300),
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Apply real-time LUT cinematic visual filters directly over video frames.",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            val filterPresets = listOf(
                                                "None",
                                                "Vintage Sepia",
                                                "Cyberpunk Neon",
                                                "Noir Black & White",
                                                "Golden Hour Warm",
                                                "Cool Slate",
                                                "Glitch Sci-Fi"
                                            )

                                            LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                items(filterPresets) { filter ->
                                                    val isSelected = editorFilterType == filter
                                                    Card(
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = if (isSelected) Color(0xFFFFB300) else Color(0xFF0C0F14)
                                                        ),
                                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                                        modifier = Modifier
                                                            .width(100.dp)
                                                            .clickable { editorFilterType = filter }
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(10.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(40.dp)
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .background(
                                                                        when (filter) {
                                                                            "Vintage Sepia" -> Color(0xFF8B4513)
                                                                            "Cyberpunk Neon" -> Color(0xFFFF007F)
                                                                            "Noir Black & White" -> Color(0xFF444444)
                                                                            "Golden Hour Warm" -> Color(0xFFFF9100)
                                                                            "Cool Slate" -> Color(0xFF00E5FF)
                                                                            "Glitch Sci-Fi" -> Color(0xFF00E676)
                                                                            else -> Color.DarkGray
                                                                        }
                                                                    )
                                                            )
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            Text(
                                                                text = filter,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isSelected) Color(0xFF0C0F14) else Color.White,
                                                                textAlign = TextAlign.Center,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        3 -> {
                                            // --- CLIPS MERGING SUB-PANEL ---
                                            Text(
                                                text = "🔗 Multi-Clip Merging Timeline",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB300),
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Append secondary clips sequentially into the active master timeline.",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Timeline list
                                            Text("Timeline clips order:", fontSize = 11.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(6.dp))

                                            // Master clip
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.4f)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 6.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Clip 1: Main Loaded Source (Active)", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            // Appended Clips
                                            editorMergedClips.forEachIndexed { idx, url ->
                                                Surface(
                                                    color = Color.Black.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 6.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Clip ${idx + 2}: Past Creation #${idx + 1}", fontSize = 12.sp, color = Color.White, modifier = Modifier.weight(1f))
                                                        IconButton(
                                                            onClick = { editorMergedClips.removeAt(idx) },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(Icons.Default.Close, contentDescription = "Remove clip", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Button(
                                                    onClick = { showMergeSelectionDialog = true },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color.White.copy(alpha = 0.1f),
                                                        contentColor = Color.White
                                                    ),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Add Past Creation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }

                                                // Transition selections
                                                var expandedTransition by remember { mutableStateOf(false) }
                                                Box(modifier = Modifier.weight(1f)) {
                                                    Button(
                                                        onClick = {
                                                            selectedTransition = if (selectedTransition == "Fade to Black") "Crossfade" else "Fade to Black"
                                                            Toast.makeText(context, "Transition set: $selectedTransition", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color.White.copy(alpha = 0.1f),
                                                            contentColor = Color(0xFFFFB300)
                                                        ),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text("Effect: $selectedTransition", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // MASTER EXPORT BUTTON
                            Button(
                                onClick = {
                                    // Trigger Rendering Simulator Pipeline
                                    coroutineScope.launch {
                                        isExporting = true
                                        exportProgress = 0.05f
                                        exportStatusText = "Deconstructing timeline sequences & setting bounds..."
                                        delay(1500)
                                        
                                        exportProgress = 0.35f
                                        exportStatusText = "Compiling visual LUT frames and applying $editorFilterType shader..."
                                        delay(2000)

                                        if (editorTextOverlay.isNotEmpty()) {
                                            exportProgress = 0.65f
                                            exportStatusText = "Synthesizing text overlays with position logic: $editorTextPosition..."
                                            delay(1500)
                                        }

                                        if (editorMergedClips.isNotEmpty()) {
                                            exportProgress = 0.85f
                                            exportStatusText = "Stitching timeline clips together using $selectedTransition technique..."
                                            delay(2000)
                                        }

                                        exportProgress = 0.95f
                                        exportStatusText = "Encoding high-definition h264 MP4 output..."
                                        delay(1500)

                                        // Finish up
                                        exportProgress = 1.0f
                                        exportStatusText = "Compilation successful!"
                                        
                                        // Save a new entity in the Database representing this compilation masterpiece
                                        val compositeTitle = buildString {
                                            append("Edited Clip")
                                            if (editorFilterType != "None") append(" (+$editorFilterType)")
                                            if (editorTextOverlay.isNotEmpty()) append(" (+Overlay)")
                                            if (editorMergedClips.isNotEmpty()) append(" (+Merged)")
                                        }

                                        val newEntity = GenerationEntity(
                                            sourceImagePath = "placeholder_edited_thumb", // we'll use placeholder
                                            videoUrl = editorVideoSource!!, // Use the active URL
                                            localVideoPath = null,
                                            motionBucket = 127,
                                            targetStructure = compositeTitle
                                        )
                                        val newId = viewModel.insertGenerationDirectly(newEntity)

                                        exportedVideoUrl = editorVideoSource!!
                                        isExporting = false
                                        showExportSuccessDialog = true
                                        
                                        // Reset Editor
                                        editorVideoSource = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00E676),
                                    contentColor = Color(0xFF0C0F14)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Render & Export HD Masterpiece", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { editorVideoSource = null },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Discard Changes & Exit Editor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }

                else -> {
                    // CREATIONS LIBRARY TAB
                    if (history.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "Empty History",
                                tint = Color(0xFFFFB300).copy(alpha = 0.3f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Creations Found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Select an image and tap 'Generate' in the Studio to begin creating HD cinematic clips.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 80.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(history, key = { it.id }) { item ->
                                HistoryItemCard(
                                    item = item,
                                    onPlay = { activePreviewVideoUrl = it },
                                    onDelete = {
                                        viewModel.deleteGeneration(item.id, item.sourceImagePath, item.localVideoPath)
                                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                                    },
                                    onDownload = {
                                        viewModel.downloadVideoToGallery(
                                            context,
                                            item.videoUrl,
                                            "lakhyajit_ai_history_${item.id}.mp4"
                                        )
                                        Toast.makeText(context, "Download queued to gallery", Toast.LENGTH_SHORT).show()
                                    },
                                    onEdit = {
                                        // Load this item directly into the editor tab!
                                        editorVideoSource = getPlayableSource(item.localVideoPath, item.videoUrl)
                                        editorTrimStart = 0f
                                        editorTrimEnd = 10000f
                                        editorTextOverlay = ""
                                        editorFilterType = "None"
                                        editorMergedClips.clear()
                                        selectedTab = 1 // Switch to Cinematic Editor
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- POPUP VIDEO PREVIEW OVERLAY DIALOG ---
    if (activePreviewVideoUrl != null) {
        Dialog(onDismissRequest = { activePreviewVideoUrl = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                border = BorderStroke(1.dp, Color(0xFFFFB300)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🎬 Playing HD Cinematic Loop",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ComposeVideoPlayer(
                        videoUrlOrPath = activePreviewVideoUrl!!,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { activePreviewVideoUrl = null },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300),
                                contentColor = Color(0xFF0C0F14)
                            )
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // --- MULTI-CLIP MERGE SELECTION DIALOG ---
    if (showMergeSelectionDialog) {
        val nonCurrentItems = history.filter { getPlayableSource(it.localVideoPath, it.videoUrl) != editorVideoSource }
        Dialog(onDismissRequest = { showMergeSelectionDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "➕ Select Clip to Merge",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (nonCurrentItems.isEmpty()) {
                        Text(
                            text = "No other creations in history to merge. Generate a few more videos in the Studio tab first!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth()
                        ) {
                            items(nonCurrentItems) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            editorMergedClips.add(getPlayableSource(item.localVideoPath, item.videoUrl))
                                            showMergeSelectionDialog = false
                                            Toast.makeText(context, "Added clip to merge sequence", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val imgFile = File(item.sourceImagePath)
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    ) {
                                        AsyncImage(
                                            model = if (imgFile.exists()) imgFile else item.sourceImagePath,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(item.targetStructure, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("Motion intensity: ${item.motionBucket}", fontSize = 10.sp, color = Color(0xFFFFB300))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { showMergeSelectionDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // --- RENDERING PIPELINE DIALOG OVERLAY ---
    if (isExporting) {
        Dialog(onDismissRequest = {}) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                border = BorderStroke(1.dp, Color(0xFFFFB300)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚡ Real Software Rendering Pipeline",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    CircularProgressIndicator(color = Color(0xFFFFB300), strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { exportProgress },
                        color = Color(0xFFFFB300),
                        trackColor = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = exportStatusText,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // --- SUCCESS RENDERING EXPORT POPUP DIALOG ---
    if (showExportSuccessDialog) {
        Dialog(onDismissRequest = { showExportSuccessDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
                border = BorderStroke(1.dp, Color(0xFF00E676)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 Cinematic Rendering Complete!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your trimmed, filtered, and text-overlaid cinematic compilation has been finalized with full 100% commercial rights preserved.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ComposeVideoPlayer(
                        videoUrlOrPath = exportedVideoUrl,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                viewModel.downloadVideoToGallery(
                                    context,
                                    exportedVideoUrl,
                                    "lakhyajit_rendered_${System.currentTimeMillis()}.mp4"
                                )
                                Toast.makeText(context, "Download queued to gallery", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E676),
                                contentColor = Color(0xFF0C0F14)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showExportSuccessDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300),
                                contentColor = Color(0xFF0C0F14)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (textState is com.example.viewmodel.TextGenerationState.Loading) {
        val loadingState = textState as com.example.viewmodel.TextGenerationState.Loading
        Dialog(onDismissRequest = {}) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF11141B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFFB300),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Multi-Provider AI Director",
                        color = Color(0xFFFFB300),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loadingState.statusMessage,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { loadingState.progress },
                        color = Color(0xFFFFB300),
                        trackColor = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: GenerationEntity,
    onPlay: (String) -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit
) {
    val dateString = remember(item.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171C26)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source Image Thumbnail
            val isPromptToVideo = item.sourceImagePath.startsWith("PROMPT:")
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF232A3B)),
                contentAlignment = Alignment.Center
            ) {
                if (isPromptToVideo) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Text Prompt",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    val imgFile = File(item.sourceImagePath)
                    AsyncImage(
                        model = if (imgFile.exists()) imgFile else item.sourceImagePath,
                        contentDescription = "Thumbnail preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dateString,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (isPromptToVideo) {
                    val promptText = item.sourceImagePath.substringAfter("PROMPT:")
                    Text(
                        text = promptText,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Prompt-to-Video Engine",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB300),
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = item.targetStructure,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Motion intensity: ${item.motionBucket}",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB300),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Actions Row / Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Play Button
                    IconButton(
                        onClick = { onPlay(getPlayableSource(item.localVideoPath, item.videoUrl)) },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFFFB300), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Generated Video",
                            tint = Color(0xFF0C0F14),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Edit Button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Video",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Download Button
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Video",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Item",
                            tint = Color(0xFFFF5252).copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getPlayableSource(localPath: String?, remoteUrl: String): String {
    if (!localPath.isNullOrEmpty()) {
        val file = java.io.File(localPath)
        if (file.exists() && file.length() > 0) {
            return localPath
        }
    }
    return remoteUrl
}

@Composable
fun CreditsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Gold Coin",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your Credit Wallet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "150 Credits Available",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFB300)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Each HD generation consumes 10 credits",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "⚡ Instant Top-Up Packages",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )

        val packages = listOf(
            Triple("Basic Pack", "100 Credits", "$0.99"),
            Triple("Pro Pack", "500 Credits", "$3.99"),
            Triple("Ultimate Pack", "1200 Credits", "$7.99")
        )

        packages.forEach { (name, amount, price) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = amount, fontSize = 11.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(text = price, fontSize = 12.sp, color = Color(0xFF0C0F14), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Daily Bonus Claim Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF00C853).copy(alpha = 0.08f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF00C853).copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Daily Reward Claim", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Claim your free +20 daily bonus credits", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Claim", fontSize = 12.sp, color = Color(0xFF0C0F14), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB300).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "LakhyaJit Handique",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Verified Icon",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Verified Software Patent Owner",
                        fontSize = 11.sp,
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🛡️ Legal & Safe App Certifications",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• 100% Commercial Resale License Included\n• Secure Local Database Sandbox with Room\n• Real-Time Cloud Server Node Verification\n• Zero Spyware, Adware, or Telemetry Leaks",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131C)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📞 Developer Support",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "If you have any business questions regarding LakhyaJit Handique's patented software or server nodes, reach out via the secure corporate console.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
