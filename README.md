# Saarthe Floating Assistant v1.2 Fixed

Android MVP for an opt-in floating Saarthe assistant.

## Fixed in this build
- Reply EditText is created before suggestion click listeners, fixing the Kotlin scope/compile issue.
- Floating overlay is focusable so the reply field can be tapped and typed into.
- Added a Close button to dismiss the overlay.
- DOT4 A-Z mapping remains exactly the locked table.
- Added `.github/workflows/build-apk.yml` so GitHub Actions can build a debug APK without Android Studio.

## Build on GitHub using only a phone
1. Create a GitHub repository and upload the project files/folders (not just a ZIP).
2. Keep the `.github/workflows/build-apk.yml` file in the repository.
3. Open the repository's **Actions** tab and run **Build Saarthe APK**.
4. After the workflow finishes, open the run and download the `saarthe-debug-apk` artifact.
5. Extract the artifact and install `app-debug.apk` on Android.

## Permissions
The app requires the user to explicitly enable Android Accessibility Service and the overlay/draw-over-other-apps permission. It does not auto-send messages.

## Important
Accessibility text capture can vary by Android version and by app. This MVP should be treated as a user-controlled prototype, not a guarantee that every chat app exposes every message node.
