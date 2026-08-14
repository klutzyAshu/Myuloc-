const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /                        IconButton\(onClick = \{ viewModel\.playerManager\.playNext\(\) \}\) \{[\s\S]*?var expansionProgress by remember \{ mutableStateOf\(0f\) \}\s*Box\(modifier = Modifier\.fillMaxSize\(\)\.background\(androidx\.compose\.material3\.MaterialTheme\.colorScheme\.background\)\) \{/;

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
            
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {`;

content = content.replace(regex, goodCode);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
