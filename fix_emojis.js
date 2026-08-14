const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex1 = /                                    Text\(\s*text = "🌸",\s*fontSize = 17\.sp,\s*modifier = Modifier\.padding\(end = 8\.dp\)\s*\)/g;
content = content.replace(regex1, '');

const regex2 = /                                    Text\(\s*text = "🌸",\s*fontSize = 17\.sp,\s*modifier = Modifier\.padding\(start = 8\.dp\)\s*\)/g;
content = content.replace(regex2, '');

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
