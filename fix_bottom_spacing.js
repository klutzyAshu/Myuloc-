const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldSpacing = 'val bottomSpacing = if (hasActiveTrack != null) 124.dp else 16.dp';
const newSpacing = `val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp else 16.dp
    } else {
        if (hasActiveTrack != null) 148.dp else 84.dp
    }`;

content = content.replace(/val bottomSpacing = if \(hasActiveTrack != null\) 124\.dp else 16\.dp/g, newSpacing);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
