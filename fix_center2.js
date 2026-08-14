const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldCenter = `        val expandedCenterX = if (isLandscape) widthDp / 4f else widthDp / 2f
        val expandedCenterY = if (isLandscape) actualHeightDp / 2f else topPadding + 64.dp + (maxVinylSize / 2f)`;

const newCenter = `        val expandedCenterX = if (isLandscape) widthDp / 4f else widthDp / 2f
        val expandedCenterY = if (isLandscape) actualHeightDp / 2f else topPadding + 88.dp + (maxVinylSize / 2f)`;

content = content.replace(oldCenter, newCenter);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
