const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const helper = `
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun IsolatedExpandedPlayerProgress(viewModel: MyuLocViewModel, textSecondary: androidx.compose.ui.graphics.Color, activeAccent: androidx.compose.ui.graphics.Color) {
    var showRemainingTime by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val isPlaying by viewModel.playerManager.isPlaying.collectAsState()
        val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
        val duration by viewModel.playerManager.duration.collectAsState()
        val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
        var sliderValue by remember { mutableStateOf<Float?>(null) }
        var lastTickValue by remember { mutableIntStateOf(-1) }
        val view = androidx.compose.ui.platform.LocalView.current
        
        val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
            targetValue = progressFraction,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = if (isPlaying && sliderValue == null) 300 else 0,
                easing = androidx.compose.animation.core.LinearEasing
            ),
            label = "SmoothExpandedMusicSlider"
        )
        
        androidx.compose.material3.Slider(
            value = (sliderValue ?: animatedProgress).coerceIn(0f, 1f),
            onValueChange = { 
                sliderValue = it
                val newInt = (it * 100).toInt()
                if (newInt != lastTickValue) {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    lastTickValue = newInt
                }
            },
            onValueChangeFinished = {
                sliderValue?.let {
                    val target = (it * duration).toLong()
                    viewModel.playerManager.seekTo(target)
                }
                sliderValue = null
            },
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = androidx.compose.ui.graphics.Color.Transparent,
                activeTrackColor = androidx.compose.ui.graphics.Color.Transparent,
                inactiveTrackColor = androidx.compose.ui.graphics.Color.Transparent,
                activeTickColor = androidx.compose.ui.graphics.Color.Transparent,
                inactiveTickColor = androidx.compose.ui.graphics.Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(activeAccent, androidx.compose.foundation.shape.CircleShape)
                )
            },
            track = { _ ->
                val activeColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                    val centerY = size.height / 2f
                    val width = size.width
                    drawLine(
                        color = inactiveColor,
                        start = androidx.compose.ui.geometry.Offset(0f, centerY),
                        end = androidx.compose.ui.geometry.Offset(width, centerY),
                        strokeWidth = 2.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    val progress = sliderValue ?: animatedProgress
                    drawLine(
                        color = activeColor,
                        start = androidx.compose.ui.geometry.Offset(0f, centerY),
                        end = androidx.compose.ui.geometry.Offset(width * progress, centerY),
                        strokeWidth = 2.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(24.dp)
        )
    }
    
    // Track timestamps
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val currentPosition by viewModel.playerManager.currentPosition.collectAsState()
        val duration by viewModel.playerManager.duration.collectAsState()
        Text(
            text = formatDuration(currentPosition),
            fontSize = 10.sp,
            color = textSecondary,
            style = androidx.compose.material3.LocalTextStyle.current.copy(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                    includeFontPadding = true
                ),
                lineHeight = 14.sp
            )
        )
        Text(
            text = if (showRemainingTime && duration > currentPosition) "-\${formatDuration(duration - currentPosition)}" else formatDuration(duration),
            fontSize = 10.sp,
            color = textSecondary,
            style = androidx.compose.material3.LocalTextStyle.current.copy(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                    includeFontPadding = true
                ),
                lineHeight = 14.sp
            ),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) {
                    showRemainingTime = !showRemainingTime
                }
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
`;

const regexPortrait = /                Column\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.padding\(horizontal = 16\.dp\)\s*\) \{\s*val currentPosition by viewModel\.playerManager\.currentPosition\.collectAsState\(\)[\s\S]*?\/\/ Track timestamps\s*Row\(\s*modifier = Modifier\.fillMaxWidth\(\)\.padding\(horizontal = 16\.dp\),\s*horizontalArrangement = Arrangement\.SpaceBetween\s*\) \{[\s\S]*?\.padding\(horizontal = 4\.dp, vertical = 2\.dp\)\s*\)\s*\}/;

content = content.replace(regexPortrait, '                IsolatedExpandedPlayerProgress(viewModel, textSecondary, activeAccent)');
content = content.replace(regexPortrait, '                IsolatedExpandedPlayerProgress(viewModel, textSecondary, activeAccent)');

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content + helper);
