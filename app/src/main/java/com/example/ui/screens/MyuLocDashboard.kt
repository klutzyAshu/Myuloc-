package com.example.ui.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars


import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.database.MyuLocDatabase
import com.example.data.database.FavoriteTrack
import com.example.data.database.OfflineTrack
import com.example.viewmodel.toPlayerTrack
import com.example.viewmodel.toFavoriteTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.draw.blur
import androidx.compose.material.icons.filled.Language
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.composed
import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.SwapCalls
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.automirrored.filled.VolumeDown
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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.zIndex

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.graphics.compositeOver
import coil.imageLoader
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
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import com.example.viewmodel.LockerUiState
import com.example.viewmodel.MyuLocViewModel
import com.example.viewmodel.SearchUiState
import com.example.viewmodel.DriveStorageState

val LocalGlassmorphismEnabled = androidx.compose.runtime.staticCompositionLocalOf { true }

@Composable
fun MyuLocDashboard(viewModel: MyuLocViewModel) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft = (sleepSecondsLeft / 60)

    val context = LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val enableGlassmorphism by viewModel.enableGlassmorphism.collectAsState()

    androidx.compose.runtime.CompositionLocalProvider(LocalGlassmorphismEnabled provides enableGlassmorphism) {
    if (!isUserLoggedIn) {
        LoginScreen(viewModel = viewModel, isDarkMode = isDarkMode)
    } else {
        val currentTab by viewModel.currentTab.collectAsState()
        val isConnected by viewModel.isConnectedToDrive.collectAsState()
        val showGenrePreferencePopup by viewModel.showGenrePreferencePopup.collectAsState()
        val customPlaylists by viewModel.customPlaylists.collectAsState()
        val enableBackgroundMotion by viewModel.enableBackgroundMotion.collectAsState()

        // Multi-select deleting state
        val isMultiSelectMode by viewModel.isMultiSelectMode.collectAsState()
        val selectedDeleteIds by viewModel.selectedDeleteIds.collectAsState()

        // Player details
        val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
        val activeTrack = currentTrack

        // OAuth Web login state
        var showWebAuth by remember { mutableStateOf(false) }
        var inputTokenDialog by remember { mutableStateOf(false) }
        var showExpandedPlayer by remember { mutableStateOf(false) }
        var showSleepDialog by remember { mutableStateOf(false) }

        val artworkThemeEnabled by viewModel.artworkThemeEnabled.collectAsState()

        LaunchedEffect(currentTrack, artworkThemeEnabled, isDarkMode) {
            if (artworkThemeEnabled && currentTrack != null) {
                val imageUrl = currentTrack!!.thumbnailUrl
                if (imageUrl.isNotEmpty()) {
                    try {
                        withContext(Dispatchers.Default) {
                            val request = coil.request.ImageRequest.Builder(context)
                                .data(imageUrl)
                                .allowHardware(false)
                                .build()
                            val result = coil.Coil.imageLoader(context).execute(request)
                            if (result is coil.request.SuccessResult) {
                                val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                                if (bitmap != null) {
                                    val palette = androidx.palette.graphics.Palette.from(bitmap).generate()
                                    val vibrantSwatch = palette.vibrantSwatch ?: palette.dominantSwatch ?: palette.mutedSwatch ?: palette.lightVibrantSwatch
                                    vibrantSwatch?.let { swatch ->
                                        val hsv = FloatArray(3)
                                        android.graphics.Color.colorToHSV(swatch.rgb, hsv)
                                        val extractedHue = hsv[0]
                                        val extractedSat = hsv[1].coerceIn(0.40f, 0.90f)
                                        val targetLit = if (isDarkMode) 0.15f else 0.85f
                                        withContext(Dispatchers.Main) {
                                            viewModel.updateThemeFromArtwork(extractedHue, extractedSat, targetLit)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MyuLocDashboard", "Error extracting theme colors: ${e.message}")
                    }
                }
            }
        }

        val playerExpansionProgress by animateFloatAsState(
            targetValue = if (showExpandedPlayer) 1f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "player_expansion"
        )

        val maxScrollOffset = with(androidx.compose.ui.platform.LocalDensity.current) { 154.dp.toPx() }
        var scrollOffset by remember { mutableStateOf(0f) }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: androidx.compose.ui.geometry.Offset, source: NestedScrollSource): androidx.compose.ui.geometry.Offset {
                    val delta = available.y
                    val newOffset = (scrollOffset - delta).coerceIn(0f, maxScrollOffset)
                    scrollOffset = newOffset
                    return androidx.compose.ui.geometry.Offset.Zero
                }
            }
        }
        val scrollFraction = (scrollOffset / maxScrollOffset).coerceIn(0f, 1f)

        LaunchedEffect(currentTab) {
            scrollOffset = 0f
        }

        // Visual theme params
        val mainBg = MaterialTheme.colorScheme.background
        val accentColor = MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .fillMaxSize()
                                
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        var hasTriggered = false
                        while (true) {
                            val event = awaitPointerEvent()
                            val changes = event.changes
                            val pressedChanges = changes.filter { it.pressed }
                            val activePointers = pressedChanges.size
                            if (activePointers >= 3) {
                                var totalDeltaX = 0f
                                pressedChanges.forEach { change ->
                                    val diff = change.position.x - change.previousPosition.x
                                    totalDeltaX += diff
                                }
                                
                                if (totalDeltaX < -120f && !hasTriggered) {
                                    hasTriggered = true
                                    pressedChanges.forEach { it.consume() }
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    viewModel.setTab("search")
                                }
                            } else {
                                hasTriggered = false
                            }
                            if (changes.all { !it.pressed }) {
                                break
                            }
                        }
                    }
                }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize()
                                ,
                containerColor = mainBg
            ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                                
                    .nestedScroll(nestedScrollConnection)
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                // Background Mesh Glow Graphics to give that fancy, luxurious visual feel
                val isGlass = LocalGlassmorphismEnabled.current
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                                
                        .zIndex(-1f)
                ) {
                    BackgroundAmbientMesh(isDarkMode = isDarkMode, enableMotion = enableBackgroundMotion)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .padding(
                            start = if (isLandscape) 96.dp else 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                ) {
                    // Top header capsule with smooth fluid scroll collapsing height, translation and fading
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((54 * (1f - scrollFraction)).dp)
                            .graphicsLayer {
                                translationY = -54.dp.toPx() * scrollFraction
                                alpha = 1f - scrollFraction
                                clip = true
                            }
                    ) {
                        HeaderCapsule(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            onThemeToggle = { viewModel.toggleTheme() },
                            onSleepDialogShow = { showSleepDialog = true },
                            onSettingsClick = { viewModel.setTab("settings") }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Scrollable listings space
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                val tabsOrder = listOf("all", "search", "favorites", "settings")
                                val initialIndex = tabsOrder.indexOf(initialState).coerceAtLeast(0)
                                val targetIndex = tabsOrder.indexOf(targetState).coerceAtLeast(0)
                                if (targetIndex > initialIndex) {
                                    (slideInHorizontally(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow)) { (it * 0.15f).toInt() } + 
                                     fadeIn(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow))).togetherWith(
                                        slideOutHorizontally(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow)) { -(it * 0.15f).toInt() } + 
                                        fadeOut(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow))
                                    )
                                } else {
                                    (slideInHorizontally(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow)) { -(it * 0.15f).toInt() } + 
                                     fadeIn(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow))).togetherWith(
                                        slideOutHorizontally(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow)) { (it * 0.15f).toInt() } + 
                                        fadeOut(animationSpec = androidx.compose.animation.core.spring(dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy, stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow))
                                    )
                                }
                            },
                            label = "tab_content_transitions",
                            modifier = Modifier.fillMaxSize()
                                
                        ) { targetTab ->
                            Box(
                                modifier = Modifier.fillMaxSize()
                                
                            ) {
                                when (targetTab) {
                                    "all" -> {
                                        AllTabContent(
                                            viewModel = viewModel,
                                            isDarkMode = isDarkMode,
                                            scrollFraction = scrollFraction
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

                }

                // Floating sleep timer countdown capsule pill
                Column(
                    modifier = Modifier
                        .align(if (isLandscape) Alignment.TopEnd else Alignment.BottomCenter)
                        .padding(
                            bottom = if (isLandscape) 0.dp else if (activeTrack != null && !showExpandedPlayer) 148.dp else 84.dp,
                            top = if (isLandscape) 84.dp else 0.dp,
                            end = if (isLandscape) 16.dp else 0.dp
                        )
                        .graphicsLayer {
                            translationY = 120.dp.toPx() * scrollFraction
                            alpha = 1f - scrollFraction
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SleepTimerMiniCapsule(viewModel = viewModel, isDarkMode = isDarkMode)
                }

                // Multiple deletion selection menu / layout bar floating on top!
                AnimatedVisibility(
                    visible = isMultiSelectMode,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = innerPadding.calculateTopPadding() + 80.dp)
                        .padding(horizontal = 20.dp)
                        .zIndex(20f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(
                                androidx.compose.material3.MaterialTheme.colorScheme.errorContainer,
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
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Remove selected tracks from device",
                                fontSize = 9.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
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

                // Put the floating AnchoredPlayerBarWrapper at the bottom of this Box with elegant entry/exit sliding transitions!
                AnimatedVisibility(
                    visible = activeTrack != null && !showExpandedPlayer,
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(animationSpec = tween(350)),
                    exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(animationSpec = tween(250)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(
                            bottom = if (isLandscape) 16.dp else 64.dp,
                            start = if (isLandscape) 80.dp else 0.dp
                        ) // Leave space for BottomNavigationBar in portrait
                        .zIndex(10f)
                ) {
                    AnchoredPlayerBarWrapper(
                        viewModel = viewModel,
                        isDarkMode = isDarkMode,
                        onCapsuleClick = { 
                            showExpandedPlayer = true 
                        }
                    )
                }

                // Global Navigation (Bottom in Portrait, Rail in Landscape)
                Box(
                    modifier = Modifier
                        .align(if (isLandscape) Alignment.CenterStart else Alignment.BottomCenter)
                        .let { if (isLandscape) it.fillMaxHeight().width(80.dp) else it.fillMaxWidth() }
                        .zIndex(20f)
                ) {
                    if (isLandscape) {
                        GlobalNavigationRail(
                            currentTab = currentTab,
                            isDarkMode = isDarkMode,
                            onTabSelected = { viewModel.setTab(it) }
                        )
                    } else {
                        GlobalBottomNavigationBar(
                            currentTab = currentTab,
                            isDarkMode = isDarkMode,
                            onTabSelected = { viewModel.setTab(it) }
                        )
                    }
                }
            }

        }
    }
        if (showExpandedPlayer) {
            BackHandler {
                showExpandedPlayer = false
            }
        }

        if (playerExpansionProgress > 0f && activeTrack != null) {
            Box(modifier = Modifier.fillMaxSize().zIndex(30f)) {
                ExpandedPlayerView(
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                expansionProgress = playerExpansionProgress,
                innerPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                onDismiss = { showExpandedPlayer = false },
                onSleepDialogShow = { showSleepDialog = true }
            )
            }
//        }
//
//        // Google Drive Inline WebView Authorization dialog
//        if (showWebAuth) {
//            GoogleDriveOAuthDialog(
//                viewModel = viewModel,
//                isDarkMode = isDarkMode,
//                onTokenCaptured = { token ->
//                    viewModel.connectWithAccessToken(token)
//                    showWebAuth = false
//                },
//                onDismiss = { showWebAuth = false }
//            )
//        }
//
//        // Direct token manually entering dialog as fallback
//        if (inputTokenDialog) {
//            ManualTokenEntryDialog(
//                isDarkMode = isDarkMode,
//                onTokenEntered = {
//                    viewModel.connectWithAccessToken(it)
//                    inputTokenDialog = false
//                },
//                onDismiss = { inputTokenDialog = false }
//            )
//        }

        // Centralized shuffle popup to insulate main visual tree from recomposing on shuffle transitions
        ShufflePopupOverlayWrapper(
            viewModel = viewModel,
            isDarkMode = isDarkMode,
            modifier = Modifier
                .padding(bottom = 125.dp)
                .zIndex(100f)
        )
        
        // Centralized custom non-flickering Sleep Timer Overlay Dialog
        SleepTimerCentralOverlay(
            showSleepDialog = showSleepDialog,
            onDismissShow = { showSleepDialog = false },
            viewModel = viewModel,
            isDarkMode = isDarkMode
        )

        } // Close outer Box wrapper!
    } // Close inner else or Box
} // Close MyuLocDashboard

}
@Composable
fun SleepTimerCentralOverlay(
    showSleepDialog: Boolean,
    onDismissShow: () -> Unit,
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean
) {
    if (showSleepDialog) {
        var slideOutTrigger by remember { mutableStateOf(false) }
        val animScope = rememberCoroutineScope()
        val dismissWithAnimation: () -> Unit = {
            animScope.launch {
                slideOutTrigger = true
                delay(280)
                onDismissShow()
                slideOutTrigger = false
            }
        }

        val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
        val sleepMinutesLeft by viewModel.sleepTimerMinutesLeft.collectAsState()
        val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
        val animSpeed by viewModel.animationSpeed.collectAsState()
        val animBounciness by viewModel.animationBounciness.collectAsState()
        val borderBrush = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

        // Prevent back clicks from leaving slide animation uncompleted
        BackHandler(enabled = true) {
            dismissWithAnimation()
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

        // Full screen non-window translucent backdrop preventing framework white flash
        Box(
            modifier = Modifier
                .fillMaxSize()
                                
                .background(Color.Black.copy(alpha = 0.56f))
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { dismissWithAnimation() }
                .zIndex(100f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .offset(y = slideOffset)
                    .alpha(fadeAlpha)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    
                    .padding(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) { /* Consumes clicks inside the modal card to avoid dismissing */ },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sleep Timer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (sleepRunning) {
                        val minutesStr = sleepSecondsLeft / 60
                        val secondsStr = String.format(java.util.Locale.US, "%02d", sleepSecondsLeft % 60)
                        "Active Countdown: $minutesStr:$secondsStr remaining"
                    } else {
                        "Pauses music playback automatically when timer lapses."
                    },
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                var customTime by remember { mutableStateOf(if(sleepRunning && sleepMinutesLeft > 0) sleepMinutesLeft.toFloat() else 30f) }
                val view = androidx.compose.ui.platform.LocalView.current
                var lastTickValue by remember { mutableIntStateOf(customTime.toInt()) }

                Text(
                    text = "${customTime.toInt()} Minutes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = customTime,
                    onValueChange = { newVal ->
                        customTime = newVal
                        val newInt = newVal.toInt()
                        if (newInt != lastTickValue) {
                            view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                            lastTickValue = newInt
                        }
                    },
                    valueRange = 0f..200f,
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            viewModel.stopSleepTimer()
                            dismissWithAnimation()
                        },
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Stop Timer", fontWeight = FontWeight.Bold)
                    }
                    
                    androidx.compose.material3.Button(
                        onClick = {
                            if (customTime.toInt() > 0) {
                                viewModel.startSleepTimer(customTime.toInt())
                            } else {
                                viewModel.stopSleepTimer()
                            }
                            dismissWithAnimation()
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Set Timer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun rememberOptimizedImageRequest(data: Any?, sizePx: Int): coil.request.ImageRequest {
    return androidx.compose.runtime.remember(data, sizePx) {
        val builder = coil.request.ImageRequest.Builder(context)
            .data(data)
            .size(sizePx)
            .precision(coil.size.Precision.AUTOMATIC)
            .crossfade(true)
            .allowHardware(true)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
        builder.build()
    }
}

// --- Glassmorphic Frost Helpers ---
@Composable
fun getGlassBackground(isDarkMode: Boolean): Brush {
    val surfaceColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    return androidx.compose.runtime.remember(surfaceColor, isDarkMode) {
        val alpha = if (isDarkMode) 0.25f else 0.4f
        androidx.compose.ui.graphics.SolidColor(surfaceColor.copy(alpha = alpha))
    }
}

@Composable
fun getGlassBorder(isDarkMode: Boolean): Brush {
    val outlineColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
    return androidx.compose.runtime.remember(outlineColor, isDarkMode) {
        val alpha = if (isDarkMode) 0.2f else 0.3f
        androidx.compose.ui.graphics.SolidColor(outlineColor.copy(alpha = alpha))
    }
}


// --- Glassmorphic Capsules ---

private fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
    val r = color1.red + (color2.red - color1.red) * ratio
    val g = color1.green + (color2.green - color1.green) * ratio
    val b = color1.blue + (color2.blue - color1.blue) * ratio
    val a = color1.alpha + (color2.alpha - color1.alpha) * ratio
    return Color(r, g, b, a)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSakuraFlower(
    centerX: Float,
    centerY: Float,
    scale: Float,
    flowerPath: Path,
    creasePath: Path,
    isDarkMode: Boolean,
    primaryColor: Color
) {
    val petalW = 24f * scale
    val petalH = 45f * scale

    // Sticker backing color: very dark shade of the theme color in dark theme, pure white in light theme
    val backingColor = if (isDarkMode) blendColors(primaryColor, Color.Black, 0.95f) else Color(0xFFFFFFFF)
    // Premium soft sticker gradients with dynamic tones reflecting the active theme
    val petalColorStart = if (isDarkMode) blendColors(primaryColor, Color.Black, 0.60f) else blendColors(primaryColor, Color.White, 0.90f) 
    val petalColorEnd = if (isDarkMode) blendColors(primaryColor, Color.Black, 0.80f) else blendColors(primaryColor, Color.White, 0.50f) 
    val centerCrownColor = if (isDarkMode) blendColors(primaryColor, Color.White, 0.40f) else blendColors(primaryColor, Color.Black, 0.20f) 
    val centerSecondaryColor = if (isDarkMode) blendColors(primaryColor, Color.White, 0.20f) else blendColors(primaryColor, Color.Black, 0.40f) 
    val creaseStrokeColor = if (isDarkMode) blendColors(primaryColor, Color.Black, 0.85f) else blendColors(primaryColor, Color.Black, 0.30f) 

    // 1. Draw sticker backings first (thick round strokes + fills to generate a high-contrast sticker border outline)
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
                color = backingColor,
                style = androidx.compose.ui.graphics.drawscope.Fill
            )
            drawPath(
                path = flowerPath,
                color = backingColor,
                style = Stroke(width = 6f * scale, join = androidx.compose.ui.graphics.StrokeJoin.Round)
            )
        }
    }

    // 2. Draw sticker inner colors (over the backing)
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
                    colors = listOf(petalColorStart, petalColorEnd),
                    center = Offset(centerX, centerY - petalH * 0.5f),
                    radius = petalH
                )
            )

            creasePath.reset()
            creasePath.moveTo(centerX, centerY)
            creasePath.lineTo(centerX, centerY - petalH * 0.7f)
            drawPath(creasePath, color = creaseStrokeColor, style = Stroke(width = 2f * scale))
        }
    }

    // 3. Draw central core
    drawCircle(color = centerCrownColor, radius = 7f * scale, center = Offset(centerX, centerY))
    drawCircle(color = centerSecondaryColor, radius = 3.5f * scale, center = Offset(centerX, centerY))
}

@Composable
fun BackgroundAmbientMesh(isDarkMode: Boolean, enableMotion: Boolean = true) {
    val bgColor = if (isDarkMode) Color(0xFF000000) else Color(0xFFFFFFFF)
    val primaryColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .fillMaxSize()
                                
            .background(bgColor)
    ) {
        val gradientBrush = remember(isDarkMode, primaryColor) {
            Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = if (isDarkMode) 0.12f else 0.08f),
                    Color.Transparent
                ),
                center = Offset.Zero, // Top left ambient glow
                radius = 1200f
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                                
                .background(gradientBrush)
        )
        if (enableMotion) {
            AnimatedDriftingParticlesOverlay(isDarkMode = isDarkMode)
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

class VinylRotationTracker {
    var lastRotationOffset: Float = 0f
    var lastFactorValue: Float = 0f
}



@Composable
fun CustomAppLogo(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_logo_vector),
        contentDescription = "MyuLoc Adaptive Logo",
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
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
fun HeaderCapsule(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onSleepDialogShow: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)
    val containerBg = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    val textColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface

    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft by viewModel.sleepTimerMinutesLeft.collectAsState()

    val animSpeed by viewModel.animationSpeed.collectAsState()
    val animBounciness by viewModel.animationBounciness.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "MyuLoc",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
        }

        // Right side: Compact layout with Mode Switcher, Sleep timer, and Settings button with robust 48x48dp interactive sizes!
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Smoothly animated theme switch icon (Rotates 360 degrees with spring tension)
            val rotationAngle by animateFloatAsState(
                targetValue = if (isDarkMode) 360f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "theme_rotation"
            )

            Box(
                modifier = Modifier
                    .size(48.dp) // Perfect 48dp touch target size!
                    .clip(CircleShape)
                    .bouncyClickable { onThemeToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = rotationAngle }
                )
            }

            // Sleep Timer Countdown trigger (No circles)
            Box(
                modifier = Modifier
                    .height(48.dp) // Touch target compliant!
                    .clip(RoundedCornerShape(percent = 50))
                    .bouncyClickable { onSleepDialogShow() }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Sleep Timer",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                    if (sleepRunning) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${sleepMinutesLeft}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Settings button (Moved from bottom menu bar to top right for compact elegant layout structure)
            Box(
                modifier = Modifier
                    .size(48.dp) // Touch target compliant!
                    .clip(CircleShape)
                    .bouncyClickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GlobalBottomNavigationBar(
    currentTab: String,
    isDarkMode: Boolean,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        Triple("all", "Home", Icons.Default.Home),
        Triple("search", "Library", Icons.Default.Folder), // "Search" maps to Library icon/concept
        Triple("favorites", "Favorites", Icons.Default.Favorite)
    )

    val activeColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val bgColor = androidx.compose.material3.MaterialTheme.colorScheme.background

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .navigationBarsPadding()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (tabId, label, icon) ->
            val isSelected = currentTab == tabId
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(tabId) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) activeColor else inactiveColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = if (isSelected) activeColor else inactiveColor,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
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
    val tabs = listOf(
        Triple("all", "Library", Icons.Default.Folder),
        Triple("search", "Search", Icons.Default.Search),
        Triple("favorites", "Favorites", Icons.Default.Favorite)
    )

    val isSettingsActive = currentTab == "settings"
    val targetIndex = if (isSettingsActive) 0 else tabs.indexOfFirst { it.first == currentTab }.coerceAtLeast(0)

    // Remember the previous index to determine direction of movement
    var prevIndex by remember { mutableIntStateOf(targetIndex) }
    
    // Smoothly track moving direction to adjust edge stiffness for stretching
    var directionIsRight by remember { mutableStateOf(targetIndex >= prevIndex) }

    LaunchedEffect(targetIndex) {
        if (targetIndex != prevIndex) {
            directionIsRight = targetIndex > prevIndex
            prevIndex = targetIndex
        }
    }

    // Animating left and right edges with smooth, non-bouncy spring physics
    val leftFraction by animateFloatAsState(
        targetValue = targetIndex / 3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tab_left"
    )

    val rightFraction by animateFloatAsState(
        targetValue = (targetIndex + 1) / 3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tab_right"
    )

    val indicatorAlpha by animateFloatAsState(
        targetValue = if (isSettingsActive) 0f else 1f,
        animationSpec = tween(200),
        label = "tab_indicator_alpha"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(26.dp))
            .padding(4.dp)
    ) {
        val parentWidth = maxWidth
        val startOffset = parentWidth * leftFraction
        val capsuleWidth = parentWidth * (rightFraction - leftFraction)

        val activeIndicatorBrush = Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
            )
        )

        // Premium dynamic sliding gradient indicator
        Box(
            modifier = Modifier
                .offset(x = startOffset)
                .width(capsuleWidth)
                .fillMaxHeight()
                .graphicsLayer { alpha = indicatorAlpha }
                .background(activeIndicatorBrush, RoundedCornerShape(22.dp))
        )

        // Horizontal Row of tabs layered smoothly above
        Row(
            modifier = Modifier.fillMaxSize()
                                ,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (tabId, tabName, icon) ->
                val isActive = currentTab == tabId && !isSettingsActive
                
                val activeTextColor = Color.White
                val inactiveTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                // Smooth spring-based scale accent
                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1.05f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "tab_scale"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .bouncyClickable { onTabSelected(tabId) },
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
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tabName,
                            fontSize = 12.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
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
    val subs = listOf(
        Triple("songs", "Songs", Icons.Default.MusicNote),
        Triple("playlists", "Playlists", Icons.AutoMirrored.Filled.QueueMusic),
        Triple("artists", "Artists", Icons.Default.Person),
        Triple("albums", "Albums", Icons.Default.Queue),
        Triple("genres", "Genres", Icons.Default.Equalizer)
    )

    val activePrimary = MaterialTheme.colorScheme.primary

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(subs) { (subId, subName, icon) ->
            val isActive = currentSubTab == subId
            val targetBgColor = if (isActive) activePrimary else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
            val targetContentColor = if (isActive) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant

            val bgColor by androidx.compose.animation.animateColorAsState(
                targetValue = targetBgColor,
                animationSpec = androidx.compose.animation.core.tween(250),
                label = "subtab_bg"
            )
            val contentColor by androidx.compose.animation.animateColorAsState(
                targetValue = targetContentColor,
                animationSpec = androidx.compose.animation.core.tween(250),
                label = "subtab_content"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(bgColor)
                    .clickable { onSubTabSelected(subId) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = subName,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = subName,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                    color = contentColor
                )
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
    val containerBrush = getGlassBackground(isDarkMode)
    val borderColor = Color.Transparent
    val contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "arrow_rotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerBrush)
            
            .bouncyClickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = playlist.icon,
                contentDescription = playlist.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${playlist.tracks.size} tracks",
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    val containerBrush = getGlassBackground(isDarkMode)
    val borderColor = Color.Transparent
    val contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
            listOf(Color(0xFFEDE9FE), Color(0xFFF5F3FF), Color(0xFFF3E8FF), Color(0xFFFAE8FF), Color(0xFFF2EAFA))
        }
        colors[Math.abs(hash % colors.size)]
    }
    val avatarTextColor = if (isDarkMode) Color.White else (if (hash % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerBrush)
            
            .bouncyClickable { onClick() }
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
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$songCount ${if (songCount == 1) "track" else "tracks"}",
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    val containerBrush = getGlassBackground(isDarkMode)
    val borderColor = Color.Transparent
    val contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
            .clip(RoundedCornerShape(12.dp))
            .background(containerBrush)
            
            .bouncyClickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = genreName,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = genreName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$songCount ${if (songCount == 1) "track" else "tracks"}",
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    isDarkMode: Boolean,
    scrollFraction: Float = 0f
) {
    val context = LocalContext.current
    val offlineList by viewModel.offlineTracksFlow.collectAsState(initial = null)
    val recommendations by viewModel.recommendationsList.collectAsState()
    val playCountsMap by viewModel.playCounts.collectAsState()
    val favoritesPlayerList by viewModel.favoritesPlayerTracksFlow.collectAsState()
    val lockerPlayerList by viewModel.lockerPlayerTracksFlow.collectAsState()
    val localPlayerList by viewModel.localPlayerTracksFlow.collectAsState()
    val mostPlayedPlayerList by viewModel.mostPlayedPlayerTracksFlow.collectAsState()
    val containerBg = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    val accentColor = MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

    var showPermissionDialog by remember { mutableStateOf(false) }
    val subTab by viewModel.subTab.collectAsState()
    val customPlaylists by viewModel.customPlaylists.collectAsState()
    var expandedItemKey by remember { mutableStateOf<String?>(null) }
    val hasActiveTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft = (sleepSecondsLeft / 60)
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp + navBarHeight else 16.dp + navBarHeight
    } else {
        if (hasActiveTrack != null) 148.dp + navBarHeight else 84.dp + navBarHeight
    }

    val favoriteTrackIds by viewModel.favoriteTrackIds.collectAsState(initial = emptySet())
    val downloadProgressMap by viewModel.downloadProgress.collectAsState(initial = emptyMap())
    val offlineTrackIds by viewModel.offlineTrackIds.collectAsState(initial = emptySet())
    val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isPlayingState by viewModel.playerManager.isPlaying.collectAsState()
    val isAudioDeliveringSoundState by viewModel.playerManager.isAudioDeliveringSound.collectAsState()

    var playlists by remember { mutableStateOf<List<PlaylistData>>(emptyList()) }
    var artistGroupsMapped by remember { mutableStateOf<Map<String, List<PlayerTrack>>>(emptyMap()) }
    var genreGroupsMapped by remember { mutableStateOf<Map<String, List<PlayerTrack>>>(emptyMap()) }

    LaunchedEffect(favoritesPlayerList, lockerPlayerList, localPlayerList, mostPlayedPlayerList, customPlaylists) {
        val list = mutableListOf<PlaylistData>()

        list.add(PlaylistData("favorites", "My Favorites", Icons.Default.Favorite, favoritesPlayerList))

        customPlaylists.forEach { plName ->
            list.add(PlaylistData(id = "custom_$plName", title = plName, icon = Icons.Default.MusicNote, tracks = emptyList()))
        }

        list.addAll(listOf(
            PlaylistData("locker", "Locker Cloud Downloads", Icons.Default.Cloud, lockerPlayerList),
            PlaylistData("local", "Local Device Imports", Icons.Default.Folder, localPlayerList),
            PlaylistData("history", "Most Played Tracks", Icons.Default.Equalizer, mostPlayedPlayerList)
        ))
        playlists = list
    }

    LaunchedEffect(offlineList) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            val aMap = (offlineList ?: emptyList()).groupBy { it.artist.ifBlank { "Unknown Artist" }.trim() }
                .toSortedMap()
                .mapValues { entry ->
                    entry.value.map {
                        PlayerTrack(
                            id = it.id, title = it.title, artist = it.artist, streamUrl = it.localUri, thumbnailUrl = it.thumbnailUrl, source = it.source, durationMs = it.durationMs
                        )
                    }
                }
            artistGroupsMapped = aMap

            val gMap = (offlineList ?: emptyList()).groupBy { track ->
                val titleUpper = track.title.uppercase()
                val artistUpper = track.artist.uppercase()
                when {
                    titleUpper.contains("LOFI") || titleUpper.contains("LO-FI") || titleUpper.contains("CHILL") || titleUpper.contains("SLEEP") || titleUpper.contains("RELAX") || titleUpper.contains("AMBIENT") || titleUpper.contains("STUDY") || titleUpper.contains("RAIN") || artistUpper.contains("LOFI") || artistUpper.contains("CHILL") -> "Chill & Lofi"
                    titleUpper.contains("REMIX") || titleUpper.contains("MIX") || titleUpper.contains("SYNTH") || titleUpper.contains("EDM") || titleUpper.contains("DANCE") || titleUpper.contains("ELECTRONIC") || titleUpper.contains("BEAT") || titleUpper.contains("CLUB") || titleUpper.contains("HOUSE") || titleUpper.contains("TECHNO") -> "Electronic & Dance"
                    titleUpper.contains("RAP") || titleUpper.contains("HIP HOP") || titleUpper.contains("HIP-HOP") || titleUpper.contains("TRAP") || titleUpper.contains("R&B") || titleUpper.contains("SOUL") || artistUpper.contains("RAP") || artistUpper.contains("HIP") -> "Hip-Hop & R&B"
                    titleUpper.contains("ACOUSTIC") || titleUpper.contains("LIVE") || titleUpper.contains("PIANO") || titleUpper.contains("CLASSICAL") || titleUpper.contains("INSTRUMENTAL") || titleUpper.contains("VIOLIN") || titleUpper.contains("GUITAR") -> "Acoustic & Classical"
                    titleUpper.contains("POP") || titleUpper.contains("LOVE") || titleUpper.contains("VOCAL") || titleUpper.contains("ROCK") || titleUpper.contains("INDIE") || titleUpper.contains("METAL") -> "Pop, Rock & Indie"
                    else -> "Alternative Indie"
                }
            }.toSortedMap().mapValues { entry ->
                entry.value.map {
                    PlayerTrack(
                        id = it.id, title = it.title, artist = it.artist, streamUrl = it.localUri, thumbnailUrl = it.thumbnailUrl, source = it.source, durationMs = it.durationMs
                    )
                }
            }
            genreGroupsMapped = gMap
        }
    }


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

    LaunchedEffect(Unit) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, permissionString) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog = true
        } else {
            viewModel.scanLocalFiles()
        }
    }

    if (showPermissionDialog) {
        Dialog(onDismissRequest = { showPermissionDialog = false }) {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Local Library Access",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                            
                            .clickable { showPermissionDialog = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Not Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(MaterialTheme.colorScheme.primary)
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

    Column(modifier = Modifier.fillMaxSize()
                                ) {
        // Embed Search Bar above SubCategoryTabCapsule with layout collapsing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((56 * (1f - scrollFraction)).dp)
                .graphicsLayer {
                    translationY = -56.dp.toPx() * scrollFraction
                    alpha = 1f - scrollFraction
                    clip = true
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.setTab("search") } // Go to search tab
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search songs, artists, albums...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.setTab("search") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = "Filter",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Embed SubCategoryTabCapsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((44 * (1f - scrollFraction)).dp)
                .graphicsLayer {
                    translationY = -44.dp.toPx() * scrollFraction
                    alpha = 1f - scrollFraction
                    clip = true
                }
        ) {
            SubCategoryTabCapsule(
                currentSubTab = subTab,
                isDarkMode = isDarkMode,
                onSubTabSelected = {
                    viewModel.setSubTab(it)
                }
            )
        }
        Spacer(modifier = Modifier.height((12 * (1f - scrollFraction)).dp))

        androidx.compose.animation.AnimatedContent(
            targetState = subTab,
            transitionSpec = {
                val subTabs = listOf("songs", "playlists", "artists", "albums", "genres")
                val initialIndex = subTabs.indexOf(initialState)
                val targetIndex = subTabs.indexOf(targetState)
                if (targetIndex > initialIndex) {
                    (androidx.compose.animation.slideInHorizontally(animationSpec = androidx.compose.animation.core.tween(300)) { width -> width } + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300))).togetherWith(
                        androidx.compose.animation.slideOutHorizontally(animationSpec = androidx.compose.animation.core.tween(300)) { width -> -width } + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                    )
                } else {
                    (androidx.compose.animation.slideInHorizontally(animationSpec = androidx.compose.animation.core.tween(300)) { width -> -width } + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300))).togetherWith(
                        androidx.compose.animation.slideOutHorizontally(animationSpec = androidx.compose.animation.core.tween(300)) { width -> width } + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                    )
                }.using(
                    androidx.compose.animation.SizeTransform(clip = false)
                )
            },
            label = "sub_tab_animation",
            modifier = Modifier.fillMaxSize()
                                
        ) { currentSubTab ->
            val listState = remember(currentSubTab) { androidx.compose.foundation.lazy.LazyListState() }
            
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = bottomSpacing),
                modifier = Modifier.fillMaxSize()
                                
            ) {
                if (currentSubTab == "playlists") {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                            
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Component B: Playlist Quick Creation Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Playlist",
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quick Create Playlist",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        var playlistNameInput by remember { mutableStateOf("") }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = playlistNameInput,
                                onValueChange = { playlistNameInput = it },
                                placeholder = { 
                                    Text(
                                        "Enter playlist name...", 
                                        fontSize = 12.sp, 
                                        color = if (isDarkMode) Color(0xFF6D5A64) else Color(0xFFA09090)
                                    ) 
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 1,
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant,
                                    cursorColor = accentColor,
                                    focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                                )
                            )

                            Button(
                                onClick = {
                                    if (playlistNameInput.isNotBlank()) {
                                        viewModel.createCustomPlaylist(playlistNameInput)
                                        android.widget.Toast.makeText(
                                            context, 
                                            "Playlist '${playlistNameInput.trim()}' Created Successfully!", 
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                        playlistNameInput = ""
                                    } else {
                                        android.widget.Toast.makeText(
                                            context, 
                                            "Playlist name cannot be empty!", 
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(38.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor,
                                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "CREATE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                } // This closes if (currentSubTab == "playlists") {

            // 1. Offline Library Header with powerful Shuffle option and refresh
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when(subTab) {
                                "songs" -> "Songs"
                                "playlists" -> "Playlists"
                                "artists" -> "Artists"
                                "albums" -> "Albums"
                                "genres" -> "Genres"
                                else -> "Songs"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )

                        if (offlineList?.isNotEmpty() == true && subTab == "songs") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val playerTracks = (offlineList ?: emptyList()).map { offlineTrack ->
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
                                    }
                                    .padding(vertical = 6.dp, horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Shuffle",
                                    tint = accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Shuffle Play", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = accentColor)
                            }
                        }
                    }
                }
            }
            if (subTab == "songs" && offlineList?.isNotEmpty() == true) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    val currentField by viewModel.sortField.collectAsState()
                    val currentDirection by viewModel.sortDirection.collectAsState()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            Text(
                                text = "Sort:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
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

            // SONGS VIEW
            if (subTab == "songs") {
                if (offlineList == null) {
                    items(15) {
                        SkeletonTrackCapsuleItem(isDarkMode = isDarkMode)
                    }
                } else if (offlineList?.isEmpty() == true) {
                    item {
                        EmptyStateCard(
                            title = "No Local Tracks Registered",
                            tip = "Scan your device folder above, or download songs from your synced Google Drive Vault folder.",
                            isDarkMode = isDarkMode
                        )
                    }
                } else {
                    itemsIndexed(offlineList ?: emptyList(), key = { _, item -> item.id }) { index, offlineTrack ->
                        val track = remember(offlineTrack) {
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
                        val isLiked = favoriteTrackIds.contains(track.id)
                        val progress = downloadProgressMap[track.id]
                        val isDownloaded = offlineTrackIds.contains(track.id)
                        val isCurrent = currentTrack?.id == track.id
                        val isPlaying = isPlayingState
                        val isAudioDeliveringSound = isAudioDeliveringSoundState
                        val playCount = playCountsMap[track.id] ?: 0

                        TrackCapsuleItem(
                            modifier = Modifier.animateItem(),
                            track = track,
                            viewModel = viewModel,
                            isLiked = isLiked,
                            progress = progress,
                            isDownloaded = isDownloaded,
                            isCurrent = isCurrent,
                            isPlaying = isPlaying,
                            isAudioDeliveringSound = isAudioDeliveringSound,
                            playCount = playCount,
                            isDarkMode = isDarkMode,
                            onTrackClick = {
                                viewModel.playTrackWithResolution(track, viewModel.offlinePlayerTracksFlow.value)
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(track) },
                            index = index
                        )
                    }
                }
            }

            // PLAYLISTS VIEW (Expandable items)
            if (subTab == "playlists") {
                if (offlineList == null) {
                    items(5) {
                        SkeletonTrackCapsuleItem(isDarkMode = isDarkMode)
                    }
                } else {
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
                                itemsIndexed(pl.tracks, key = { _, track -> track.id }, contentType = { _, _ -> "track" }) { index, track ->
                                    val isLiked = favoriteTrackIds.contains(track.id)
                                    val progress = downloadProgressMap[track.id]
                                    val isDownloaded = offlineTrackIds.contains(track.id)
                                    val isCurrent = currentTrack?.id == track.id
                                    val isPlaying = isPlayingState
                                    val isAudioDeliveringSound = isAudioDeliveringSoundState
                                    val playCount = playCountsMap[track.id] ?: 0

                                    Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                        TrackCapsuleItem(
                                            modifier = Modifier.animateItem(),
                                            track = track,
                                            viewModel = viewModel,
                                            isLiked = isLiked,
                                            progress = progress,
                                            isDownloaded = isDownloaded,
                                            isCurrent = isCurrent,
                                            isPlaying = isPlaying,
                                            isAudioDeliveringSound = isAudioDeliveringSound,
                                            playCount = playCount,
                                            isDarkMode = isDarkMode,
                                            onTrackClick = {
                                                viewModel.playTrackWithResolution(track, pl.tracks)
                                            },
                                            onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                            index = index
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ARTISTS VIEW (Expandable items)
            if (subTab == "artists") {
                if (offlineList == null) {
                    items(5) {
                        SkeletonTrackCapsuleItem(isDarkMode = isDarkMode)
                    }
                } else if (artistGroupsMapped.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No Artists Discovered",
                            tip = "Catalog audio files or complete track syncing to view artist segments.",
                            isDarkMode = isDarkMode
                        )
                    }
                } else {
                    artistGroupsMapped.forEach { (artistName, artistTracks) ->
                        item {
                            ArtistHeaderCard(
                                artistName = artistName,
                                songCount = artistTracks.size,
                                isExpanded = expandedItemKey == artistName,
                                isDarkMode = isDarkMode,
                                onClick = {
                                    expandedItemKey = if (expandedItemKey == artistName) null else artistName
                                }
                            )
                        }
                        if (expandedItemKey == artistName) {
                            itemsIndexed(artistTracks, key = { _, track -> track.id }, contentType = { _, _ -> "track" }) { index, track ->
                                val isLiked = favoriteTrackIds.contains(track.id)
                                val progress = downloadProgressMap[track.id]
                                val isDownloaded = offlineTrackIds.contains(track.id)
                                val isCurrent = currentTrack?.id == track.id
                                val isPlaying = isPlayingState
                                val isAudioDeliveringSound = isAudioDeliveringSoundState
                                val playCount = playCountsMap[track.id] ?: 0

                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    TrackCapsuleItem(
                                        modifier = Modifier.animateItem(),
                                        track = track,
                                        viewModel = viewModel,
                                        isLiked = isLiked,
                                        progress = progress,
                                        isDownloaded = isDownloaded,
                                        isCurrent = isCurrent,
                                        isPlaying = isPlaying,
                                        isAudioDeliveringSound = isAudioDeliveringSound,
                                        playCount = playCount,
                                        isDarkMode = isDarkMode,
                                        onTrackClick = {
                                            viewModel.playTrackWithResolution(track, artistTracks)
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                        index = index
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // GENRES VIEW (Expandable items)
            if (subTab == "genres") {
                if (offlineList == null) {
                    items(5) {
                        SkeletonTrackCapsuleItem(isDarkMode = isDarkMode)
                    }
                } else if (genreGroupsMapped.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No Tracks for Genre Classification",
                            tip = "Genre tags are computed dynamically from song titles or artists.",
                            isDarkMode = isDarkMode
                        )
                    }
                } else {
                    item {
                        val containerBrush = getGlassBackground(isDarkMode)
                        val borderColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .background(containerBrush, RoundedCornerShape(16.dp))
                                
                                .padding(12.dp)
                        ) {
                            Text("Genre Quick Actions", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val genreMap = genreGroupsMapped
                                val fgColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                androidx.compose.material3.TextButton(onClick = { viewModel.playerManager.playGenreMix("Chill", genreMap) }) {
                                    Icon(Icons.Default.Coffee, null, tint = fgColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Chill Mix", color = fgColor)
                                }
                                androidx.compose.material3.TextButton(onClick = { viewModel.playerManager.playGenreMix("Energy", genreMap) }) {
                                    Icon(Icons.Default.Bolt, null, tint = fgColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Energy Mix", color = fgColor)
                                }
                                androidx.compose.material3.TextButton(onClick = { viewModel.playerManager.playRandomGenre(genreMap) }) {
                                    Icon(Icons.Default.Casino, null, tint = fgColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Random Genre", color = fgColor)
                                }
                                androidx.compose.material3.TextButton(onClick = { viewModel.playerManager.playGenreJumping(genreMap) }) {
                                    Icon(Icons.Default.SwapCalls, null, tint = fgColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Genre Jumper", color = fgColor)
                                }
                            }
                        }
                    }
                    genreGroupsMapped.forEach { (genreName, genreTracks) ->
                        item {
                            GenreHeaderCard(
                                genreName = genreName,
                                songCount = genreTracks.size,
                                isExpanded = expandedItemKey == genreName,
                                isDarkMode = isDarkMode,
                                onClick = {
                                    expandedItemKey = if (expandedItemKey == genreName) null else genreName
                                }
                            )
                        }
                        if (expandedItemKey == genreName) {
                            itemsIndexed(genreTracks, key = { _, track -> track.id }, contentType = { _, _ -> "track" }) { index, track ->
                                val isLiked = favoriteTrackIds.contains(track.id)
                                val progress = downloadProgressMap[track.id]
                                val isDownloaded = offlineTrackIds.contains(track.id)
                                val isCurrent = currentTrack?.id == track.id
                                val isPlaying = isPlayingState
                                val isAudioDeliveringSound = isAudioDeliveringSoundState
                                val playCount = playCountsMap[track.id] ?: 0

                                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    TrackCapsuleItem(
                                        modifier = Modifier.animateItem(),
                                        track = track,
                                        viewModel = viewModel,
                                        isLiked = isLiked,
                                        progress = progress,
                                        isDownloaded = isDownloaded,
                                        isCurrent = isCurrent,
                                        isPlaying = isPlaying,
                                        isAudioDeliveringSound = isAudioDeliveringSound,
                                        playCount = playCount,
                                        isDarkMode = isDarkMode,
                                        onTrackClick = {
                                            viewModel.playTrackWithResolution(track, genreTracks)
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                        index = index
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
}

fun SortPill(
    text: String,
    isSelected: Boolean,
    direction: com.example.viewmodel.SortDirection?,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val containerBg = if (isSelected) {
        androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
    } else {
        androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderCol = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkMode) 0.5f else 0.4f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .background(containerBg, RoundedCornerShape(100.dp))
            
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
    val hasActiveTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft = (sleepSecondsLeft / 60)
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp + navBarHeight else 16.dp + navBarHeight
    } else {
        if (hasActiveTrack != null) 148.dp + navBarHeight else 84.dp + navBarHeight
    }

    val accentColor = MaterialTheme.colorScheme.primary
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

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

    Column(modifier = Modifier.fillMaxSize()
                                ) {
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
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back to Cloud Index",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
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
            contentPadding = PaddingValues(bottom = bottomSpacing),
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
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )

                        if (isConnected && lockerState is LockerUiState.Success) {
                            val cloudTracks = (lockerState as LockerUiState.Success).tracks
                            if (cloudTracks.isNotEmpty()) {
                                Button(
                                    onClick = { viewModel.downloadAllLockerTracks(cloudTracks) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                            items(5) {
                                SkeletonTrackCapsuleItem(isDarkMode = isDarkMode)
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
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                                itemsIndexed(state.tracks, key = { _, track -> track.id }, contentType = { _, _ -> "track" }) { index, track ->
                                    TrackCapsuleItem(
                                        modifier = Modifier.animateItem(),
                                        track = track,
                                        viewModel = viewModel,
                                        isDarkMode = isDarkMode,
                                        onTrackClick = {
                                            viewModel.playTrackWithResolution(track, state.tracks)
                                        },
                                        onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                        index = index
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
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Paste your shared Google Drive folder link containing songs (.mp3, .wav, .flac). We will parse the ID and index tracks directly.",
                                fontSize = 11.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
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
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary),
                                shape = RoundedCornerShape(percent = 50),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Text("Submit Link", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
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
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                        )
                    }

                    if (isConnected) {
                        // Real Google Drive tracks are syncing!
                        when (val state = lockerState) {
                            is LockerUiState.Idle -> {
                                item { Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { RotatingAppLogo(modifier = Modifier.size(25.dp).clip(CircleShape)) } }
                            }
                            is LockerUiState.Loading -> {
                                items(5) {
                                    SkeletonTrackCapsuleItem(isDarkMode = isDarkMode)
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
                                    itemsIndexed(state.tracks, key = { _, track -> track.id }, contentType = { _, _ -> "track" }) { index, track ->
                                        TrackCapsuleItem(
                                            modifier = Modifier.animateItem(),
                                            track = track,
                                            viewModel = viewModel,
                                            isDarkMode = isDarkMode,
                                            onTrackClick = {
                                                viewModel.playTrackWithResolution(track, state.tracks)
                                            },
                                            onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                            index = index
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
    val accentColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerBrush, RoundedCornerShape(24.dp))
            
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
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
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
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
                Text("Secure Sign-In with Google Drive", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
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
    val hasActiveTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft = (sleepSecondsLeft / 60)
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp + navBarHeight else 16.dp + navBarHeight
    } else {
        if (hasActiveTrack != null) 148.dp + navBarHeight else 84.dp + navBarHeight
    }

    // Debounced search query execution with a 300ms delay to prevent heavy DB query load during typing
    LaunchedEffect(searchInput) {
        if (searchInput.trim().isEmpty()) {
            viewModel.performSearch(searchInput)
        } else {
            kotlinx.coroutines.delay(300)
            viewModel.performSearch(searchInput)
        }
    }

    val accentColor = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxSize()
                                ) {
        // Search Capsule Bar with clean horizontal separation to fix text placement
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchInput.isEmpty()) {
                            Text("Search offline/local songs...", fontSize = 13.sp, color = Color.Gray)
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { viewModel.performSearch(searchInput) }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = { searchInput = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search text", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.performSearch(searchInput) },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier.height(48.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Search", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Results List
        Text(
            text = "Matching Device Songs",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
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
                            .background(accentColor.copy(alpha = 0.15f), CircleShape),
                            
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
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter track titles or artists in the search bar above to look up scanned local songs and downloads instantly.",
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 5) {
                        SkeletonTrackCapsuleItem(isDarkMode = isDarkMode)
                    }
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
                        tip = "Double check search query spelling or try scanning local storage in Settings.",
                        isDarkMode = isDarkMode
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = bottomSpacing),
                        modifier = Modifier.fillMaxSize()
                                
                    ) {
                        itemsIndexed(state.results, key = { _, track -> track.id }, contentType = { _, _ -> "track" }) { index, track ->
                            TrackCapsuleItem(
                                modifier = Modifier.animateItem(),
                                track = track,
                                viewModel = viewModel,
                                isDarkMode = isDarkMode,
                                onTrackClick = {
                                    viewModel.playTrackWithResolution(track, state.results)
                                },
                                onFavoriteToggle = { viewModel.toggleFavorite(track) },
                                index = index
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
    val hasActiveTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft = (sleepSecondsLeft / 60)
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp + navBarHeight else 16.dp + navBarHeight
    } else {
        if (hasActiveTrack != null) 148.dp + navBarHeight else 84.dp + navBarHeight
    }
    val accentColor = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxSize()
                                ) {
        Text(
            text = "Your Curated Ecosystem",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        if (favoritesList.isEmpty()) {
            EmptyStateCard(
                title = "Your Vault is Empty",
                tip = "Tap the heart icon on any local track capsule to catalog it instantly into your custom vault.",
                isDarkMode = isDarkMode
            )
        } else {
            val mappedList = favoritesList.map { it.toPlayerTrack() }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = bottomSpacing),
                modifier = Modifier.fillMaxSize()
                                
            ) {
                itemsIndexed(favoritesList, key = { _, favorite -> favorite.id }, contentType = { _, _ -> "track" }) { index, favorite ->
                    val track = favorite.toPlayerTrack()
                    TrackCapsuleItem(
                        modifier = Modifier.animateItem(),
                        track = track,
                        viewModel = viewModel,
                        isDarkMode = isDarkMode,
                        onTrackClick = {
                            viewModel.playTrackWithResolution(track, mappedList)
                        },
                        onFavoriteToggle = { viewModel.toggleFavorite(track) },
                        index = index
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
    val hasActiveTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft = (sleepSecondsLeft / 60)
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp + navBarHeight else 16.dp + navBarHeight
    } else {
        if (hasActiveTrack != null) 148.dp + navBarHeight else 84.dp + navBarHeight
    }

    val loggedInEmail by viewModel.loggedInEmail.collectAsState()
    val loggedInName by viewModel.loggedInName.collectAsState()
    val loggedInPhone by viewModel.loggedInPhone.collectAsState()
    val isAdmin by viewModel.isCurrentUserAdmin.collectAsState()
    val enableBackgroundMotion by viewModel.enableBackgroundMotion.collectAsState()
    val animSpeed by viewModel.animationSpeed.collectAsState()
    val animBounciness by viewModel.animationBounciness.collectAsState()

    val filterOutSmallFiles by viewModel.filterOutSmallFiles.collectAsState()
    val filterOutShortAudios by viewModel.filterOutShortAudios.collectAsState()
    val cachedAudioSize by viewModel.cachedAudioSize.collectAsState()

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

    val accentColor = MaterialTheme.colorScheme.primary
    val pinkAccent = MaterialTheme.colorScheme.primary
    val containerBg = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = bottomSpacing),
        modifier = Modifier.fillMaxSize()
                                
    ) {

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    
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
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
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
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
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
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Manually refresh catalog of offline audio files",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = { viewModel.scanLocalFiles(force = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan Now", fontSize = 11.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }


        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Aesthetics & Cache",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aesthetics & Cache",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Dark Mode",
                    subtitle = "Toggle dark theme visuals",
                    checked = isDarkMode,
                    onCheckedChange = { if (it != isDarkMode) viewModel.toggleTheme() },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(12.dp))

                val dynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsState()
                val artworkThemeEnabled by viewModel.artworkThemeEnabled.collectAsState()

                Spacer(modifier = Modifier.height(8.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Material 3 Wallpaper Sync",
                    subtitle = "Automatically set application colors using Android system wallpaper palette",
                    checked = dynamicColorEnabled,
                    onCheckedChange = { viewModel.setDynamicColorEnabled(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(8.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Match Album Art Colors",
                    subtitle = "Dynamically extract and apply active track artwork colors as application's theme",
                    checked = artworkThemeEnabled,
                    onCheckedChange = { viewModel.setArtworkThemeEnabled(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(8.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Ambient Particle Motion",
                    subtitle = "Enable dynamic drifting cherry blossom petals and background physics animation",
                    checked = enableBackgroundMotion,
                    onCheckedChange = { viewModel.setEnableBackgroundMotion(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Spacer(modifier = Modifier.height(8.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Storage and Cache management
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Streaming Stream Cache",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Used size: $cachedAudioSize",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = { viewModel.clearCachedAudioStreams() },
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.height(32.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Clear Cache",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }


        item {
            val videoDuckingEnabled by viewModel.videoDuckingEnabled.collectAsState()
            val videoDuckingVolume by viewModel.videoDuckingVolume.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                            contentDescription = "Smart Video Ducking",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Video Ducking",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (videoDuckingEnabled) Color(0x3300FF66) else Color(0x1F9E9E9E))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (videoDuckingEnabled) "ACTIVE" else "DISABLED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (videoDuckingEnabled) Color(0xFF00FF66) else Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Duck on Video Playback",
                    subtitle = "Reduce music volume instead of pausing when playing videos on YouTube, Instagram, or browsers (except call apps)",
                    checked = videoDuckingEnabled,
                    onCheckedChange = { viewModel.setVideoDuckingEnabled(it) },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                if (videoDuckingEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsVolumeSliderRow(
                        title = "Selected Volume Level",
                        subtitle = "Target volume level during concurrent video playback",
                        value = videoDuckingVolume,
                        valueRange = 0f..1.0f,
                        onValueChange = { viewModel.setVideoDuckingVolume(it) },
                        isDarkMode = isDarkMode,
                        accentColor = accentColor
                    )
                }
            }
        }


        item {
            val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
            val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
            val sleepMinutesLeft by viewModel.sleepTimerMinutesLeft.collectAsState()
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerBg, RoundedCornerShape(24.dp))
                    
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Sleep Timer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MyuLoc Sleep Timer",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sleepRunning) Color(0x3300FF66) else Color(0x1F9E9E9E))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (sleepRunning) "ACTIVE" else "STANDBY",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sleepRunning) Color(0xFF00FF66) else Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                SettingsSwitchRow(
                    title = "Enable Sleep Timer",
                    subtitle = if (sleepRunning) {
                        val minutesStr = sleepSecondsLeft / 60
                        val secondsStr = String.format(java.util.Locale.US, "%02d", sleepSecondsLeft % 60)
                        "Countdown: $minutesStr:$secondsStr remaining"
                    } else {
                        "Automatically stops music playback when timer lapses"
                    },
                    checked = sleepRunning,
                    onCheckedChange = { enable ->
                        if (enable) {
                            viewModel.startSleepTimer(30)
                        } else {
                            viewModel.stopSleepTimer()
                        }
                    },
                    isDarkMode = isDarkMode,
                    accentColor = accentColor
                )

                if (sleepRunning) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Quick Adjust Time:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 15, 30, 45, 60).forEach { mins ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(if (Math.abs(sleepMinutesLeft - mins) < 2) accentColor else (androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant))
                                    




                                    .bouncyClickable {
                                        viewModel.startSleepTimer(mins)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (Math.abs(sleepMinutesLeft - mins) < 2) Color.White else (androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
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
                    
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MyuLoc Ecosystem",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Version 1.0.0 (Minimalist Capsule Design)",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(14.dp))
                CustomAppLogo(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }
        }
    }

    if (showStorageDialog) {
        val storageAccent = MaterialTheme.colorScheme.primary
        val dialogBg = androidx.compose.material3.MaterialTheme.colorScheme.surface
        val cardBg = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
        
        Dialog(onDismissRequest = { showStorageDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(dialogBg)
                    
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
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
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
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
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
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Close",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
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
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
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
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                checkedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
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
    val view = androidx.compose.ui.platform.LocalView.current
    var lastTickValue by remember { mutableIntStateOf(value) }
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
            onValueChange = { 
                val newInt = it.toInt()
                if (newInt != lastTickValue) {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    lastTickValue = newInt
                }
                onValueChange(newInt) 
            },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SettingsVolumeSliderRow(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    isDarkMode: Boolean,
    accentColor: Color
) {
    val view = androidx.compose.ui.platform.LocalView.current
    var lastTickValue by remember { mutableIntStateOf((value * 100).toInt()) }
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "${(value * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Slider(
            value = value.coerceIn(valueRange),
            onValueChange = { 
                val newInt = (it * 100).toInt()
                if (newInt != lastTickValue) {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    lastTickValue = newInt
                }
                onValueChange(it) 
            },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
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



private val SwipeSpringSpec = androidx.compose.animation.core.spring<Float>(
    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
)

private val AlphaSpringSpec = androidx.compose.animation.core.spring<Float>(
    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
)

private val ScaleSpringSpec = androidx.compose.animation.core.spring<Float>(
    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
)


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
        count == 1 -> "1 play"
        else -> "$count plays"
    }
}

@Composable
fun SkeletonTrackCapsuleItem(isDarkMode: Boolean) {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(1000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )

    val containerBg = if (isDarkMode) Color.DarkGray.copy(alpha = alpha) else Color.LightGray.copy(alpha = alpha)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDarkMode) Color.Gray.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.6f).clip(RoundedCornerShape(4.dp)).background(if (isDarkMode) Color.Gray.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.2f)))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.height(12.dp).fillMaxWidth(0.4f).clip(RoundedCornerShape(4.dp)).background(if (isDarkMode) Color.Gray.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.2f)))
        }
        Spacer(modifier = Modifier.width(16.dp))
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
    modifier: Modifier = Modifier,
    isScrollingFast: Boolean = false,
    index: Int = -1
) {
    val favoriteTrackIds by viewModel.favoriteTrackIds.collectAsState(initial = emptySet())
    val downloadProgressMap by viewModel.downloadProgress.collectAsState()
    val offlineTrackIds by viewModel.offlineTrackIds.collectAsState()
    val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isPlayingState by viewModel.playerManager.isPlaying.collectAsState()
    val isAudioDeliveringSoundState by viewModel.playerManager.isAudioDeliveringSound.collectAsState()
    val playCountsMapState by viewModel.playCounts.collectAsState()

    val isLiked = remember(favoriteTrackIds, track.id) { favoriteTrackIds.contains(track.id) }
    val progress = remember(downloadProgressMap, track.id) { downloadProgressMap[track.id] }
    val isDownloaded = remember(offlineTrackIds, track.id) { offlineTrackIds.contains(track.id) }
    val isCurrent = currentTrack?.id == track.id
    val isPlaying = isPlayingState
    val isAudioDeliveringSound = isAudioDeliveringSoundState
    val playCount = remember(playCountsMapState, track.id) { playCountsMapState[track.id] ?: 0 }

    TrackCapsuleItem(
        track = track,
        viewModel = viewModel,
        isLiked = isLiked,
        progress = progress,
        isDownloaded = isDownloaded,
        isCurrent = isCurrent,
        isPlaying = isPlaying,
        isAudioDeliveringSound = isAudioDeliveringSound,
        playCount = playCount,
        isDarkMode = isDarkMode,
        onTrackClick = onTrackClick,
        onFavoriteToggle = onFavoriteToggle,
        modifier = modifier,
        index = index
    )
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TrackCapsuleItem(
    track: PlayerTrack,
    viewModel: MyuLocViewModel,
    isLiked: Boolean,
    progress: Int?,
    isDownloaded: Boolean,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isAudioDeliveringSound: Boolean,
    playCount: Int,
    isDarkMode: Boolean,
    onTrackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isScrollingFast: Boolean = false,
    index: Int = -1
) {
    val containerBrush = getGlassBackground(isDarkMode)
    val borderBrush = getGlassBorder(isDarkMode)
    val textPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val iconColor = MaterialTheme.colorScheme.primary

    val displayPlayCount = playCount

    val isHovered = remember { mutableStateOf(false) }
    val pointerInteraction = remember { MutableInteractionSource() }
    val isPressed by pointerInteraction.collectIsPressedAsState()

    val animatedScaleState = androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.95f else (if (isCurrent) 1.03f else (if (isHovered.value) 1.02f else 1.0f)),
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "track_scale"
    )

    val hoverBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isHovered.value) {
            androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
        } else {
            androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
        },
        animationSpec = androidx.compose.animation.core.tween(150),
        label = "hover_bg"
    )

    val borderThickness = if (isCurrent) 1.5.dp else 0.5.dp
    val computedBorderBrush = if (isCurrent) {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(iconColor, iconColor.copy(alpha = 0.5f))
        )
    } else if (isHovered.value) {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(iconColor.copy(alpha = 0.4f), iconColor.copy(alpha = 0.15f))
        )
    } else {
        borderBrush
    }

    val isMultiSelect by viewModel.isMultiSelectMode.collectAsState()
    val selectedIds by viewModel.selectedDeleteIds.collectAsState()
    val isSelected = selectedIds.contains(track.id)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .graphicsLayer {
                scaleX = animatedScaleState.value
                scaleY = animatedScaleState.value
            }
            .pointerInput(track.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            androidx.compose.ui.input.pointer.PointerEventType.Enter -> isHovered.value = true
                            androidx.compose.ui.input.pointer.PointerEventType.Exit -> isHovered.value = false
                        }
                    }
                }
            }
            .background(
                if (isSelected) {
                    androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                } else if (isCurrent) {
                    androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else if (isHovered.value) {
                    androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.Transparent
                },
                RoundedCornerShape(12.dp)
            )
            .let {
                if (isCurrent) {
                    it.border(
                        width = 1.5.dp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else it
            }
            .combinedClickable(
                interactionSource = pointerInteraction,
                indication = androidx.compose.foundation.LocalIndication.current,
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCurrent) {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(4.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(iconColor)
            )
        }

        if (isMultiSelect) {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) iconColor else Color.Transparent),
                    
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

        // Titles block formatted exactly like the custom chalkboard sketch image
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 0.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.title,
                fontSize = 15.sp,
                fontFamily = com.example.ui.theme.CaveatFontFamily,
                fontWeight = FontWeight.Bold,
                color = if (isCurrent) iconColor else textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Smartphone outline icon to match the image precisely
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.7f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = track.artist,
                    fontSize = 13.sp,
                    fontFamily = com.example.ui.theme.CaveatFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = textPrimary.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (track.source == "Locker") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF7C3AED).copy(alpha = 0.35f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Cloud",
                            fontSize = 9.sp,
                            fontFamily = com.example.ui.theme.CaveatFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA78BFA)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = formatPlayCount(displayPlayCount),
                    fontSize = 13.sp,
                    fontFamily = com.example.ui.theme.CaveatFontFamily,
                    color = Color.Gray.copy(alpha = 0.6f),
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
                        tint = Color(0xFF00C853),
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                val currentProgress = progress
                if (currentProgress != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(36.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { currentProgress / 100f },
                            color = iconColor,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$currentProgress",
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
        }

        // Beautiful custom frequency bars audio visualizer
        if (isCurrent && isAudioDeliveringSound) {
            PlayingAudioWaveVisualizer(color = iconColor)
            Spacer(modifier = Modifier.width(8.dp))
        }

        // High contrast Heart Icon
        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isLiked) "Remove from Favorites" else "Add to Favorites",
                tint = if (isLiked) Color.Red else Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Modern 3-dot contextual drop menu
        var showMenu by remember { mutableStateOf(false) }
        Box(modifier = Modifier.padding(end = 4.dp)) {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = textPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
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
                    text = { Text("Share Track", color = textPrimary, fontSize = 13.sp) },
                    onClick = {
                        showMenu = false
                        viewModel.shareTrack(context, track)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = iconColor, modifier = Modifier.size(16.dp))
                    }
                )
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
                .fillMaxHeight()
                .graphicsLayer {
                    scaleY = scale1
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                }
                .background(color, RoundedCornerShape(percent = 50))
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .graphicsLayer {
                    scaleY = scale2
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                }
                .background(color, RoundedCornerShape(percent = 50))
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .graphicsLayer {
                    scaleY = scale3
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                }
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
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
    
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val activeAccent = MaterialTheme.colorScheme.primary
    val view = androidx.compose.ui.platform.LocalView.current

    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
    val duration by viewModel.playerManager.duration.collectAsState()
    val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    var sliderValue by remember { mutableStateOf<Float?>(null) }
    var lastTickValue by remember { mutableIntStateOf(-1) }
    
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
            onValueChange = { 
                sliderValue = it 
                val newInt = (it * 100).toInt()
                if (newInt != lastTickValue) {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    lastTickValue = newInt
                }
            },
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
                    Canvas(modifier = Modifier.fillMaxSize()
                                ) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val scale = 0.85f
                        val petalW = 6f * scale
                        val petalH = 12f * scale
                        
                        val petalColor = activeAccent // Adaptive dynamic theme tone
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
                val outlineVariantColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                Canvas(modifier = Modifier.fillMaxWidth().height(4.dp)) {
                    val centerY = size.height / 2f
                    val width = size.width
                    drawLine(
                        color = outlineVariantColor,
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

// --- Anchored bottom player bar ---

@Composable
fun AnchoredPlayerBar(
    track: PlayerTrack,
    isPlaying: Boolean,
    isAudioDeliveringSound: Boolean,
    viewModel: MyuLocViewModel,
    isBuffering: Boolean,
    isDarkMode: Boolean,
    shuffleMode: com.example.player.MusicPlayerManager.MyuLocShuffleMode,
    repeatMode: PlaybackRepeatMode,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onCapsuleClick: () -> Unit
) {
    val activeAccent = MaterialTheme.colorScheme.primary
    val iconTint = if (isDarkMode) Color(0xFFE2E8F0) else activeAccent
    val activeIconTint = activeAccent
    
    val shuffleActive = shuffleMode != com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF
    val shuffleIconTint = if (shuffleActive) activeIconTint else iconTint.copy(alpha = 0.5f)
    
    val repeatActive = repeatMode != PlaybackRepeatMode.OFF
    val repeatIconTint = if (repeatActive) activeIconTint else iconTint.copy(alpha = 0.5f)
    val repeatIcon = when (repeatMode) {
        PlaybackRepeatMode.ONE -> Icons.Default.RepeatOne
        else -> Icons.Default.Repeat
    }

    val textColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val artistColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf<Color?>(null) }
    
    LaunchedEffect(track.thumbnailUrl) {
        if (!track.thumbnailUrl.isNullOrEmpty()) {
            try {
                withContext(Dispatchers.Default) {
                    val req = coil.request.ImageRequest.Builder(context)
                        .data(track.thumbnailUrl)
                        .size(100)
                        .allowHardware(false)
                        .build()
                    val result = context.imageLoader.execute(req)
                    if (result is coil.request.SuccessResult) {
                        val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                        bitmap?.let { b ->
                            val palette = androidx.palette.graphics.Palette.from(b).generate()
                            val c = palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb ?: palette.darkVibrantSwatch?.rgb
                            if (c != null) {
                                withContext(Dispatchers.Main) {
                                    dominantColor = Color(c)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore background extraction errors safely
            }
        } else {
            dominantColor = null
        }
    }

    val baseBgColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    
    val targetMiniPlayerBgColor = if (dominantColor != null) {
        if (isDarkMode) dominantColor!!.copy(alpha = 0.15f).compositeOver(baseBgColor) 
        else dominantColor!!.copy(alpha = 0.1f).compositeOver(baseBgColor)
    } else {
        baseBgColor
    }
    
    val targetMiniPlayerBorderColor = if (isDarkMode) {
        dominantColor?.copy(alpha = 0.3f) ?: Color(0x1BFFFFFF)
    } else {
        dominantColor?.copy(alpha = 0.2f) ?: Color(0x1A000000)
    }

    val swipeOffset = remember { androidx.compose.animation.core.Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val miniPlayerBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = targetMiniPlayerBgColor,
        animationSpec = tween(800),
        label = "mini_player_bg_anim"
    )

    val miniPlayerBorderColor by androidx.compose.animation.animateColorAsState(
        targetValue = targetMiniPlayerBorderColor,
        animationSpec = tween(800),
        label = "mini_player_border_anim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset { androidx.compose.ui.unit.IntOffset(swipeOffset.value.toInt(), 0) }
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .pointerInput(Unit) {
                var cumulativeDragX = 0f
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (cumulativeDragX > 150f) {
                                // Swiped Right: Skip Previous
                                swipeOffset.animateTo(size.width.toFloat(), tween(200))
                                onSkipPrevious()
                                swipeOffset.snapTo(-size.width.toFloat())
                                swipeOffset.animateTo(0f, tween(300))
                            } else if (cumulativeDragX < -150f) {
                                // Swiped Left: Skip Next
                                swipeOffset.animateTo(-size.width.toFloat(), tween(200))
                                onSkipNext()
                                swipeOffset.snapTo(size.width.toFloat())
                                swipeOffset.animateTo(0f, tween(300))
                            } else {
                                // Snap back
                                swipeOffset.animateTo(0f, androidx.compose.animation.core.spring())
                            }
                            cumulativeDragX = 0f
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch { swipeOffset.animateTo(0f, androidx.compose.animation.core.spring()) }
                        cumulativeDragX = 0f
                    },
                    onDrag = { change: androidx.compose.ui.input.pointer.PointerInputChange, dragAmount: androidx.compose.ui.geometry.Offset ->
                        change.consume()
                        val isVerticalSwipe = Math.abs(dragAmount.y) > Math.abs(dragAmount.x) * 1.5f
                        if (isVerticalSwipe && cumulativeDragX == 0f) {
                            if (dragAmount.y < -12f) {
                                onCapsuleClick()
                            }
                        } else {
                            cumulativeDragX += dragAmount.x
                            coroutineScope.launch {
                                swipeOffset.snapTo(cumulativeDragX)
                            }
                        }
                    }
                )
            }
            
            .clickable { onCapsuleClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = miniPlayerBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Elegant thin progress indicator perfectly flush at the top of the capsule card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
            ) {
                
    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
    val duration by viewModel.playerManager.duration.collectAsState()
                val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
                val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = progressFraction,
                    animationSpec = androidx.compose.animation.core.tween(
                        durationMillis = if (isPlaying) 300 else 0,
                        easing = androidx.compose.animation.core.LinearEasing
                    ),
                    label = "SmoothMusicSlider"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth().graphicsLayer { scaleX = animatedProgress; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f) }
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                 )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Single unified, compact control Row matching screenshot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.animation.AnimatedContent(
                    targetState = track,
                    transitionSpec = {
                        androidx.compose.animation.fadeIn(
                            animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                        ) togetherWith androidx.compose.animation.fadeOut(
                            animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    label = "mini_player_crossfade"
                ) { current ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Album art with rounded corners
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!current.thumbnailUrl.isNullOrEmpty()) {
                                val miniArtRequest = rememberOptimizedImageRequest(
                                    data = current.thumbnailUrl,
                                    sizePx = 120
                                )
                                AsyncImage(
                                    model = miniArtRequest,
                                    contentDescription = "Cover Art",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = current.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = current.artist,
                                fontSize = 13.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        IconButton(onClick = { viewModel.playerManager.togglePlayPause() }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        IconButton(onClick = { viewModel.playerManager.skipNext() }) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
}

}
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ExpandedPlayerView(
    viewModel: com.example.viewmodel.MyuLocViewModel,
    isDarkMode: Boolean,
    expansionProgress: Float,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onDismiss: () -> Unit,
    onSleepDialogShow: () -> Unit
) {
    val topPadding = innerPadding.calculateTopPadding()
    val enableBackgroundMotion = true
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val sleepSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()
    val sleepRunning by viewModel.sleepTimerRunning.collectAsState()
    val sleepMinutesLeft = (sleepSecondsLeft / 60)

            val animSpeed = 0.8f
            val animBounciness = 0.4f
            
            val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
            val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
            val activeAccent = androidx.compose.material3.MaterialTheme.colorScheme.primary
            val textPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            val textSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            
            val queueList by viewModel.playerManager.queue.collectAsState()
            val shuffleMode by viewModel.playerManager.shuffleMode.collectAsState()
            val repeatMode by viewModel.playerManager.repeatMode.collectAsState()
            val shuffleInterval by viewModel.playerManager.shuffleInterval.collectAsState()
    var showRemainingTime by remember { mutableStateOf(false) }
            val favoriteTrackIds by viewModel.favoriteTrackIds.collectAsState(initial = emptySet())
            val eqEnabled by viewModel.playerManager.eqEnabled.collectAsState()
            var activePreset by remember { mutableStateOf("Custom") }
            val eqBands by viewModel.playerManager.eqBands.collectAsState()
            val view = androidx.compose.ui.platform.LocalView.current

            
            var showQueuePanel by remember { mutableStateOf(false) }
            var showTrackDetailsDialogInPlayer by remember { mutableStateOf(false) }
            var showShuffleIntervalDialog by remember { mutableStateOf(false) }
            
            val isAudioDeliveringSound = isPlaying
            var maxVinylSize by remember { mutableStateOf(0f) }
            var outerBoxPositionInWindow by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
            var targetCenter by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
            var showEqPanel by remember { mutableStateOf(false) }
            var currentCenterX by remember { mutableStateOf(0f) }
            var currentCenterY by remember { mutableStateOf(0f) }
            var currentSize by remember { mutableStateOf(0f) }
            var queueTransitionProgress by remember { mutableStateOf(0f) }
            var controlsAlpha by remember { mutableStateOf(1f) }
            
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {
        // Background Artwork Crossfade
        androidx.compose.animation.AnimatedContent(
            targetState = currentTrack,
            transitionSpec = {
                androidx.compose.animation.fadeIn(animationSpec = tween(700)) togetherWith androidx.compose.animation.fadeOut(animationSpec = tween(700))
            },
            label = "background_artwork_crossfade",
            modifier = Modifier.fillMaxSize()
        ) { track ->
            if (track != null && !track.thumbnailUrl.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = rememberOptimizedImageRequest(track.thumbnailUrl, 16),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { 
                                alpha = if (isDarkMode) 0.35f else 0.6f
                            },
                        filterQuality = androidx.compose.ui.graphics.FilterQuality.High
                    )
                        // Add a gradient overlay to blend into the background and ensure high-contrast text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = if (isDarkMode) 0.6f else 0.2f),
                                            Color.Black.copy(alpha = if (isDarkMode) 0.2f else 0.05f),
                                            Color.Black.copy(alpha = if (isDarkMode) 0.85f else 0.4f)
                                        )
                                    )
                                )
                        )
                    }
                } else {
                    BackgroundAmbientMesh(isDarkMode = isDarkMode, enableMotion = enableBackgroundMotion)
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                                
                .padding(
                    top = topPadding,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .graphicsLayer {
                    // Smoothly scale down background elements from 1.0f to 0.95f when queue is open
                    val s = 1f - (0.05f * queueTransitionProgress)
                    scaleX = s
                    scaleY = s
                    // Smoothly fade out completely when queue is open to prevent overlapping text/clicks
                    alpha = expansionProgress * (1f - queueTransitionProgress)
                    translationY = 40.dp.toPx() * (1f - expansionProgress)
                },
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

                    // Top-Right 3-dots Dropdown Menu (Replacing the Equalizer shortcut)
                    var expandedTopMenu by remember { mutableStateOf(false) }
                    val isCurrentFavorite = remember(favoriteTrackIds, currentTrack) {
                        currentTrack?.let { ct -> favoriteTrackIds.contains(ct.id) } ?: false
                    }

                    Box {
                        IconButton(onClick = { expandedTopMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = textPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = expandedTopMenu,
                            onDismissRequest = { expandedTopMenu = false },
                            modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isCurrentFavorite) "Remove from Favorites" else "Add to Favorites", color = textPrimary) },
                                onClick = {
                                    expandedTopMenu = false
                                    currentTrack?.let { viewModel.toggleFavorite(it) }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isCurrentFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite Toggle",
                                        tint = if (isCurrentFavorite) Color.Red else activeAccent
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Equalizer Control", color = textPrimary) },
                                onClick = {
                                    expandedTopMenu = false
                                    showEqPanel = !showEqPanel
                                },
                                leadingIcon = { Icon(Icons.Default.Equalizer, null, tint = activeAccent) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (sleepRunning) "Sleep Timer: Active (${sleepMinutesLeft}m)" else "Set Sleep Timer", color = textPrimary) },
                                onClick = {
                                    expandedTopMenu = false
                                    onSleepDialogShow()
                                },
                                leadingIcon = { Icon(Icons.Default.Schedule, null, tint = activeAccent) }
                            )
                            DropdownMenuItem(
                                text = { Text("Track Details", color = textPrimary) },
                                onClick = {
                                    expandedTopMenu = false
                                    showTrackDetailsDialogInPlayer = true
                                },
                                leadingIcon = { Icon(Icons.Default.Info, null, tint = activeAccent) }
                            )
                            DropdownMenuItem(
                                text = { Text("Playback Queue (${queueList.size})", color = textPrimary) },
                                onClick = {
                                    expandedTopMenu = false
                                    showQueuePanel = true
                                },
                                leadingIcon = { Icon(Icons.Default.Queue, null, tint = activeAccent) }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Track", color = textPrimary) },
                                onClick = {
                                    expandedTopMenu = false
                                    currentTrack?.let { viewModel.shareTrack(context, it) }
                                },
                                leadingIcon = { Icon(Icons.Default.Share, null, tint = activeAccent) }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear All Queue", color = textPrimary) },
                                onClick = {
                                    expandedTopMenu = false
                                    viewModel.clearQueue()
                                },
                                leadingIcon = { Icon(Icons.Default.DeleteSweep, null, tint = Color.Gray) }
                            )
                        }
                    }
                }
                                // Adaptive Layout for Landscape and Portrait
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .size(maxVinylSize.dp)
                                    .onGloballyPositioned { coordinates ->
                                        val positionInWindow = coordinates.positionInWindow()
                                        targetCenter = androidx.compose.ui.geometry.Offset(
                                            x = positionInWindow.x + coordinates.size.width / 2f - outerBoxPositionInWindow.x,
                                            y = positionInWindow.y + coordinates.size.height / 2f - outerBoxPositionInWindow.y
                                        )
                                    }
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Track Info & Options (Completely symmetrical and beautifully centered)
                    androidx.compose.animation.AnimatedContent(
                        targetState = currentTrack,
                        transitionSpec = {
                            androidx.compose.animation.fadeIn(
                                animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            ) togetherWith androidx.compose.animation.fadeOut(
                                animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            )
                        },
                    label = "track_info_crossfade",
                    modifier = Modifier.fillMaxWidth()
                ) { track ->
                    if (track != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Favorite toggle button
                            val isCurrentFavorite = remember(favoriteTrackIds, track) {
                                favoriteTrackIds.contains(track.id)
                            }
                            IconButton(
                                onClick = { viewModel.toggleFavorite(track) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Toggle Favorite",
                                    tint = if (isCurrentFavorite) Color.Red else textPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = track.title,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )

                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = track.artist + "  •  " + track.source,
                                    fontSize = 13.sp,
                                    color = textPrimary.copy(alpha = 0.65f),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            // Symmetrically placed neat trailing options (Queue)
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { showQueuePanel = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Queue,
                                        contentDescription = "Show Active Queue",
                                        tint = textPrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Thin progress line where progress is shown by dark cherry flower thumb
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
                    val duration by viewModel.playerManager.duration.collectAsState()
                    val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
                    var sliderValue by remember { mutableStateOf<Float?>(null) }
                    var lastTickValue by remember { mutableIntStateOf(-1) }
                    val view = androidx.compose.ui.platform.LocalView.current
                    
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
                        onValueChange = { 
                            sliderValue = it
                            val newInt = (it * 100).toInt()
                            if (newInt != lastTickValue) {
                                view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                                lastTickValue = newInt
                            }
                        },
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
                                    .size(16.dp)
                                    .background(activeAccent, androidx.compose.foundation.shape.CircleShape)
                            )
                        },
                        track = { _ ->
                            val activeColor = MaterialTheme.colorScheme.primary
                            val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
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
                    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
                    val duration by viewModel.playerManager.duration.collectAsState()
                    Text(
                        text = formatDuration(currentPosition),
                        fontSize = 10.sp,
                        color = textSecondary,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = true
                            ),
                            lineHeight = 14.sp
                        )
                    )
                    Text(
                        text = if (showRemainingTime && duration > currentPosition) "-${formatDuration(duration - currentPosition)}" else formatDuration(duration),
                        fontSize = 10.sp,
                        color = textSecondary,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = true
                            ),
                            lineHeight = 14.sp
                        ),
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null
                            ) {
                                showRemainingTime = !showRemainingTime
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val playProgress by animateFloatAsState(
                    targetValue = if (isPlaying) 0f else 1f,
                    animationSpec = tunedSpring(animSpeed, animBounciness),
                    label = "play_pause_morph"
                )

                // Immersive Audio/Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle Mode Controller Key supporting 3 modes with real-time HUD indicator overlays
                    Box(contentAlignment = Alignment.TopEnd) {
                        val shuffleInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .combinedClickable(
                                    interactionSource = shuffleInteractionSource,
                                    indication = androidx.compose.material3.ripple(bounded = true, radius = 24.dp),
                                    onClick = { 
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                                view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                                            } else {
                                                view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                                            }
                                        } catch (e: Exception) {}
                                        val nextMode = when (shuffleMode) {
                                            com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD
                                            com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC
                                            com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF
                                        }
                                        viewModel.playerManager.shuffleMode.value = nextMode
                                        if (nextMode == com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC) {
                                            showShuffleIntervalDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        showShuffleIntervalDialog = true
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle Mode",
                                tint = when (shuffleMode) {
                                    com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF -> textSecondary
                                    com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD -> activeAccent
                                    com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC -> activeAccent
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        if (shuffleMode == com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC) {
                            val remaining = (shuffleInterval - (viewModel.playerManager.songsPlayedInBlock - 1)).coerceAtLeast(1)
                            Box(
                                modifier = Modifier
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .background(activeAccent, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$remaining",
                                    color = Color.White,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (shuffleMode == com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .background(activeAccent, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    color = Color.White,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    BouncyIconButton(
                        onClick = { viewModel.playerManager.skipPrevious() },
                        hoverScale = 1.15f,
                        pressScale = 0.85f
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track", tint = textPrimary, modifier = Modifier.size(28.dp))
                    }
                    
                    BouncyIconButton(
                        onClick = { viewModel.playerManager.togglePlayPause() },
                        modifier = Modifier
                            .size(72.dp)
                            .background(activeAccent.copy(alpha = 0.05f), CircleShape),
                            
                        hoverScale = 1.1f,
                        pressScale = 0.9f
                    ) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            val w = size.width
                            val h = size.height
                            val path = Path()
                            val controlColor = Color.White
                            if (playProgress > 0.01f) {
                                // Draw standard play triangle (progressively mapped inside bounds)
                                val scaleWidth = 0.25f + 0.6f * playProgress
                                path.moveTo(w * 0.25f, h * 0.2f)
                                path.lineTo(w * scaleWidth, h * 0.5f)
                                path.lineTo(w * 0.25f, h * 0.8f)
                                path.close()
                                drawPath(path, color = controlColor)
                            }
                            if (playProgress < 0.99f) {
                                // Draw custom pause lines dynamically
                                val alpha = (1f - playProgress).coerceIn(0f, 1f)
                                val barW = w * 0.25f
                                val barGap = w * 0.2f
                                drawRect(
                                    color = controlColor,
                                    topLeft = Offset(w * 0.2f, h * 0.2f),
                                    size = androidx.compose.ui.geometry.Size(barW, h * 0.6f),
                                    alpha = alpha
                                )
                                drawRect(
                                    color = controlColor,
                                    topLeft = Offset(w * 0.2f + barW + barGap, h * 0.2f),
                                    size = androidx.compose.ui.geometry.Size(barW, h * 0.6f),
                                    alpha = alpha
                                )
                            }
                        }
                    }
                    
                    BouncyIconButton(
                        onClick = { viewModel.playerManager.skipNext() },
                        hoverScale = 1.15f,
                        pressScale = 0.85f
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Track", tint = textPrimary, modifier = Modifier.size(28.dp))
                    }

                    // Loop/Repeat Mode Controller Key
                    val repeatIcon = when (repeatMode) {
                        PlaybackRepeatMode.ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    }
                    BouncyIconButton(
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
                    
                }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Spacer(
                            modifier = Modifier
                                .size(maxVinylSize.dp)
                                .onGloballyPositioned { coordinates ->
                                    val positionInWindow = coordinates.positionInWindow()
                                    targetCenter = androidx.compose.ui.geometry.Offset(
                                        x = positionInWindow.x + coordinates.size.width / 2f - outerBoxPositionInWindow.x,
                                        y = positionInWindow.y + coordinates.size.height / 2f - outerBoxPositionInWindow.y
                                    )
                                }
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
// Track Info & Options (Completely symmetrical and beautifully centered)
                    androidx.compose.animation.AnimatedContent(
                        targetState = currentTrack,
                        transitionSpec = {
                            androidx.compose.animation.fadeIn(
                                animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            ) togetherWith androidx.compose.animation.fadeOut(
                                animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            )
                        },
                    label = "track_info_crossfade",
                    modifier = Modifier.fillMaxWidth()
                ) { track ->
                    if (track != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Favorite toggle button
                            val isCurrentFavorite = remember(favoriteTrackIds, track) {
                                favoriteTrackIds.contains(track.id)
                            }
                            IconButton(
                                onClick = { viewModel.toggleFavorite(track) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Toggle Favorite",
                                    tint = if (isCurrentFavorite) Color.Red else textPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = track.title,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )

                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = track.artist + "  •  " + track.source,
                                    fontSize = 13.sp,
                                    color = textPrimary.copy(alpha = 0.65f),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            // Symmetrically placed neat trailing options (Queue)
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { showQueuePanel = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Queue,
                                        contentDescription = "Show Active Queue",
                                        tint = textPrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Thin progress line where progress is shown by dark cherry flower thumb
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
                    val duration by viewModel.playerManager.duration.collectAsState()
                    val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
                    var sliderValue by remember { mutableStateOf<Float?>(null) }
                    var lastTickValue by remember { mutableIntStateOf(-1) }
                    val view = androidx.compose.ui.platform.LocalView.current
                    
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
                        onValueChange = { 
                            sliderValue = it
                            val newInt = (it * 100).toInt()
                            if (newInt != lastTickValue) {
                                view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                                lastTickValue = newInt
                            }
                        },
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
                                Canvas(modifier = Modifier.fillMaxSize()
                                ) {
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f
                                    val petalW = 7f
                                    val petalH = 14f
                                    
                                    val petalColor = activeAccent // Dark Cherry Pink
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
                            val activeColor = MaterialTheme.colorScheme.primary
                            val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
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
                    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
                    val duration by viewModel.playerManager.duration.collectAsState()
                    Text(
                        text = formatDuration(currentPosition),
                        fontSize = 10.sp,
                        color = textSecondary,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = true
                            ),
                            lineHeight = 14.sp
                        )
                    )
                    Text(
                        text = if (showRemainingTime && duration > currentPosition) "-${formatDuration(duration - currentPosition)}" else formatDuration(duration),
                        fontSize = 10.sp,
                        color = textSecondary,
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = true
                            ),
                            lineHeight = 14.sp
                        ),
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = null
                            ) {
                                showRemainingTime = !showRemainingTime
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val playProgress by animateFloatAsState(
                    targetValue = if (isPlaying) 0f else 1f,
                    animationSpec = tunedSpring(animSpeed, animBounciness),
                    label = "play_pause_morph"
                )

                // Immersive Audio/Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle Mode Controller Key supporting 3 modes with real-time HUD indicator overlays
                    Box(contentAlignment = Alignment.TopEnd) {
                        val shuffleInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .combinedClickable(
                                    interactionSource = shuffleInteractionSource,
                                    indication = androidx.compose.material3.ripple(bounded = true, radius = 24.dp),
                                    onClick = { 
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                                view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                                            } else {
                                                view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                                            }
                                        } catch (e: Exception) {}
                                        val nextMode = when (shuffleMode) {
                                            com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD
                                            com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC
                                            com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF
                                        }
                                        viewModel.playerManager.shuffleMode.value = nextMode
                                        if (nextMode == com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC) {
                                            showShuffleIntervalDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        showShuffleIntervalDialog = true
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle Mode",
                                tint = when (shuffleMode) {
                                    com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF -> textSecondary
                                    com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD -> activeAccent
                                    com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC -> activeAccent
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        if (shuffleMode == com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC) {
                            val remaining = (shuffleInterval - (viewModel.playerManager.songsPlayedInBlock - 1)).coerceAtLeast(1)
                            Box(
                                modifier = Modifier
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .background(activeAccent, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$remaining",
                                    color = Color.White,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (shuffleMode == com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD) {
                            Box(
                                modifier = Modifier
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .background(activeAccent, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    color = Color.White,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    BouncyIconButton(
                        onClick = { viewModel.playerManager.skipPrevious() },
                        hoverScale = 1.15f,
                        pressScale = 0.85f
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track", tint = textPrimary, modifier = Modifier.size(28.dp))
                    }
                    
                    BouncyIconButton(
                        onClick = { viewModel.playerManager.togglePlayPause() },
                        modifier = Modifier
                            .size(72.dp)
                            .background(activeAccent.copy(alpha = 0.05f), CircleShape),
                            
                        hoverScale = 1.1f,
                        pressScale = 0.9f
                    ) {
                        Canvas(modifier = Modifier.size(30.dp)) {
                            val w = size.width
                            val h = size.height
                            val path = Path()
                            val controlColor = Color.White
                            if (playProgress > 0.01f) {
                                // Draw standard play triangle (progressively mapped inside bounds)
                                val scaleWidth = 0.25f + 0.6f * playProgress
                                path.moveTo(w * 0.25f, h * 0.2f)
                                path.lineTo(w * scaleWidth, h * 0.5f)
                                path.lineTo(w * 0.25f, h * 0.8f)
                                path.close()
                                drawPath(path, color = controlColor)
                            }
                            if (playProgress < 0.99f) {
                                // Draw custom pause lines dynamically
                                val alpha = (1f - playProgress).coerceIn(0f, 1f)
                                val barW = w * 0.25f
                                val barGap = w * 0.2f
                                drawRect(
                                    color = controlColor,
                                    topLeft = Offset(w * 0.2f, h * 0.2f),
                                    size = androidx.compose.ui.geometry.Size(barW, h * 0.6f),
                                    alpha = alpha
                                )
                                drawRect(
                                    color = controlColor,
                                    topLeft = Offset(w * 0.2f + barW + barGap, h * 0.2f),
                                    size = androidx.compose.ui.geometry.Size(barW, h * 0.6f),
                                    alpha = alpha
                                )
                            }
                        }
                    }
                    
                    BouncyIconButton(
                        onClick = { viewModel.playerManager.skipNext() },
                        hoverScale = 1.15f,
                        pressScale = 0.85f
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Track", tint = textPrimary, modifier = Modifier.size(28.dp))
                    }

                    // Loop/Repeat Mode Controller Key
                    val repeatIcon = when (repeatMode) {
                        PlaybackRepeatMode.ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    }
                    BouncyIconButton(
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
                    
                }
                        Spacer(modifier = Modifier.weight(1.2f))
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
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
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
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
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
                                                containerColor = if (isSelected) activeAccent else (androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
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
                                val view = androidx.compose.ui.platform.LocalView.current
                                val bandLabels = listOf("60Hz", "230Hz", "910Hz", "4kHz", "14kHz")
                                eqBands.forEachIndexed { idx, value ->
                                    var lastTickValue by remember(idx) { mutableIntStateOf(value / 100) }
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
                                                val newInt = newValue.toInt()
                                                val tickVal = newInt / 100
                                                if (tickVal != lastTickValue) {
                                                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                                                    lastTickValue = tickVal
                                                }
                                                viewModel.setEqBandLevel(idx, newInt)
                                            },
                                            valueRange = -1500f..1500f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = activeAccent,
                                                activeTrackColor = activeAccent,
                                                inactiveTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
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
            


        // The Floating Turntable (Shared Element morph!)
        val turntableLeft = currentCenterX - (maxVinylSize / 2f)
        val turntableTop = currentCenterY - (maxVinylSize / 2f)

        // Realistic Record Player with pivoting tonearm and hardware-accelerated infinite rotation
        var horizontalOffset by remember { mutableStateOf(0f) }
        var hasTriggeredSwipe by remember { mutableStateOf(false) }
        var settlingJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

        // Smooth hardware-accelerated continuous rotation with zero-allocation tracking directly in the draw phase
        val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotation")
        val rotationFactorState = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "vinyl_rotate"
        )
        val rotationTracker = remember { VinylRotationTracker() }

        // Pivoting tonearm stylus rotation animation (swings onto record when playing, swings off when paused)
        val tonearmAngleState = androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (isAudioDeliveringSound) 25f else 0f,
            animationSpec = tunedSpring(animSpeed, animBounciness),
            label = "tonearm_rotation"
        )

        // Layout scaling and shadow alpha transition (Apple Music Style)
        val vinylScaleState = animateFloatAsState(
            targetValue = if (isAudioDeliveringSound) 1.05f else 0.95f,
            animationSpec = tunedSpring(animSpeed, animBounciness),
            label = "vinyl_scale"
        )
        val vinylAlphaState = animateFloatAsState(
            targetValue = if (isAudioDeliveringSound) 1.0f else 0.75f,
            animationSpec = tween(400),
            label = "vinyl_alpha"
        )
        val shadowElevationState = animateDpAsState(
            targetValue = if (isAudioDeliveringSound) 16.dp else 2.dp,
            animationSpec = tunedSpring(animSpeed, animBounciness),
            label = "vinyl_shadow"
        )

        val dragScope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .offset(x = turntableLeft, y = turntableTop)
                .graphicsLayer {
                    // Apply expansion scale factor to scale correctly during transition
                    val scaleFactor = currentSize / maxVinylSize
                    // Smoothly scale down vinyl/turntable from its current size to 0.95f of its size when queue is open
                    val s = (1f - (0.05f * queueTransitionProgress)) * scaleFactor
                    scaleX = vinylScaleState.value * s
                    scaleY = vinylScaleState.value * s
                    translationX = horizontalOffset
                    // Smoothly dim/fade out vinyl from its active alpha to 0f when queue is open
                    val viewAlpha = (1f - queueTransitionProgress)
                    alpha = (1f - (Math.abs(horizontalOffset) / 280f)).coerceIn(0.1f, 1.0f) * vinylAlphaState.value * controlsAlpha * viewAlpha
                    shadowElevation = shadowElevationState.value.toPx()
                    shape = RoundedCornerShape((28f * (maxVinylSize / 240f) * expansionProgress).dp)
                    clip = true
                }
                .size(maxVinylSize.dp)
                .background(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shape = RoundedCornerShape((28f * (maxVinylSize / 240f) * expansionProgress).dp)
                )
                




                .pointerInput(Unit) {
                    if (expansionProgress == 1f) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                hasTriggeredSwipe = false
                                settlingJob?.cancel()
                            },
                            onDragEnd = {
                                hasTriggeredSwipe = false
                                settlingJob?.cancel()
                                settlingJob = dragScope.launch {
                                    androidx.compose.animation.core.animate(
                                        initialValue = horizontalOffset,
                                        targetValue = 0f,
                                        animationSpec = androidx.compose.animation.core.tween(
                                            durationMillis = 200,
                                            easing = androidx.compose.animation.core.FastOutSlowInEasing
                                        )
                                    ) { value, _ ->
                                        horizontalOffset = value
                                    }
                                }
                            },
                            onDragCancel = {
                                hasTriggeredSwipe = false
                                settlingJob?.cancel()
                                settlingJob = dragScope.launch {
                                    androidx.compose.animation.core.animate(
                                        initialValue = horizontalOffset,
                                        targetValue = 0f,
                                        animationSpec = androidx.compose.animation.core.tween(
                                            durationMillis = 200,
                                            easing = androidx.compose.animation.core.FastOutSlowInEasing
                                        )
                                    ) { value, _ ->
                                        horizontalOffset = value
                                    }
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                settlingJob?.cancel()
                                horizontalOffset += dragAmount
                                if (!hasTriggeredSwipe) {
                                    if (horizontalOffset > 75f) {
                                        hasTriggeredSwipe = true
                                        viewModel.playerManager.skipPrevious()
                                        settlingJob?.cancel()
                                        settlingJob = dragScope.launch {
                                            androidx.compose.animation.core.animate(
                                                initialValue = horizontalOffset,
                                                targetValue = 0f,
                                                animationSpec = androidx.compose.animation.core.tween(
                                                    durationMillis = 200,
                                                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                                                )
                                            ) { value, _ ->
                                                horizontalOffset = value
                                            }
                                        }
                                    } else if (horizontalOffset < -75f) {
                                        hasTriggeredSwipe = true
                                        viewModel.playerManager.skipNext()
                                        settlingJob?.cancel()
                                        settlingJob = dragScope.launch {
                                            androidx.compose.animation.core.animate(
                                                initialValue = horizontalOffset,
                                                targetValue = 0f,
                                                animationSpec = androidx.compose.animation.core.tween(
                                                    durationMillis = 200,
                                                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                                                )
                                            ) { value, _ ->
                                                horizontalOffset = value
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl Disk
            Box(
                modifier = Modifier
                    .fillMaxSize(0.88f)
                    .graphicsLayer {
                        val currentFactor = rotationFactorState.value
                        if (isAudioDeliveringSound) {
                            val delta = (currentFactor - rotationTracker.lastFactorValue + 360f) % 360f
                            rotationTracker.lastRotationOffset = (rotationTracker.lastRotationOffset + delta) % 360f
                        }
                        rotationTracker.lastFactorValue = currentFactor
                        rotationZ = rotationTracker.lastRotationOffset
                    }
                    .background(Color(0xFF0C0C0C), CircleShape),
                    
                contentAlignment = Alignment.Center
            ) {
                // Concentric grooves
                Box(modifier = Modifier.fillMaxSize(0.85f))
                Box(modifier = Modifier.fillMaxSize(0.70f))
                Box(modifier = Modifier.fillMaxSize(0.55f))
                Box(modifier = Modifier.fillMaxSize(0.40f))

                // Center label disk containing actual cover art!
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.38f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedContent(
                        targetState = currentTrack,
                        transitionSpec = {
                            androidx.compose.animation.fadeIn(
                                animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            ) togetherWith androidx.compose.animation.fadeOut(
                                animationSpec = androidx.compose.animation.core.tween(350, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            )
                        },
                        label = "album_art_crossfade",
                        contentAlignment = Alignment.Center
                    ) { track ->
                        if (!track?.thumbnailUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = rememberOptimizedImageRequest(
                                    data = track?.thumbnailUrl,
                                    sizePx = 240
                                ),
                                contentDescription = "Album Art Label",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                                
                            )
                        } else if (track != null) {
                            // Clean sleek vector icon placeholder
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Accent metal ring around spindle point
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.24f)
                            
                    )

                    // Spindle Hole in the center
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Black, CircleShape)
                            
                    )
                }
            }
        }

        // 1.5. Dynamic Shuffle Interval Settings Dialog inside Expanded Player View
        if (showShuffleIntervalDialog) {
            Dialog(onDismissRequest = { showShuffleIntervalDialog = false }) {
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .widthIn(max = 350.dp)
                        .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp), clip = false),
                    shape = RoundedCornerShape(28.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = null,
                                    tint = activeAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Shuffle Settings",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = textPrimary
                                )
                            }
                            IconButton(onClick = { showShuffleIntervalDialog = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Text(
                            text = "Configure how many tracks from the current source play randomly before reshuffling automatically.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = textSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Interval Size:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = textPrimary
                            )
                            Text(
                                text = "$shuffleInterval ${if (shuffleInterval == 1) "Song" else "Songs"}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = activeAccent
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val view = androidx.compose.ui.platform.LocalView.current
                        var lastShuffleTick by remember { mutableIntStateOf(shuffleInterval) }
                        
                        Slider(
                            value = shuffleInterval.toFloat(),
                            onValueChange = { newValue ->
                                val newInt = newValue.toInt()
                                if (newInt != lastShuffleTick) {
                                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                                    lastShuffleTick = newInt
                                }
                                viewModel.playerManager.shuffleInterval.value = newInt
                            },
                            valueRange = 1f..35f,
                            steps = 33, // 35 - 1 - 1 = 33 discrete steps
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = activeAccent,
                                activeTrackColor = activeAccent,
                                inactiveTrackColor = if (isDarkMode) activeAccent.copy(alpha = 0.2f) else Color(0xFFEBE0E0)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val remainingInBlock = (shuffleInterval - (viewModel.playerManager.songsPlayedInBlock - 1)).coerceIn(1, shuffleInterval)
                        Text(
                            text = "Remaining in current play block: $remainingInBlock",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary.copy(alpha = 0.9f)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        androidx.compose.material3.Button(
                            onClick = { showShuffleIntervalDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = activeAccent),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Apply Interval Set",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Track Details Metadata Dialog inside Expanded Player View
        if (showTrackDetailsDialogInPlayer) {
            Dialog(onDismissRequest = { showTrackDetailsDialogInPlayer = false }) {
                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                        
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Track Details",
                            tint = activeAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Track Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val duration by viewModel.playerManager.duration.collectAsState()
                    currentTrack?.let { track ->
                        val detailRows = listOf(
                            "Title" to track.title,
                            "Artist" to track.artist,
                            "Source" to track.source,
                            "Track ID" to track.id,
                            "Track URL" to (track.streamUrl.ifEmpty { "Local File" }),
                            "Duration" to formatDuration(duration)
                        )

                        detailRows.forEach { (label, value) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = value,
                                    fontSize = 13.sp,
                                    color = textPrimary,
                                    maxLines = 3,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    } ?: run {
                        Text("No track currently loaded.", color = textPrimary)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Text(
                            text = "Close",
                            modifier = Modifier
                                .bouncyClickable { showTrackDetailsDialogInPlayer = false }
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            color = activeAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Swipeable / dismissible pop-up window bottom sheet active queue panel (Issue 2)
        // Positioned outside of main Column and vinyl views at the root level of ExpandedPlayerView
        // to prevent alpha translation/fading and drawn cleanly on top of all other elements (turntable & player)
        androidx.compose.animation.AnimatedVisibility(
            visible = showQueuePanel,
            enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)),
            exit = androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)),
            modifier = Modifier.fillMaxSize()
                                .zIndex(100f)
        ) {
            Box(modifier = Modifier.fillMaxSize()
                                ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                                
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) {
                            showQueuePanel = false
                        }
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.65f) // Expands cleanly showing upcoming sequence
                        .align(Alignment.BottomCenter)
                        .animateEnterExit(
                            enter = androidx.compose.animation.slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tunedSpring(animSpeed, animBounciness)
                            ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)),
                            exit = androidx.compose.animation.slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tunedSpring(animSpeed, animBounciness)
                            ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(durationMillis = 300))
                        )
                        .background(
                            brush = getGlassBackground(isDarkMode),
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

                    if (queueList.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = { viewModel.playerManager.sortQueueByTitle(ascending = true) }) {
                                Icon(Icons.Default.SortByAlpha, contentDescription = "A to Z", tint = textPrimary, modifier = Modifier.size(24.dp))
                            }
                            IconButton(onClick = { viewModel.playerManager.sortQueueByTitle(ascending = false) }) {
                                Icon(Icons.Default.SortByAlpha, contentDescription = "Z to A", tint = textPrimary, modifier = Modifier.size(24.dp).rotate(180f))
                            }
                            IconButton(onClick = { viewModel.playerManager.randomizeQueue() }) {
                                Icon(Icons.Default.Casino, contentDescription = "Randomize", tint = textPrimary, modifier = Modifier.size(24.dp))
                            }
                            IconButton(onClick = { viewModel.playerManager.sortQueueByLanguage() }) {
                                Icon(Icons.Default.Translate, contentDescription = "By Language", tint = textPrimary, modifier = Modifier.size(24.dp))
                            }
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
        val containerBrush = getGlassBackground(isDarkMode)
        val borderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
        val accentColor = MaterialTheme.colorScheme.primary
        val textColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface

        val mins = sleepSecondsLeft / 60
        val secs = sleepSecondsLeft % 60
        val secondsStr = if (secs < 10) "0$secs" else "$secs"
        val countdownDisplay = "$mins:$secondsStr"

        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .background(containerBrush, RoundedCornerShape(100.dp))
                
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
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = countdownDisplay,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .bouncyClickable { viewModel.stopSleepTimer() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Sleep Timer",
                    tint = Color.Red,
                    modifier = Modifier.size(14.dp)
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

    val enableBackgroundMotion by viewModel.enableBackgroundMotion.collectAsState()
    val accentColor = MaterialTheme.colorScheme.primary
    val containerBg = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    val textColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface

    val scrollState = androidx.compose.foundation.rememberScrollState()

    androidx.compose.ui.window.Dialog(
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {} // System lock auth screen
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                                
                .background(MaterialTheme.colorScheme.background)
        ) {
            // High aesthetic ambient mesh glow backgrounds with customizable blur
            val isGlass = LocalGlassmorphismEnabled.current
            Box(
                modifier = Modifier
                    .fillMaxSize()
                                
            ) {
                BackgroundAmbientMesh(isDarkMode = isDarkMode, enableMotion = enableBackgroundMotion)
            }

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
                        .size(38.dp)
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
                        
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Selection tabs: Standard vs Phone (Only when not in Sign Up view)
                    if (!isSignUp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(99.dp))
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
                                Text("Email Login", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!isPhoneLogin) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else Color.Gray)
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
                                Text("Phone OTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isPhoneLogin) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else Color.Gray)
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
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary),
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(99.dp)
                            ) {
                                Text(if (!otpSent) "Send 6-Digit SMS Code" else "Verify & Login", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
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
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary),
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(99.dp)
                            ) {
                                Text(if (isSignUp) "Register Account" else "Authorize Securely", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant))
                        Text("OR", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.loginUser("google.oauth@gmail.com", "Google Authorized User")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer,
                            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    
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
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text("Acknowledge", fontSize = 11.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
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
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Dispatch Key", fontSize = 11.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorWheelPicker(
    hue: Float,
    saturation: Float,
    onColorChange: (hue: Float, saturation: Float) -> Unit,
    onDragEnd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth(0.85f)
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        val center = androidx.compose.ui.geometry.Offset(widthPx / 2f, heightPx / 2f)
        val radius = (widthPx / 2f) * 0.9f

        val currentOnColorChange by rememberUpdatedState(onColorChange)
        val currentOnDragEnd by rememberUpdatedState(onDragEnd)
        val currentCenter by rememberUpdatedState(center)
        val currentRadius by rememberUpdatedState(radius)

        val pointerInputModifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset: androidx.compose.ui.geometry.Offset ->
                    val c = currentCenter
                    val r = currentRadius
                    val safeRadius = if (r > 0f) r else 1f
                    val dx = offset.x - c.x
                    val dy = offset.y - c.y
                    val dist = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                    val angleRad = Math.atan2(dy.toDouble(), dx.toDouble())
                    val h = ((Math.toDegrees(angleRad) + 360) % 360).toFloat()
                    val s = (dist / safeRadius).coerceIn(0f, 1f)
                    
                    val finalH = if (h.isNaN() || h.isInfinite()) 0f else h.coerceIn(0f, 360f)
                    val finalS = if (s.isNaN() || s.isInfinite()) 0f else s.coerceIn(0f, 1f)
                    currentOnColorChange(finalH, finalS)
                },
                onDragEnd = {
                    currentOnDragEnd()
                },
                onDragCancel = {
                    currentOnDragEnd()
                },
                onDrag = { change: androidx.compose.ui.input.pointer.PointerInputChange, dragAmount: androidx.compose.ui.geometry.Offset ->
                    change.consume()
                    val c = currentCenter
                    val r = currentRadius
                    val safeRadius = if (r > 0f) r else 1f
                    val position = change.position
                    val dx = position.x - c.x
                    val dy = position.y - c.y
                    val dist = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                    val angleRad = Math.atan2(dy.toDouble(), dx.toDouble())
                    val h = ((Math.toDegrees(angleRad) + 360) % 360).toFloat()
                    val s = (dist / safeRadius).coerceIn(0f, 1f)
                    
                    val finalH = if (h.isNaN() || h.isInfinite()) 0f else h.coerceIn(0f, 360f)
                    val finalS = if (s.isNaN() || s.isInfinite()) 0f else s.coerceIn(0f, 1f)
                    currentOnColorChange(finalH, finalS)
                }
            )
        }.pointerInput(Unit) {
            detectTapGestures { offset: androidx.compose.ui.geometry.Offset ->
                val c = currentCenter
                val r = currentRadius
                val safeRadius = if (r > 0f) r else 1f
                val dx = offset.x - c.x
                val dy = offset.y - c.y
                val dist = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                val angleRad = Math.atan2(dy.toDouble(), dx.toDouble())
                val h = ((Math.toDegrees(angleRad) + 360) % 360).toFloat()
                val s = (dist / safeRadius).coerceIn(0f, 1f)
                
                val finalH = if (h.isNaN() || h.isInfinite()) 0f else h.coerceIn(0f, 360f)
                val finalS = if (s.isNaN() || s.isInfinite()) 0f else s.coerceIn(0f, 1f)
                currentOnColorChange(finalH, finalS)
                currentOnDragEnd()
            }
        }

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                                
                .then(pointerInputModifier)
        ) {
            val c = if (!center.x.isNaN() && !center.x.isInfinite() && !center.y.isNaN() && !center.y.isInfinite()) center else androidx.compose.ui.geometry.Offset(0f, 0f)
            val r = if (radius.isNaN() || radius.isInfinite() || radius <= 0f) 1f else radius

            val hueColors = listOf(
                Color.Red, Color(0xFFFF7F00), Color.Yellow, Color.Green,
                Color.Cyan, Color.Blue, Color(0xFF4B0082), Color(0xFF9400D3), Color.Red
            )
            val sweepBrush = androidx.compose.ui.graphics.Brush.sweepGradient(
                colors = hueColors,
                center = c
            )
            drawCircle(
                brush = sweepBrush,
                radius = r,
                center = c
            )

            val radialBrush = androidx.compose.ui.graphics.Brush.radialGradient(
                colors = listOf(Color.White, Color.Transparent),
                center = c,
                radius = r
            )
            drawCircle(
                brush = radialBrush,
                radius = r,
                center = c
            )

            val angleRad = Math.toRadians(hue.toDouble())
            val rawThumbX = c.x + r * saturation * Math.cos(angleRad).toFloat()
            val rawThumbY = c.y + r * saturation * Math.sin(angleRad).toFloat()
            val thumbX = if (rawThumbX.isNaN() || rawThumbX.isInfinite()) c.x else rawThumbX
            val thumbY = if (rawThumbY.isNaN() || rawThumbY.isInfinite()) c.y else rawThumbY
            val thumbCenter = androidx.compose.ui.geometry.Offset(thumbX, thumbY)

            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = c,
                end = thumbCenter,
                strokeWidth = 3f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            drawCircle(
                color = Color.Black.copy(alpha = 0.3f),
                radius = 18.dp.toPx(),
                center = thumbCenter
            )
            drawCircle(
                color = Color.White,
                radius = 15.dp.toPx(),
                center = thumbCenter
            )
            val selectedColor = com.example.ui.theme.createHsvColor(hue, saturation, 0.85f)
            drawCircle(
                color = selectedColor,
                radius = 10.dp.toPx(),
                center = thumbCenter
            )
        }
    }
}

@Composable
fun LightnessSliderPicker(
    hue: Float,
    saturation: Float,
    lightness: Float,
    onLightnessChange: (Float) -> Unit,
    onDragEnd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trackHeight = 16.dp
    val activeHsvColor = com.example.ui.theme.createHsvColor(hue, saturation, 0.5f)
    val trackGradientBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(
        colors = listOf(Color.Black, activeHsvColor, Color.White)
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dynamic Brightness / Contrast",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = "${(lightness * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.layout.BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            val density = androidx.compose.ui.platform.LocalDensity.current
            val widthPx = with(density) { maxWidth.toPx() }
            val thumbRadiusPx = with(density) { 14.dp.toPx() }

            val safeWidthPx = if (widthPx.isNaN() || widthPx.isInfinite() || widthPx <= 0f) 100f else widthPx
            val safeThumbRadiusPx = if (thumbRadiusPx.isNaN() || thumbRadiusPx.isInfinite() || thumbRadiusPx <= 0f) 14f else thumbRadiusPx
            val rawActiveRangeWidth = safeWidthPx - 2 * safeThumbRadiusPx
            val activeRangeWidth = if (rawActiveRangeWidth <= 0f) 1f else rawActiveRangeWidth

            val rawCurrentThumbX = safeThumbRadiusPx + lightness * activeRangeWidth
            val currentThumbX = if (rawCurrentThumbX.isNaN() || rawCurrentThumbX.isInfinite()) safeThumbRadiusPx else rawCurrentThumbX

            val currentOnLightnessChange by rememberUpdatedState(onLightnessChange)
            val currentOnDragEnd by rememberUpdatedState(onDragEnd)
            val currentThumbRadiusPx by rememberUpdatedState(safeThumbRadiusPx)
            val currentActiveRangeWidth by rememberUpdatedState(activeRangeWidth)
            val view = androidx.compose.ui.platform.LocalView.current
            var lastTickValue by remember { mutableIntStateOf((lightness * 100).toInt()) }

            val dragPointerInput = Modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset: androidx.compose.ui.geometry.Offset ->
                        val tr = currentThumbRadiusPx
                        val rWidth = currentActiveRangeWidth
                        val pct = ((offset.x - tr) / rWidth).coerceIn(0f, 1f)
                        val finalPct = if (pct.isNaN() || pct.isInfinite()) 0f else pct
                        val newInt = (finalPct * 100).toInt()
                        if (newInt != lastTickValue) {
                            view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                            lastTickValue = newInt
                        }
                        currentOnLightnessChange(finalPct)
                    },
                    onDragEnd = {
                        currentOnDragEnd()
                    },
                    onDragCancel = {
                        currentOnDragEnd()
                    },
                    onDrag = { change: androidx.compose.ui.input.pointer.PointerInputChange, dragAmount: androidx.compose.ui.geometry.Offset ->
                        change.consume()
                        val tr = currentThumbRadiusPx
                        val rWidth = currentActiveRangeWidth
                        val pct = ((change.position.x - tr) / rWidth).coerceIn(0f, 1f)
                        val finalPct = if (pct.isNaN() || pct.isInfinite()) 0f else pct
                        val newInt = (finalPct * 100).toInt()
                        if (newInt != lastTickValue) {
                            view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                            lastTickValue = newInt
                        }
                        currentOnLightnessChange(finalPct)
                    }
                )
            }.pointerInput(Unit) {
                detectTapGestures { offset: androidx.compose.ui.geometry.Offset ->
                    val tr = currentThumbRadiusPx
                    val rWidth = currentActiveRangeWidth
                    val pct = ((offset.x - tr) / rWidth).coerceIn(0f, 1f)
                    val finalPct = if (pct.isNaN() || pct.isInfinite()) 0f else pct
                    val newInt = (finalPct * 100).toInt()
                    if (newInt != lastTickValue) {
                        view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                        lastTickValue = newInt
                    }
                    currentOnLightnessChange(finalPct)
                    currentOnDragEnd()
                }
            }

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxSize()
                                
                    .then(dragPointerInput)
            ) {
                val canvasHeight = size.height
                val yCenter = canvasHeight / 2f

                val tr = safeThumbRadiusPx
                val rWidth = activeRangeWidth

                val rRectCorner = CornerRadius(trackHeight.toPx() / 2f)
                drawRoundRect(
                    brush = trackGradientBrush,
                    topLeft = androidx.compose.ui.geometry.Offset(tr, yCenter - trackHeight.toPx() / 2f),
                    size = androidx.compose.ui.geometry.Size(rWidth, trackHeight.toPx()),
                    cornerRadius = rRectCorner
                )

                drawRoundRect(
                    color = Color.White.copy(alpha = 0.25f),
                    topLeft = androidx.compose.ui.geometry.Offset(tr, yCenter - trackHeight.toPx() / 2f),
                    size = androidx.compose.ui.geometry.Size(rWidth, trackHeight.toPx()),
                    cornerRadius = rRectCorner,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )

                val thumbOffset = androidx.compose.ui.geometry.Offset(currentThumbX, yCenter)
                drawCircle(
                    color = Color.Black.copy(alpha = 0.35f),
                    radius = tr + 2f,
                    center = thumbOffset
                )
                drawCircle(
                    color = Color.White,
                    radius = tr,
                    center = thumbOffset
                )
                drawCircle(
                    color = com.example.ui.theme.createHsvColor(hue, saturation, lightness),
                    radius = tr - 4.dp.toPx(),
                    center = thumbOffset
                )
            }
        }
    }
}

fun Modifier.bouncyClickable(
    enabled: Boolean = true,
    hoverScale: Float = 1.03f,
    pressScale: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) pressScale else (if (isHovered) hoverScale else 1f),
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "bouncy_click_scale"
    )
    val view = androidx.compose.ui.platform.LocalView.current

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            onClick = {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                    } else {
                        view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                } catch (e: Exception) {
                    // Fail gracefully
                }
                onClick()
            }
        )
}

@Composable
fun BouncyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hoverScale: Float = 1.12f,
    pressScale: Float = 0.88f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .bouncyClickable(
                enabled = enabled,
                hoverScale = hoverScale,
                pressScale = pressScale,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun AnchoredPlayerBarWrapper(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onCapsuleClick: () -> Unit
) {
    val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
    val activeTrack = currentTrack ?: return
    
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val isAudioDeliveringSound by viewModel.playerManager.isAudioDeliveringSound.collectAsState()
    
    val isBuffering by viewModel.playerManager.isBuffering.collectAsState()
    val shuffleMode by viewModel.playerManager.shuffleMode.collectAsState()
    val repeatMode by viewModel.playerManager.repeatMode.collectAsState()

    AnchoredPlayerBar(
        track = activeTrack,
        isPlaying = isPlaying,
        isAudioDeliveringSound = isAudioDeliveringSound,
        viewModel = viewModel,
        isBuffering = isBuffering,
        isDarkMode = isDarkMode,
        shuffleMode = shuffleMode,
        repeatMode = repeatMode,
        onShuffleToggle = {
            val nextMode = when (shuffleMode) {
                com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD
                com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC
                com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC -> com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF
            }
            viewModel.playerManager.shuffleMode.value = nextMode
        },
        onRepeatToggle = {
            val nextRepeat = when (repeatMode) {
                PlaybackRepeatMode.OFF -> PlaybackRepeatMode.ALL
                PlaybackRepeatMode.ALL -> PlaybackRepeatMode.ONE
                PlaybackRepeatMode.ONE -> PlaybackRepeatMode.OFF
            }
            viewModel.playerManager.repeatMode.value = nextRepeat
        },
        onPlayPauseToggle = { viewModel.playerManager.togglePlayPause() },
        onSkipNext = { viewModel.playerManager.skipNext() },
        onSkipPrevious = { viewModel.playerManager.skipPrevious() },
        onSeek = { target -> viewModel.playerManager.seekTo(target) },
        onCapsuleClick = onCapsuleClick
    )
}

@Composable
fun ShufflePopupOverlayWrapper(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val shuffleMode by viewModel.playerManager.shuffleMode.collectAsState()
    val shuffleInterval by viewModel.playerManager.shuffleInterval.collectAsState()
    var showShufflePopup by remember { mutableStateOf(false) }
    var shufflePopupText by remember { mutableStateOf("") }
    var isFirstShuffleCompose by remember { mutableStateOf(true) }

    LaunchedEffect(shuffleMode) {
        if (isFirstShuffleCompose) {
            isFirstShuffleCompose = false
            return@LaunchedEffect
        }
        shufflePopupText = when (shuffleMode) {
            com.example.player.MusicPlayerManager.MyuLocShuffleMode.OFF -> "Shuffle Mode: OFF [Sequential Playback]"
            com.example.player.MusicPlayerManager.MyuLocShuffleMode.STANDARD -> "Shuffle Mode: STANDARD [Randomized Order]"
            com.example.player.MusicPlayerManager.MyuLocShuffleMode.DYNAMIC -> "Shuffle Mode: DYNAMIC [Interval: $shuffleInterval]"
        }
        showShufflePopup = false
    }

    LaunchedEffect(showShufflePopup) {
        if (showShufflePopup) {
            kotlinx.coroutines.delay(2000)
            showShufflePopup = false
        }
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = showShufflePopup,
        enter = androidx.compose.animation.scaleIn(
            initialScale = 0.85f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 250)
        ) + androidx.compose.animation.fadeIn(
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 250)
        ),
        exit = androidx.compose.animation.scaleOut(
            targetScale = 0.85f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 200)
        ) + androidx.compose.animation.fadeOut(
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 200)
        ),
        modifier = modifier
    ) {
        androidx.compose.material3.Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(0.85f)
                .widthIn(max = 380.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                ),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                width = 1.dp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.outline
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle State",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = shufflePopupText,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

class ParticleSpec(
    val startXFraction: Float,
    val speedY: Float,
    val swayAmp: Float,
    val swayFreq: Float,
    val scale: Float,
    val colorType: Int,
    val randomYOffsetFraction: Float
)

@Composable
fun AnimatedDriftingParticlesOverlay(isDarkMode: Boolean) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "time")
    
    val timeState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(20000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "time"
    )

    val particles = remember {
        List(5) { i ->
            val random = kotlin.random.Random(i)
            ParticleSpec(
                startXFraction = random.nextFloat(),
                speedY = 0.05f + random.nextFloat() * 0.1f,
                swayAmp = 40f + random.nextFloat() * 60f,
                swayFreq = 1000f + random.nextFloat() * 2000f,
                scale = 0.6f + random.nextFloat() * 0.8f,
                colorType = i % 2,
                randomYOffsetFraction = random.nextFloat()
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        
        val particleColor1 = primaryColor.copy(alpha = if (isDarkMode) 0.3f else 0.5f)
        val particleColor2 = secondaryColor.copy(alpha = if (isDarkMode) 0.2f else 0.4f)
        
        val elapsed = timeState.value * 20000f
        
        for (i in 0 until 5) {
            val spec = particles[i]
            val startX = spec.startXFraction * w
            
            val currentY = ((elapsed * spec.speedY) + spec.randomYOffsetFraction * h) % (h + 100f) - 50f
            val currentX = startX + kotlin.math.sin(elapsed / spec.swayFreq).toFloat() * spec.swayAmp
            
            val radius = 8f * spec.scale
            
            drawCircle(
                color = if (spec.colorType == 0) particleColor1 else particleColor2,
                radius = radius,
                center = Offset(currentX, currentY)
            )
        }
    }
}

@Composable
fun GlobalNavigationRail(
    currentTab: String,
    isDarkMode: Boolean,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        Triple("all", "Home", Icons.Default.Home),
        Triple("search", "Library", Icons.Default.Folder),
        Triple("favorites", "Favorites", Icons.Default.Favorite)
    )

    val activeColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val bgColor = androidx.compose.material3.MaterialTheme.colorScheme.background.copy(alpha = 0.8f)

    androidx.compose.material3.NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp),
        containerColor = bgColor
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items.forEach { (tabId, tabName, icon) ->
                val isSelected = currentTab == tabId
                androidx.compose.material3.NavigationRailItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tabId) },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = tabName,
                            tint = if (isSelected) activeColor else inactiveColor
                        )
                    },
                    label = {
                        Text(
                            text = tabName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) activeColor else inactiveColor
                        )
                    },
                    colors = androidx.compose.material3.NavigationRailItemDefaults.colors(
                        selectedIconColor = activeColor,
                        unselectedIconColor = inactiveColor,
                        selectedTextColor = activeColor,
                        unselectedTextColor = inactiveColor,
                        indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun IsolatedPlayerProgress(viewModel: MyuLocViewModel) {
    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
    val duration by viewModel.playerManager.duration.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = if (isPlaying) 300 else 0,
            easing = androidx.compose.animation.core.LinearEasing
        ),
        label = "SmoothMusicSlider"
    )
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun IsolatedExpandedPlayerProgress(viewModel: MyuLocViewModel, textSecondary: androidx.compose.ui.graphics.Color, activeAccent: androidx.compose.ui.graphics.Color) {
    var showRemainingTime by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
        val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
        val duration by viewModel.playerManager.duration.collectAsState()
        val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
        var sliderValue by remember { mutableStateOf<Float?>(null) }
        var lastTickValue by remember { mutableIntStateOf(-1) }
        val view = androidx.compose.ui.platform.LocalView.current
        
        val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
            targetValue = progressFraction,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = if (isPlaying && sliderValue == null) 300 else 0,
                easing = androidx.compose.animation.core.LinearEasing
            ),
            label = "SmoothExpandedMusicSlider"
        )
        
        androidx.compose.material3.Slider(
            value = (sliderValue ?: animatedProgress).coerceIn(0f, 1f),
            onValueChange = { 
                sliderValue = it
                val newInt = (it * 100).toInt()
                if (newInt != lastTickValue) {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    lastTickValue = newInt
                }
            },
            onValueChangeFinished = {
                sliderValue?.let {
                    val target = (it * duration).toLong()
                    viewModel.playerManager.seekTo(target)
                }
                sliderValue = null
            },
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = androidx.compose.ui.graphics.Color.Transparent,
                activeTrackColor = androidx.compose.ui.graphics.Color.Transparent,
                inactiveTrackColor = androidx.compose.ui.graphics.Color.Transparent,
                activeTickColor = androidx.compose.ui.graphics.Color.Transparent,
                inactiveTickColor = androidx.compose.ui.graphics.Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(activeAccent, androidx.compose.foundation.shape.CircleShape)
                )
            },
            track = { _ ->
                val activeColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                    val centerY = size.height / 2f
                    val width = size.width
                    drawLine(
                        color = inactiveColor,
                        start = androidx.compose.ui.geometry.Offset(0f, centerY),
                        end = androidx.compose.ui.geometry.Offset(width, centerY),
                        strokeWidth = 2.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    val progress = sliderValue ?: animatedProgress
                    drawLine(
                        color = activeColor,
                        start = androidx.compose.ui.geometry.Offset(0f, centerY),
                        end = androidx.compose.ui.geometry.Offset(width * progress, centerY),
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
        val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
        val duration by viewModel.playerManager.duration.collectAsState()
        Text(
            text = formatDuration(currentPosition),
            fontSize = 10.sp,
            color = textSecondary,
            style = androidx.compose.material3.LocalTextStyle.current.copy(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                    includeFontPadding = true
                ),
                lineHeight = 14.sp
            )
        )
        Text(
            text = if (showRemainingTime && duration > currentPosition) "-${formatDuration(duration - currentPosition)}" else formatDuration(duration),
            fontSize = 10.sp,
            color = textSecondary,
            style = androidx.compose.material3.LocalTextStyle.current.copy(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                    includeFontPadding = true
                ),
                lineHeight = 14.sp
            ),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) {
                    showRemainingTime = !showRemainingTime
                }
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

fun formatDuration(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
