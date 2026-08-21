import java.io.File

val file = File("app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt")
var content = file.readText()

val replacements = mapOf(
    "if (isDarkMode) Color(0xFF1E1E24) else Color(0xFFF2F2F2)" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant",
    "if (isDarkMode) Color(0xFF130F11) else Color(0xFFFAF5FF)" to "androidx.compose.material3.MaterialTheme.colorScheme.surface",
    "if (isDarkMode) Color(0xFF2A2027) else Color(0xFFE9D5FF)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xFF0F0C0E) else Color(0xFFF3E8FF)" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant",
    "if (isDarkMode) Color(0x22FFFFFF) else Color(0x1A000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0x11FFFFFF) else Color(0x0A000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)",
    "if (isDarkMode) Color(0xFF1F1F23) else Color(0xFFE0E0E0)" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant",
    "if (isDarkMode) Color(0xFF121215) else androidx.compose.material3.MaterialTheme.colorScheme.surface" to "androidx.compose.material3.MaterialTheme.colorScheme.surface",
    "if (isDarkMode) Color(0xFF1F1F23) else Color(0x0A000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant",
    "if (isDarkMode) Color(0x33FFFFFF) else Color(0x1F000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xFF1F1F23) else Color(0xFFE0E0E0)" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant",
    "if (isDarkMode) Color(0x33FFFFFF) else Color(0x0A000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0x22FFFFFF) else Color(0x11000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xFF1F1F23) else Color(0xFFF3E8FF)" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant",
    "if (isDarkMode) Color(0xFF121215).copy(alpha = 0.40f) else Color.Transparent" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)",
    "if (isDarkMode) Color(0xFF2E1A47).copy(alpha = 0.5f) else Color(0xFFE0CEF5)" to "androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer",
    "if (isDarkMode) Color(0xFF121215) else Color(0xFFF9F5FF)" to "androidx.compose.material3.MaterialTheme.colorScheme.surface",
    "if (isDarkMode) Color(0xFF0D0D11) else androidx.compose.material3.MaterialTheme.colorScheme.surface" to "androidx.compose.material3.MaterialTheme.colorScheme.surface",
    "if (isDarkMode) Color(0x10FFFFFF) else Color(0x0E000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xB5FFFFFF) else MaterialTheme.colorScheme.primary" to "androidx.compose.material3.MaterialTheme.colorScheme.primary",
    "if (isDarkMode) Color(0x19FFFFFF) else Color(0x0E000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0x1AFFFFFF) else Color(0x0D000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0x1F000000) else Color(0x0A000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0x1AFFFFFF) else Color(0x1A000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xFF0A0A0D) else Color(0xFFF9F6FC)" to "androidx.compose.material3.MaterialTheme.colorScheme.background",
    "if (isDarkMode) Color(0x19FFFFFF) else Color(0x14000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xFF121215) else Color(0xFFF3E8FF)" to "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant",
    "if (isDarkMode) Color(0x15FFFFFF) else Color(0x0A000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0x22FFFFFF) else Color(0x15000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xFF0F0E13).copy(alpha = 0.9f) else Color(0xFFFAF6FC).copy(alpha = 0.95f)" to "androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)",
    "if (isDarkMode) Color(0x44FFFFFF) else Color(0x22000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outline",
    "if (isDarkMode) Color(0xFF1E1E1E) else androidx.compose.material3.MaterialTheme.colorScheme.surface" to "androidx.compose.material3.MaterialTheme.colorScheme.surface",
    "if (isDarkMode) Color(0x33FFFFFF) else Color(0x22000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outline",
    "if (isDarkMode) Color(0x33FFFFFF) else Color(0x0F000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0x1AFFFFFF) else Color(0x33000000)" to "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant",
    "if (isDarkMode) Color(0xFF22110D) else Color(0xFFFAEEEE)" to "androidx.compose.material3.MaterialTheme.colorScheme.errorContainer"
)

for ((old, new) in replacements) {
    content = content.replace(old, new)
}

file.writeText(content)
println("Replacements done")
