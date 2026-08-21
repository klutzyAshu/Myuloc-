const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

// 1. In AnchoredPlayerBarWrapper, remove currentPosition and duration collectAsState
content = content.replace(
    /val currentPosition by viewModel\.playerManager\.currentPosition\.collectAsState\(\)\s*val duration by viewModel\.playerManager\.duration\.collectAsState\(\)/g,
    ""
);

// 2. In AnchoredPlayerBarWrapper call to AnchoredPlayerBar, pass the viewmodel
content = content.replace(
    /AnchoredPlayerBar\(\s*track = activeTrack,\s*isPlaying = isPlaying,\s*isAudioDeliveringSound = isAudioDeliveringSound,\s*currentPosition = currentPosition,\s*duration = duration,/g,
    "AnchoredPlayerBar(\n        track = activeTrack,\n        isPlaying = isPlaying,\n        isAudioDeliveringSound = isAudioDeliveringSound,\n        viewModel = viewModel,"
);

// 3. Update AnchoredPlayerBar signature
content = content.replace(
    /fun AnchoredPlayerBar\(\s*track: PlayerTrack,\s*isPlaying: Boolean,\s*isAudioDeliveringSound: Boolean,\s*currentPosition: Long,\s*duration: Long,/g,
    "fun AnchoredPlayerBar(\n    track: PlayerTrack,\n    isPlaying: Boolean,\n    isAudioDeliveringSound: Boolean,\n    viewModel: MyuLocViewModel,"
);

// 4. Update the Box with progress fraction inside AnchoredPlayerBar
const anchoredPlayerBarBoxRegex = /Box\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.height\(2\.dp\)\s*\.background\(androidx\.compose\.material3\.MaterialTheme\.colorScheme\.outlineVariant\)\s*\)\s*\{[\s\S]*?val progressFraction = if \(duration > 0f\) \(currentPosition\.toFloat\(\) \/ duration\.toFloat\(\)\)\.coerceIn\(0f, 1f\) else 0f[\s\S]*?val animatedProgress by androidx\.compose\.animation\.core\.animateFloatAsState\([\s\S]*?label = "SmoothMusicSlider"\s*\)\s*Box\(\s*modifier = Modifier\s*\.fillMaxHeight\(\)\s*\.fillMaxWidth\(\)\.graphicsLayer \{ scaleX = animatedProgress; transformOrigin = androidx\.compose\.ui\.graphics\.TransformOrigin\(0f, 0\.5f\) \}\s*\.background\(androidx\.compose\.material3\.MaterialTheme\.colorScheme\.primary\)\s*\)\s*\}/;

const anchoredPlayerBarBoxReplacement = "Box(\n" +
"                modifier = Modifier\n" +
"                    .fillMaxWidth()\n" +
"                    .height(2.dp)\n" +
"                    .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)\n" +
"            ) {\n" +
"                val currentPosition by viewModel.playerManager.currentPosition.collectAsState()\n" +
"                val duration by viewModel.playerManager.duration.collectAsState()\n" +
"                val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f\n" +
"                val animatedProgress by androidx.compose.animation.core.animateFloatAsState(\n" +
"                    targetValue = progressFraction,\n" +
"                    animationSpec = androidx.compose.animation.core.tween(\n" +
"                        durationMillis = if (isPlaying) 300 else 0,\n" +
"                        easing = androidx.compose.animation.core.LinearEasing\n" +
"                    ),\n" +
"                    label = \"SmoothMusicSlider\"\n" +
"                )\n" +
"                \n" +
"                Box(\n" +
"                    modifier = Modifier\n" +
"                        .fillMaxHeight()\n" +
"                        .fillMaxWidth().graphicsLayer { scaleX = animatedProgress; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f) }\n" +
"                        .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)\n" +
"                 )\n" +
"            }";

content = content.replace(anchoredPlayerBarBoxRegex, anchoredPlayerBarBoxReplacement);


// 5. MainPlayerContent refactoring
content = content.replace(
    /val currentPosition by viewModel\.playerManager\.currentPosition\.collectAsState\(\)\s*val duration by viewModel\.playerManager\.duration\.collectAsState\(\)/g,
    ""
);

// 6. Fix MainPlayerContent's custom slider box
const mainPlayerSliderRegex = /val progressFraction = if \(duration > 0f\) \(currentPosition\.toFloat\(\) \/ duration\.toFloat\(\)\)\.coerceIn\(0f, 1f\) else 0f\s*var sliderValue by remember \{ mutableStateOf<Float\?>\(null\) \}\s*var lastTickValue by remember \{ mutableIntStateOf\(-1\) \}\s*val view = androidx\.compose\.ui\.platform\.LocalView\.current\s*val animatedProgress by androidx\.compose\.animation\.core\.animateFloatAsState\([\s\S]*?label = "SmoothExpandedMusicSlider"\s*\)\s*Slider\([\s\S]*?modifier = Modifier\.fillMaxWidth\(\)\.height\(24\.dp\)\s*\)/;

const mainPlayerSliderReplacement = "val currentPosition by viewModel.playerManager.currentPosition.collectAsState()\n" +
"                    val duration by viewModel.playerManager.duration.collectAsState()\n" +
"                    val progressFraction = if (duration > 0f) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f\n" +
"                    var sliderValue by remember { mutableStateOf<Float?>(null) }\n" +
"                    var lastTickValue by remember { mutableIntStateOf(-1) }\n" +
"                    val view = androidx.compose.ui.platform.LocalView.current\n" +
"                    \n" +
"                    val animatedProgress by androidx.compose.animation.core.animateFloatAsState(\n" +
"                        targetValue = progressFraction,\n" +
"                        animationSpec = androidx.compose.animation.core.tween(\n" +
"                            durationMillis = if (isPlaying && sliderValue == null) 300 else 0,\n" +
"                            easing = androidx.compose.animation.core.LinearEasing\n" +
"                        ),\n" +
"                        label = \"SmoothExpandedMusicSlider\"\n" +
"                    )\n" +
"                    \n" +
"                    Slider(\n" +
"                        value = (sliderValue ?: animatedProgress).coerceIn(0f, 1f),\n" +
"                        onValueChange = { \n" +
"                            sliderValue = it\n" +
"                            val newInt = (it * 100).toInt()\n" +
"                            if (newInt != lastTickValue) {\n" +
"                                view.playSoundEffect(android.view.SoundEffectConstants.CLICK)\n" +
"                                lastTickValue = newInt\n" +
"                            }\n" +
"                        },\n" +
"                        onValueChangeFinished = {\n" +
"                            sliderValue?.let {\n" +
"                                val target = (it * duration).toLong()\n" +
"                                viewModel.playerManager.seekTo(target)\n" +
"                            }\n" +
"                            sliderValue = null\n" +
"                        },\n" +
"                        colors = SliderDefaults.colors(\n" +
"                            thumbColor = Color.Transparent,\n" +
"                            activeTrackColor = Color.Transparent,\n" +
"                            inactiveTrackColor = Color.Transparent,\n" +
"                            activeTickColor = Color.Transparent,\n" +
"                            inactiveTickColor = Color.Transparent\n" +
"                        ),\n" +
"                        thumb = {\n" +
"                            Box(\n" +
"                                modifier = Modifier\n" +
"                                    .size(28.dp)\n" +
"                                    .background(Color.Transparent),\n" +
"                                contentAlignment = Alignment.Center\n" +
"                            ) {\n" +
"                                Canvas(modifier = Modifier.fillMaxSize()) {\n" +
"                                    val cx = size.width / 2f\n" +
"                                    val cy = size.height / 2f\n" +
"                                    val petalW = 7f\n" +
"                                    val petalH = 14f\n" +
"                                    \n" +
"                                    val petalColor = activeAccent // Dark Cherry Pink\n" +
"                                    for (i in 0 until 5) {\n" +
"                                        val angle = i * 72f\n" +
"                                        rotate(degrees = angle, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {\n" +
"                                            val path = Path().apply {\n" +
"                                                moveTo(cx, cy)\n" +
"                                                cubicTo(\n" +
"                                                    cx - petalW, cy - petalH * 0.3f,\n" +
"                                                    cx - petalW, cy - petalH,\n" +
"                                                    cx, cy - petalH\n" +
"                                                )\n" +
"                                                cubicTo(\n" +
"                                                    cx + petalW, cy - petalH,\n" +
"                                                    cx + petalW, cy - petalH * 0.3f,\n" +
"                                                    cx, cy\n" +
"                                                )\n" +
"                                                close()\n" +
"                                            }\n" +
"                                            drawPath(path, color = petalColor)\n" +
"                                        }\n" +
"                                    }\n" +
"                                    drawCircle(color = Color(0xFFFFD700), radius = 2.5f, center = androidx.compose.ui.geometry.Offset(cx, cy))\n" +
"                                }\n" +
"                            }\n" +
"                        },\n" +
"                        track = { _ ->\n" +
"                            val activeColor = MaterialTheme.colorScheme.primary\n" +
"                            val inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant\n" +
"                            Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {\n" +
"                                val centerY = size.height / 2f\n" +
"                                val width = size.width\n" +
"                                drawLine(\n" +
"                                    color = inactiveColor,\n" +
"                                    start = Offset(0f, centerY),\n" +
"                                    end = Offset(width, centerY),\n" +
"                                    strokeWidth = 2.dp.toPx(),\n" +
"                                    cap = androidx.compose.ui.graphics.StrokeCap.Round\n" +
"                                )\n" +
"                                val progress = sliderValue ?: animatedProgress\n" +
"                                drawLine(\n" +
"                                    color = activeColor,\n" +
"                                    start = Offset(0f, centerY),\n" +
"                                    end = Offset(width * progress, centerY),\n" +
"                                    strokeWidth = 2.dp.toPx(),\n" +
"                                    cap = androidx.compose.ui.graphics.StrokeCap.Round\n" +
"                                )\n" +
"                            }\n" +
"                        },\n" +
"                        modifier = Modifier.fillMaxWidth().height(24.dp)\n" +
"                    )";

content = content.replace(mainPlayerSliderRegex, mainPlayerSliderReplacement);

// 7. Fix MainPlayerContent's timestamps Row
const timestampRegex = /Row\(\s*modifier = Modifier\.fillMaxWidth\(\)\.padding\(horizontal = 16\.dp\),\s*horizontalArrangement = Arrangement\.SpaceBetween\s*\)\s*\{\s*Text\(\s*text = formatDuration\(currentPosition\),[\s\S]*?padding\(horizontal = 4\.dp, vertical = 2\.dp\)\s*\)\s*\}/;

const timestampReplacement = "Row(\n" +
"                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),\n" +
"                    horizontalArrangement = Arrangement.SpaceBetween\n" +
"                ) {\n" +
"                    val currentPosition by viewModel.playerManager.currentPosition.collectAsState()\n" +
"                    val duration by viewModel.playerManager.duration.collectAsState()\n" +
"                    Text(\n" +
"                        text = formatDuration(currentPosition),\n" +
"                        fontSize = 10.sp,\n" +
"                        color = textSecondary,\n" +
"                        style = androidx.compose.material3.LocalTextStyle.current.copy(\n" +
"                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(\n" +
"                                includeFontPadding = true\n" +
"                            ),\n" +
"                            lineHeight = 14.sp\n" +
"                        )\n" +
"                    )\n" +
"                    Text(\n" +
"                        text = if (showRemainingTime && duration > currentPosition) \"-${formatDuration(duration - currentPosition)}\" else formatDuration(duration),\n" +
"                        fontSize = 10.sp,\n" +
"                        color = textSecondary,\n" +
"                        style = androidx.compose.material3.LocalTextStyle.current.copy(\n" +
"                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(\n" +
"                                includeFontPadding = true\n" +
"                            ),\n" +
"                            lineHeight = 14.sp\n" +
"                        ),\n" +
"                        modifier = Modifier\n" +
"                            .clickable(\n" +
"                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },\n" +
"                                indication = null\n" +
"                            ) {\n" +
"                                showRemainingTime = !showRemainingTime\n" +
"                            }\n" +
"                            .padding(horizontal = 4.dp, vertical = 2.dp)\n" +
"                    )\n" +
"                }";

content = content.replace(timestampRegex, timestampReplacement);

// 8. Fix LyricsView call in MainPlayerContent
content = content.replace(
    /LyricsView\(\s*currentPosition = currentPosition,\s*track = currentTrack,/g,
    "val currentPositionForLyrics by viewModel.playerManager.currentPosition.collectAsState()\n                            LyricsView(\n                                currentPosition = currentPositionForLyrics,\n                                track = currentTrack,"
);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
