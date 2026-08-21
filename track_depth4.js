const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

let lines = content.split('\n');
let depth = 0;

for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    let open = (line.match(/\{/g) || []).length;
    let close = (line.match(/\}/g) || []).length;
    
    if (line.includes('"{') || line.includes('}"') || line.trim().startsWith('//')) {
        // skip
    } else {
        depth += open;
        depth -= close;
    }
    
    if (depth < 0) {
        console.log(`Depth ${depth} at line ${i}: ${line.trim()}`);
    }
}
