const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

for (let i = 0; i < lines.length; i++) {
    if (lines[i].includes('currentTrack?.let { track ->') && lines[i+1].includes('val detailRows = listOf(')) {
        lines.splice(i, 0, "                    val duration by viewModel.playerManager.duration.collectAsState()");
        break;
    }
}
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', lines.join('\n'));
