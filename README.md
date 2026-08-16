# MyuLoc 🎵

MyuLoc is a cloud-integrated, minimalist music player ecosystem built for Android, written entirely in **Kotlin** and designed with a visually stunning, premium **Jetpack Compose** interface. 

Featuring a modern **Glassmorphism (Frosted Glass)** design paradigm, MyuLoc blends local media playback, Google Drive personal locker streaming, and online YouTube/audio stream capabilities into a unified, fluid, and highly interactive audio experience.

---

## ✨ Primary Features

### 🎨 Visual & Theme Customization
* **Dynamic Dark & Light Modes**: Full, custom color schema adapting organically between day and night.
* **Granular Theme Customizer**: Real-time hue, saturation, and lightness controls that dynamically update the app's accent system.
* **Edge-to-Edge Fluidity**: Immersive navigation layout with a transparent status bar matching background lightness, ensuring content stretches seamlessly.

### 🍃 Glassmorphic Design Paradigm
* **Premium Frosted Cards**: Semi-translucent layout gradients with soft, high-fidelity glowing borders.
* **Spring-Loaded Animations**: Implements custom tuned physical springs for organic tactile feedback on interactive buttons and details cards.
* **Vinyl Spin Spinner**: Active spinning track artwork resembling traditional physical vinyl, scaling responsively upon user interaction.

### 👆 Gesture-Driven Interactions
* **Mini-Player Swiping**: Swipe left on the anchored miniature bottom capsule to easily skip to the next track; swipe right to play the previous song.
* **Vertical Dismissal**: Drag the expanded immersive player screen downward to dismiss it with simulated inertial physics.
* **Bouncy Tactile Clickables**: Every button is loaded with a spring-scale bounce gesture handler for high satisfaction.

### ☁️ Dual Audio Ecosystems
* **Local Device Library**: Indexes, filters, and plays local audio files with custom caching.
* **Google Drive Cloud Locker**: Mount personal Google Drive locker repositories to stream music directly from the cloud.
* **Advanced Player Utilities**: Custom sleep countdown timer, details inspector, favorite systems, playlist managers, and active queues.

---

## 🛠️ Built With

* **UI Framework**: Jetpack Compose (Material Design 3)
* **Language**: 100% Kotlin
* **Architecture**: MVVM (Model-View-ViewModel) + Unidirectional State Flow (StateFlow)
* **Concurrency**: Kotlin Coroutines & Flow
* **Platform APIs**: Android Media3 ExoPlayer, Google Drive REST API
* **Build System**: Gradle Wrapper (Kotlin DSL `.gradle.kts`)

---

## 🚀 Setting Up & Running the Application

### Prerequisites
* **Android Studio** (Ladybug or newer recommended)
* **Android SDK 34** or newer
* **Java Development Kit (JDK 17)**

### Detailed Build Steps
1. Clone the repository into your local workspace:
   ```bash
   git clone https://github.com/YOUR_GITHUB_USERNAME/MyuLoc.git
   ```
2. Open the project in Android Studio.
3. Allow Gradle project synchronization to download dependencies.
4. Connect an Android device or launch an Emulator (minimum SDK 26 recommended).
5. Press the **Run** button or execute the Gradle build command:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📂 Architecture Overview

```text
app/src/main/java/com/example/
├── MainActivity.kt                 # Application Entrypoint & Edge-to-Edge System Setting
├── ui/
│   ├── theme/
│   │   ├── Color.kt               # Handpicked Color Palettes
│   │   ├── Theme.kt               # Centralized MyApplicationTheme with Transparent Status Bars
│   │   └── Type.kt                # Custom Typography Matching
│   └── screens/
│       └── MyuLocDashboard.kt     # Dashboard views, Expanded Player, Anchored Capsules, & Sleep Utilities
```

---

## 🧪 Development & Quality Assurance
The codebase uses modern Gradle configurations caching and pre-validated code configurations. You can run unit testing and clean tasks directly using:
```bash
gradle compileDebugKotlin
```

---

## 📄 License
This project is open-source and available under the MIT License. 
buymeacoffee.com/klutzya
