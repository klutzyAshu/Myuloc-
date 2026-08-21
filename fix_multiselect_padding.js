const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /                        \.padding\(top = if \(isLandscape\) 16\.dp else 80\.dp\)/;
const newPadding = `                        .padding(top = innerPadding.calculateTopPadding() + 80.dp)`;

content = content.replace(regex, newPadding);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
