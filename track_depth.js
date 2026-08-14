const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

let lines = content.split('\n');
let depth = 0;
let inShowExpandedPlayer = false;

for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    let open = (line.match(/\{/g) || []).length;
    let close = (line.match(/\}/g) || []).length;
    
    // Ignore braces in strings/comments roughly
    if (line.includes('"{') || line.includes('}"') || line.trim().startsWith('//')) {
        // skip naive count for strings/comments
    } else {
        depth += open;
        depth -= close;
    }
    
    if (line.includes('if (showExpandedPlayer) {')) {
        console.log(`Found if (showExpandedPlayer) { at line ${i} with depth ${depth}`);
    }
}
