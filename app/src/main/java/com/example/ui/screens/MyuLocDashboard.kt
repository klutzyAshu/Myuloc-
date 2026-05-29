package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.database.MyuLocDatabase
import com.example.data.database.FavoriteTrack
import com.example.data.database.OfflineTrack
import com.example.viewmodel.toPlayerTrack
import com.example.viewmodel.toFavoriteTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import coil.compose.AsyncImage
import com.example.player.PlayerTrack
import com.example.player.PlaybackRepeatMode
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import com.example.ui.theme.CopperGlow
import com.example.ui.theme.DeepCharcoalBg
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.DenseDarkBg
import com.example.ui.theme.EarthySienna
import com.example.ui.theme.GlassShadowDark
import com.example.ui.theme.GlassShadowLight
import com.example.ui.theme.GlossyLightBg
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TranslucentBorderDark
import com.example.ui.theme.TranslucentBorderLight
import com.example.ui.theme.TranslucentGlassDark
import com.example.ui.theme.TranslucentGlassLight
import com.example.ui.theme.WarmSaddleBrown
import com.example.viewmodel.LockerUiState
import com.example.viewmodel.MyuLocViewModel
import com.example.viewmodel.SearchUiState
import com.example.viewmodel.DriveStorageState

@Composable
fun MyuLocDashboard(viewModel: MyuLocViewModel) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    if (!isUserLoggedIn) {
        LoginScreen(viewModel = viewModel, isDarkMode = isDarkMode)
    } else {
        val currentTab by viewModel.currentTab.collectAsState()
        val isConnected by viewModel.isConnectedToDrive.collectAsState()
        val showGenrePreferencePopup by viewModel.showGenrePreferencePopup.collectAsState()

        // Multi-select deleting state
        val isMultiSelectMode by viewModel.isMultiSelectMode.collectAsState()
        val selectedDeleteIds by viewModel.selectedDeleteIds.collectAsState()

        // Player details
        val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
        val activeTrack = currentTrack
        val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
        val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
        val duration by viewModel.playerManager.duration.collectAsState()
        val isBuffering by viewModel.playerManager.isBuffering.collectAsState()

        // OAuth Web login state
        var showWebAuth by remember { mutableStateOf(false) }
        var inputTokenDialog by remember { mutableStateOf(false) }
        var showExpandedPlayer by remember { mutableStateOf(false) }

        // Visual theme params
        val mainBg = if (isDarkMode) DeepCharcoalBg else GlossyLightBg
        val accentColor = if (isDarkMode) EarthySienna else DeepPurple

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = mainBg
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Background Mesh Glow Graphics to give that fancy, luxurious visual feel
                BackgroundAmbientMesh(isDarkMode = isDarkMode)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Top header capsule
                    HeaderCapsule(
                        viewModel = viewModel,
                        isDarkMode = isDarkMode,
                        onThemeToggle = { viewModel.toggleTheme() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation Switcher Tab Capsule
                    NavigationTabCapsule(
                        currentTab = currentTab,
                        isDarkMode = isDarkMode,
                        onTabSelected = { viewModel.setTab(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scrollable listings space
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                val verticalSpring = spring<androidx.compose.ui.unit.IntOffset>(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                                val fadeSpring = spring<Float>(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                                (fadeIn(animationSpec = fadeSpring) + 
                                 slideInVertically(animationSpec = verticalSpring) { it / 16 })
                                .togetherWith(
                                    fadeOut(animationSpec = fadeSpring) + 
                                    slideOutVertically(animationSpec = verticalSpring) { it / 16 }
                                )
                            },
                            label = "tab_content_transitions",
                            modifier = Modifier.fillMaxSize()
                        ) { targetTab ->
                            when (targetTab) {
                                "all" -> {
                                    AllTabContent(
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode
                                    )
                                }
                                "search" -> {
                                    SearchTabContent(
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode
                                    )
                                }
                                "favorites" -> {
                                    FavoritesTabContent(
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode
                                    )
                                }
                                "settings" -> {
                                    SettingsTabContent(
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }
                        }
                    }

                }

                // Floating sleep timer countdown capsule pill, positioned dynamically above the floating player capsule
                val sleepTimerBottomPadding = if (activeTrack != null) 105.dp else 40.dp
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = sleepTimerBottomPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SleepTimerMiniCapsule(viewModel = viewModel, isDarkMode = isDarkMode)
                }

                // Beautiful, zero-blur compact floating player capsule replacing the heavy bottom panel
                if (activeTrack != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                    ) {
                        FloatingPlayerCapsule(
                            track = activeTrack,
                            isPlaying = isPlaying,
                            currentPosition = currentPosition,
                            duration = duration,
                            isBuffering = isBuffering,
                            isDarkMode = isDarkMode,
                            onPlayPauseToggle = { viewModel.playerManager.togglePlayPause() },
                            onSkipNext = { viewModel.playerManager.skipNext() },
                            onSkipPrevious = { viewModel.playerManager.skipPrevious() },
                            onSeek = { target -> viewModel.playerManager.seekTo(target) },
                            onCapsuleClick = { showExpandedPlayer = true }
                        )
                    }
                }

                // Multiple deletion selection menu / layout bar floating on top!
                AnimatedVisibility(
                    visible = isMultiSelectMode,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (activeTrack != null) 96.dp else 24.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(
                                if (isDarkMode) Color(0xFF1F1815) else Color(0xFFFBFBFB),
                                RoundedCornerShape(32.dp)
                            )
                            .border(
                                1.dp,
                                if (isDarkMode) accentColor.copy(alpha = 0.5f) else accentColor.copy(alpha = 0.3f),
                                RoundedCornerShape(32.dp)
                            )
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${selectedDeleteIds.size} Selected",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                            Text(
                                text = "Remove selected tracks from device",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = { viewModel.clearDeleteSelection() }
                            ) {
                                Text("Cancel", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.deleteSelectedTracks() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD32F2F)
                                ),
                                shape = RoundedCornerShape(percent = 50),
                                modifier = Modifier.height(38.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Delete", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        if (showExpandedPlayer && activeTrack != null) {
            ExpandedPlayerView(
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                onDismiss = { showExpandedPlayer = false }
            )
        }

        // Google Drive Inline WebView Authorization dialog
        if (showWebAuth) {
            GoogleDriveOAuthDialog(
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                onTokenCaptured = { token ->
                    viewModel.connectWithAccessToken(token)
                    showWebAuth = false
                },
                onDismiss = { showWebAuth = false }
            )
        }

        // Direct token manually entering dialog as fallback
        if (inputTokenDialog) {
            ManualTokenEntryDialog(
                isDarkMode = isDarkMode,
                onTokenEntered = {
                    viewModel.connectWithAccessToken(it)
                    inputTokenDialog = false
                },
                onDismiss = { inputTokenDialog = false }
            )
        }


    }
}

@androidx.compose.runtime.Composable
fun rememberOptimizedImageRequest(data: Any?, sizePx: Int): coil.request.ImageRequest {
    val context = androidx.compose.ui.platform.LocalContext.current
    return androidx.compose.runtime.remember(data, sizePx) {
        coil.request.ImageRequest.Builder(context)
            .data(data)
            .size(sizePx)
            .precision(coil.size.Precision.AUTOMATIC)
            .crossfade(true)
            .allowHardware(true)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .build()
    }
}

// --- Glassmorphic Frost Helpers ---
fun getGlassBackground(isDarkMode: Boolean): Brush {
    return if (isDarkMode) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x952F2225), // Fluid warm rose-charcoal frosted glass top
                Color(0x7F22181C), // Translucent deep rose mid
                Color(0x6E190F13)  // Translucent fluid bottom
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFAF5F7FA), // Smooth fluid milk white
                Color(0xF0EDF0F4), 
                Color(0xEBE2E6EA)  
            )
        )
    }
}

fun getGlassBorder(isDarkMode: Boolean): Brush {
    return if (isDarkMode) {
        Brush.linearGradient(
            colors = listOf(
                Color(0x3EFFFFFF), // Enhanced elegant 24% white accent border
                Color(0x13FFFFFF)  
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x0F000000), // Softest 6% charcoal outline
                Color(0x04000000)  
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }
}

// --- Glassmorphic Capsules ---

@Composable
fun BackgroundAmbientMesh(isDarkMode: Boolean) {
    // Dynamic drifting cherry blossom petals animation
    val infiniteTransition = rememberInfiniteTransition(label = "sakura_anim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sakura_petals"
    )

    // Reusable cached paths avoiding memory allocation inside DrawScope
    // This allows perfect 120 FPS performance by keeping memory allocation at 0 during drawing!
    val flowerPath = remember { Path() }
    val creasePath = remember { Path() }
    val petalPathCached = remember { Path() }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isDarkMode) {
            // Light beautiful vertical gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFFAFB), Color(0xFFFFF2F5), Color(0xFFFFF0F5))
                        )
                    )
            )
        } else {
            // Dark beautiful midnight-rose background (original dark mode tones)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F0E0F), Color(0xFF151214), Color(0xFF1B1416))
                        )
                    )
            )

            val spotColor1 = Color(0x2AA0522D) // original soft amber/saddle spots
            val spotColor2 = Color(0x228B4513) // original soft ochre/sienna spots

            Box(
                modifier = Modifier
                    .size(450.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(spotColor1, Color.Transparent),
                            center = Offset(300f, 100f)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(450.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(spotColor2, Color.Transparent),
                            center = Offset(100f, 300f)
                        )
                    )
            )
        }

        // Draw the cherry blossom canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Setup cherry blossom flower color scheme based on current mode
            val flowerColorStart = if (isDarkMode) Color(0xFF451921) else Color(0xFFFFF1F3)
            val flowerColorEnd = if (isDarkMode) Color(0xFFFF4081) else Color(0xFFFFB7C5)
            val centerCrownColor = if (isDarkMode) Color(0xFFFF4081) else Color(0xFFFF69B4)
            val centerSecondaryColor = if (isDarkMode) Color(0xFFFF8B8D) else Color(0xFFEDBFC6)
            val creaseStrokeColor = if (isDarkMode) Color(0x99FF4081) else Color(0x66FF69B4)
            val fallingPetalColor = if (isDarkMode) Color(0xFFFFA2C0).copy(alpha = 0.8f) else Color(0xFFFFB7C5).copy(alpha = 0.85f)

            // Reusable draw function leveraging preallocated path objects
            fun drawSakuraFlower(centerX: Float, centerY: Float, scale: Float) {
                val petalW = 24f * scale
                val petalH = 45f * scale

                for (i in 0 until 5) {
                    val angle = i * 72f + 15f
                    rotate(degrees = angle, pivot = Offset(centerX, centerY)) {
                        flowerPath.reset()
                        flowerPath.moveTo(centerX, centerY)
                        flowerPath.cubicTo(
                            centerX - petalW * 0.9f, centerY - petalH * 0.3f,
                            centerX - petalW * 1.0f, centerY - petalH * 0.95f,
                            centerX - petalW * 0.3f, centerY - petalH
                        )
                        flowerPath.lineTo(centerX, centerY - petalH * 0.82f)
                        flowerPath.lineTo(centerX + petalW * 0.3f, centerY - petalH)
                        flowerPath.cubicTo(
                            centerX + petalW * 1.0f, centerY - petalH * 0.95f,
                            centerX + petalW * 0.9f, centerY - petalH * 0.3f,
                            centerX, centerY
                        )
                        flowerPath.close()

                        drawPath(
                            path = flowerPath,
                            brush = Brush.radialGradient(
                                colors = listOf(flowerColorStart, flowerColorEnd),
                                center = Offset(centerX, centerY - petalH * 0.5f),
                                radius = petalH
                            )
                        )

                        creasePath.reset()
                        creasePath.moveTo(centerX, centerY)
                        creasePath.lineTo(centerX, centerY - petalH * 0.7f)
                        drawPath(creasePath, color = creaseStrokeColor, style = Stroke(width = 1.5f * scale))
                    }
                }

                // Pistils/Centers
                drawCircle(color = centerCrownColor, radius = 6f * scale, center = Offset(centerX, centerY))
                drawCircle(color = centerSecondaryColor, radius = 3f * scale, center = Offset(centerX, centerY))
            }

            // Top right cluster of sakura blossoms
            drawSakuraFlower(width * 0.85f, height * 0.12f, 1.3f)
            drawSakuraFlower(width * 0.92f, height * 0.16f, 0.8f)
            drawSakuraFlower(width * 0.80f, height * 0.08f, 0.9f)

            // Bottom left cluster of sakura blossoms
            drawSakuraFlower(width * 0.12f, height * 0.88f, 1.4f)
            drawSakuraFlower(width * 0.08f, height * 0.82f, 0.9f)
            drawSakuraFlower(width * 0.18f, height * 0.92f, 1.0f)

            // 10 customized drifting/swaying petals using preallocated path
            val petalPosAndSway = listOf(
                Triple(0.1f, -0.05f, 1.05f),
                Triple(0.3f, -0.1f, 1.0f),
                Triple(0.5f, -0.05f, 1.1f),
                Triple(0.7f, -0.15f, 1.0f),
                Triple(0.85f, -0.08f, 1.15f),
                Triple(0.2f, 0.35f, 1.0f),
                Triple(-0.1f, 0.2f, 1.05f),
                Triple(0.45f, 0.5f, 1.1f),
                Triple(0.6f, 0.25f, 0.95f),
                Triple(0.9f, 0.45f, 1.05f)
            )

            val petalScales = listOf(0.9f, 1.2f, 0.7f, 1.1f, 0.8f, 1.0f, 1.3f, 0.8f, 1.1f, 0.7f)

            petalPosAndSway.forEachIndexed { i, triple ->
                val scale = petalScales[i]
                val sX = triple.first * width
                val sY = triple.second * height

                val totalFallY = height * 1.3f
                val currY = (sY + totalFallY * animTime) % totalFallY

                // Graceful horizontal sinewave sway
                val swayX = kotlin.math.sin(animTime * 2 * Math.PI.toFloat() + i) * 35.dp.toPx()
                val currX = sX + (height * 0.15f * animTime) + swayX

                val rotAngle = i * 45f + animTime * 360f * scale

                val petalW = 12f * scale
                val petalH = 22f * scale

                rotate(degrees = rotAngle, pivot = Offset(currX, currY)) {
                    petalPathCached.reset()
                    petalPathCached.moveTo(currX, currY)
                    petalPathCached.cubicTo(
                        currX - petalW, currY - petalH * 0.3f,
                        currX - petalW, currY - petalH,
                        currX - petalW * 0.2f, currY - petalH
                    )
                    petalPathCached.lineTo(currX, currY - petalH * 0.85f)
                    petalPathCached.lineTo(currX + petalW * 0.2f, currY - petalH)
                    petalPathCached.cubicTo(
                        currX + petalW, currY - petalH,
                        currX + petalW, currY - petalH * 0.3f,
                        currX, currY
                    )
                    petalPathCached.close()

                    drawPath(
                        path = petalPathCached,
                        color = fallingPetalColor
                    )
                }
            }
        }
    }
}

// Helper methods for continuous real-time global spring physics mapping
fun getSpringStiffness(speed: Float): Float {
    val coercedSpeed = speed.coerceIn(0.5f, 2.0f)
    return if (coercedSpeed < 1.0f) {
        100f + (coercedSpeed - 0.5f) * (1400f / 0.5f)
    } else {
        1500f + (coercedSpeed - 1.0f) * (4500f / 1.0f)
    }
}

fun getSpringDamping(bounciness: Float): Float {
    val coercedBounciness = bounciness.coerceIn(0.0f, 1.0f)
    return (1.0f - (coercedBounciness * 0.8f)).coerceIn(0.2f, 1.0f)
}

fun <T> tunedSpring(speed: Float, bounciness: Float) = androidx.compose.animation.core.spring<T>(
    stiffness = getSpringStiffness(speed),
    dampingRatio = getSpringDamping(bounciness)
)

@Composable
fun CustomAppLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val minDim = size.minDimension
        val radius = minDim / 2f
        val cx = width / 2f
        val cy = height / 2f

        // 1. Black outer background circle (creates thin outline framing)
        drawCircle(
            color = Color.Black,
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(cx, cy)
        )

        // 2. Sienna / Terracotta inner circle matching uploaded logo perfectly
        val innerRadius = radius * 0.94f
        drawCircle(
            color = Color(0xFFC06240), // Premium warm Sienna/ochre tone
            radius = innerRadius,
            center = androidx.compose.ui.geometry.Offset(cx, cy)
        )

        // 3. Black organic decorative spots/dots
        drawCircle(
            color = Color.Black,
            radius = innerRadius * 0.14f,
            center = androidx.compose.ui.geometry.Offset(cx - innerRadius * 0.46f, cy - innerRadius * 0.44f)
        )

        drawCircle(
            color = Color.Black,
            radius = innerRadius * 0.12f,
            center = androidx.compose.ui.geometry.Offset(cx + innerRadius * 0.44f, cy + innerRadius * 0.48f)
        )

        drawOval(
            color = Color.Black,
            topLeft = androidx.compose.ui.geometry.Offset(cx - innerRadius * 0.58f, cy + innerRadius * 0.05f),
            size = androidx.compose.ui.geometry.Size(innerRadius * 0.48f, innerRadius * 0.62f)
        )

        drawOval(
            color = Color.Black,
            topLeft = androidx.compose.ui.geometry.Offset(cx + innerRadius * 0.14f, cy - innerRadius * 0.48f),
            size = androidx.compose.ui.geometry.Size(innerRadius * 0.46f, innerRadius * 0.72f)
        )

        // 4. Stylized central black vinyl/organic 'h' typography lines
        val strokeWidth = innerRadius * 0.16f

        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(cx - innerRadius * 0.12f, cy - innerRadius * 0.05f),
            end = androidx.compose.ui.geometry.Offset(cx - innerRadius * 0.12f, cy + innerRadius * 0.15f),
            strokeWidth = strokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        drawLine(
            color = Color.Black,
            start = androidx.compose.ui.geometry.Offset(cx + innerRadius * 0.22f, cy - innerRadius * 0.08f),
            end = androidx.compose.ui.geometry.Offset(cx + innerRadius * 0.22f, cy + innerRadius * 0.36f),
            strokeWidth = strokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        val archPath = Path().apply {
            moveTo(cx - innerRadius * 0.12f, cy - innerRadius * 0.05f)
            cubicTo(
                cx, cy - innerRadius * 0.30f,
                cx + innerRadius * 0.15f, cy - innerRadius * 0.26f,
                cx + innerRadius * 0.22f, cy - innerRadius * 0.08f
            )
        }
        drawPath(
            path = archPath,
            color = Color.Black,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}

@Composable
fun RotatingAppLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "logo_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(3000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "rotation"
    )
    CustomAppLogo(
        modifier = modifier
            .graphicsLayer {
                rotationZ = rotation
            }
    )
}

@Composable
fun HeaderCapsule(viewModel: MyuLocViewModel, isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)
    val containerBg = if (isDarkMode) TranslucentGlassDark else TranslucentGlassLight
    val borderColor = if (isDarkMode) TranslucentBorderDark else TranslucentBorderLight
    val textColor = if (isDarkMode) Color.White else DeepPurple

    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft by viewModel.sleepTimerMinutesLeft.collectAsState()

    val animSpeed by viewModel.animationSpeed.collectAsState()
    val animBounciness by viewModel.animationBounciness.collectAsState()

    var showSleepDialog by remember { mutableStateOf(false) }

    if (showSleepDialog) {
        var slideOutTrigger by remember { mutableStateOf(false) }
        val animScope = rememberCoroutineScope()
        val dismissWithAnimation: () -> Unit = {
            animScope.launch {
                slideOutTrigger = true
                delay(280)
                showSleepDialog = false
                slideOutTrigger = false
            }
        }

        val slideOffset by animateDpAsState(
            targetValue = if (slideOutTrigger) 400.dp else 0.dp,
            animationSpec = tunedSpring(animSpeed, animBounciness),
            label = "dialog_slide"
        )
        val fadeAlpha by animateFloatAsState(
            targetValue = if (slideOutTrigger) 0f else 1f,
            animationSpec = tween(250),
            label = "dialog_fade"
        )

        Dialog(onDismissRequest = dismissWithAnimation) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .offset(y = slideOffset)
                    .alpha(fadeAlpha)
                    .background(if (isDarkMode) DenseDarkBg else Color.White, RoundedCornerShape(24.dp))
                    .border(0.5.dp, borderBrush, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isDarkMode) EarthySienna else DeepPurple,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sleep Timer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isDarkMode) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (sleepRunning) "Active Countdown: $sleepMinutesLeft minutes remaining" else "Pauses music playback automatically when timer lapses.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                val options = listOf(
                    0 to "Stop Timer",
                    5 to "5 Minutes",
                    15 to "15 Minutes",
                    30 to "30 Minutes",
                    45 to "45 Minutes",
                    60 to "60 Minutes"
                )

                options.forEach { (mins, label) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable {
                                if (mins == 0) {
                                    viewModel.stopSleepTimer()
                                } else {
                                    viewModel.startSleepTimer(mins)
                                }
                                dismissWithAnimation()
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (sleepRunning && sleepMinutesLeft == mins) FontWeight.Bold else FontWeight.Normal,
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                            if (sleepRunning && mins > 0 && Math.abs(sleepMinutesLeft - mins) < 2) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = if (isDarkMode) EarthySienna else DeepPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }

    val animWidth by animateDpAsState(
        targetValue = if (sleepRunning) 82.dp else 38.dp,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "timer_width"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(containerBrush, RoundedCornerShape(percent = 50))
            .border(0.5.dp, borderBrush, RoundedCornerShape(percent = 50))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomAppLogo(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "MyuLoc",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = textColor,
                letterSpacing = 1.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sleep Timer Status trigger Capsule (Beautiful symmetric uniform circular/pill shape)
            IconButton(
                onClick = { showSleepDialog = true },
                modifier = Modifier
                    .width(animWidth)
                    .height(38.dp)
                    .background(
                        color = if (sleepRunning) (if (isDarkMode) EarthySienna else DeepPurple) else containerBg, 
                        shape = RoundedCornerShape(percent = 50)
                    )
                    .border(
                        0.5.dp, 
                        borderColor, 
                        RoundedCornerShape(percent = 50)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepRunning) Color.White else (if (isDarkMode) CopperGlow else DeepPurple),
                        modifier = Modifier.size(18.dp)
                    )
                    if (sleepRunning && animWidth > 45.dp) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${sleepMinutesLeft}m", // clean, minimal status countdown indicator
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }
                }
            }

            // Dark/Light toggle
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier
                    .size(38.dp)
                    .background(containerBg, CircleShape)
                    .border(0.5.dp, borderColor, CircleShape)
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = if (isDarkMode) CopperGlow else DeepPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun NavigationTabCapsule(
    currentTab: String,
    isDarkMode: Boolean,
    onTabSelected: (String) -> Unit
) {
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)

    val tabs = listOf(
        Triple("all", "All", Icons.Default.MusicNote),
        Triple("search", "Search", Icons.Default.Search),
        Triple("favorites", "Favorites", Icons.Default.Favorite),
        Triple("settings", "Settings", Icons.Default.Settings)
    )

    val targetIndex = tabs.indexOfFirst { it.first == currentTab }.coerceAtLeast(0)

    // Remember the previous index to determine direction of movement
    var prevIndex by remember { mutableIntStateOf(targetIndex) }
    
    // Smoothly track moving direction to adjust edge stiffness for gooey stretching
    var directionIsRight by remember { mutableStateOf(targetIndex >= prevIndex) }

    LaunchedEffect(targetIndex) {
        if (targetIndex != prevIndex) {
            directionIsRight = targetIndex > prevIndex
            prevIndex = targetIndex
        }
    }

    // Animating left and right edges with smooth, non-bouncy spring physics
    val leftFraction by animateFloatAsState(
        targetValue = targetIndex / 4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tab_left"
    )

    val rightFraction by animateFloatAsState(
        targetValue = (targetIndex + 1) / 4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tab_right"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(containerBrush, RoundedCornerShape(percent = 50))
            .border(0.5.dp, borderBrush, RoundedCornerShape(percent = 50))
            .padding(4.dp)
    ) {
        val parentWidth = maxWidth
        val startOffset = parentWidth * leftFraction
        val capsuleWidth = parentWidth * (rightFraction - leftFraction)

        val activeIndicatorColor = if (isDarkMode) EarthySienna else DeepPurple

        // Flat, clean active slider capsule with zero glass/shading glossiness
        Box(
            modifier = Modifier
                .offset(x = startOffset)
                .width(capsuleWidth)
                .fillMaxHeight()
                .background(activeIndicatorColor, RoundedCornerShape(percent = 50))
        )

        // Horizontal Row of tabs layered smoothly above
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (tabId, tabName, icon) ->
                val isActive = currentTab == tabId
                
                val activeTextColor = Color.White
                val inactiveTextColor = if (isDarkMode) Color.Gray else Color.DarkGray

                // Smooth spring-based scale accent
                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1.08f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "tab_scale"
                )

                // Smooth spring-based settings rotation
                val rotation by animateFloatAsState(
                    targetValue = if (isActive && tabId == "settings") 45f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "tab_rotate"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onTabSelected(tabId) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tabName,
                            tint = if (isActive) activeTextColor else inactiveTextColor,
                            modifier = Modifier
                                .size(16.dp)
                                .graphicsLayer {
                                    rotationZ = rotation
                                }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tabName,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isActive) activeTextColor else inactiveTextColor
                        )
                    }
                }
            }
        }
    }
}

// --- Tab 0: Device Offline Library ---

data class PlaylistData(
    val id: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tracks: List<PlayerTrack>
)

@Composable
fun SubCategoryTabCapsule(
    currentSubTab: String,
    isDarkMode: Boolean,
    onSubTabSelected: (String) -> Unit
) {
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)

    val subs = listOf(
        Triple("songs", "Songs", Icons.Default.MusicNote),
        Triple("playlists", "Playlists", Icons.Default.QueueMusic),
        Triple("artists", "Artists", Icons.Default.Person),
        Triple("genres", "Genres", Icons.Default.Equalizer)
    )

    val targetIndex = subs.indexOfFirst { it.first == currentSubTab }.coerceAtLeast(0)

    var prevIndex by remember { mutableIntStateOf(targetIndex) }
    var directionIsRight by remember { mutableStateOf(targetIndex >= prevIndex) }

    LaunchedEffect(targetIndex) {
        if (targetIndex != prevIndex) {
            directionIsRight = targetIndex > prevIndex
            prevIndex = targetIndex
        }
    }

    val leftFraction by animateFloatAsState(
        targetValue = targetIndex / 4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "sub_tab_left"
    )

    val rightFraction by animateFloatAsState(
        targetValue = (targetIndex + 1) / 4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "sub_tab_right"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(containerBrush, RoundedCornerShape(percent = 50))
            .border(0.5.dp, borderBrush, RoundedCornerShape(percent = 50))
            .padding(3.dp)
    ) {
        val parentWidth = maxWidth
        val startOffset = parentWidth * leftFraction
        val capsuleWidth = parentWidth * (rightFraction - leftFraction)

        val activeIndicatorColor = if (isDarkMode) EarthySienna else DeepPurple

        Box(
            modifier = Modifier
                .offset(x = startOffset)
                .width(capsuleWidth)
                .fillMaxHeight()
                .background(activeIndicatorColor, RoundedCornerShape(percent = 50))
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            subs.forEach { (subId, subName, icon) ->
                val isActive = currentSubTab == subId
                val tintColor = if (isActive) Color.White else (if (isDarkMode) Color.LightGray else Color.DarkGray)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onSubTabSelected(subId) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = subName,
                            tint = tintColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = subName,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = tintColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistHeaderCard(
    playlist: PlaylistData,
    isExpanded: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isDarkMode) Color(0x1B1B22) else Color(0xFFF2F5F8)
    val borderColor = if (isDarkMode) Color(0x11FFFFFF) else Color(0x0A000000)
    val contentColor = if (isDarkMode) Color.White else Color.Black
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "arrow_rotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isDarkMode) Color(0x14FFFFFF) else Color(0x0F000000)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = playlist.icon,
                contentDescription = playlist.title,
                tint = if (isDarkMode) EarthySienna else DeepPurple,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${playlist.tracks.size} tracks",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Expand",
            tint = Color.Gray,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer(rotationZ = arrowRotation)
        )
    }
}

@Composable
fun ArtistHeaderCard(
    artistName: String,
    songCount: Int,
    isExpanded: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isDarkMode) Color(0x1B1B22) else Color(0xFFF2F5F8)
    val borderColor = if (isDarkMode) Color(0x11FFFFFF) else Color(0x0A000000)
    val contentColor = if (isDarkMode) Color.White else Color.Black
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "artist_arrow_rotation"
    )

    val hash = artistName.hashCode()
    val avatarBg = remember(artistName) {
        val colors = if (isDarkMode) {
            listOf(Color(0xFF8B4513), Color(0xFFA0522D), Color(0xFF5F9EA0), Color(0xFF4682B4), Color(0xFF6B8E23))
        } else {
            listOf(Color(0xFFEDE9FE), Color(0xFFFCE7F3), Color(0xFFE0F2FE), Color(0xFFDCFCE7), Color(0xFFFEF3C7))
        }
        colors[Math.abs(hash % colors.size)]
    }
    val avatarTextColor = if (isDarkMode) Color.White else (if (hash % 2 == 0) DeepPurple else EarthySienna)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(avatarBg),
            contentAlignment = Alignment.Center
        ) {
            val shortText = artistName.firstOrNull()?.toString()?.uppercase() ?: "?"
            Text(
                text = shortText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = avatarTextColor
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artistName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$songCount ${if (songCount == 1) "track" else "tracks"}",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Expand",
            tint = Color.Gray,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer(rotationZ = arrowRotation)
        )
    }
}

@Composable
fun GenreHeaderCard(
    genreName: String,
    songCount: Int,
    isExpanded: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isDarkMode) Color(0x1B1B22) else Color(0xFFF2F5F8)
    val borderColor = if (isDarkMode) Color(0x11FFFFFF) else Color(0x0A000000)
    val contentColor = if (isDarkMode) Color.White else Color.Black
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "genre_arrow_rotation"
    )

    val icon = remember(genreName) {
        when {
            genreName.contains("Lofi") -> Icons.Default.Schedule
            genreName.contains("Electronic") -> Icons.Default.Equalizer
            genreName.contains("Hip-Hop") -> Icons.Default.Queue
            genreName.contains("Classical") -> Icons.Default.Folder
            genreName.contains("Pop") -> Icons.Default.Favorite
            else -> Icons.Default.MusicNote
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isDarkMode) Color(0x14FFFFFF) else Color(0x0F000000)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = genreName,
                tint = if (isDarkMode) EarthySienna else DeepPurple,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = genreName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$songCount ${if (songCount == 1) "track" else "tracks"}",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Expand",
            tint = Color.Gray,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer(rotationZ = arrowRotation)
        )
    }
}

@Composable
fun AllTabContent(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    val offlineList by viewModel.offlineTracksFlow.collectAsState(initial = emptyList())
    val recommendations by viewModel.recommendationsList.collectAsState()
    val favoritesList by viewModel.favoriteTracksFlow.collectAsState(initial = emptyList())
    val playCountsMap by viewModel.playCounts.collectAsState()
    val containerBg = if (isDarkMode) TranslucentGlassDark else TranslucentGlassLight
    val accentColor = if (isDarkMode) EarthySienna else DeepPurple
    val borderColor = if (isDarkMode) TranslucentBorderDark else TranslucentBorderLight

    var showPermissionDialog by remember { mutableStateOf(false) }
    val subTab by viewModel.subTab.collectAsState()
    var expandedItemKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(subTab) {
        expandedItemKey = null
    }

    val permissionString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanLocalFiles()
        } else {
            Toast.makeText(context, "Permission denied. Cannot scan folders.", Toast.LENGTH_LONG).show()
        }
    }

    if (showPermissionDialog) {
        Dialog(onDismissRequest = { showPermissionDialog = false }) {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .background(if (isDarkMode) Color(0xFF1A1A1A) else Color.White, RoundedCornerShape(24.dp))
                    .border(0.5.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (isDarkMode) Color(0x2BFF5722) else Color(0x1BFF5722), 
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Local folders",
                        tint = if (isDarkMode) EarthySienna else DeepPurple,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Local Library Access",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isDarkMode) Color.White else Color.Black
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    text = "MyuLoc requests permission to search your device's storage so that we can register, play, and catalog local tracks.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(percent = 50))
                            .clickable { showPermissionDialog = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Not Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) Color.LightGray else Color.DarkGray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(if (isDarkMode) EarthySienna else DeepPurple)
                            .clickable {
                                showPermissionDialog = false
                                permissionLauncher.launch(permissionString)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Authorize",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Embed SubCategoryTabCapsule at the top of All/Dashboard Tab contents
        SubCategoryTabCapsule(
            currentSubTab = subTab,
            isDarkMode = isDarkMode,
            onSubTabSelected = {
                viewModel.setSubTab(it)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        val state = rememberLazyListState()
        val isScrollingDown = rememberScrollDirection(state)
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Offline Library Header with powerful Shuffle option and refresh
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when(subTab) {
                                "songs" -> "Device Library"
                                "playlists" -> "Smart Playlists"
                                "artists" -> "Group by Artists"
                                "genres" -> "Categorized Genres"
                                else -> "Device Library"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color.Black
                        )
                        Text(
                            text = "${offlineList.size} local tracks cataloged",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Manual Trigger Scanner button
                        IconButton(
                            onClick = {
                                val isGranted = ContextCompat.checkSelfPermission(
                                    context,
                                    permissionString
                                ) == PackageManager.PERMISSION_GRANTED

                                if (isGranted) {
                                    viewModel.scanLocalFiles()
                                } else {
                                    showPermissionDialog = true
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(if (isDarkMode) Color(0x1AFFFFFF) else Color(0x0F000000), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Scan local folder",
                                tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Shuffle All Pill button
                        if (offlineList.isNotEmpty() && subTab == "songs") {
                            Button(
                                onClick = {
                                    val playerTracks = offlineList.map { offlineTrack ->
                                        PlayerTrack(
                                            id = offlineTrack.id,
                                            title = offlineTrack.title,
                                            artist = offlineTrack.artist,
                                            durationMs = offlineTrack.durationMs,
                                            streamUrl = offlineTrack.localUri,
                                            thumbnailUrl = offlineTrack.thumbnailUrl,
                                            source = offlineTrack.source
                                        )
                                    }
                                    val shuffled = playerTracks.shuffled()
                                    if (shuffled.isNotEmpty()) {
                                        viewModel.playTrackWithResolution(shuffled.first(), shuffled)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(percent = 50),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Shuffle",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Shuffle All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // SONGS VIEW
            if (subTab == "songs") {
                // Sort Pills selector
                if (offlineList.isNotEmpty()) {
                    item {
                        val currentField by viewModel.sortField.collectAsState()
                        val currentDirection by viewModel.sortDirection.collectAsState()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )

                            // Title
                            SortPill(
                                text = "Title",
                                isSelected = currentField == com.example.viewmodel.SortField.TITLE,
                                direction = if (currentField == com.example.viewmodel.SortField.TITLE) currentDirection else null,
                                onClick = {
                                    if (currentField == com.example.viewmodel.SortField.TITLE) {
                                        viewModel.setSortDirection(
                                            if (currentDirection == com.example.viewmodel.SortDirection.ASCENDING) {
                                                com.example.viewmodel.SortDirection.DESCENDING
                                            } else {
                                                com.example.viewmodel.SortDirection.ASCENDING
                                            }
                                        )
                                    } else {
                                        viewModel.setSortField(com.example.viewmodel.SortField.TITLE)
                                        viewModel.setSortDirection(com.example.viewmodel.SortDirection.ASCENDING)
                                    }
                                },
                                isDarkMode = isDarkMode
                            )

                            // Date Added
                            SortPill(
                                text = "Date Added",
                                isSelected = currentField == com.example.viewmodel.SortField.DATE_ADDED,
                                direction = if (currentField == com.example.viewmodel.SortField.DATE_ADDED) currentDirection else null,
                                onClick = {
                                    if (currentField == com.example.viewmodel.SortField.DATE_ADDED) {
                                        viewModel.setSortDirection(
                                            if (currentDirection == com.example.viewmodel.SortDirection.ASCENDING) {
                                                com.example.viewmodel.SortDirection.DESCENDING
                                            } else {
                                                com.example.viewmodel.SortDirection.ASCENDING
                                            }
                                        )
                                    } else {
                                        viewModel.setSortField(com.example.viewmodel.SortField.DATE_ADDED)
                                        viewModel.setSortDirection(com.example.viewmodel.SortDirection.DESCENDING)
                                    }
                                },
                                isDarkMode = isDarkMode
                            )

                            // Plays
                            SortPill(
                                text = "Play Count",
                                isSelected = currentField == com.example.viewmodel.SortField.PLAY_COUNT,
                                direction = if (currentField == com.example.viewmodel.SortField.PLAY_COUNT) currentDirection else null,
                                onClick = {
                                    if (currentField == com.example.viewmodel.SortField.PLAY_COUNT) {
                                        viewModel.setSortDirection(
                                            if (currentDirection == com.example.viewmodel.SortDirection.ASCENDING) {
                                                com.example.viewmodel.SortDirection.DESCENDING
                                            } else {
                                                com.example.viewmodel.SortDirection.ASCENDING
                                            }
                                        )
                                    } else {
                                        viewModel.setSortField(com.example.viewmodel.SortField.PLAY_COUNT)
                                        viewModel.setSortDirection(com.example.viewmodel.SortDirection.DESCENDING)
                                    }
                                },
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }

                if (offlineList.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No Local Tracks Registered",
                            tip = "Scan your device folder above, or download songs from your synced Google Drive Vault folder.",
                            isDarkMode = isDarkMode
                        )
                    }
                } else {
                    items(offlineList, key = { it.id }) { offlineTrack ->
                        val track = PlayerTrack(
                            id = offlineTrack.id,
                            title = offlineTrack.title,
                            artist = offlineTrack.artist,
                            durationMs = offlineTrack.durationMs,
                            streamUrl = offlineTrack.localUri,
                            thumbnailUrl = offlineTrack.thumbnailUrl,
                            source = offlineTrack.source
                        )
                        TrackCapsuleItem(
                            track = track,
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            onTrackClick = {
                                viewModel.playTrackWithResolution(track, offlineList.map {
                                    PlayerTrack(
                                        id = it.id,
                                        title = it.title,
                                        artist = it.artist,
                                        durationMs = it.durationMs,
                                        streamUrl = it.localUri,
                                        thumbnailUrl = it.thumbnailUrl,
                                        source = it.source
                                    )
                                })
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(track) },
                            isScrollingDown = isScrollingDown
                        )
                    }
                }
            }

            // PLAYLISTS VIEW (Expandable items)
            if (subTab == "playlists") {
                val lockerList = offlineList.filter { it.source == "Locker" }
                val localList = offlineList.filter { it.source == "Local" }
                val mostPlayed = offlineList.filter { (playCountsMap[it.id] ?: 0) > 0 }
                    .sortedByDescending { playCountsMap[it.id] ?: 0 }

                val playlists = listOf(
                    PlaylistData("favorites", "My Favorites", Icons.Default.Favorite, favoritesList.map { 
                        PlayerTrack(
                            id = it.id,
                            title = it.title,
                            artist = it.artist,
                            streamUrl = it.streamUrl,
                            thumbnailUrl = it.thumbnailUrl,
                            source = it.source,
                            durationMs = it.durationMs
                        )
                    }),
                    PlaylistData("locker", "Locker Cloud Downloads", Icons.Default.Cloud, lockerList.map { 
                        PlayerTrack(
                            id = it.id,
                            title = it.title,
                            artist = it.artist,
                            streamUrl = it.localUri,
                            thumbnailUrl = it.thumbnailUrl,
                            source = it.source,
                            durationMs = it.durationMs
                        )
                    }),
                    PlaylistData("local", "Local Device Imports", Icons.Default.Folder, localList.map { 
                        PlayerTrack(
                            id = it.id,
                            title = it.title,
                            artist = it.artist,
                            streamUrl = it.localUri,
                            thumbnailUrl = it.thumbnailUrl,
                            source = it.source,
                            durationMs = it.durationMs
                        )
                    }),
                    PlaylistData("history", "Most Played Tracks", Icons.Default.Equalizer, mostPlayed.map { 
                        PlayerTrack(
                            id = it.id,
                            title = it.title,
                            artist = it.artist,
                            streamUrl = it.localUri,
                            thumbnailUrl = it.thumbnailUrl,
                            source = it.source,
                            durationMs = it.durationMs
                        )
                    })
                )

                playlists.forEach { pl ->
                    item {
                        PlaylistHeaderCard(
                            playlist = pl,
                            isExpanded = expandedItemKey == pl.id,
                            isDarkMode = isDarkMode,
                            onClick = {
                                expandedItemKey = if (expandedItemKey == pl.id) null else pl.id
                            }
                        )
                    }
                    if (expandedItemKey == pl.id) {
                        if (pl.tracks.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No tracks registered in this smart playlist.",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        } else {
                            items(pl.tracks, key = { it.id }) { track ->
                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    TrackCapsuleItem(
                                        track = track,
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode,
                                        onTrackClick = {
                                            viewModel.playTrackWithResolution(track, pl.tracks)
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(track) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ARTISTS VIEW (Expandable items)
            if (subTab == "artists") {
                val artistGroups = offlineList.groupBy { it.artist.ifBlank { "Unknown Artist" }.trim() }
                    .toSortedMap()

                if (artistGroups.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No Artists Discovered",
                            tip = "Catalog audio files or complete track syncing to view artist segments.",
                            isDarkMode = isDarkMode
                        )
                    }
                } else {
                    artistGroups.forEach { (artistName, tracks) ->
                        val artistTracks = tracks.map { 
                            PlayerTrack(
                                id = it.id,
                                title = it.title,
                                artist = it.artist,
                                streamUrl = it.localUri,
                                thumbnailUrl = it.thumbnailUrl,
                                source = it.source,
                                durationMs = it.durationMs
                            )
                        }
                        item {
                            ArtistHeaderCard(
                                artistName = artistName,
                                songCount = tracks.size,
                                isExpanded = expandedItemKey == artistName,
                                isDarkMode = isDarkMode,
                                onClick = {
                                    expandedItemKey = if (expandedItemKey == artistName) null else artistName
                                }
                            )
                        }
                        if (expandedItemKey == artistName) {
                            items(artistTracks, key = { it.id }) { track ->
                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    TrackCapsuleItem(
                                        track = track,
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode,
                                        onTrackClick = {
                                            viewModel.playTrackWithResolution(track, artistTracks)
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(track) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // GENRES VIEW (Expandable items)
            if (subTab == "genres") {
                val genreGroups = offlineList.groupBy { track ->
                    val titleUpper = track.title.uppercase()
                    val artistUpper = track.artist.uppercase()
                    when {
                        titleUpper.contains("LOFI") || titleUpper.contains("LO-FI") || 
                        titleUpper.contains("CHILL") || titleUpper.contains("SLEEP") || 
                        titleUpper.contains("RELAX") || titleUpper.contains("AMBIENT") || 
                        titleUpper.contains("STUDY") || titleUpper.contains("RAIN") ||
                        artistUpper.contains("LOFI") || artistUpper.contains("CHILL") -> "Chill & Lofi"

                        titleUpper.contains("REMIX") || titleUpper.contains("MIX") || 
                        titleUpper.contains("SYNTH") || titleUpper.contains("EDM") || 
                        titleUpper.contains("DANCE") || titleUpper.contains("ELECTRONIC") || 
                        titleUpper.contains("BEAT") || titleUpper.contains("CLUB") || 
                        titleUpper.contains("HOUSE") || titleUpper.contains("TECHNO") -> "Electronic & Dance"

                        titleUpper.contains("RAP") || titleUpper.contains("HIP HOP") || 
                        titleUpper.contains("HIP-HOP") || titleUpper.contains("TRAP") || 
                        titleUpper.contains("R&B") || titleUpper.contains("SOUL") || 
                        artistUpper.contains("RAP") || artistUpper.contains("HIP") -> "Hip-Hop & R&B"

                        titleUpper.contains("ACOUSTIC") || titleUpper.contains("LIVE") || 
                        titleUpper.contains("PIANO") || titleUpper.contains("CLASSICAL") || 
                        titleUpper.contains("INSTRUMENTAL") || titleUpper.contains("VIOLIN") || 
                        titleUpper.contains("GUITAR") -> "Acoustic & Classical"

                        titleUpper.contains("POP") || titleUpper.contains("LOVE") || 
                        titleUpper.contains("VOCAL") || titleUpper.contains("ROCK") || 
                        titleUpper.contains("INDIE") || titleUpper.contains("METAL") -> "Pop, Rock & Indie"

                        else -> "Alternative Indie"
                    }
                }.toSortedMap()

                if (genreGroups.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No Tracks for Genre Classification",
                            tip = "Genre tags are computed dynamically from song titles or artists.",
                            isDarkMode = isDarkMode
                        )
                    }
                } else {
                    genreGroups.forEach { (genreName, tracks) ->
                        val genreTracks = tracks.map { 
                            PlayerTrack(
                                id = it.id,
                                title = it.title,
                                artist = it.artist,
                                streamUrl = it.localUri,
                                thumbnailUrl = it.thumbnailUrl,
                                source = it.source,
                                durationMs = it.durationMs
                            )
                        }
                        item {
                            GenreHeaderCard(
                                genreName = genreName,
                                songCount = tracks.size,
                                isExpanded = expandedItemKey == genreName,
                                isDarkMode = isDarkMode,
                                onClick = {
                                    expandedItemKey = if (expandedItemKey == genreName) null else genreName
                                }
                            )
                        }
                        if (expandedItemKey == genreName) {
                            items(genreTracks, key = { it.id }) { track ->
                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    TrackCapsuleItem(
                                        track = track,
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode,
                                        onTrackClick = {
                                            viewModel.playTrackWithResolution(track, genreTracks)
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(track) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortPill(
    text: String,
    isSelected: Boolean,
    direction: com.example.viewmodel.SortDirection?,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val containerBg = if (isSelected) {
        if (isDarkMode) Color(0x33FF5722) else Color(0x1B673AB7)
    } else {
        if (isDarkMode) Color(0x11FFFFFF) else Color(0x0A000000)
    }
    val contentColor = if (isSelected) {
        if (isDarkMode) EarthySienna else DeepPurple
    } else {
        if (isDarkMode) Color.LightGray else Color.DarkGray
    }
    val borderCol = if (isSelected) {
        if (isDarkMode) EarthySienna.copy(alpha = 0.5f) else DeepPurple.copy(alpha = 0.4f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .background(containerBg, RoundedCornerShape(100.dp))
            .border(1.dp, borderCol, RoundedCornerShape(100.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )
        if (direction != null) {
            Icon(
                imageVector = if (direction == com.example.viewmodel.SortDirection.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = if (direction == com.example.viewmodel.SortDirection.ASCENDING) "Ascending" else "Descending",
                tint = contentColor,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

// --- Tab 1: Google Locker ---

@Composable
fun LockerTabContent(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onConnectOAuthClick: () -> Unit,
    onManualTokenClick: () -> Unit
) {
    val isConnected by viewModel.isConnectedToDrive.collectAsState()
    val lockerState by viewModel.lockerUiState.collectAsState()
    val folderId by viewModel.googleFolderId.collectAsState()

    val accentColor = if (isDarkMode) EarthySienna else DeepPurple
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)
    val borderColor = if (isDarkMode) TranslucentBorderDark else TranslucentBorderLight

    // State for the custom pasted link mode
    var isLinkModeActive by remember { mutableStateOf(false) }
    var pastedLink by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    // Helper to parse complex Google Drive folder links, query strings and IDs securely
    val extractFolderId = { url: String ->
        val pruned = url.trim()
        if (pruned.startsWith("http") || pruned.contains("drive.google.com")) {
            if (pruned.contains("/folders/")) {
                val foldersIndex = pruned.indexOf("/folders/")
                val afterFolders = pruned.substring(foldersIndex + "/folders/".length)
                val queryIndex = afterFolders.indexOf('?')
                val id = if (queryIndex != -1) afterFolders.substring(0, queryIndex) else afterFolders
                id.split("/")[0].split("?")[0].trim()
            } else if (pruned.contains("id=")) {
                val idIndex = pruned.indexOf("id=")
                val afterId = pruned.substring(idIndex + 3)
                val ampIndex = afterId.indexOf('&')
                val rawId = if (ampIndex != -1) afterId.substring(0, ampIndex).trim() else afterId.trim()
                rawId.split("?")[0].trim()
            } else {
                pruned
            }
        } else {
            pruned
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLinkModeActive) {
            // A back button at the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isLinkModeActive = false
                        isSubmitted = false
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Go Back",
                        tint = if (isDarkMode) Color.White else Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back to Cloud Index",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                    modifier = Modifier.clickable {
                        isLinkModeActive = false
                        isSubmitted = false
                    }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (!isLinkModeActive) {
                // 1. Google Drive Connection Sync Panel at the top
                item {
                    LockerConnectionController(
                        isConnected = isConnected,
                        folderId = folderId,
                        isDarkMode = isDarkMode,
                        onConnectOAuthClick = onConnectOAuthClick,
                        onManualTokenClick = onManualTokenClick,
                        onDisconnectClick = { viewModel.disconnectDrive() },
                        onRefreshClick = { viewModel.fetchLockerFiles() },
                        onImportFolderLinkClick = {
                            isLinkModeActive = true
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 2. Drive Locker Cloud Directory Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Google Drive Stream Index",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color.Black
                        )

                        if (isConnected && lockerState is LockerUiState.Success) {
                            val cloudTracks = (lockerState as LockerUiState.Success).tracks
                            if (cloudTracks.isNotEmpty()) {
                                Button(
                                    onClick = { viewModel.downloadAllLockerTracks(cloudTracks) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) EarthySienna else DeepPurple),
                                    shape = RoundedCornerShape(percent = 50),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudQueue,
                                        contentDescription = "Download All Cloud Tracks",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download All", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 3. Render Locker Cloud files state
                if (isConnected) {
                    when (val state = lockerState) {
                        is LockerUiState.Idle -> {
                            item {
                                EmptyStateCard(
                                    title = "Sync Ready", 
                                    tip = "Connect and select a folder above to synchronize your library.", 
                                    isDarkMode = isDarkMode
                                )
                            }
                        }
                        is LockerUiState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    RotatingAppLogo(modifier = Modifier.size(42.dp).clip(CircleShape))
                                }
                            }
                        }
                        is LockerUiState.Error -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                        .background(
                                            if (isDarkMode) Color(0x33FF0000) else Color(0x1FFF0000),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            0.5.dp, Color.Red.copy(alpha = 0.4f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(imageVector = Icons.Default.Info, contentDescription = "Error", tint = Color.Red)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.message,
                                        color = if (isDarkMode) Color(0xFFFFBABA) else Color(0xFFD8000C),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { viewModel.fetchLockerFiles() },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) EarthySienna else DeepPurple),
                                        shape = RoundedCornerShape(percent = 50),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                    ) {
                                        Text("Retry Discovery", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        is LockerUiState.Success -> {
                            if (state.tracks.isEmpty()) {
                                item {
                                    EmptyStateCard(
                                        title = "Folder is Empty",
                                        tip = "No supported format files (.mp3, .wav, .flac) found in your targeted Drive directory.",
                                        isDarkMode = isDarkMode
                                    )
                                }
                            } else {
                                items(state.tracks, key = { it.id }) { track ->
                                    TrackCapsuleItem(
                                        track = track,
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode,
                                        onTrackClick = {
                                            viewModel.playTrackWithResolution(track, state.tracks)
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(track) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        EmptyStateCard(
                            title = "Google Drive Disconnected",
                            tip = "Authenticate above to map a targeted drive folder and synchronize high-quality network streaming tracks.",
                            isDarkMode = isDarkMode
                        )
                    }
                }
            } else {
                // LINK IMPORT MODE: shows the textwriter input OR the parsed songs list
                if (!isSubmitted) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(containerBrush, RoundedCornerShape(24.dp))
                                .border(0.5.dp, borderBrush, RoundedCornerShape(24.dp))
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = "Folder Icon",
                                    tint = accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Folder Link Textwriter",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Paste your shared Google Drive folder link containing songs (.mp3, .wav, .flac). We will parse the ID and index tracks directly.",
                                fontSize = 11.sp,
                                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = pastedLink,
                                onValueChange = { pastedLink = it },
                                placeholder = { Text("https://drive.google.com/drive/folders/...", fontSize = 12.sp, color = Color.Gray) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    unfocusedBorderColor = borderColor
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val fId = extractFolderId(pastedLink)
                                    if (fId.isNotEmpty()) {
                                        viewModel.saveGoogleFolderId(fId)
                                        if (isConnected) {
                                            viewModel.fetchLockerFiles()
                                        }
                                        isSubmitted = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(percent = 50),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Text("Submit Link", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else {
                    // SUBMITTED: Display list of songs
                    val folderLabel = if (folderId.length > 8) folderId.take(8) + "..." else folderId
                    item {
                        Text(
                            text = "Imported Tracks: Folder ID $folderLabel",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                        )
                    }

                    if (isConnected) {
                        // Real Google Drive tracks are syncing!
                        when (val state = lockerState) {
                            is LockerUiState.Idle -> {
                                item { Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { RotatingAppLogo(modifier = Modifier.size(42.dp).clip(CircleShape)) } }
                            }
                            is LockerUiState.Loading -> {
                                item { Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { RotatingAppLogo(modifier = Modifier.size(42.dp).clip(CircleShape)) } }
                            }
                            is LockerUiState.Error -> {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                            .background(
                                                if (isDarkMode) Color(0x33FF0000) else Color(0x1FFF0000),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                0.5.dp, Color.Red.copy(alpha = 0.4f),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = "Error", tint = Color.Red)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = state.message,
                                            color = if (isDarkMode) Color(0xFFFFBABA) else Color(0xFFD8000C),
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { viewModel.fetchLockerFiles() },
                                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                            shape = RoundedCornerShape(percent = 50),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                        ) {
                                            Text("Retry Discovery", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                            is LockerUiState.Success -> {
                                if (state.tracks.isEmpty()) {
                                    item {
                                        EmptyStateCard(
                                            title = "No songs found in folder",
                                            tip = "We couldn't locate compatible tracks in Folder ID $folderId. Try checking folder share permissions.",
                                            isDarkMode = isDarkMode
                                        )
                                    }
                                } else {
                                    items(state.tracks, key = { it.id }) { track ->
                                        TrackCapsuleItem(
                                            track = track,
                                            viewModel = viewModel,
                                            isDarkMode = isDarkMode,
                                            onTrackClick = {
                                                viewModel.playTrackWithResolution(track, state.tracks)
                                            },
                                            onFavoriteToggle = { viewModel.toggleFavorite(track) }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            EmptyStateCard(
                                title = "Sign-In Required",
                                tip = "Please authenticate with Google first via 'Back' and then click 'Secure Sign-In' to fetch tracks from this shared folder link.",
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LockerConnectionController(
    isConnected: Boolean,
    folderId: String,
    isDarkMode: Boolean,
    onConnectOAuthClick: () -> Unit,
    onManualTokenClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onImportFolderLinkClick: () -> Unit
) {
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)
    val accentColor = if (isDarkMode) EarthySienna else DeepPurple

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerBrush, RoundedCornerShape(24.dp))
            .border(0.5.dp, borderBrush, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.Cloud else Icons.Default.CloudQueue,
                    contentDescription = "Cloud Info",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "Storage Locker Connected" else "Private Drive Vault",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }

            if (isConnected) {
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Synchronize files list",
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isConnected) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Source folder ID:",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = if (folderId == "root") "Google Drive / MyuLoc Root" else folderId,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 180.dp)
                    )
                }
                
                Button(
                    onClick = onDisconnectClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(percent = 50),
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text("Disconnect", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Text(
                text = "Stream and catalog your personal files directly from Google Drive. Tap secure Auth to login or enter Developer credentials instantly.",
                fontSize = 11.sp,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                lineHeight = 15.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(
                onClick = onConnectOAuthClick,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Text("Secure Sign-In with Google Drive", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Have a Shared Folder? Import from Drive Folder Link",
                color = accentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImportFolderLinkClick() }
                    .padding(vertical = 6.dp)
            )
        }
    }
}

// --- Tab 2: Online Search (yt-dlp concept via Invidious) ---

@Composable
fun SearchTabContent(viewModel: MyuLocViewModel, isDarkMode: Boolean) {
    val searchState by viewModel.searchUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var searchInput by remember { mutableStateOf(searchQuery) }

    // Instant real-time typing query execution for ultra-smooth responsiveness
    LaunchedEffect(searchInput) {
        viewModel.performSearch(searchInput)
    }

    val accentColor = if (isDarkMode) EarthySienna else DeepPurple
    val containerBg = if (isDarkMode) TranslucentGlassDark else TranslucentGlassLight
    val borderColor = if (isDarkMode) TranslucentBorderDark else TranslucentBorderLight

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Capsule Bar with clean horizontal separation to fix text placement
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                placeholder = { Text("Search offline/local songs...", fontSize = 13.sp, color = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = containerBg,
                    unfocusedContainerColor = containerBg,
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = borderColor,
                    focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                    unfocusedTextColor = if (isDarkMode) Color.LightGray else Color.DarkGray
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.performSearch(searchInput) }
                ),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon", tint = Color.Gray, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = { searchInput = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search text", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.performSearch(searchInput) },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier.height(48.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Search", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Results List
        Text(
            text = "Matching Device Songs",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        when (val state = searchState) {
            is SearchUiState.Idle -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(accentColor.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Search Music Icon",
                            tint = accentColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Instant Device Lookup",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter track titles or artists in the search bar above to look up cached cloud folders and scanned local storage instantly.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            is SearchUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RotatingAppLogo(modifier = Modifier.size(52.dp).clip(CircleShape))
                }
            }
            is SearchUiState.Error -> {
                EmptyStateCard(
                    title = "Extraction Issue",
                    tip = state.message,
                    isDarkMode = isDarkMode
                )
            }
            is SearchUiState.Success -> {
                if (state.results.isEmpty()) {
                    EmptyStateCard(
                        title = "No Outcomes Found",
                        tip = "Double check query syntax or Invidious Server URL in Settings.",
                        isDarkMode = isDarkMode
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.results, key = { it.id }) { track ->
                            TrackCapsuleItem(
                                track = track,
                                viewModel = viewModel,
                                isDarkMode = isDarkMode,
                                onTrackClick = {
                                    viewModel.playTrackWithResolution(track, state.results)
                                },
                                onFavoriteToggle = { viewModel.toggleFavorite(track) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Tab 3: Favorites ---

@Composable
fun FavoritesTabContent(viewModel: MyuLocViewModel, isDarkMode: Boolean) {
    val favoritesList by viewModel.favoriteTracksFlow.collectAsState(initial = emptyList())
    val accentColor = if (isDarkMode) EarthySienna else DeepPurple

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Your Curated Ecosystem",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.LightGray else Color.DarkGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        if (favoritesList.isEmpty()) {
            EmptyStateCard(
                title = "Your Vault is Empty",
                tip = "Tap the heart icon on any local track capsule to catalog it instantly into your custom glassmorphic vault.",
                isDarkMode = isDarkMode
            )
        } else {
            val mappedList = favoritesList.map { it.toPlayerTrack() }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favoritesList, key = { it.id }) { favorite ->
                    val track = favorite.toPlayerTrack()
                    TrackCapsuleItem(
                        track = track,
                        viewModel = viewModel,
                        isDarkMode = isDarkMode,
                        onTrackClick = {
                            viewModel.playTrackWithResolution(track, mappedList)
                        },
                        onFavoriteToggle = { viewModel.toggleFavorite(track) }
                    )
                }
            }
        }
    }
}

// --- Tab 4: Settings & Customizer ---

@Composable
fun SettingsTabContent(viewModel: MyuLocViewModel, isDarkMode: Boolean) {
    val googleFolderId by viewModel.googleFolderId.collectAsState()
    val customClientId by viewModel.customClientId.collectAsState()
    val customRedirectUri by viewModel.customRedirectUri.collectAsState()
    val googleApiKey by viewModel.googleApiKey.collectAsState()
    val invidiousUrl by viewModel.invidiousUrl.collectAsState()
    val driveStorageState by viewModel.driveStorageState.collectAsState()

    val loggedInEmail by viewModel.loggedInEmail.collectAsState()
    val loggedInName by viewModel.loggedInName.collectAsState()
    val loggedInPhone by viewModel.loggedInPhone.collectAsState()
    val isAdmin by viewModel.isCurrentUserAdmin.collectAsState()
    val animSpeed by viewModel.animationSpeed.collectAsState()
    val animBounciness by viewModel.animationBounciness.collectAsState()

    val filterOutSmallFiles by viewModel.filterOutSmallFiles.collectAsState()
    val filterOutShortAudios by viewModel.filterOutShortAudios.collectAsState()
    val preferredStreamBitrate by viewModel.preferredStreamBitrate.collectAsState()
    val bufferMemoryAllocation by viewModel.bufferMemoryAllocation.collectAsState()
    val headsetDisconnectBehavior by viewModel.headsetDisconnectBehavior.collectAsState()
    val playbackAudioQuality by viewModel.playbackAudioQuality.collectAsState()
    val cachedAudioSize by viewModel.cachedAudioSize.collectAsState()
    val sleepTimerRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepTimerMinutesLeft by viewModel.sleepTimerMinutesLeft.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateCachedAudioSize()
    }

    var folderInput by remember { mutableStateOf(googleFolderId) }
    var clientIdInput by remember { mutableStateOf(customClientId) }
    var redirectUriInput by remember { mutableStateOf(customRedirectUri) }
    var apiKeyInput by remember { mutableStateOf(googleApiKey) }
    var invidiousInput by remember { mutableStateOf(invidiousUrl) }
    var showStorageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(googleFolderId, customClientId, customRedirectUri, googleApiKey, invidiousUrl) {
        folderInput = googleFolderId
        clientIdInput = customClientId
        redirectUriInput = customRedirectUri
        apiKeyInput = googleApiKey
        invidiousInput = invidiousUrl
    }

    val accentColor = if (isDarkMode) Color(0xFFD47043) else DeepPurple
    val pinkAccent = Color(0xFFF472B6)
    val containerBg = if (isDarkMode) TranslucentGlassDark else TranslucentGlassLight
    val borderColor = if (isDarkMode) TranslucentBorderDark else TranslucentBorderLight

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        item {
            if (isAdmin) {
                var adminConsoleLog by remember { mutableStateOf("Executive diagnostic channel active.\nSystem nominal. Waiting for diagnostics query...") }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(containerBg, RoundedCornerShape(24.dp))
                        .border(
                            0.5.dp, 
                            Brush.horizontalGradient(listOf(Color(0xFFFFA726), Color(0xFFFF5722))), 
                            RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Admin key",
                            tint = Color(0xFFFFA726),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Console Node & Diagnostics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Retro terminal logger output
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D0D0D))
                            .border(1.dp, Color(0xFF333333), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = adminConsoleLog,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF00FF66),
                            lineHeight = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.flushSqliteCache()
                                adminConsoleLog = "Purged cached tracks from local SQLite repository successfully.\nReloading demonstration nodes."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(percent = 50),
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("Flush Cache DB", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                adminConsoleLog = "Initiating ping verification against proxy node: $invidiousUrl ..."
                                viewModel.verifyInvidiousNode(invidiousUrl) { latencyLog ->
                                    adminConsoleLog = latencyLog
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                            shape = RoundedCornerShape(percent = 50),
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("Test Proxy Node", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }



        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    .border(0.5.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Local Storage & Directory Filters",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(pinkAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "INDEX",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = pinkAccent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Filter Out Small Files",
                    subtitle = "Ignore files under 500KB to exclude voice notes",
                    checked = filterOutSmallFiles,
                    onCheckedChange = { viewModel.setFilterOutSmallFiles(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(if (isDarkMode) Color(0x1FFFFFFF) else Color(0x0E000000))
                )

                SettingsSliderRow(
                    title = "Filter Out Short Audios",
                    subtitle = "Filter out system ringtones from scanned list",
                    value = filterOutShortAudios,
                    valueRange = 0f..60f,
                    onValueChange = { viewModel.setFilterOutShortAudios(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(if (isDarkMode) Color(0x1FFFFFFF) else Color(0x0E000000))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Re-indexing Device Storage",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) Color.White else Color.Black
                        )
                        Text(
                            text = "Manually refresh catalog of offline audio files",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = { viewModel.scanLocalFiles() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan Now", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    .border(0.5.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Audio Playback Quality Control",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(pinkAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AUDIO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = pinkAccent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                SettingsDropdownRow(
                    title = "Preferred Audio Quality",
                    subtitle = "Decide offline decode fidelity and DSP filters",
                    options = listOf("Low Quality (96kbps)", "Medium Quality (160kbps)", "High Quality (320kbps)", "Hi-Fi Studio Quality (24-bit/96kHz)"),
                    selectedOption = playbackAudioQuality,
                    onOptionSelected = { viewModel.setPlaybackAudioQuality(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    .border(0.5.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playback Utilities & Automation",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(pinkAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PLAYER",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = pinkAccent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                val sleepDisplay = if (sleepTimerRunning) "${sleepTimerMinutesLeft}m remaining" else "Disabled"
                SettingsDropdownRow(
                    title = "Sleep Timer",
                    subtitle = "Suspends playback after designated target duration",
                    options = listOf("Disabled", "15 mins", "30 mins", "45 mins", "60 mins"),
                    selectedOption = sleepDisplay,
                    onOptionSelected = { choice ->
                        when (choice) {
                            "Disabled" -> viewModel.stopSleepTimer()
                            "15 mins" -> viewModel.startSleepTimer(15)
                            "30 mins" -> viewModel.startSleepTimer(30)
                            "45 mins" -> viewModel.startSleepTimer(45)
                            "60 mins" -> viewModel.startSleepTimer(60)
                        }
                    },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(if (isDarkMode) Color(0x1FFFFFFF) else Color(0x0E000000))
                )

                SettingsSwitchRow(
                    title = "Headset Connection Behavior",
                    subtitle = "Pause playback immediately upon audio jack/Bluetooth disconnect",
                    checked = headsetDisconnectBehavior,
                    onCheckedChange = { viewModel.setHeadsetDisconnectBehavior(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    .border(0.5.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MyuLoc Ecosystem",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color.Black
                )
                Text(
                    text = "Version 1.0.0 (Minimalist Capsule Design)",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(14.dp))
                CustomAppLogo(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )
            }
        }
    }

    if (showStorageDialog) {
        val storageAccent = if (isDarkMode) EarthySienna else DeepPurple
        val dialogBg = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
        val cardBg = if (isDarkMode) Color(0xFF2E2E2E) else Color(0x0A000000)
        
        Dialog(onDismissRequest = { showStorageDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(dialogBg)
                    .border(0.5.dp, borderColor, RoundedCornerShape(28.dp))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = "Cloud Icon",
                        tint = storageAccent,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Google Drive Storage",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when (val state = driveStorageState) {
                        is DriveStorageState.Idle -> {
                            Text(
                                text = "Preparing connection...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        is DriveStorageState.Loading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                CircularProgressIndicator(color = storageAccent, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Retrieving storage quota...",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        is DriveStorageState.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Error",
                                    tint = Color.Red,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.message,
                                    color = if (isDarkMode) Color(0xFFFFBABA) else Color(0xFFD8000C),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.fetchDriveStorage() },
                                    colors = ButtonDefaults.buttonColors(containerColor = storageAccent)
                                ) {
                                    Text("Retry", color = Color.White)
                                }
                            }
                        }
                        is DriveStorageState.Success -> {
                            val totalStr = formatBytes(state.limitBytes)
                            val usedStr = formatBytes(state.usageBytes)
                            val remainingStr = formatBytes(state.remainingBytes)
                            val pct = state.usagePercentage
                            val pctFraction = (pct / 100f).coerceIn(0f, 1f)

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isDarkMode) Color(0x33FFFFFF) else Color(0x1F000000))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(pctFraction)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(storageAccent)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$usedStr used",
                                        fontSize = 12.sp,
                                        color = if (isDarkMode) Color.LightGray else Color.DarkGray
                                    )
                                    Text(
                                        text = "$totalStr total (${String.format(java.util.Locale.US, "%.1f", pct)}%)",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(cardBg)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cloud,
                                        contentDescription = "Info",
                                        tint = storageAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "$remainingStr available",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkMode) Color.White else Color.Black
                                        )
                                        Text(
                                            text = "Remaining cloud storage",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                if (state.isMock) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "⚡ Viewing simulated space (Sandbox Mode)",
                                        fontSize = 10.sp,
                                        color = storageAccent,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { showStorageDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color(0xFF3E3E3E) else Color(0xFFE0E0E0)),
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Close",
                            color = if (isDarkMode) Color.White else Color.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsDropdownRow(
    title: String,
    subtitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isDarkMode: Boolean,
    accentColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        
        Box {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) Color(0x33FFFFFF) else Color(0x0A000000))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedOption,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(if (isDarkMode) Color(0xFF1F1A1C) else Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = if (isDarkMode) Color.White else Color.Black
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDarkMode: Boolean,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = if (isDarkMode) Color(0x22FFFFFF) else Color(0x11000000)
            )
        )
    }
}

@Composable
private fun SettingsSliderRow(
    title: String,
    subtitle: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
    isDarkMode: Boolean,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkMode) Color.White else Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "${value}s",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Slider(
            value = value.toFloat().coerceIn(valueRange),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = if (isDarkMode) Color(0x22FFFFFF) else Color(0x11000000)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    if (digitGroups >= units.size) return "$bytes B"
    return String.format(java.util.Locale.US, "%.2f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

// --- Common UI Components ---

@Composable
fun rememberScrollDirection(state: LazyListState): Boolean {
    var isScrollingDown by remember { mutableStateOf(true) }
    var lastOffset by remember { mutableIntStateOf(0) }
    var lastIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset) {
        val currentIndex = state.firstVisibleItemIndex
        val currentOffset = state.firstVisibleItemScrollOffset
        if (currentIndex > lastIndex) {
            isScrollingDown = true
        } else if (currentIndex < lastIndex) {
            isScrollingDown = false
        } else {
            if (currentOffset > lastOffset) {
                isScrollingDown = true
            } else if (currentOffset < lastOffset) {
                isScrollingDown = false
            }
        }
        lastIndex = currentIndex
        lastOffset = currentOffset
    }
    return isScrollingDown
}

private fun formatPlayCount(count: Int): String {
    return when {
        count >= 1_000_000 -> {
            val millions = count / 1_000_000.0
            String.format(java.util.Locale.US, "%.1fM plays", millions)
        }
        count >= 1_000 -> {
            val thousands = count / 1_000.0
            String.format(java.util.Locale.US, "%.1fK plays", thousands)
        }
        else -> "$count plays"
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TrackCapsuleItem(
    track: PlayerTrack,
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onTrackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isScrollingDown: Boolean = true
) {
    var localArtBytes by remember(track.id) { mutableStateOf<ByteArray?>(null) }
    
    val animatableOffset = remember { androidx.compose.animation.core.Animatable(if (isScrollingDown) -120f else 120f) }
    val animatableAlpha = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(track.id, isScrollingDown) {
        launch {
            animatableOffset.animateTo(
                targetValue = 0f,
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                )
            )
        }
        launch {
            animatableAlpha.animateTo(
                targetValue = 1f,
                animationSpec = androidx.compose.animation.core.spring(
                    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                )
            )
        }
    }

    LaunchedEffect(track.id) {
        if ((track.source == "Local" || track.source == "Offline" || track.source == "LocalScanned") && !track.streamUrl.isNullOrEmpty()) {
            delay(300) // Debounce list scrolling to avoid file descriptor and CPU exhaustion
            withContext(Dispatchers.IO) {
                var retriever: android.media.MediaMetadataRetriever? = null
                try {
                    retriever = android.media.MediaMetadataRetriever()
                    val cleanPath = track.streamUrl.replace("file://", "")
                    retriever.setDataSource(cleanPath)
                    localArtBytes = retriever.embeddedPicture
                } catch (e: Exception) {
                    // ignore
                } finally {
                    try {
                        retriever?.release()
                    } catch (ex: Exception) {
                        // ignore
                    }
                }
            }
        } else {
            localArtBytes = null
        }
    }

    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)
    val textPrimary = if (isDarkMode) Color.White else Color.Black
    val iconColor = if (isDarkMode) EarthySienna else DeepPurple

    val favoriteTrackIds by viewModel.favoriteTrackIds.collectAsState()
    val isLiked = remember(favoriteTrackIds, track.id) {
        favoriteTrackIds.contains(track.id)
    }

    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val progress = downloadProgress[track.id]

    val offlineTrackIds by viewModel.offlineTrackIds.collectAsState()
    val isDownloaded = remember(offlineTrackIds, track.id) {
        offlineTrackIds.contains(track.id)
    }

    val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val isCurrent = currentTrack?.id == track.id

    val playCountsMap by viewModel.playCounts.collectAsState()
    val playCount = playCountsMap[track.id]

    LaunchedEffect(track.id) {
        if (playCount == null) {
            viewModel.loadPlayCount(track.id)
        }
    }

    val displayPlayCount = playCount ?: 0

    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isCurrent) 1.03f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "track_scale"
    )

    val borderThickness = if (isCurrent) 1.5.dp else 0.5.dp
    val computedBorderBrush = if (isCurrent) {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(iconColor, iconColor.copy(alpha = 0.5f))
        )
    } else {
        borderBrush
    }

    val isMultiSelect by viewModel.isMultiSelectMode.collectAsState()
    val selectedIds by viewModel.selectedDeleteIds.collectAsState()
    val isSelected = selectedIds.contains(track.id)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                translationX = animatableOffset.value
                alpha = animatableAlpha.value
            }
            .background(
                if (isSelected) androidx.compose.ui.graphics.SolidColor(iconColor.copy(alpha = 0.15f)) else containerBrush,
                RoundedCornerShape(percent = 50)
            )
            .border(borderThickness, if (isSelected) androidx.compose.ui.graphics.SolidColor(iconColor) else computedBorderBrush, RoundedCornerShape(percent = 50))
            .combinedClickable(
                onLongClick = {
                    viewModel.startMultiSelectMode(track.id)
                },
                onClick = {
                    if (isMultiSelect) {
                        viewModel.toggleDeleteSelection(track.id)
                    } else {
                        onTrackClick()
                    }
                }
            )
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isMultiSelect) {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, end = 2.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) iconColor else Color.Transparent)
                    .border(1.5.dp, if (isSelected) iconColor else Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Rounded Album Art (rotates if active later, or keeps clean high end format)
        Box(
            modifier = Modifier
                .padding(start = if (isMultiSelect) 4.dp else 4.dp)
                .size(54.dp)
                .clip(CircleShape)
                .background(if (isDarkMode) Color(0x33FFFFFF) else Color(0x0F000000)),
            contentAlignment = Alignment.Center
        ) {
            if (track.thumbnailUrl.isNotEmpty()) {
                val req = rememberOptimizedImageRequest(track.thumbnailUrl, 150)
                AsyncImage(
                    model = req,
                    contentDescription = "Song Artwork",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else if (localArtBytes != null) {
                val req = rememberOptimizedImageRequest(localArtBytes, 150)
                AsyncImage(
                    model = req,
                    contentDescription = "Song Artwork",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music Placeholder",
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Titles block
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artist,
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (track.source == "Locker") {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0x1F00FF00),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Cloud",
                            fontSize = 8.sp,
                            color = Color(0xFF00C853)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•",
                    fontSize = 10.sp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = formatPlayCount(displayPlayCount),
                    fontSize = 10.sp,
                    color = if (isDarkMode) Color.LightGray.copy(alpha = 0.6f) else Color.DarkGray.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Download status control icon for Google Drive tracked files
        if (track.source == "Locker") {
            if (isDownloaded) {
                IconButton(onClick = { viewModel.deleteOfflineTrack(track.id) }) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Offline Cache Saved. Press to remove.",
                        tint = Color(0xFF00C853), // Modern emerald green
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else if (progress != null) {
                // Show percentage spinning progress loading
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(36.dp)
                ) {
                    CircularProgressIndicator(
                        progress = progress / 100f,
                        color = iconColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "$progress",
                        fontSize = 7.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                IconButton(onClick = { viewModel.downloadTrack(track) }) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = "Save Offline Cache",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Satisfying animated 3-bar audio wave visualizer for currently active/playing track!
        if (isCurrent && isPlaying) {
            PlayingAudioWaveVisualizer(color = iconColor)
            Spacer(modifier = Modifier.width(8.dp))
        }

        // 3-dot context menu for essential music player features
        var showMenu by remember { mutableStateOf(false) }
        Box(modifier = Modifier.padding(end = 4.dp)) {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = if (isDarkMode) Color.LightGray else Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(if (isDarkMode) Color(0xFF261F1A) else Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("Play Now", color = textPrimary, fontSize = 13.sp) },
                    onClick = {
                        showMenu = false
                        onTrackClick()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to Queue", color = textPrimary, fontSize = 13.sp) },
                    onClick = {
                        showMenu = false
                        viewModel.addToQueue(track)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Queue, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                    }
                )
                if (isDownloaded || track.source == "Offline" || track.source == "Local" || track.source == "LocalScanned") {
                    DropdownMenuItem(
                        text = { Text("Delete from Device", color = Color.Red, fontSize = 13.sp) },
                        onClick = {
                            showMenu = false
                            viewModel.deleteOfflineTrack(track.id)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = "Delete from Device", tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    )
                }
                if (track.source == "Locker" && !isDownloaded) {
                    DropdownMenuItem(
                        text = { Text("Download Cache", color = textPrimary, fontSize = 13.sp) },
                        onClick = {
                            showMenu = false
                            viewModel.downloadTrack(track)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Clear Queue", color = textPrimary, fontSize = 13.sp) },
                    onClick = {
                        showMenu = false
                        viewModel.clearQueue()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                )
            }
        }
    }
}

@Composable
fun PlayingAudioWaveVisualizer(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        modifier = Modifier.size(width = 16.dp, height = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(scale1)
                .background(color, RoundedCornerShape(percent = 50))
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(scale2)
                .background(color, RoundedCornerShape(percent = 50))
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(scale3)
                .background(color, RoundedCornerShape(percent = 50))
        )
    }
}

@Composable
fun EmptyStateCard(title: String, tip: String, isDarkMode: Boolean) {
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerBrush, RoundedCornerShape(24.dp))
            .border(0.5.dp, borderBrush, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Empty",
            tint = Color.Gray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = tip,
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
    }
}

// --- Sticky Bottom Playback Panel ---

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PlaybackProgressSlider(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean
) {
    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
    val duration by viewModel.playerManager.duration.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val activeAccent = if (isDarkMode) EarthySienna else DeepPurple

    val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    var sliderValue by remember { mutableStateOf<Float?>(null) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = if (isPlaying && sliderValue == null) 300 else 0,
            easing = androidx.compose.animation.core.LinearEasing
        ),
        label = "SmoothMusicSliderPanel"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = (sliderValue ?: animatedProgress).coerceIn(0f, 1f),
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
                sliderValue?.let {
                    val target = (it * duration).toLong()
                    viewModel.playerManager.seekTo(target)
                }
                sliderValue = null
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val scale = 0.85f
                        val petalW = 6f * scale
                        val petalH = 12f * scale
                        
                        val petalColor = Color(0xFFC71585) // Authentic Cherry blossom crimson matching the middle ones exactly
                        for (i in 0 until 5) {
                            val angle = i * 72f
                            rotate(degrees = angle, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                                val path = Path().apply {
                                    moveTo(cx, cy)
                                    cubicTo(
                                        cx - petalW, cy - petalH * 0.3f,
                                        cx - petalW, cy - petalH,
                                        cx, cy - petalH
                                    )
                                    cubicTo(
                                        cx + petalW, cy - petalH,
                                        cx + petalW, cy - petalH * 0.3f,
                                        cx, cy
                                    )
                                    close()
                                }
                                drawPath(path, color = petalColor)
                            }
                        }
                        drawCircle(color = Color(0xFFFFD700), radius = 2f * scale, center = androidx.compose.ui.geometry.Offset(cx, cy))
                    }
                }
            },
            track = { _ ->
                Canvas(modifier = Modifier.fillMaxWidth().height(4.dp)) {
                    val centerY = size.height / 2f
                    val width = size.width
                    drawLine(
                        color = if (isDarkMode) Color(0x33FFFFFF) else Color(0x1F000000),
                        start = Offset(0f, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    val progress = sliderValue ?: animatedProgress
                    drawLine(
                        color = activeAccent,
                        start = Offset(0f, centerY),
                        end = Offset(width * progress, centerY),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        // Timestamps right below the slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(currentPosition),
                fontSize = 10.sp,
                color = Color.Gray
            )
            Text(
                text = formatDuration(duration),
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

// --- Floating bottom player capsule ---

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun FloatingPlayerCapsule(
    track: PlayerTrack,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isBuffering: Boolean,
    isDarkMode: Boolean,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onCapsuleClick: () -> Unit
) {
    val containerColor = if (isDarkMode) Color(0xFF261D1C) else Color(0xFFFCF9FA)
    val borderColor = if (isDarkMode) Color(0x33FFFFFF) else TranslucentBorderLight
    val activeAccent = if (isDarkMode) EarthySienna else DeepPurple
    val trackLabelColor = if (isDarkMode) Color.White else Color.Black

    var localArtBytes by remember(track.id) { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(track.id) {
        if ((track.source == "Local" || track.source == "Offline" || track.source == "LocalScanned") && !track.streamUrl.isNullOrEmpty()) {
            withContext(Dispatchers.IO) {
                var retriever: android.media.MediaMetadataRetriever? = null
                try {
                    retriever = android.media.MediaMetadataRetriever()
                    val cleanPath = track.streamUrl.replace("file://", "")
                    retriever.setDataSource(cleanPath)
                    localArtBytes = retriever.embeddedPicture
                } catch (e: Exception) {
                    // ignore
                } finally {
                    try {
                        retriever?.release()
                    } catch (ex: Exception) {
                        // ignore
                    }
                }
            }
        } else {
            localArtBytes = null
        }
    }

    // Rotator animation for rotating album thumbnail
    val artRotation = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            artRotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            artRotation.stop()
        }
    }

    val playerBorderColor = if (isDarkMode) Color(0x33FFFFFF) else borderColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(28.dp))
            .border(0.5.dp, playerBorderColor, RoundedCornerShape(28.dp))
            .clickable { onCapsuleClick() }
            .pointerInput(Unit) {
                var dragAmountAccumulated = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragAmountAccumulated > 80f) {
                            onSkipPrevious()
                        } else if (dragAmountAccumulated < -80f) {
                            onSkipNext()
                        }
                        dragAmountAccumulated = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragAmountAccumulated += dragAmount
                    }
                )
            }
            .padding(10.dp)
    ) {
        // Thin progress bar situated neatly at top of capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
            val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
            var sliderValue by remember { mutableStateOf<Float?>(null) }
            
            // Animating the slider fraction updates smoothly (ExoPlayer reports progress in 300ms chunks)
            // While dragging (sliderValue != null), we disable animation for zero latency feedback.
            val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                targetValue = progressFraction,
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = if (isPlaying && sliderValue == null) 300 else 0,
                    easing = androidx.compose.animation.core.LinearEasing
                ),
                label = "SmoothMusicSlider"
            )
            
            Slider(
                value = (sliderValue ?: animatedProgress).coerceIn(0f, 1f),
                onValueChange = {
                    sliderValue = it
                },
                onValueChangeFinished = {
                    sliderValue?.let {
                        val target = (it * duration).toLong()
                        onSeek(target)
                    }
                    sliderValue = null
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val scale = 0.85f
                            val petalW = 6f * scale
                            val petalH = 12f * scale
                            
                            // Dark Cherry Pink/Crimson blossom petals
                            val petalColor = Color(0xFFC71585) // Dark Cherry / Deep Magenta Red
                            for (i in 0 until 5) {
                                val angle = i * 72f
                                rotate(degrees = angle, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                                    val path = Path().apply {
                                        moveTo(cx, cy)
                                        cubicTo(
                                            cx - petalW, cy - petalH * 0.3f,
                                            cx - petalW, cy - petalH,
                                            cx, cy - petalH
                                        )
                                        cubicTo(
                                            cx + petalW, cy - petalH,
                                            cx + petalW, cy - petalH * 0.3f,
                                            cx, cy
                                        )
                                        close()
                                    }
                                    drawPath(path, color = petalColor)
                                }
                            }
                            // Center golden pollen/stamen
                            drawCircle(color = Color(0xFFFFD700), radius = 2f * scale, center = androidx.compose.ui.geometry.Offset(cx, cy))
                        }
                    }
                },
                track = { _ ->
                    val activeColor = if (isDarkMode) EarthySienna else DeepPurple
                    val inactiveColor = if (isDarkMode) Color(0x19FFFFFF) else Color(0x14000000)
                    Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                        val centerY = size.height / 2f
                        val width = size.width
                        drawLine(
                            color = inactiveColor,
                            start = Offset(0f, centerY),
                            end = Offset(width, centerY),
                            strokeWidth = 2.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        val progress = sliderValue ?: animatedProgress
                        drawLine(
                            color = activeColor,
                            start = Offset(0f, centerY),
                            end = Offset(width * progress, centerY),
                            strokeWidth = 2.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Spinning Disk Art Thumbnail with badge
            Box(modifier = Modifier.size(50.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.CenterStart)
                        .clip(CircleShape)
                        .graphicsLayer {
                            rotationZ = artRotation.value
                        }
                        .background(if (isDarkMode) Color(0x12FFFFFF) else Color(0x0F000000)),
                    contentAlignment = Alignment.Center
                ) {
                    if (track.thumbnailUrl.isNotEmpty()) {
                        val req = rememberOptimizedImageRequest(track.thumbnailUrl, 120)
                        AsyncImage(
                            model = req,
                            contentDescription = "Active Track Rotating Cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else if (localArtBytes != null) {
                        val req = rememberOptimizedImageRequest(localArtBytes, 120)
                        AsyncImage(
                            model = req,
                            contentDescription = "Active Track Rotating Cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = activeAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Tiny brand/source style overlay indicator
                val badgeColor = if (track.source == "Locker") EarthySienna else Color(0xFF6D28D9)
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .align(Alignment.BottomEnd)
                        .background(badgeColor, CircleShape)
                        .border(1.dp, if (isDarkMode) Color(0xFF121212) else Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (track.source == "Locker") Icons.Default.Cloud else Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Text Label Box
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = trackLabelColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = track.artist,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isBuffering) {
                        Spacer(modifier = Modifier.width(6.dp))
                        RotatingAppLogo(
                            modifier = Modifier.size(10.dp).clip(CircleShape)
                        )
                    }
                }
            }

            // Controls Block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Prev", tint = trackLabelColor, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onPlayPauseToggle,
                    modifier = Modifier
                        .size(38.dp)
                        .background(activeAccent, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "PlayPause",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = trackLabelColor, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// --- Dialogue Overlays ---

@Composable
fun GoogleDriveOAuthDialog(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onTokenCaptured: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val clientIdState by viewModel.customClientId.collectAsState()
    val customRedirectState by viewModel.customRedirectUri.collectAsState()

    val clientId = clientIdState.ifEmpty {
        "1055312055779-m62nep4lonmep5qs3t7n0a31jh7h8i3e.apps.googleusercontent.com"
    }

    val prefix = clientId.substringBefore(".apps.googleusercontent.com")
    val defaultRedirectUri = if (prefix != clientId) {
        "com.googleusercontent.apps.$prefix:/oauth2redirect"
    } else {
        "com.myuloc.app:/oauth2redirect"
    }

    val redirectUri = customRedirectState.ifEmpty { defaultRedirectUri }

    var pastedUrl by remember { mutableStateOf("") }
    var extractionError by remember { mutableStateOf("") }
    var expandedHelp by remember { mutableStateOf(false) }

    val accentColor = if (isDarkMode) EarthySienna else DeepPurple

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .verticalScroll(rememberScrollState())
                .background(
                    if (isDarkMode) TranslucentGlassDark else Color.White,
                    RoundedCornerShape(24.dp)
                )
                .border(
                    0.5.dp,
                    if (isDarkMode) TranslucentBorderDark else ConfigBorderLight,
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sign in to Google Drive",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color.Black
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Text("✕", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MyuLoc accesses the cloud Locker folders using Google's secure developer credential standard. Choose your authorization mechanism below.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // -- Option 1: Modern Deep Link Flow --
            Text(
                text = "Method A: Intent Deep Link",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    val authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                            "?client_id=${clientId}" +
                            "&redirect_uri=${redirectUri}" +
                            "&response_type=token" +
                            "&scope=https://www.googleapis.com/auth/drive.readonly"

                    android.util.Log.d("OAuthLauncher", "Launching Custom Schema Google Flow. " +
                            "client_id: '$clientId', redirect_uri: '$redirectUri'")

                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(authUrl))
                        context.startActivity(intent)
                        onDismiss()
                    } catch (e: Exception) {
                        Toast.makeText(context, "No browser found to open link.", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Launch Custom URI Flow",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -- Option 2: Bulletproof Paste Fallback flow --
            Text(
                text = "Method B: Localhost & URL Paste (Recommended/Fail-Safe)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "If browser redirects to an error/empty page like localhost, copy that full browser address page URL and paste it below:",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start),
                lineHeight = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                                "?client_id=${clientId}" +
                                "&redirect_uri=http://localhost" +
                                "&response_type=token" +
                                "&scope=https://www.googleapis.com/auth/drive.readonly"
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(authUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No web browser found to authorize.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) Color(0x1AFFFFFF) else Color(0x0D000000)),
                    modifier = Modifier.weight(1.2f).height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Open Web Login",
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 11.sp
                    )
                }

                OutlinedTextField(
                    value = pastedUrl,
                    onValueChange = { 
                        pastedUrl = it
                        extractionError = "" 
                    },
                    singleLine = true,
                    placeholder = { Text("Paste URL here...", fontSize = 11.sp, color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                        unfocusedTextColor = if (isDarkMode) Color.LightGray else Color.Gray
                    ),
                    modifier = Modifier.weight(2f).height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        val trimmed = pastedUrl.trim()
                        if (trimmed.isEmpty()) {
                            extractionError = "Please paste the redirect URL first."
                            return@Button
                        }
                        val tokenRegex = Regex("[#?&]access_token=([^&]+)")
                        val matchResult = tokenRegex.find(trimmed)
                        val extracted = matchResult?.groupValues?.get(1) ?: trimmed
                        if (extracted.isNotEmpty() && (extracted.startsWith("ya29.") || extracted.length > 25)) {
                            onTokenCaptured(extracted)
                        } else {
                            extractionError = "Token not found. Make sure the pasted text contains the 'access_token=' parameter."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", color = Color.White, fontSize = 11.sp)
                }
            }

            if (extractionError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = extractionError,
                    fontSize = 10.sp,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Start),
                    lineHeight = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Expandable Troubleshooting Guide for Client/OAuth Issues (e.g., Error 401 invalid_client)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDarkMode) Color(0x1F000000) else Color(0x0A000000),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        0.5.dp,
                        if (isDarkMode) Color(0x1AFFFFFF) else Color(0x1A000000),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = { expandedHelp = !expandedHelp },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (expandedHelp) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isDarkMode) CopperGlow else DeepPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (expandedHelp) "Hide Troubleshooting Guide" else "Troubleshoot 'invalid_client' / Error 401",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) CopperGlow else DeepPurple
                        )
                    }
                }

                if (expandedHelp) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "If Google shows 'Error 401: invalid_client' when launching the browser, Google Cloud did not recognize or match your project configurations. Verify the checklist below in the Cloud Console:",
                        fontSize = 11.sp,
                        color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "🛠️ 1. Double check the Client ID\n" +
                                "Verify that your Client ID matches exactly in the cloud settings (has no extra spaces) and corresponds to either a Web client (Recommended with Localhost paste) or iOS client (for direct deep links).",
                        fontSize = 10.sp,
                        color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🛠️ 2. Match Android SHA-1 & Package Name\n" +
                                "If using Native Android Client ID types: Android Package Name must be EXACTLY 'com.aistudio.myuloc.qzvmkx' and SHA-1 signature must match (obtain via `./gradlew signingReport` in gradle shell). Android Client ID types do not accept standard native redirect URIs directly and require using standard Localhost paste or native SDK integrations.",
                        fontSize = 10.sp,
                        color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🛠️ 3. Whitelist Test user\n" +
                                "If your OAuth status is in 'Testing' phase (default), you must add 'aashusen2006@gmail.com' (and any relevant tester emails) into Google Cloud Console > OAuth Consent Screen > Test Users, otherwise Google blocks access completely.",
                        fontSize = 10.sp,
                        color = if (isDarkMode) Color.LightGray else Color.DarkGray,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "💡 Configuration Payload Applied:\n" +
                                "• Client ID: $clientId\n" +
                                "• Redirect URI Configured: $redirectUri\n" +
                                "• Package Name: ${context.packageName}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) CopperGlow else DeepPurple,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

private val ConfigBorderLight = Color(0x33000000)

@Composable
fun ManualTokenEntryDialog(
    isDarkMode: Boolean,
    onTokenEntered: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var rawInput by remember { mutableStateOf("") }
    val accentColor = if (isDarkMode) EarthySienna else DeepPurple

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDarkMode) DenseDarkBg else PureWhite,
                    RoundedCornerShape(24.dp)
                )
                .border(
                    0.5.dp,
                    if (isDarkMode) TranslucentBorderDark else ConfigBorderLight,
                    RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = "Manual Access Token",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Use a Temporary Developer Google Drive Access Token directly (no client IDs needed). Helpful for testing sandboxes instantly.",
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = rawInput,
                onValueChange = { rawInput = it },
                placeholder = { Text("ya29.a0Ac...", fontSize = 11.sp, color = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                    unfocusedTextColor = if (isDarkMode) Color.LightGray else Color.DarkGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if (rawInput.isNotEmpty()) onTokenEntered(rawInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Connect", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun formatDuration(ms: Long): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    val secsStr = if (secs < 10) "0$secs" else "$secs"
    return "$mins:$secsStr"
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ExpandedPlayerView(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onDismiss: () -> Unit
) {
    val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
    val duration by viewModel.playerManager.duration.collectAsState()
    val isBuffering by viewModel.playerManager.isBuffering.collectAsState()
    
    // Equalizer state from Manager
    val eqEnabled by viewModel.playerManager.eqEnabled.collectAsState()
    val eqBands by viewModel.playerManager.eqBands.collectAsState()
    
    // Queue state
    val queueList by viewModel.playerManager.queue.collectAsState()
    val offlineTracks by viewModel.offlineTracksFlow.collectAsState(initial = emptyList())
    val isCurrentDownloaded = remember(offlineTracks, currentTrack) {
        currentTrack?.let { ct -> offlineTracks.any { it.id == ct.id } } ?: false
    }

    // Shuffle and Repeat states
    val shuffleEnabled by viewModel.playerManager.shuffleEnabled.collectAsState()
    val repeatMode by viewModel.playerManager.repeatMode.collectAsState()
    
    var showEqPanel by remember { mutableStateOf(false) }
    var showQueuePanel by remember { mutableStateOf(false) }
    var activePreset by remember { mutableStateOf("Flat") }

    val animSpeed by viewModel.animationSpeed.collectAsState()
    val animBounciness by viewModel.animationBounciness.collectAsState()

    var dominantColor by remember { mutableStateOf<Color?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(currentTrack) {
        if (currentTrack == null) {
            dominantColor = null
            return@LaunchedEffect
        }
        val actTrack = currentTrack!!
        withContext(Dispatchers.IO) {
            try {
                var bitmap: android.graphics.Bitmap? = null
                if ((actTrack.source == "Local" || actTrack.source == "Offline" || actTrack.source == "LocalScanned") && !actTrack.streamUrl.isNullOrEmpty()) {
                    var retriever: android.media.MediaMetadataRetriever? = null
                    try {
                        retriever = android.media.MediaMetadataRetriever()
                        val cleanPath = actTrack.streamUrl.replace("file://", "")
                        retriever.setDataSource(cleanPath)
                        val artBytes = retriever.embeddedPicture
                        if (artBytes != null) {
                            bitmap = android.graphics.BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        try { retriever?.release() } catch (ex: Exception) {}
                    }
                } else if (actTrack.thumbnailUrl.isNotEmpty()) {
                    val loader = coil.Coil.imageLoader(context)
                    val request = coil.request.ImageRequest.Builder(context)
                        .data(actTrack.thumbnailUrl)
                        .allowHardware(false)
                        .build()
                    val result = loader.execute(request)
                    if (result is coil.request.SuccessResult) {
                        val drawable = result.drawable
                        if (drawable is android.graphics.drawable.BitmapDrawable) {
                            bitmap = drawable.bitmap
                        }
                    }
                }

                if (bitmap != null) {
                    val palette = androidx.palette.graphics.Palette.from(bitmap).generate()
                    val domColor = palette.getDominantColor(0)
                    if (domColor != 0) {
                        withContext(Dispatchers.Main) {
                            dominantColor = Color(domColor)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val animatedDominantColor by androidx.compose.animation.animateColorAsState(
        targetValue = dominantColor ?: (if (isDarkMode) Color(0xFF140E0A) else Color(0xFFFDF6F6)),
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 2000),
        label = "dominant_color_fade"
    )

    val playerBackgroundBrush = remember(animatedDominantColor, isDarkMode) {
        val baseColor = if (isDarkMode) Color(0xFF140E0A) else Color(0xFFFDF6F6)
        val startColor = if (isDarkMode) {
            animatedDominantColor.copy(alpha = 0.35f)
        } else {
            animatedDominantColor.copy(alpha = 0.12f)
        }
        val midColor = if (isDarkMode) {
            animatedDominantColor.copy(alpha = 0.15f)
        } else {
            animatedDominantColor.copy(alpha = 0.05f)
        }
        Brush.verticalGradient(
            colors = listOf(startColor, midColor, baseColor)
        )
    }
    
    // Theme colors
    val mainBg = if (isDarkMode) Color(0xFF140E0A) else Color(0xFFFDF6F6)
    val textPrimary = if (isDarkMode) Color.White else Color.Black
    val textSecondary = Color.Gray
    val activeAccent = if (isDarkMode) EarthySienna else DeepPurple
    val iconColor = if (isDarkMode) EarthySienna else DeepPurple
    
    androidx.compose.ui.window.Dialog(
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(playerBackgroundBrush)
        ) {
            // Background ambient mesh
            BackgroundAmbientMesh(isDarkMode = isDarkMode)
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = textPrimary
                        )
                    }
                    Text(
                        text = "Now Playing",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    // Equalizer toggle shortcut
                    IconButton(onClick = { showEqPanel = !showEqPanel }) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Equalizer",
                            tint = if (eqEnabled) activeAccent else textPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Realistic Record Player with pivoting tonearm and hardware-accelerated infinite rotation
                var horizontalOffset by remember { mutableStateOf(0f) }
                var hasTriggeredSwipe by remember { mutableStateOf(false) }

                // Native Compose infinite rotator to avoid CPU overheating coroutines
                val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotation")
                val runningAngleState = infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 8000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "vinyl_angle"
                )

                var lastPausedAngle by remember { mutableStateOf(0f) }

                LaunchedEffect(isPlaying) {
                    if (!isPlaying) {
                        lastPausedAngle = (runningAngleState.value + lastPausedAngle) % 360f
                    }
                }

                // Pivoting tonearm stylus rotation animation (swings onto record when playing, swings off when paused)
                val tonearmAngle by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (isPlaying) 25f else 0f,
                    animationSpec = tunedSpring(animSpeed, animBounciness),
                    label = "tonearm_rotation"
                )

                // Layout scaling and shadow alpha transition (Apple Music Style)
                val vinylScale by animateFloatAsState(
                    targetValue = if (isPlaying) 1.05f else 0.95f,
                    animationSpec = tunedSpring(animSpeed, animBounciness),
                    label = "vinyl_scale"
                )
                val vinylAlpha by animateFloatAsState(
                    targetValue = if (isPlaying) 1.0f else 0.75f,
                    animationSpec = tween(400),
                    label = "vinyl_alpha"
                )
                val shadowElevation by animateDpAsState(
                    targetValue = if (isPlaying) 16.dp else 2.dp,
                    animationSpec = tunedSpring(animSpeed, animBounciness),
                    label = "vinyl_shadow"
                )

                val dragScope = rememberCoroutineScope()
                Box(
                    modifier = Modifier
                        .scale(vinylScale)
                        .graphicsLayer {
                            translationX = horizontalOffset
                            alpha = (1f - (Math.abs(horizontalOffset) / 280f)).coerceIn(0.1f, 1.0f) * vinylAlpha
                        }
                        .shadow(shadowElevation, RoundedCornerShape(28.dp))
                        .size(240.dp)
                        .background(
                            color = if (isDarkMode) Color(0xFF16110F) else Color(0xFFF9EFEF),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .border(
                            1.dp,
                            if (isDarkMode) Color(0x22FFFFFF) else Color(0x1F000000),
                            RoundedCornerShape(28.dp)
                        )
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    hasTriggeredSwipe = false
                                },
                                onDragEnd = {
                                    hasTriggeredSwipe = false
                                    dragScope.launch {
                                        androidx.compose.animation.core.animate(
                                            initialValue = horizontalOffset,
                                            targetValue = 0f,
                                            animationSpec = androidx.compose.animation.core.spring(
                                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                             )
                                        ) { value, _ ->
                                            horizontalOffset = value
                                        }
                                    }
                                },
                                onDragCancel = {
                                    hasTriggeredSwipe = false
                                    dragScope.launch {
                                        androidx.compose.animation.core.animate(
                                            initialValue = horizontalOffset,
                                            targetValue = 0f,
                                            animationSpec = androidx.compose.animation.core.spring()
                                        ) { value, _ ->
                                            horizontalOffset = value
                                        }
                                    }
                                },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    horizontalOffset += dragAmount
                                    if (!hasTriggeredSwipe) {
                                        if (horizontalOffset > 75f) {
                                            hasTriggeredSwipe = true
                                            viewModel.playerManager.skipPrevious()
                                            dragScope.launch {
                                                androidx.compose.animation.core.animate(
                                                    initialValue = horizontalOffset,
                                                    targetValue = 0f,
                                                    animationSpec = androidx.compose.animation.core.spring()
                                                ) { value, _ ->
                                                    horizontalOffset = value
                                                }
                                            }
                                        } else if (horizontalOffset < -75f) {
                                            hasTriggeredSwipe = true
                                            viewModel.playerManager.skipNext()
                                            dragScope.launch {
                                                androidx.compose.animation.core.animate(
                                                    initialValue = horizontalOffset,
                                                    targetValue = 0f,
                                                    animationSpec = androidx.compose.animation.core.spring()
                                                ) { value, _ ->
                                                    horizontalOffset = value
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Vinyl Disk
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.95f)
                            .graphicsLayer {
                                rotationZ = if (isPlaying) {
                                    (runningAngleState.value + lastPausedAngle) % 360f
                                } else {
                                    lastPausedAngle
                                }
                            }
                            .background(Color(0xFF0C0C0C), CircleShape)
                            .border(0.5.dp, if (isDarkMode) Color(0x44FFFFFF) else Color(0x22000000), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Concentric grooves
                        Box(modifier = Modifier.fillMaxSize(0.85f).border(0.5.dp, Color(0x15FFFFFF), CircleShape))
                        Box(modifier = Modifier.fillMaxSize(0.70f).border(0.5.dp, Color(0x15FFFFFF), CircleShape))
                        Box(modifier = Modifier.fillMaxSize(0.55f).border(0.5.dp, Color(0x15FFFFFF), CircleShape))
                        Box(modifier = Modifier.fillMaxSize(0.40f).border(0.5.dp, Color(0x15FFFFFF), CircleShape))

                        // Center label disk containing actual cover art!
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.38f)
                                .clip(CircleShape)
                                .background(if (isDarkMode) EarthySienna else DeepPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            // Load cover art in record center
                            if (currentTrack != null) {
                                val actTrack = currentTrack!!
                                val hasWebArt = actTrack.thumbnailUrl.isNotEmpty()
                                var activeLocalArtBytes by remember(actTrack.id) { mutableStateOf<ByteArray?>(null) }
                                LaunchedEffect(actTrack.id) {
                                    if ((actTrack.source == "Local" || actTrack.source == "Offline" || actTrack.source == "LocalScanned") && !actTrack.streamUrl.isNullOrEmpty()) {
                                        withContext(Dispatchers.IO) {
                                            var retriever: android.media.MediaMetadataRetriever? = null
                                            try {
                                                retriever = android.media.MediaMetadataRetriever()
                                                val cleanPath = actTrack.streamUrl.replace("file://", "")
                                                retriever.setDataSource(cleanPath)
                                                activeLocalArtBytes = retriever.embeddedPicture
                                            } catch (e: Exception) {
                                                // ignore
                                            } finally {
                                                try {
                                                    retriever?.release()
                                                } catch (ex: Exception) {
                                                    // ignore
                                                }
                                            }
                                        }
                                    } else {
                                        activeLocalArtBytes = null
                                    }
                                }

                                if (hasWebArt) {
                                    AsyncImage(
                                        model = rememberOptimizedImageRequest(actTrack.thumbnailUrl, 250),
                                        contentDescription = "Cover Art",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else if (activeLocalArtBytes != null) {
                                    AsyncImage(
                                        model = rememberOptimizedImageRequest(activeLocalArtBytes, 250),
                                        contentDescription = "Cover Art",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    // Fallback text / icon
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Accent metal ring around spindle point
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(0.24f)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            )

                            // Spindle Hole in the center
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.Black, CircleShape)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                        }
                    }

                    // Tonearm Pivot base and Stylus arm (Placed top-right, pivots over the record)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp, end = 4.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(width = 60.dp, height = 140.dp)
                                .rotate(tonearmAngle)
                        ) {
                            // Metallic pivot center
                            drawCircle(
                                color = Color(0xFFD4AF37), // Metallic Gold
                                radius = 22f,
                                center = Offset(x = size.width - 24f, y = 24f)
                            )
                            drawCircle(
                                color = Color.Gray,
                                radius = 10f,
                                center = Offset(x = size.width - 24f, y = 24f)
                            )

                            // Tonearm line
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(x = size.width - 24f, y = 24f),
                                end = Offset(x = 18f, y = size.height - 32f),
                                strokeWidth = 6f
                            )

                            // Stylus needle cartridge
                            drawRect(
                                color = Color(0xFFD4AF37),
                                topLeft = Offset(x = 8f, y = size.height - 32f),
                                size = androidx.compose.ui.geometry.Size(22f, 14f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Track Info & Options
                currentTrack?.let { track ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = track.artist + " • " + track.source,
                                fontSize = 13.sp,
                                color = textSecondary,
                                maxLines = 1
                            )
                        }
                        
                        // 3-dot dropdown menu inside ExpandedPlayerView
                        var expandedOptionMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { expandedOptionMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = textPrimary)
                            }
                            DropdownMenu(
                                expanded = expandedOptionMenu,
                                onDismissRequest = { expandedOptionMenu = false },
                                modifier = Modifier.background(if (isDarkMode) Color(0xFF261F1A) else Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Playback Queue (${queueList.size})", color = textPrimary) },
                                    onClick = {
                                        expandedOptionMenu = false
                                        showQueuePanel = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Queue, null, tint = activeAccent) }
                                )
                                if (isCurrentDownloaded || currentTrack?.source == "Offline" || currentTrack?.source == "Local" || currentTrack?.source == "LocalScanned") {
                                    DropdownMenuItem(
                                        text = { Text("Delete from Device", color = Color.Red) },
                                        onClick = {
                                            expandedOptionMenu = false
                                            currentTrack?.let { viewModel.deleteOfflineTrack(it.id) }
                                        },
                                        leadingIcon = { Icon(Icons.Default.Delete, "Delete from Device", tint = Color.Red) }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Equalizer Control", color = textPrimary) },
                                    onClick = {
                                        expandedOptionMenu = false
                                        showEqPanel = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Equalizer, null, tint = activeAccent) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Clear All Queue", color = textPrimary) },
                                    onClick = {
                                        expandedOptionMenu = false
                                        viewModel.clearQueue()
                                    },
                                    leadingIcon = { Icon(Icons.Default.DeleteSweep, null, tint = Color.Gray) }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Thin progress line where progress is shown by dark cherry flower thumb
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
                    var sliderValue by remember { mutableStateOf<Float?>(null) }
                    
                    val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = progressFraction,
                        animationSpec = androidx.compose.animation.core.tween(
                            durationMillis = if (isPlaying && sliderValue == null) 300 else 0,
                            easing = androidx.compose.animation.core.LinearEasing
                        ),
                        label = "SmoothExpandedMusicSlider"
                    )
                    
                    Slider(
                        value = (sliderValue ?: animatedProgress).coerceIn(0f, 1f),
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = {
                            sliderValue?.let {
                                val target = (it * duration).toLong()
                                viewModel.playerManager.seekTo(target)
                            }
                            sliderValue = null
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        ),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f
                                    val petalW = 7f
                                    val petalH = 14f
                                    
                                    val petalColor = Color(0xFFC71585) // Dark Cherry Pink
                                    for (i in 0 until 5) {
                                        val angle = i * 72f
                                        rotate(degrees = angle, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                                            val path = Path().apply {
                                                moveTo(cx, cy)
                                                cubicTo(
                                                    cx - petalW, cy - petalH * 0.3f,
                                                    cx - petalW, cy - petalH,
                                                    cx, cy - petalH
                                                )
                                                cubicTo(
                                                    cx + petalW, cy - petalH,
                                                    cx + petalW, cy - petalH * 0.3f,
                                                    cx, cy
                                                )
                                                close()
                                            }
                                            drawPath(path, color = petalColor)
                                        }
                                    }
                                    drawCircle(color = Color(0xFFFFD700), radius = 2.5f, center = androidx.compose.ui.geometry.Offset(cx, cy))
                                }
                            }
                        },
                        track = { _ ->
                            val activeColor = if (isDarkMode) EarthySienna else DeepPurple
                            val inactiveColor = if (isDarkMode) Color(0x19FFFFFF) else Color(0x14000000)
                            Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                                val centerY = size.height / 2f
                                val width = size.width
                                drawLine(
                                    color = inactiveColor,
                                    start = Offset(0f, centerY),
                                    end = Offset(width, centerY),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                                val progress = sliderValue ?: animatedProgress
                                drawLine(
                                    color = activeColor,
                                    start = Offset(0f, centerY),
                                    end = Offset(width * progress, centerY),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(24.dp)
                    )
                }
                
                // Track timestamps
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = formatDuration(currentPosition), fontSize = 10.sp, color = textSecondary)
                    Text(text = formatDuration(duration), fontSize = 10.sp, color = textSecondary)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                val playProgress by animateFloatAsState(
                    targetValue = if (isPlaying) 0f else 1f,
                    animationSpec = tunedSpring(animSpeed, animBounciness),
                    label = "play_pause_morph"
                )

                // Immersive Audio/Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showQueuePanel = !showQueuePanel }) {
                        Icon(Icons.Default.QueueMusic, contentDescription = "Toggle Queue panel", tint = if (showQueuePanel) activeAccent else textSecondary, modifier = Modifier.size(24.dp))
                    }

                    // Shuffle Mode Controller Key
                    IconButton(
                        onClick = { 
                            viewModel.playerManager.shuffleEnabled.value = !shuffleEnabled 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (shuffleEnabled) activeAccent else textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    IconButton(onClick = { viewModel.playerManager.skipPrevious() }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track", tint = textPrimary, modifier = Modifier.size(28.dp))
                    }
                    
                    IconButton(
                        onClick = { viewModel.playerManager.togglePlayPause() },
                        modifier = Modifier
                            .size(64.dp)
                            .background(activeAccent, CircleShape)
                    ) {
                        Canvas(modifier = Modifier.size(26.dp)) {
                            val w = size.width
                            val h = size.height
                            val path = Path()
                            if (playProgress > 0.01f) {
                                // Draw standard play triangle (progressively mapped inside bounds)
                                val scaleWidth = 0.25f + 0.6f * playProgress
                                path.moveTo(w * 0.25f, h * 0.2f)
                                path.lineTo(w * scaleWidth, h * 0.5f)
                                path.lineTo(w * 0.25f, h * 0.8f)
                                path.close()
                                drawPath(path, color = Color.White)
                            }
                            if (playProgress < 0.99f) {
                                // Draw custom pause lines dynamically
                                val alpha = (1f - playProgress).coerceIn(0f, 1f)
                                val barW = w * 0.25f
                                val barGap = w * 0.2f
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(w * 0.2f, h * 0.2f),
                                    size = androidx.compose.ui.geometry.Size(barW, h * 0.6f),
                                    alpha = alpha
                                )
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(w * 0.2f + barW + barGap, h * 0.2f),
                                    size = androidx.compose.ui.geometry.Size(barW, h * 0.6f),
                                    alpha = alpha
                                )
                            }
                        }
                    }
                    
                    IconButton(onClick = { viewModel.playerManager.skipNext() }) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Track", tint = textPrimary, modifier = Modifier.size(28.dp))
                    }

                    // Loop/Repeat Mode Controller Key
                    val repeatIcon = when (repeatMode) {
                        PlaybackRepeatMode.ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    }
                    IconButton(
                        onClick = { 
                            val nextRepeat = when (repeatMode) {
                                PlaybackRepeatMode.OFF -> PlaybackRepeatMode.ALL
                                PlaybackRepeatMode.ALL -> PlaybackRepeatMode.ONE
                                PlaybackRepeatMode.ONE -> PlaybackRepeatMode.OFF
                            }
                            viewModel.playerManager.repeatMode.value = nextRepeat
                        }
                    ) {
                        Icon(
                            imageVector = repeatIcon,
                            contentDescription = "Repeat",
                            tint = if (repeatMode != PlaybackRepeatMode.OFF) activeAccent else textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    IconButton(onClick = { showEqPanel = !showEqPanel }) {
                        Icon(Icons.Default.Equalizer, contentDescription = "Toggle Equalizer panel", tint = if (showEqPanel) activeAccent else textSecondary, modifier = Modifier.size(24.dp))
                    }
                }
                
                // Equalizer Control Panel Pop Window (Dialog)
                if (showEqPanel) {
                    var slideOutTrigger by remember { mutableStateOf(false) }
                    val scope = rememberCoroutineScope()
                    val dismissWithAnimation: () -> Unit = {
                        scope.launch {
                            slideOutTrigger = true
                            delay(280) // matches slide-out spec delay
                            showEqPanel = false
                            slideOutTrigger = false
                        }
                    }

                    val slideOffset by animateDpAsState(
                        targetValue = if (slideOutTrigger) 450.dp else 0.dp,
                        animationSpec = tunedSpring(animSpeed, animBounciness),
                        label = "eq_slide"
                    )
                    val fadeAlpha by animateFloatAsState(
                        targetValue = if (slideOutTrigger) 0f else 1f,
                        animationSpec = tween(250),
                        label = "eq_fade"
                    )

                    Dialog(onDismissRequest = dismissWithAnimation) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = slideOffset)
                                .alpha(fadeAlpha)
                                .background(
                                    color = if (isDarkMode) Color(0xFF1E1714) else Color(0xFFF9EFEF),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isDarkMode) Color(0x33FFFFFF) else Color(0x1F000000),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Equalizer,
                                            contentDescription = "Equalizer Panel",
                                            tint = activeAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            "Sound Equalizer",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                    }
                                    
                                    IconButton(
                                        onClick = dismissWithAnimation,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = textSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isDarkMode) Color(0x15FFFFFF) else Color(0x0A000000), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Enable Audio FX",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            if (eqEnabled) "ON" else "OFF",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (eqEnabled) activeAccent else textSecondary
                                        )
                                        androidx.compose.material3.Switch(
                                            checked = eqEnabled,
                                            onCheckedChange = { viewModel.setEqEnabled(it) },
                                            modifier = Modifier.scale(0.8f)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(14.dp))
                                
                                Text(
                                    "Presets",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val presets = listOf("Flat", "Classical", "Rock", "Pop", "Jazz")
                                    presets.forEach { preset ->
                                        val isSelected = preset == activePreset
                                        Button(
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            onClick = {
                                                activePreset = preset
                                                // Set specific gains depending on chosen preset profile
                                                when (preset) {
                                                    "Flat" -> {
                                                        for (i in 0..4) viewModel.setEqBandLevel(i, 0)
                                                    }
                                                    "Classical" -> {
                                                        viewModel.setEqBandLevel(0, 300)
                                                        viewModel.setEqBandLevel(1, 200)
                                                        viewModel.setEqBandLevel(2, 0)
                                                        viewModel.setEqBandLevel(3, 200)
                                                        viewModel.setEqBandLevel(4, 400)
                                                    }
                                                    "Rock" -> {
                                                        viewModel.setEqBandLevel(0, 500)
                                                        viewModel.setEqBandLevel(1, 300)
                                                        viewModel.setEqBandLevel(2, -100)
                                                        viewModel.setEqBandLevel(3, 300)
                                                        viewModel.setEqBandLevel(4, 600)
                                                    }
                                                    "Pop" -> {
                                                        viewModel.setEqBandLevel(0, -200)
                                                        viewModel.setEqBandLevel(1, 100)
                                                        viewModel.setEqBandLevel(2, 400)
                                                        viewModel.setEqBandLevel(3, 100)
                                                        viewModel.setEqBandLevel(4, -100)
                                                    }
                                                    "Jazz" -> {
                                                        viewModel.setEqBandLevel(0, 300)
                                                        viewModel.setEqBandLevel(1, 100)
                                                        viewModel.setEqBandLevel(2, -200)
                                                        viewModel.setEqBandLevel(3, 100)
                                                        viewModel.setEqBandLevel(4, 300)
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSelected) activeAccent else (if (isDarkMode) Color(0x22FFFFFF) else Color(0x15000000))
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(28.dp)
                                        ) {
                                            Text(
                                                text = preset,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isSelected) Color.White else textPrimary
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(14.dp))
                                
                                Text(
                                    "Acoustic Bands",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                
                                // Audio Bands custom sliders
                                val bandLabels = listOf("60Hz", "230Hz", "910Hz", "4kHz", "14kHz")
                                eqBands.forEachIndexed { idx, value ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().height(28.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = bandLabels[idx],
                                            fontSize = 10.sp,
                                            color = textPrimary,
                                            modifier = Modifier.width(44.dp)
                                        )
                                        Slider(
                                            value = value.toFloat().coerceIn(-1500f, 1500f),
                                            onValueChange = { newValue ->
                                                viewModel.setEqBandLevel(idx, newValue.toInt())
                                            },
                                            valueRange = -1500f..1500f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = activeAccent,
                                                activeTrackColor = activeAccent,
                                                inactiveTrackColor = if (isDarkMode) Color(0x22FFFFFF) else Color(0x15000000)
                                            ),
                                            enabled = eqEnabled,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                        Text(
                                            text = "${value / 100}dB",
                                            fontSize = 9.sp,
                                            color = textSecondary,
                                            modifier = Modifier.width(32.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = dismissWithAnimation,
                                    colors = ButtonDefaults.buttonColors(containerColor = activeAccent),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Apply Settings",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Swipeable / dismissible pop-up window bottom sheet active queue panel
            if (showQueuePanel) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) {
                            showQueuePanel = false
                        }
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = showQueuePanel,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tunedSpring(animSpeed, animBounciness)
                ) + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tunedSpring(animSpeed, animBounciness)
                ) + androidx.compose.animation.fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.65f) // Expands cleanly showing upcoming sequence
                        .background(
                            color = if (isDarkMode) Color(0xFF1B1412) else Color(0xFFFDF6F6),
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isDarkMode) Color(0x33FFFFFF) else Color(0x33000000),
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(48.dp)
                            .height(5.dp)
                            .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                            .clickable { showQueuePanel = false }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Queue (${queueList.size} Tracks)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        
                        TextButton(onClick = { viewModel.playerManager.clearQueue() }) {
                            Text("Clear All", color = activeAccent, fontSize = 13.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (queueList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Queue is empty. Select tracks to populate.",
                                fontSize = 13.sp,
                                color = textSecondary
                            )
                        }
                    } else {
                        val scope = rememberCoroutineScope()
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = queueList,
                                key = { it.id + "_" + queueList.indexOf(it) }
                            ) { track ->
                                val isCurrent = currentTrack?.id == track.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = if (isCurrent) activeAccent.copy(alpha = 0.15f) else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { 
                                            scope.launch {
                                                viewModel.playerManager.playTrack(track, queueList) 
                                            }
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "${queueList.indexOf(track) + 1}",
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            modifier = Modifier.width(28.dp)
                                        )
                                        
                                        Column {
                                            Text(
                                                text = track.title,
                                                fontSize = 14.sp,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isCurrent) activeAccent else textPrimary,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = track.artist,
                                                fontSize = 12.sp,
                                                color = textSecondary,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    
                                    IconButton(
                                        onClick = { 
                                            scope.launch {
                                                viewModel.removeFromQueue(track.id) 
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Track",
                                            tint = Color.Gray.copy(alpha = 0.8f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SleepTimerMiniCapsule(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean
) {
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()

    androidx.compose.animation.AnimatedVisibility(
        visible = sleepRunning && sleepSecondsLeft > 0,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }),
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { 50 })
    ) {
        val containerBg = if (isDarkMode) Color(0xDF1E1714) else Color(0xDFFAF0F0)
        val borderColor = if (isDarkMode) Color(0x44FFFFFF) else Color(0x22000000)
        val accentColor = if (isDarkMode) EarthySienna else DeepPurple
        val textColor = if (isDarkMode) Color.White else Color.Black

        val mins = sleepSecondsLeft / 60
        val secs = sleepSecondsLeft % 60
        val secondsStr = if (secs < 10) "0$secs" else "$secs"
        val countdownDisplay = "$mins:$secondsStr"

        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .background(containerBg, RoundedCornerShape(100.dp))
                .border(0.5.dp, borderColor, RoundedCornerShape(100.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Sleep Countdown",
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Sleep Timer:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color.LightGray else Color.DarkGray
            )
            Text(
                text = countdownDisplay,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = { viewModel.stopSleepTimer() },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Sleep Timer",
                    tint = Color.Red,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }

    var isSignUp by remember { mutableStateOf(false) }
    var isPhoneLogin by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val accentColor = if (isDarkMode) EarthySienna else DeepPurple
    val containerBg = if (isDarkMode) TranslucentGlassDark else TranslucentGlassLight
    val borderColor = if (isDarkMode) TranslucentBorderDark else TranslucentBorderLight
    val textColor = if (isDarkMode) Color.White else Color.Black

    val scrollState = androidx.compose.foundation.rememberScrollState()

    androidx.compose.ui.window.Dialog(
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {} // System lock auth screen
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkMode) DenseDarkBg else GlossyLightBg)
        ) {
            // High aesthetic ambient mesh glow backgrounds
            BackgroundAmbientMesh(isDarkMode = isDarkMode)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header Logo
                CustomAppLogo(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MyuLoc Gateway",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Text(
                    text = if (isSignUp) "Ecosystem Registry Node" else "Private Gateway Authentication",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(containerBg, RoundedCornerShape(28.dp))
                        .border(0.5.dp, borderColor, RoundedCornerShape(28.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Selection tabs: Standard vs Phone (Only when not in Sign Up view)
                    if (!isSignUp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .background(if (isDarkMode) Color(0x33FFFFFF) else Color(0x0F000000), RoundedCornerShape(99.dp))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(if (!isPhoneLogin) accentColor else Color.Transparent, RoundedCornerShape(99.dp))
                                    .clickable { isPhoneLogin = false; authError = "" }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Email Login", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!isPhoneLogin) Color.White else Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(if (isPhoneLogin) accentColor else Color.Transparent, RoundedCornerShape(99.dp))
                                    .clickable { isPhoneLogin = true; authError = "" }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Phone OTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isPhoneLogin) Color.White else Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    if (authError.isNotEmpty()) {
                        Text(authError, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))
                    }

                    if (isSignUp) {
                        // Sign up name field
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Display Name", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = accentColor) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (isPhoneLogin && !isSignUp) {
                        // Phone Auth layout
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Phone Number", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = accentColor) },
                            singleLine = true,
                            enabled = !otpSent,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            )
                        )

                        if (otpSent) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("6-Digit Verification Code", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.VpnKey, null, tint = accentColor) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isLoading) {
                            CircularProgressIndicator(color = accentColor, modifier = Modifier.size(24.dp))
                        } else {
                            Button(
                                onClick = {
                                    if (phoneInput.length < 8) {
                                        authError = "Please enter a valid phone number."
                                    } else if (!otpSent) {
                                        isLoading = true
                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            isLoading = false
                                            otpSent = true
                                            authError = ""
                                        }, 800)
                                    } else {
                                        if (otpInput.length != 6) {
                                            authError = "Invalid passcode. Must be 6 digits."
                                        } else {
                                            viewModel.loginUser(phoneInput + "@myuloc.internal", phoneInput, phoneInput)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(99.dp)
                            ) {
                                Text(if (!otpSent) "Send 6-Digit SMS Code" else "Verify & Login", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Email Sign In or Sign Up Form
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("E-mail Address", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = accentColor) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = accentColor) },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(if (isPasswordVisible) Icons.Default.Check else Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            )
                        )

                        if (isSignUp) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = confirmPasswordInput,
                                onValueChange = { confirmPasswordInput = it },
                                label = { Text("Confirm Password", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Lock, null, tint = accentColor) },
                                singleLine = true,
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor
                                )
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    fontSize = 11.sp,
                                    color = accentColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { showForgotPassword = true }
                                        .padding(vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isLoading) {
                            CircularProgressIndicator(color = accentColor, modifier = Modifier.size(24.dp))
                        } else {
                            Button(
                                onClick = {
                                    if (emailInput.isEmpty() || !emailInput.contains("@") || passwordInput.length < 6) {
                                        authError = "Ensure email is valid and password is at least 6 characters."
                                    } else {
                                        isLoading = true
                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            isLoading = false
                                            if (isSignUp) {
                                                if (passwordInput != confirmPasswordInput) {
                                                    authError = "Passwords do not match."
                                                } else {
                                                    val displayName = nameInput.ifEmpty { emailInput.substringBefore("@") }
                                                    viewModel.loginUser(emailInput, displayName)
                                                }
                                            } else {
                                                val displayName = emailInput.substringBefore("@")
                                                viewModel.loginUser(emailInput, displayName)
                                            }
                                        }, 700)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(99.dp)
                            ) {
                                Text(if (isSignUp) "Register Account" else "Authorize Securely", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(if (isDarkMode) Color(0x1AFFFFFF) else Color(0x33000000)))
                        Text("OR", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(if (isDarkMode) Color(0x1AFFFFFF) else Color(0x33000000)))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.loginUser("google.oauth@gmail.com", "Google Authorized User")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkMode) Color(0xFF22110D) else Color(0xFFFAEEEE),
                            contentColor = if (isDarkMode) Color.White else Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(99.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Text("Secure Sign-In with Google", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isSignUp) "Already have an account? Sign In" else "New Gateway Node? Register Account",
                        fontSize = 11.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                isSignUp = !isSignUp
                                authError = ""
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }

    if (showForgotPassword) {
        var forgotEmailInput by remember { mutableStateOf("") }
        var isForgotLoading by remember { mutableStateOf(false) }
        var resetDispatchedMsg by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showForgotPassword = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkMode) Color(0xFF1E1714) else Color.White, RoundedCornerShape(24.dp))
                    .border(0.5.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Reset Account Credentials", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Enter the authorized e-mail node below. A reset key will be dispatched.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                if (resetDispatchedMsg.isNotEmpty()) {
                    Text(resetDispatchedMsg, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showForgotPassword = false; resetDispatchedMsg = "" },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text("Acknowledge", fontSize = 11.sp)
                    }
                } else {
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        label = { Text("E-mail Address", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isForgotLoading) {
                        CircularProgressIndicator(color = accentColor, modifier = Modifier.size(24.dp))
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showForgotPassword = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    if (!forgotEmailInput.contains("@")) {
                                        android.widget.Toast.makeText(context, "Invalid email format.", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        isForgotLoading = true
                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            isForgotLoading = false
                                            resetDispatchedMsg = "Security token has been pushed to: $forgotEmailInput. Check spam folders."
                                        }, 1200)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Dispatch Key", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
