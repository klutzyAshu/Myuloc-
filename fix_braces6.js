const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /\}\s*@Composable\s*\}\s*fun SleepTimerMiniCapsule\(/;
const replacement = `    }
}

@Composable
fun SleepTimerMiniCapsule(`;

content = content.replace(regex, replacement);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
