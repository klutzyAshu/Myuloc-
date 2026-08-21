const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// Find the start of the injected code
const badCodeStart = `                        IconButton(onClick = { viewModel.playerManager.playNext() }) {
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
}
fun ExpandedPlayerView(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    expansionProgress: Float = 1f,
    innerPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
    onSleepDialogShow: () -> Unit = {}
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
    val view = androidx.compose.ui.platform.LocalView.current
    var dominantColor by remember { mutableStateOf(androidx.compose.ui.graphics.Color.DarkGray) }
    var maxVinylSize by remember { mutableStateOf(0f) }
    var outerBoxPositionInWindow by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    val favoritesList by viewModel.favorites.collectAsState()
    var targetCenter by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    val eqEnabled by viewModel.playerManager.eqEnabled.collectAsState()
    val activePreset by viewModel.playerManager.activePreset.collectAsState()
    val eqBands by viewModel.playerManager.eqBands.collectAsState()
    var showEqPanel by remember { mutableStateOf(false) }
    var currentCenterX by remember { mutableStateOf(0f) }
    var currentCenterY by remember { mutableStateOf(0f) }
    var currentSize by remember { mutableStateOf(0f) }
    var queueTransitionProgress by remember { mutableStateOf(0f) }
    var controlsAlpha by remember { mutableStateOf(1f) }
    var expansionProgress by remember { mutableStateOf(0f) }
    
    Box(modifier = Modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {`;

const goodCode = `                        IconButton(onClick = { viewModel.playerManager.playNext() }) {
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
    
        if (showExpandedPlayer) {
            val animSpeed = 0.8f
            val animBounciness = 0.4f
            
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
            var expansionProgress by remember { mutableStateOf(0f) }
            
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {`;

content = content.replace(badCodeStart, goodCode);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
