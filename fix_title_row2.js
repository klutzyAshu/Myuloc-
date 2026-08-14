const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// Replace the Queue button on the left with the Favorite button
const queueBtnRegex = /\/\/ Active queue toggle button\s+IconButton\(\s+onClick = \{ showQueuePanel = true \},\s+modifier = Modifier\.size\(36\.dp\)\s+\) \{\s+Icon\(\s+imageVector = Icons\.Default\.Queue,\s+contentDescription = "Show Active Queue",\s+tint = activeAccent,\s+modifier = Modifier\.size\(24\.dp\)\s+\)\s+\}/;

const favoriteBtn = `// Favorite toggle button
                            val isCurrentFavorite = remember(favoritesList, track) {
                                favoritesList.any { it.id == track.id }
                            }
                            IconButton(
                                onClick = { viewModel.toggleFavorite(track) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Toggle Favorite",
                                    tint = if (isCurrentFavorite) Color.Red else textPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }`;

content = content.replace(queueBtnRegex, favoriteBtn);

// Replace the Share button on the right with the Queue button
const shareBtnRegex = /\/\/ Symmetrically placed neat trailing options\s+Box\(\s+modifier = Modifier\.size\(36\.dp\),\s+contentAlignment = Alignment\.Center\s+\) \{\s+IconButton\(\s+onClick = \{ viewModel\.shareTrack\(context, track\) \},\s+modifier = Modifier\.size\(36\.dp\)\s+\) \{\s+Icon\(\s+imageVector = Icons\.Default\.Share,\s+contentDescription = "Share Track",\s+tint = textPrimary\.copy\(alpha = 0\.7f\),\s+modifier = Modifier\.size\(20\.dp\)\s+\)\s+\}\s+\}/;

const queueBtnRight = `// Symmetrically placed neat trailing options (Queue)
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { showQueuePanel = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Queue,
                                        contentDescription = "Show Active Queue",
                                        tint = textPrimary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }`;

content = content.replace(shareBtnRegex, queueBtnRight);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
