const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldBg = `.background(
                if (isSelected) {
                    androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                } else if (isCurrent) {
                    androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                } else if (isHovered.value) {
                    androidx.compose.material3.MaterialTheme.colorScheme.surface
                } else {
                    Color.Transparent
                },
                RoundedCornerShape(12.dp)
            )`;

const newBg = `.background(
                if (isSelected) {
                    androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                } else if (isCurrent) {
                    androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else if (isHovered.value) {
                    androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                } else {
                    androidx.compose.material3.MaterialTheme.colorScheme.surface
                },
                RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isCurrent) 1.5.dp else 0.5.dp,
                color = if (isCurrent) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )`;

content = content.replace(oldBg, newBg);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
