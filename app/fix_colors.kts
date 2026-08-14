import java.io.File

fun main() {
    val file = File("app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt")
    var content = file.readText()

    // Specific replacements for text and icons where they shouldn't be hardcoded White
    // 1. DropdownMenuItem texts
    content = content.replace("Text(\"Play Now\", color = Color.White", "Text(\"Play Now\", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface")
    content = content.replace("Text(\"Add to Queue\", color = Color.White", "Text(\"Add to Queue\", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface")
    content = content.replace("Text(\"Download Cache\", color = Color.White", "Text(\"Download Cache\", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface")
    content = content.replace("Text(\"Share Track\", color = Color.White", "Text(\"Share Track\", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface")
    content = content.replace("Text(\"Clear Queue\", color = Color.White", "Text(\"Clear Queue\", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface")

    // 2. Backgrounds
    content = content.replace("if (isDarkMode) Color(0xFF1A1A1A) else Color.White", "if (isDarkMode) Color(0xFF1A1A1A) else androidx.compose.material3.MaterialTheme.colorScheme.surface")
    content = content.replace("if (isDarkMode) Color(0xFF121215) else Color.White", "if (isDarkMode) Color(0xFF121215) else androidx.compose.material3.MaterialTheme.colorScheme.surface")
    content = content.replace("if (isDarkMode) Color(0xFF1E1E1E) else Color.White", "if (isDarkMode) Color(0xFF1E1E1E) else androidx.compose.material3.MaterialTheme.colorScheme.surface")
    content = content.replace("if (isDarkMode) Color(0xFF0D0D11) else Color.White", "if (isDarkMode) Color(0xFF0D0D11) else androidx.compose.material3.MaterialTheme.colorScheme.surface")

    // 3. Icons that are not in primary-colored buttons
    content = content.replace("tint = Color.White.copy(alpha = 0.7f)", "tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)")
    content = content.replace("tint = Color.White.copy(alpha = 0.8f)", "tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)")

    // Fix the moon/sun icon
    content = content.replace("imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode", "imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode")

    file.writeText(content)
    println("Replacements done")
}
