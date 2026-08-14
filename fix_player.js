const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/player/MusicPlayerManager.kt', 'utf8');

const target = `            mediaPlayer = android.media.MediaPlayer().apply {
                setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)`;

const replacement = `            mediaPlayer = android.media.MediaPlayer().apply {
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .build()
                )`;

content = content.replace(target, replacement);

const target2 = `wifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyuLoc:WifiLock")`;
const replacement2 = `wifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL, "MyuLoc:WifiLock")`;
content = content.replace(target2, replacement2);

fs.writeFileSync('app/src/main/java/com/example/player/MusicPlayerManager.kt', content);
