const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldSpacing = `val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp else 16.dp
    } else {
        if (hasActiveTrack != null) 148.dp else 84.dp
    }`;

const newSpacing = `val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val navBarHeight = androidx.compose.foundation.layout.WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpacing = if (isLandscape) {
        if (hasActiveTrack != null) 90.dp + navBarHeight else 16.dp + navBarHeight
    } else {
        if (hasActiveTrack != null) 148.dp + navBarHeight else 84.dp + navBarHeight
    }`;

content = content.replace(/val isLandscape = androidx\.compose\.ui\.platform\.LocalConfiguration\.current\.orientation == android\.content\.res\.Configuration\.ORIENTATION_LANDSCAPE\n    val bottomSpacing = if \(isLandscape\) \{\n        if \(hasActiveTrack != null\) 90\.dp else 16\.dp\n    \} else \{\n        if \(hasActiveTrack != null\) 148\.dp else 84\.dp\n    \}/g, newSpacing);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
