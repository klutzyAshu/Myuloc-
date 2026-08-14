const fs = require('fs');
const path = '../app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt';

let content = fs.readFileSync(path, 'utf8');

content = content.replace(/if\s*\(\s*!isDarkMode\s*\)\s*Color\.Black\s*else\s*Color\.White/g, 'MaterialTheme.colorScheme.onSurface');
content = content.replace(/if\s*\(\s*isDarkMode\s*\)\s*Color\.White\s*else\s*Color\.Black/g, 'MaterialTheme.colorScheme.onSurface');
content = content.replace(/if\s*\(\s*isDarkMode\s*\)\s*Color\.Black\s*else\s*Color\.White/g, 'MaterialTheme.colorScheme.primary');
content = content.replace(/if\s*\(\s*!isDarkMode\s*\)\s*Color\.White\s*else\s*Color\.Black/g, 'MaterialTheme.colorScheme.primary');
content = content.replace(/if\s*\(\s*isDarkMode\s*\)\s*Color\.LightGray\s*else\s*Color\.DarkGray/g, 'MaterialTheme.colorScheme.onSurfaceVariant');
content = content.replace(/if\s*\(\s*isDarkMode\s*\)\s*Color\(0x1FFFFFFF\)\s*else\s*Color\(0x0E000000\)/g, 'MaterialTheme.colorScheme.outlineVariant');

fs.writeFileSync(path, content, 'utf8');
console.log('Replacements complete');
