const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

content = content.replace(/currentTrack\?\.let \{ ct -> favoriteTrackIds\.any \{ it\.id == ct\.id \} \} \?: false/g, 'currentTrack?.let { ct -> favoriteTrackIds.contains(ct.id) } ?: false');
content = content.replace(/favoriteTrackIds\.any \{ it\.id == track\.id \}/g, 'favoriteTrackIds.contains(track.id)');

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
