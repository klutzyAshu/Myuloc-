const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const targetStr = `                                AsyncImage(
                            model = rememberOptimizedImageRequest(track.thumbnailUrl, 16),
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { 
                                    alpha = if (isDarkMode) 0.35f else 0.6f
                                },
                            filterQuality = androidx.compose.ui.graphics.FilterQuality.High
                        )`;

const split = content.split(targetStr);

const restoredCode = `                                AsyncImage(
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
                        
                        IconButton(onClick = { viewModel.playerManager.playNext() }) {
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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ExpandedPlayerView(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onDismiss: () -> Unit
) {
    val currentTrack by viewModel.playerManager.currentTrack.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
    val activeAccent = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val textPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val textSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    val animSpeed = 0.8f
    val animBounciness = 0.4f
    
    var showQueuePanel by remember { mutableStateOf(false) }
    var showTrackDetailsDialogInPlayer by remember { mutableStateOf(false) }
    var showShuffleIntervalDialog by remember { mutableStateOf(false) }
    
    val queueList by viewModel.playerManager.queue.collectAsState()
    val shuffleMode by viewModel.playerManager.shuffleMode.collectAsState()
    val repeatMode by viewModel.playerManager.repeatMode.collectAsState()
    val shuffleInterval by viewModel.playerManager.shuffleInterval.collectAsState()
    
    val isAudioDeliveringSound = isPlaying
    
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
                    )`;

content = split[0] + restoredCode + split[1];
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
