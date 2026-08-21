const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');
for (let i = 7120; i < 7130; i++) {
    console.log(`${i+1}: ${lines[i]}`);
}
