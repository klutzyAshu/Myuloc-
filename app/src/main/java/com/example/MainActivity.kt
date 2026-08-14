package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.zIndex
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.example.ui.screens.MyuLocDashboard
import com.example.ui.screens.GlobalToastOverlay
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MyuLocViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private lateinit var mainViewModel: MyuLocViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen().apply {
      setOnExitAnimationListener { splashScreenView ->
        val fadeAnimator = android.animation.ObjectAnimator.ofFloat(
          splashScreenView.view,
          android.view.View.ALPHA,
          1f,
          0f
        ).apply {
          duration = 250L
          interpolator = android.view.animation.DecelerateInterpolator()
        }
        
        fadeAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: android.animation.Animator) {
            splashScreenView.remove()
          }
        })
        
        fadeAnimator.start()
      }
    }
    super.onCreate(savedInstanceState)
    
    // Remove default Android opening & closing animations
    if (android.os.Build.VERSION.SDK_INT >= 34) {
      overrideActivityTransition(android.app.Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
      overrideActivityTransition(android.app.Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
    } else {
      @Suppress("DEPRECATION")
      overridePendingTransition(0, 0)
    }
    
    enableEdgeToEdge()

    // Initialize the ViewModel safely in activity lifecycle phase
    mainViewModel = androidx.lifecycle.ViewModelProvider(this)[MyuLocViewModel::class.java]

    // Safely collect the close app trigger when emitted by the sleep timer
    lifecycleScope.launch {
      mainViewModel.appCloseTrigger.collect {
        android.util.Log.d("MainActivity", "App close trigger received. Finishing activity.")
        finishAndRemoveTask()
      }
    }

    // Globally optimize Coil ImageLoader to aggressively cache in memory / disk, eliminate listener allocations and stutters
    try {
        val loader = coil.ImageLoader.Builder(applicationContext)
            .memoryCache {
                coil.memory.MemoryCache.Builder(applicationContext)
                    .maxSizePercent(0.25) // Cache allocation up to 25% of available application RAM
                    .build()
            }
            .diskCache {
                coil.disk.DiskCache.Builder()
                    .directory(applicationContext.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(1024 * 1024 * 50) // High-density 50MB disk caching boundary
                    .build()
            }
            .crossfade(true)
            .allowHardware(true) // Optimizes GPU allocation and transfer of bitmap bytes
            .build()
        coil.Coil.setImageLoader(loader)
        android.util.Log.i("MainActivity", "Coil aggressive caching system successfully initialized.")
    } catch (e: Throwable) {
        android.util.Log.e("MainActivity", "Error configuring Coil aggressive cache: ${e.message}")
    }

    // Process initial incoming intent (if app was started from a link click)
    android.util.Log.d("MainActivity", "Processing intent in onCreate: ${intent?.data}")
    handleIntent(intent)

    setContent {
      val isDarkMode by mainViewModel.isDarkMode.collectAsState()
      val customThemeEnabled by mainViewModel.customThemeEnabled.collectAsState()
      val dynamicColorEnabled by mainViewModel.dynamicColorEnabled.collectAsState()
      val customHue by mainViewModel.customHue.collectAsState()
      val customSaturation by mainViewModel.customSaturation.collectAsState()
      val customLightness by mainViewModel.customLightness.collectAsState()

      val useDarkIcons = if (customThemeEnabled) {
        customLightness >= 0.5f // Light background needs dark icons
      } else {
        !isDarkMode // Light theme needs dark icons
      }

      androidx.compose.runtime.DisposableEffect(useDarkIcons) {
        enableEdgeToEdge(
          statusBarStyle = if (useDarkIcons) {
            androidx.activity.SystemBarStyle.light(
              android.graphics.Color.TRANSPARENT,
              android.graphics.Color.TRANSPARENT
            )
          } else {
            androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
          },
          navigationBarStyle = if (useDarkIcons) {
            androidx.activity.SystemBarStyle.light(
              android.graphics.Color.TRANSPARENT,
              android.graphics.Color.TRANSPARENT
            )
          } else {
            androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
          }
        )
        onDispose {}
      }

      MyApplicationTheme(
        darkTheme = isDarkMode,
        customThemeEnabled = customThemeEnabled,
        customHue = customHue,
        customSaturation = customSaturation,
        customLightness = customLightness,
        dynamicColor = dynamicColorEnabled
      ) {
        val currentDensity = androidx.compose.ui.platform.LocalDensity.current
        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val customDensity = androidx.compose.runtime.remember(currentDensity, configuration) {
            val screenWidthDp = configuration.screenWidthDp
            // Adaptive scaling based on device screen width classes:
            // - Phone (< 600dp): 0.92f for beautiful, spacious but compact content
            // - Medium/Foldable (600dp to 840dp): 0.96f
            // - Tablet/Expanded (>= 840dp): 1.0f to ensure high legibility and large targets
            val scaleFactor = when {
                screenWidthDp >= 840 -> 1.0f
                screenWidthDp >= 600 -> 0.96f
                else -> 0.92f
            }
            object : androidx.compose.ui.unit.Density {
                override val density: Float get() = currentDensity.density * scaleFactor
                override val fontScale: Float get() = currentDensity.fontScale * scaleFactor
            }
        }
        androidx.compose.runtime.CompositionLocalProvider(
          androidx.compose.ui.platform.LocalDensity provides customDensity
        ) {
          androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            MyuLocDashboard(viewModel = mainViewModel)
            
            GlobalToastOverlay()
          }
        }
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    // Request high refresh rate (90 FPS/120 FPS support) on compatible displays while remaining adaptive
    if (android.os.Build.VERSION.SDK_INT >= 30) {
      try {
        val window = this.window
        val params = window.attributes
        val display = this.display
        val modes = display?.supportedModes
        val highestMode = modes?.maxByOrNull { it.refreshRate }
        
        if (highestMode != null) {
          // Clear fixed display mode ID to allow variable refresh rate (VRR) / adaptive sync
          params.preferredDisplayModeId = 0 
          
          @Suppress("DEPRECATION")
          // removed refresh rate hack
          
          window.attributes = params
        }
      } catch (e: Throwable) {
        android.util.Log.e("MainActivity", "Error enabling Adaptive High Refresh Rate: ${e.message}")
      }
    }
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    android.util.Log.d("MainActivity", "Processing intent in onNewIntent: ${intent.data}")
    handleIntent(intent)
  }

  private fun handleIntent(intent: android.content.Intent?) {
    try {
      intent?.data?.let { uri ->
        val uriStr = uri.toString()
        android.util.Log.d("MainActivity", "Parsing incoming intent URL string safely: $uriStr")

        var token = ""

        // 1. Try to extract access_token from fragment (after #)
        val hashIndex = uriStr.indexOf('#')
        if (hashIndex != -1) {
          val fragmentPart = uriStr.substring(hashIndex + 1)
          val params = fragmentPart.split("&").associate {
            val parts = it.split("=")
            if (parts.size >= 2) parts[0] to parts[1] else parts[0] to ""
          }
          token = params["access_token"] ?: ""
        }

        // 2. Try to extract access_token from query params (after ?) if not found in fragment
        if (token.isEmpty()) {
          val queryIndex = uriStr.indexOf('?')
          if (queryIndex != -1) {
            val queryPart = uriStr.substring(queryIndex + 1).split("#")[0]
            val params = queryPart.split("&").associate {
              val parts = it.split("=")
              if (parts.size >= 2) parts[0] to parts[1] else parts[0] to ""
            }
            token = params["access_token"] ?: ""
          }
        }

        // 3. Fallback: regex search for access_token=
        if (token.isEmpty()) {
          val regex = Regex("[#?&]access_token=([^&]+)")
          val match = regex.find(uriStr)
          token = match?.groupValues?.get(1) ?: ""
        }

        if (token.isNotEmpty()) {
          if (::mainViewModel.isInitialized) {
            android.util.Log.d("MainActivity", "Successfully parsed Google OAuth access token from URI string!")
            mainViewModel.connectWithAccessToken(token)
          } else {
            android.util.Log.e("MainActivity", "ViewModel not initialized yet while capturing token!")
          }
        } else {
          android.util.Log.d("MainActivity", "No access_token found in incoming URI string.")
        }
      }
    } catch (e: Throwable) {
      android.util.Log.e("MainActivity", "Error processing intent data safely: ${e.message}")
      e.printStackTrace()
    }
  }
}

object NoIndication : androidx.compose.foundation.IndicationNodeFactory {
  override fun create(interactionSource: androidx.compose.foundation.interaction.InteractionSource): androidx.compose.ui.node.DelegatableNode {
    return object : androidx.compose.ui.Modifier.Node(), androidx.compose.ui.node.DrawModifierNode {
      override fun androidx.compose.ui.graphics.drawscope.ContentDrawScope.draw() {
        drawContent()
      }
    }
  }

  override fun equals(other: Any?): Boolean = other === this
  override fun hashCode(): Int = 0
}
