const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const nestedScrollStr = '.nestedScroll(nestedScrollConnection)';
let startIdx = content.indexOf(nestedScrollStr);
let boxStart = content.lastIndexOf('Box(', startIdx);
let zIndexNav = content.indexOf('GlobalBottomNavigationBar(');
let endIdx = content.indexOf('}', zIndexNav) + 1;
// Find the closing brace of the Box containing GlobalBottomNavigationBar
let boxEndIdx = content.indexOf('}', endIdx) + 1;

console.log("Start:", boxStart, "End:", boxEndIdx);
