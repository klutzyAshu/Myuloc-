const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// Replace rememberScrollStateInfo and its usages
content = content.replace(/val scrollStateInfo = rememberScrollStateInfo\(listState\)\n\s*val isScrollingDown = scrollStateInfo\.isScrollingDown\n\s*val isScrollingFast = scrollStateInfo\.isScrollingFast\n/g, '');

content = content.replace(/,\n\s*isScrollingFast = isScrollingFast/g, '');
content = content.replace(/,\n\s*isScrollingDown = isScrollingDown/g, '');
content = content.replace(/,\n\s*isScrollingFast = false/g, '');
content = content.replace(/,\n\s*isScrollingDown: Boolean = true/g, '');

// Clean up rememberOptimizedImageRequest
content = content.replace(/fun rememberOptimizedImageRequest\(data: Any\?, sizePx: Int, isScrollingFast: Boolean = false\)/g, 'fun rememberOptimizedImageRequest(data: Any?, sizePx: Int)');
content = content.replace(/return androidx\.compose\.runtime\.remember\(data, sizePx, isScrollingFast\) \{/g, 'return androidx.compose.runtime.remember(data, sizePx) {');
content = content.replace(/\.crossfade\(!isScrollingFast\)/g, '.crossfade(true)');
content = content.replace(/        if \(isScrollingFast\) \{\n            builder\.networkCachePolicy\(coil\.request\.CachePolicy\.DISABLED\)\n        \}\n/g, '');


fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
