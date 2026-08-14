const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

content = content.replace(/fun ExpandedPlayerView\(\s*viewModel: MyuLocViewModel,\s*isDarkMode: Boolean,\s*onDismiss: \(\) -> Unit\s*\)/, 
`fun ExpandedPlayerView(
    viewModel: MyuLocViewModel,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    expansionProgress: Float = 1f,
    innerPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
    onSleepDialogShow: () -> Unit = {}
)`);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
