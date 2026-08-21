const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

// Delete dangling border args at 3949-3951
lines[3948] = "";
lines[3949] = "";
lines[3950] = "";
lines[3951] = "";

// Fix BouncyIconButton comma at 6746 (which was .background...)
if (lines[6745].includes('.background(')) {
    lines[6745] = lines[6745] + ",";
}

// Fix Boxes at 7270-7273
for (let i = 7269; i <= 7272; i++) {
    if (lines[i].includes('Box(modifier = Modifier.fillMaxSize(') && !lines[i].trim().endsWith(')')) {
        lines[i] = lines[i] + ")";
    }
}

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', lines.join('\n'));
