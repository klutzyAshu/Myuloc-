const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldGlass = `fun getGlassBackground(isDarkMode: Boolean): Brush {
    val surfaceColor = MaterialTheme.colorScheme.surface
    return androidx.compose.runtime.remember(surfaceColor) {
        androidx.compose.ui.graphics.SolidColor(surfaceColor)
    }
}`;

const newGlass = `fun getGlassBackground(isDarkMode: Boolean): Brush {
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    return androidx.compose.runtime.remember(surfaceColor) {
        androidx.compose.ui.graphics.SolidColor(surfaceColor)
    }
}`;

content = content.replace(oldGlass, newGlass);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
