const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /modifier = Modifier\.weight\(1\.2f\)\.padding\(horizontal = 16\.dp\)\.verticalScroll\(rememberScrollState\(\)\),/;
const replacement = `modifier = Modifier.weight(1f).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),`;

content = content.replace(regex, replacement);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
