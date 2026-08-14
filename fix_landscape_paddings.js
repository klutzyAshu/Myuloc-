const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const dashStart = 'fun MyuLocDashboard(viewModel: MyuLocViewModel) {';
const isLandscapeVar = `    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
`;

content = content.replace(dashStart, dashStart + '\n' + isLandscapeVar);

// Update SleepTimer alignment
const sleepTimerOld = `                // Floating sleep timer countdown capsule pill
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (activeTrack != null && !showExpandedPlayer) 148.dp else 84.dp)
                        .graphicsLayer {
                            translationY = 120.dp.toPx() * scrollFraction
                            alpha = 1f - scrollFraction
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {`;

const sleepTimerNew = `                // Floating sleep timer countdown capsule pill
                Column(
                    modifier = Modifier
                        .align(if (isLandscape) Alignment.TopEnd else Alignment.BottomCenter)
                        .padding(
                            bottom = if (isLandscape) 0.dp else if (activeTrack != null && !showExpandedPlayer) 148.dp else 84.dp,
                            top = if (isLandscape) 84.dp else 0.dp,
                            end = if (isLandscape) 16.dp else 0.dp
                        )
                        .graphicsLayer {
                            translationY = 120.dp.toPx() * scrollFraction
                            alpha = 1f - scrollFraction
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {`;
content = content.replace(sleepTimerOld, sleepTimerNew);

// Update MultiSelectMenu alignment
const multiSelectOld = `                // Multiple deletion selection menu / layout bar floating on top!
                AnimatedVisibility(
                    visible = isMultiSelectMode,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                        .padding(horizontal = 20.dp)
                        .zIndex(20f)
                ) {`;

const multiSelectNew = `                // Multiple deletion selection menu / layout bar floating on top!
                AnimatedVisibility(
                    visible = isMultiSelectMode,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = if (isLandscape) 16.dp else 80.dp)
                        .padding(horizontal = 20.dp)
                        .zIndex(20f)
                ) {`;
content = content.replace(multiSelectOld, multiSelectNew);

// We need to write back the file
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
