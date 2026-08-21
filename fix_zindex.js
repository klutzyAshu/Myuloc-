const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /        if \(playerExpansionProgress > 0f && activeTrack != null\) \{\n            ExpandedPlayerView\(/;
const newContent = `        if (playerExpansionProgress > 0f && activeTrack != null) {
            Box(modifier = Modifier.fillMaxSize().zIndex(30f)) {
                ExpandedPlayerView(`;

content = content.replace(regex, newContent);
content = content.replace(/                onSleepDialogShow = \{ showSleepDialog = true \}\n            \)\n        \}/, `                onSleepDialogShow = { showSleepDialog = true }
            )
            }
        }`);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
