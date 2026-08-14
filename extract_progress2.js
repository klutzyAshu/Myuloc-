const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
const regexPortrait = /                Column\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.padding\(horizontal = 16\.dp\)\s*\) \{\s*val currentPosition by viewModel\.playerManager\.currentPosition\.collectAsState\(\)[\s\S]*?\/\/ Track timestamps\s*Row\(\s*modifier = Modifier\.fillMaxWidth\(\)\.padding\(horizontal = 16\.dp\),\s*horizontalArrangement = Arrangement\.SpaceBetween\s*\) \{[\s\S]*?\.padding\(horizontal = 4\.dp, vertical = 2\.dp\)\s*\)\s*\}/;
content = content.replace(regexPortrait, '                IsolatedExpandedPlayerProgress(viewModel, textSecondary, activeAccent)');
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
