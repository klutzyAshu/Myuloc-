const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const staticPetals = `        if (enableMotion) {
            CherryBlossomPetalsOverlay()
        }`;

const animatedPetals = `        if (enableMotion) {
            AnimatedDriftingPetalsOverlay(isDarkMode = isDarkMode)
        }`;

content = content.replace(staticPetals, animatedPetals);

const animatedOverlayFunc = `
@Composable
fun AnimatedDriftingPetalsOverlay(isDarkMode: Boolean) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "time")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(20000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        
        val petalColor1 = primaryColor.copy(alpha = if (isDarkMode) 0.3f else 0.5f)
        val petalColor2 = secondaryColor.copy(alpha = if (isDarkMode) 0.2f else 0.4f)
        
        for (i in 0 until 14) {
            val random = kotlin.random.Random(i)
            val startX = random.nextFloat() * w
            val speedY = 0.05f + random.nextFloat() * 0.1f
            val swayAmp = 40f + random.nextFloat() * 60f
            val swayFreq = 1000f + random.nextFloat() * 2000f
            val rotSpeed = 360f + random.nextFloat() * 720f
            
            val elapsed = time * 20000f
            
            var currentY = ((elapsed * speedY) + random.nextFloat() * h) % (h + 100f) - 50f
            val currentX = startX + kotlin.math.sin(elapsed / swayFreq).toFloat() * swayAmp
            val currentRot = (time * rotSpeed) % 360f
            
            val scale = 0.6f + random.nextFloat() * 0.8f
            
            rotate(currentRot, Offset(currentX, currentY)) {
                val cx = currentX
                val cy = currentY
                val petalW = 10f * scale
                val petalH = 20f * scale
                
                val path = Path().apply {
                    moveTo(cx, cy)
                    cubicTo(
                        cx - petalW, cy - petalH * 0.3f,
                        cx - petalW, cy - petalH,
                        cx, cy - petalH
                    )
                    cubicTo(
                        cx + petalW, cy - petalH,
                        cx + petalW, cy - petalH * 0.3f,
                        cx, cy
                    )
                    close()
                }
                drawPath(path, color = if (i % 2 == 0) petalColor1 else petalColor2)
            }
        }
    }
}
`;

content = content + animatedOverlayFunc;

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
