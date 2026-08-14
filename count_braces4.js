const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

let startIndex = content.indexOf('fun AnchoredPlayerBar(');
let endIndex = content.indexOf('fun SleepTimerMiniCapsule(');

let block = content.substring(startIndex, endIndex);

let openCount = 0;
let closeCount = 0;

for (let i = 0; i < block.length; i++) {
    if (block[i] === '{') openCount++;
    if (block[i] === '}') closeCount++;
}

console.log('Open:', openCount, 'Close:', closeCount);
