const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// Replace blur(80.dp) block with a tiny low-res request that stretches
content = content.replace(/                        AsyncImage\([\s\S]*?model = track\.thumbnailUrl,[\s\S]*?contentDescription = null,[\s\S]*?contentScale = androidx\.compose\.ui\.layout\.ContentScale\.Crop,[\s\S]*?modifier = Modifier[\s\S]*?\.fillMaxSize\(\)[\s\S]*?\.blur\(80\.dp\)[\s\S]*?\.graphicsLayer \{[\s\S]*?alpha = if \(isDarkMode\) 0\.35f else 0\.6f[\s\S]*?\}[\s\S]*?\)/, `                        AsyncImage(
                            model = rememberOptimizedImageRequest(track.thumbnailUrl, 16),
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { 
                                    alpha = if (isDarkMode) 0.35f else 0.6f
                                },
                            filterQuality = androidx.compose.ui.graphics.FilterQuality.High
                        )`);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
