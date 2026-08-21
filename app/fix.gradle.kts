tasks.register("fixColors") {
    doLast {
        val file = file("app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt")
        var content = file.readText()
        content = content.replace("if (isDarkMode) Color.White else Color.Black", "androidx.compose.material3.MaterialTheme.colorScheme.onSurface")
        content = content.replace("if (isDarkMode) Color.Black else Color.White", "androidx.compose.material3.MaterialTheme.colorScheme.surface")
        content = content.replace("if (!isDarkMode) Color.Black else Color.White", "androidx.compose.material3.MaterialTheme.colorScheme.onSurface")
        content = content.replace("if (!isDarkMode) Color.White else Color.Black", "androidx.compose.material3.MaterialTheme.colorScheme.surface")
        content = content.replace("if (isDarkMode) Color.LightGray else Color.DarkGray", "androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant")
        content = content.replace("if (isDarkMode) Color(0x1FFFFFFF) else Color(0x0E000000)", "androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant")
        file.writeText(content)
        println("Colors fixed")
    }
}
