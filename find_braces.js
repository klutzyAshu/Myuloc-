const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

let open = 0;
for(let i=0; i<5503; i++) {
    if (lines[i].includes('fun AnchoredPlayerBar(')) {
        open = 1;
    } else if (open > 0) {
        for(let c of lines[i]) {
            if (c === '{') open++;
            if (c === '}') open--;
        }
    }
}
console.log('Open braces in AnchoredPlayerBar at line 5503:', open);
