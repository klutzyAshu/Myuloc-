const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

let lines = content.split('\n');
let depth = 0;

for (let i = 8488; i < 8590; i++) {
    let line = lines[i];
    let open = (line.match(/\{/g) || []).length;
    let close = (line.match(/\}/g) || []).length;
    
    if (line.includes('"{') || line.includes('}"') || line.trim().startsWith('//')) {
        continue;
    }
    
    let net = open - close;
    depth += net;
    console.log(`${i}: net=${net} depth=${depth} ${line.trim()}`);
}
