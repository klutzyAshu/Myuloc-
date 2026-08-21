const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const regex = /                    \}\n                \}\n            \}\n        \n        if \(showExpandedPlayer\) \{/;
const replacement = `                    }
                }
            }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ExpandedPlayerView(
    viewModel: com.example.viewmodel.MyuLocViewModel,
    isDarkMode: Boolean,
    expansionProgress: Float,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onDismiss: () -> Unit,
    onSleepDialogShow: () -> Unit
) {
    val topPadding = innerPadding.calculateTopPadding()
    val enableBackgroundMotion = true
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
`;

if (content.match(regex)) {
    content = content.replace(regex, replacement);
    fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
    console.log('Replaced successfully');
} else {
    console.log('Regex not matched');
}
