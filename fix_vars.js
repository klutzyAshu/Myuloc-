const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

content = content.replace(/val isAudioDeliveringSound = isPlaying/, `val isAudioDeliveringSound = isPlaying
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
    `);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
