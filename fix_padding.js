const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /Column\(\s*modifier = Modifier\s*\.fillMaxSize\(\)\s*\.zIndex\(1f\)\s*\.padding\(horizontal = 16\.dp, vertical = 8\.dp\)\s*\)\s*\{/;
const columnNew = `Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .padding(
                            start = if (isLandscape) 96.dp else 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                ) {`;

content = content.replace(regex, columnNew);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
