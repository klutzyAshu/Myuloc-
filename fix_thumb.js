const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', 'utf8');

const oldThumb = `                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()
                                ) {
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f
                                    val petalW = 7f
                                    val petalH = 14f
                                    
                                    val petalColor = activeAccent // Dark Cherry Pink
                                    for (i in 0 until 5) {
                                        val angle = i * 72f
                                        rotate(degrees = angle, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
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
                                            drawPath(path, color = petalColor)
                                        }
                                    }
                                    drawCircle(color = Color(0xFFFFD700), radius = 2.5f, center = androidx.compose.ui.geometry.Offset(cx, cy))
                                }
                            }
                        },`;

const newThumb = `                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(activeAccent, androidx.compose.foundation.shape.CircleShape)
                            )
                        },`;

content = content.replace(oldThumb, newThumb);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/MyuLocDashboard.kt', content);
