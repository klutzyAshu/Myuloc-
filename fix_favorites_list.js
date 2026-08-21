const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// Remove favoritesList from Header Row
content = content.replace(/                    val favoritesList by viewModel\.favoriteTracksFlow\.collectAsState\(initial = emptyList\(\)\)\n/, '');

// Add it to the top of ExpandedPlayerView
const topMarker = `    val queueList by viewModel.playerManager.queue.collectAsState()`;
const newTop = `    val favoritesList by viewModel.favoriteTracksFlow.collectAsState(initial = emptyList())
    val queueList by viewModel.playerManager.queue.collectAsState()`;
content = content.replace(topMarker, newTop);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
