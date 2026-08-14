const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldCardBg = `            .let { modifier ->
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

const newCardBg = `            .background(
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

content = content.replace(oldCardBg, newCardBg);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
