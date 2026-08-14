const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldSortPill = `fun SortPill(
    text: String,
    isSelected: Boolean,
    direction: com.example.viewmodel.SortDirection?,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val containerBg = if (isSelected) {
        androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
    } else {
        androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderCol = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkMode) 0.5f else 0.4f)
    } else {
        Color.Transparent
    }
    Row(
        modifier = Modifier
            .background(containerBg, RoundedCornerShape(100.dp))
            
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {`;

const newSortPill = `fun SortPill(
    text: String,
    isSelected: Boolean,
    direction: com.example.viewmodel.SortDirection?,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val containerBg = if (isSelected) {
        androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderCol = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkMode) 0.5f else 0.4f)
    } else {
        Color.Transparent
    }
    Row(
        modifier = Modifier
            .background(containerBg, RoundedCornerShape(100.dp))
            .border(1.dp, borderCol, RoundedCornerShape(100.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {`;

content = content.replace(oldSortPill, newSortPill);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
