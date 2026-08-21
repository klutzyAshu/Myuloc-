const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /        val expandedCenterX = if \(isLandscape\) widthDp \/ 4f else widthDp \/ 2f\n        val expandedCenterY = if \(isLandscape\) actualHeightDp \/ 2f else topPadding \+ 88\.dp \+ \(maxVinylSize \/ 2f\)/;
const newCenter = `        val expandedCenterX = if (targetCenter != null) {
            with(density) { targetCenter!!.x.toDp() }
        } else {
            widthDp / 2f
        }
        val expandedCenterY = if (targetCenter != null) {
            with(density) { targetCenter!!.y.toDp() }
        } else {
            topPadding + 88.dp + (maxVinylSize / 2f)
        }`;

content = content.replace(regex, newCenter);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
