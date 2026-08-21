const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// 1. Restore getGlassBackground and getGlassBorder
const oldGlass = `fun getGlassBackground(isDarkMode: Boolean): Brush {
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    return androidx.compose.runtime.remember(surfaceColor) {
        androidx.compose.ui.graphics.SolidColor(surfaceColor)
    }
}

@Composable
fun getGlassBorder(isDarkMode: Boolean): Brush {
    return androidx.compose.runtime.remember {
        androidx.compose.ui.graphics.SolidColor(Color.Transparent)
    }
}`;

const newGlass = `fun getGlassBackground(isDarkMode: Boolean): Brush {
    val surfaceColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    return androidx.compose.runtime.remember(surfaceColor, isDarkMode) {
        val alpha = if (isDarkMode) 0.25f else 0.4f
        androidx.compose.ui.graphics.SolidColor(surfaceColor.copy(alpha = alpha))
    }
}

@Composable
fun getGlassBorder(isDarkMode: Boolean): Brush {
    val outlineColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
    return androidx.compose.runtime.remember(outlineColor, isDarkMode) {
        val alpha = if (isDarkMode) 0.2f else 0.3f
        androidx.compose.ui.graphics.SolidColor(outlineColor.copy(alpha = alpha))
    }
}`;

content = content.replace(oldGlass, newGlass);

// 2. Restore TrackCapsuleItem background to use glassmorphism
const oldCardBg = `            .background(
                if (isSelected) {
                    androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                } else if (isCurrent) {
                    androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else if (isHovered.value) {
                    androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                } else {
                    Color.Transparent
                },
                RoundedCornerShape(12.dp)
            )
            .let {
                if (isCurrent) {
                    it.border(
                        width = 1.5.dp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else it
            }`;

const newCardBg = `            .let { modifier ->
                if (isSelected) {
                    modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                } else if (isCurrent) {
                    modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                } else if (isHovered.value) {
                    modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                } else {
                    val glassEnabled = LocalGlassmorphismEnabled.current
                    if (glassEnabled) {
                        modifier.background(containerBrush, RoundedCornerShape(12.dp))
                            .border(width = 0.5.dp, brush = computedBorderBrush, shape = RoundedCornerShape(12.dp))
                    } else {
                        modifier.background(Color.Transparent)
                    }
                }
            }`;

content = content.replace(oldCardBg, newCardBg);

// 3. Add blur back to ExpandedPlayerView
const oldBlur = `                        AsyncImage(
                            model = track.thumbnailUrl,
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { 
                                     alpha = if (isDarkMode) 0.35f else 0.6f
                                }
                        )`;

const newBlur = `                        AsyncImage(
                            model = track.thumbnailUrl,
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(80.dp)
                                .graphicsLayer { 
                                     alpha = if (isDarkMode) 0.35f else 0.6f
                                }
                        )`;

content = content.replace(oldBlur, newBlur);

// 4. Add CherryBlossomPetalsOverlay back
const oldPetals = `        if (enableMotion) {
            AnimatedDriftingPetalsOverlay(isDarkMode = isDarkMode)
        }`;

const newPetals = `        CherryBlossomPetalsOverlay()
        if (enableMotion) {
            AnimatedDriftingPetalsOverlay(isDarkMode = isDarkMode)
        }`;

content = content.replace(oldPetals, newPetals);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
