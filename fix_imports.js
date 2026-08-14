const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// Remove the first line which is now the import
const lines = content.split('\n');
if (lines[0].startsWith('import androidx.compose.foundation.layout.navigationBars')) {
    lines.shift();
}
content = lines.join('\n');

// Add the imports after the package declaration
const packageDecl = 'package com.example.ui.screens';
const newImports = `package com.example.ui.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
`;
content = content.replace(packageDecl, newImports);

// Fix the Unresolved reference 'asPaddingValues'
// The previous code had androidx.compose.foundation.layout.WindowInsets.navigationBars.asPaddingValues()
// But wait, navigationBars is an extension on WindowInsets.Companion. So it should be WindowInsets.navigationBars
content = content.replace(/androidx\.compose\.foundation\.layout\.WindowInsets\.navigationBars/g, 'WindowInsets.navigationBars');

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
