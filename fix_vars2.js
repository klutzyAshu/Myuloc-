const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /        if \(showExpandedPlayer\) \{\s*val animSpeed = 0\.8f\s*val animBounciness = 0\.4f/;

const replacement = `        if (showExpandedPlayer) {
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
            val favoritesList by viewModel.favorites.collectAsState()
            val eqEnabled by viewModel.playerManager.eqEnabled.collectAsState()
            val activePreset by viewModel.playerManager.activePreset.collectAsState()
            val eqBands by viewModel.playerManager.eqBands.collectAsState()
            val view = androidx.compose.ui.platform.LocalView.current
`;

content = content.replace(regex, replacement);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
