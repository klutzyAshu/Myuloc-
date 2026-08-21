const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

content = content.replace(
    /        Box\(\s*modifier = Modifier\s*\.fillMaxSize\(\)\s*\.background\(gradientBrush\)\s*\)\s*\}/,
    "        Box(\n" +
    "            modifier = Modifier\n" +
    "                .fillMaxSize()\n" +
    "                .background(gradientBrush)\n" +
    "        )\n" +
    "        if (enableMotion) {\n" +
    "            CherryBlossomPetalsOverlay()\n" +
    "        }\n" +
    "    }"
);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
