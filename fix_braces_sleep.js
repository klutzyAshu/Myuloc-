const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /    \}\n    \}\n\}\n\}\n\}\n\}\nfun SleepTimerMiniCapsule\(/;
const replacement = `    }
}
@Composable
fun SleepTimerMiniCapsule(`;

if (content.match(regex)) {
    content = content.replace(regex, replacement);
    fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
    console.log("Matched and replaced");
} else {
    console.log("Not matched");
}
