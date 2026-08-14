const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

let lines = content.split('\n');
let depth = 0;
let funcStart = -1;
let currentFunc = "";

for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    let open = (line.match(/\{/g) || []).length;
    let close = (line.match(/\}/g) || []).length;
    
    if (line.includes('"{') || line.includes('}"') || line.trim().startsWith('//')) {
        continue;
    }
    
    let net = open - close;
    
    if (line.includes('fun ') && depth === 0) {
        if (funcStart !== -1) {
            console.log(`Func ${currentFunc} (lines ${funcStart}-${i-1}) net: ${depth}`);
        }
        funcStart = i;
        currentFunc = line.trim();
        depth = 0; // reset for individual tracking
    }
    
    if (funcStart !== -1) {
        depth += net;
    }
}
if (funcStart !== -1) {
    console.log(`Func ${currentFunc} (lines ${funcStart}-${lines.length-1}) net: ${depth}`);
}
