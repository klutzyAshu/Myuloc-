package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MyuLocDashboard
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MyuLocViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private lateinit var mainViewModel: MyuLocViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Request high refresh rate (90 FPS/120 FPS support) on compatible displays
    if (android.os.Build.VERSION.SDK_INT >= 30) {
      try {
        val window = this.window
        val params = window.attributes
        val display = windowManager.defaultDisplay
        val modes = display?.supportedModes
        val highestMode = modes?.maxByOrNull { it.refreshRate }
        if (highestMode != null) {
          params.preferredDisplayModeId = highestMode.modeId
          window.attributes = params
        }
      } catch (e: Throwable) {
        android.util.Log.e("MainActivity", "Error enabling High Refresh Rate: ${e.message}")
      }
    }

    // Initialize the ViewModel safely in activity lifecycle phase
    mainViewModel = androidx.lifecycle.ViewModelProvider(this)[MyuLocViewModel::class.java]

    // Safely collect the close app trigger when emitted by the sleep timer
    lifecycleScope.launch {
      mainViewModel.appCloseTrigger.collect {
        android.util.Log.d("MainActivity", "App close trigger received. Finishing activity.")
        finishAndRemoveTask()
      }
    }

    // Request notification permission for background media controls on Android 13+ (Tiramisu)
    if (android.os.Build.VERSION.SDK_INT >= 33) {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
      }
    }

    // Process initial incoming intent (if app was started from a link click)
    android.util.Log.d("MainActivity", "Processing intent in onCreate: ${intent?.data}")
    handleIntent(intent)

    setContent {
      val isDarkMode by mainViewModel.isDarkMode.collectAsState()

      MyApplicationTheme(darkTheme = isDarkMode) {
        androidx.compose.runtime.CompositionLocalProvider(
          androidx.compose.foundation.LocalIndication provides NoIndication
        ) {
          MyuLocDashboard(viewModel = mainViewModel)
        }
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
