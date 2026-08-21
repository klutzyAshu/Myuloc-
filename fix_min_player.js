const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /                        \.align\(Alignment\.BottomCenter\)\n                        \.padding\(\n                            bottom = if \(isLandscape\) 16\.dp else 72\.dp,\n                            start = if \(isLandscape\) 80\.dp else 0\.dp\n                        \) \/\/ Leave space for BottomNavigationBar in portrait\n                        \.zIndex\(10f\)/;

const newModifier = `                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(
                            bottom = if (isLandscape) 16.dp else 64.dp,
                            start = if (isLandscape) 80.dp else 0.dp
                        ) // Leave space for BottomNavigationBar in portrait
                        .zIndex(10f)`;

content = content.replace(regex, newModifier);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
