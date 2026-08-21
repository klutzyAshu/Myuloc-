const fs = require('fs');
const path = 'app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt';

let content = fs.readFileSync(path, 'utf8');

content = content.replace(/if\s*\(\s*!isDarkMode\s*\)\s*Color\.Black\s*else\s*Color\.White/g, 'MaterialTheme.colorScheme.onSurface');
content = content.replace(/if\s*\(\s*isDarkMode\s*\)\s*Color\.White\s*else\s*Color\.Black/g, 'MaterialTheme.colorScheme.onSurface');
content = content.replace(/if\s*\(\s*isDarkMode\s*\)\s*Color\.Black\s*else\s*Color\.White/g, 'MaterialTheme.colorScheme.primary');
content = content.replace(/if\s*\(\s*!isDarkMode\s*\)\s*Color\.White\s*else\s*Color\.Black/g, 'MaterialTheme.colorScheme.primary');

fs.writeFileSync(path, content, 'utf8');
console.log('Replacements complete');
