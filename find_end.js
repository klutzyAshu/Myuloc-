const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

let open = 0;
for(let i=0; i<lines.length; i++) {
    if (lines[i].includes('fun MyuLocDashboard(')) {
        open = 1;
        console.log(`Starts at ${i+1}`);
    } else if (open > 0) {
        for(let c of lines[i]) {
            if (c === '{') open++;
            if (c === '}') open--;
        }
        if (open === 0) {
            console.log(`Ends at ${i+1}`);
            break;
        }
    }
}
