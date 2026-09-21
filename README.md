# StarMaker - Live Singing & Karaoke Android App

A modern Android Karaoke & Live Singing app built with Jetpack Compose, Kotlin Coroutines, and Android Audio Synthesizer/Recorder.

---

## 📥 How to Download the APK from GitHub (Without Errors)

### Method 1: Automatic Download via GitHub Actions (Recommended)
This repository includes an automated GitHub Actions workflow (`.github/workflows/build-apk.yml`) that builds the APK automatically on every push or manual run:

1. Open your repository on **GitHub**.
2. Click on the **Actions** tab at the top.
3. Select the latest run under **"Build & Download Android APK"** (or click **Run workflow**).
4. Scroll down to the **Artifacts** section at the bottom of the page.
5. Click **`StarMaker-SingLive-debug.apk`** to download the ready-to-install APK directly!

---

### Method 2: Building Locally on Your Computer
If you clone this repository to your computer:

```bash
# Clone the repository
git clone <your-repo-url>
cd <repo-folder>

# Build the Debug APK
./gradlew assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

### Method 3: Direct Download from Google AI Studio
1. Open the project in Google AI Studio.
2. Click on the top-right settings/export menu.
3. Select **"Download APK"** or **"Generate APK"** to get the compiled APK file directly.
