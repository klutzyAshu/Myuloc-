const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

let open = 0;
let first = -1;
let last = -1;
for(let i=0; i<lines.length; i++) {
    if (lines[i].includes('fun AnchoredPlayerBar(')) {
        open = 1;
        first = i + 1;
    } else if (open > 0) {
        for(let c of lines[i]) {
            if (c === '{') open++;
            if (c === '}') open--;
        }
        if (open === 0) {
            last = i + 1;
            break;
        }
    }
}
console.log('AnchoredPlayerBar starts at', first, 'ends at', last);
