const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/player/MusicPlayerManager.kt', 'utf8');

// Load EQ settings in init
const loadSettingsStr = `                    val savedEqEnabled = db.preferenceDao().getPreference("eq_enabled")?.toBoolean() ?: false
                    eqEnabled.value = savedEqEnabled
                    
                    val savedEqBands = db.preferenceDao().getPreference("eq_bands")
                    if (savedEqBands != null) {
                        try {
                            val bands = savedEqBands.split(",").map { it.toInt() }
                            if (bands.size == 5) {
                                eqBands.value = bands
                            }
                        } catch (e: Exception) { }
                    }
                    
                    val savedRepeat = db.preferenceDao().getPreference("repeat_mode") ?: "OFF"`;

content = content.replace(/                    val savedRepeat = db\.preferenceDao\(\)\.getPreference\("repeat_mode"\) \?: "OFF"/, loadSettingsStr);

// Save EQ settings helper
const saveEqSettingsHelper = `    private fun saveEqSettings() {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                db.preferenceDao().setPreference(
                    com.example.data.database.PreferenceEntity("eq_enabled", eqEnabled.value.toString())
                )
                db.preferenceDao().setPreference(
                    com.example.data.database.PreferenceEntity("eq_bands", eqBands.value.joinToString(","))
                )
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun setEqEnabled(enabled: Boolean) {`;

content = content.replace(/    fun setEqEnabled\(enabled: Boolean\) \{/, saveEqSettingsHelper);

// Call saveEqSettings in setEqEnabled
content = content.replace(/    fun setEqEnabled\(enabled: Boolean\) \{\n        eqEnabled\.value = enabled\n        synchronized\(equalizerLock\) \{\n            try \{\n                equalizer\?\.enabled = enabled\n            \} catch \(e: Throwable\) \{ e\.printStackTrace\(\) \}\n        \}\n    \}/, `    fun setEqEnabled(enabled: Boolean) {
        eqEnabled.value = enabled
        synchronized(equalizerLock) {
            try {
                equalizer?.enabled = enabled
            } catch (e: Throwable) { e.printStackTrace() }
        }
        saveEqSettings()
    }`);

// Call saveEqSettings in setEqBandLevel
content = content.replace(/        if \(band in currentLevels\.indices\) \{\n            currentLevels\[band\] = levelMilliBels\n            eqBands\.value = currentLevels\n            synchronized\(equalizerLock\) \{\n                try \{\n                    equalizer\?\.let \{ eq ->\n                        if \(band < eq\.numberOfBands\) \{\n                            eq\.setBandLevel\(band\.toShort\(\), levelMilliBels\.toShort\(\)\)\n                        \}\n                    \}\n                \} catch \(e: Throwable\) \{ e\.printStackTrace\(\) \}\n            \}\n        \}\n    \}/, `        if (band in currentLevels.indices) {
            currentLevels[band] = levelMilliBels
            eqBands.value = currentLevels
            synchronized(equalizerLock) {
                try {
                    equalizer?.let { eq ->
                        if (band < eq.numberOfBands) {
                            eq.setBandLevel(band.toShort(), levelMilliBels.toShort())
                        }
                    }
                } catch (e: Throwable) { e.printStackTrace() }
            }
            saveEqSettings()
        }
    }`);

fs.writeFileSync('app/src/main/java/com/example/player/MusicPlayerManager.kt', content);
