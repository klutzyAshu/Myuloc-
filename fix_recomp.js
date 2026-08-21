const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// We will add an IsolatedPlayerProgress and IsolatedPlayerTimestamps helper

const helperStr = `
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
`;

content = content.replace(/    val currentPosition by viewModel\.playerManager\.currentPosition\.collectAsState\(\)\n    val duration by viewModel\.playerManager\.duration\.collectAsState\(\)\n                val progressFraction = if \(duration > 0f\) \(currentPosition\.toFloat\(\) \/ duration\.toFloat\(\)\)\.coerceIn\(0f, 1f\) else 0f\n                val animatedProgress by androidx\.compose\.animation\.core\.animateFloatAsState\([\s\S]*?Box\([\s\S]*?\.background\(androidx\.compose\.material3\.MaterialTheme\.colorScheme\.primary\)\n                \)/, 'IsolatedPlayerProgress(viewModel)');

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content + helperStr);
