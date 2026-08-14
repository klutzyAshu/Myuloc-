const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const targetLayoutStart = '// Nested Column to top-align all visual components as one cohesive block below the header to match the image precisely';
const targetLayoutMiddle = '// Track Info & Options (Completely symmetrical and beautifully centered)';
const targetLayoutEndStr = '} // End of nested top-aligned Column';

const startIndex = content.indexOf(targetLayoutStart);
const middleIndex = content.indexOf(targetLayoutMiddle, startIndex);
const endIndex = content.indexOf(targetLayoutEndStr, startIndex) + targetLayoutEndStr.length;

if (startIndex === -1 || middleIndex === -1 || content.indexOf(targetLayoutEndStr) === -1) {
    console.log("Could not find layout start, middle or end.");
    process.exit(1);
}

// Find the last Spacer before the end
let spacerEndIndex = content.lastIndexOf('Spacer(modifier = Modifier.weight(', endIndex);
if (spacerEndIndex < middleIndex) {
    spacerEndIndex = endIndex - targetLayoutEndStr.length;
}

const controlsContent = content.substring(middleIndex, spacerEndIndex).trimEnd();

const newLayout = `                // Adaptive Layout for Landscape and Portrait
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .size(maxVinylSize)
                                    .onGloballyPositioned { coordinates ->
                                        val positionInWindow = coordinates.positionInWindow()
                                        targetCenter = androidx.compose.ui.geometry.Offset(
                                            x = positionInWindow.x + coordinates.size.width / 2f - outerBoxPositionInWindow.x,
                                            y = positionInWindow.y + coordinates.size.height / 2f - outerBoxPositionInWindow.y
                                        )
                                    }
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1.2f).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
${controlsContent.split('\\n').map(line => '                            ' + line).join('\\n')}
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Spacer(
                            modifier = Modifier
                                .size(maxVinylSize)
                                .onGloballyPositioned { coordinates ->
                                    val positionInWindow = coordinates.positionInWindow()
                                    targetCenter = androidx.compose.ui.geometry.Offset(
                                        x = positionInWindow.x + coordinates.size.width / 2f - outerBoxPositionInWindow.x,
                                        y = positionInWindow.y + coordinates.size.height / 2f - outerBoxPositionInWindow.y
                                    )
                                }
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
${controlsContent}
                        Spacer(modifier = Modifier.weight(1.2f))
                    }
                }`;

content = content.substring(0, startIndex) + newLayout + content.substring(endIndex);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
console.log("Successfully replaced layout.");
