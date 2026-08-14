const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldFallbackX = `        val expandedCenterX = if (targetCenter != null) {
            with(density) { targetCenter!!.x.toDp() }
        } else {
            widthDp / 2f
        }`;

const newFallbackX = `        val expandedCenterX = if (targetCenter != null) {
            with(density) { targetCenter!!.x.toDp() }
        } else {
            if (isLandscape) widthDp / 4f else widthDp / 2f
        }`;

content = content.replace(oldFallbackX, newFallbackX);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
