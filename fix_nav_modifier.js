const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const navModOld = `.fillMaxWidth(if (isLandscape) 0f else 1f)
                        .fillMaxHeight(if (isLandscape) 1f else 0f)`;
const navModNew = `.let { if (isLandscape) it.fillMaxHeight().width(80.dp) else it.fillMaxWidth() }`;

content = content.replace(navModOld, navModNew);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
