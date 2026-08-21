const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /        val expandedCenterX = if \(targetCenter != null\) \{\n            with\(density\) \{ targetCenter!!\.x\.toDp\(\) \}\n        \} else \{\n            widthDp \/ 2f\n        \}/;
const newCenter = `        val expandedCenterX = if (targetCenter != null) {
            with(density) { targetCenter!!.x.toDp() }
        } else {
            if (isLandscape) widthDp / 4f else widthDp / 2f
        }`;

content = content.replace(regex, newCenter);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
