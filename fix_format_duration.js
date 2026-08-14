const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// I need to add formatDuration back.
const formatDurationFn = `
fun formatDuration(ms: Float): String {
    if (ms <= 0f) return "0:00"
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
`;

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content + formatDurationFn);
