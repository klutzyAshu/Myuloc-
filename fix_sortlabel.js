const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldSortLabel = `                            Text(
                                text = "Sort:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )`;

const newSortLabel = `                            Text(
                                text = "Sort:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )`;

content = content.replace(oldSortLabel, newSortLabel);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
