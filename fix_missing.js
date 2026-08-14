const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

for (let i = 0; i < lines.length; i++) {
    // Check AnimatedMusicSlider
    if (lines[i].includes('val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f')) {
        // Look up a few lines to see if we need to insert the collection
        if (!lines[i-1].includes('val duration') && !lines[i-2].includes('val duration')) {
            // Need to insert it right above
            lines.splice(i, 0, 
                "    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()",
                "    val duration by viewModel.playerManager.duration.collectAsState()"
            );
            i += 2; // adjust index
        }
    }
}

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', lines.join('\n'));
