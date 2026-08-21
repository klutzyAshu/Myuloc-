const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/player/MyuLocPlaybackService.kt', 'utf8');

const target = `val keyEvent = mediaButtonIntent.getParcelableExtra<android.view.KeyEvent>(Intent.EXTRA_KEY_EVENT)`;
const replacement = `val keyEvent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, android.view.KeyEvent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        mediaButtonIntent.getParcelableExtra<android.view.KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    }`;

content = content.replace(target, replacement);
fs.writeFileSync('app/src/main/java/com/example/player/MyuLocPlaybackService.kt', content);
