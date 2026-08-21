const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

let lines = content.split('\n');

for (let i = Math.max(0, 5450); i >= 0; i--) {
    let line = lines[i];
    if (line.includes('fun ')) {
        console.log(`Line ${i}: ${line.trim()}`);
        break;
    }
}
