const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

let lines = content.split('\n');
let depth = 0;
let lastFunc = "";
let lastFuncLine = 0;

for (let i = 0; i < lines.length; i++) {
    let line = lines[i];
    let open = (line.match(/\{/g) || []).length;
    let close = (line.match(/\}/g) || []).length;
    
    if (line.includes('"{') || line.includes('}"') || line.trim().startsWith('//')) {
        continue;
    }
    
    if (line.includes('fun ')) {
        if (lastFunc !== "") {
            console.log(`Before ${line.trim()} (line ${i}), depth is ${depth}. Last func was ${lastFunc} at line ${lastFuncLine}`);
        }
        lastFunc = line.trim();
        lastFuncLine = i;
    }
    
    depth += open - close;
}
console.log(`Final depth: ${depth}`);
