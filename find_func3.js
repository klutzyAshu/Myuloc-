const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');
let lines = content.split('\n');

let currentFunc = "";
for(let i=0; i<5515; i++) {
    if (lines[i].startsWith('fun ') || lines[i].startsWith('@Composable')) {
        currentFunc = lines[i];
    }
}
console.log("Function at 5509:", currentFunc);
