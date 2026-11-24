# Reno10 Capture — experimental Android 15 source project

Independent app-local rewrite for OPPO Reno10 PHW110 (Chinese-market model) / ColorOS 15. Uses classic Xposed API 82; the installed framework must support legacy modules. No root installer is included. This is not a verified replacement for all functions of LSPosed/DisableFlagSecure, and does not suppress capture notifications, modify protected surfaces or hook system services. It only changes ordinary Android window flags inside manually selected, non-system apps on API 35. OEM-specific, DRM and other non-window restrictions may remain. Use only with content you are authorized to capture; enabling screenshots can expose confidential data.

## Build on GitHub (no Android Studio required)
1. Extract this ZIP. Create an empty GitHub repository and push the extracted project contents, including `.github/workflows/build.yml` (hidden directory). GitHub Actions must be enabled. For example, from the extracted project: `git init`, `git add .`, `git commit -m "Initial experimental module"`, `git branch -M main`, `git remote add origin YOUR_REPO_URL`, `git push -u origin main`.
2. Open Actions > Build experimental APK. Run workflow if it hasn't started automatically. Check the build log for errors. No APK exists until the workflow succeeds.
3. Download the Reno10-Capture-debug artifact from the successful run and extract app-debug.apk. This is a debug-signed experimental APK.

## Local build
Use JDK 17, Android SDK platform 35, build tools 35.0.0 and Gradle 8.9. Set ANDROID_HOME to the SDK path (or create local.properties with sdk.dir). From the project root run `gradle :app:assembleDebug`. This source ZIP does not contain a Gradle wrapper or offline dependencies. Build output: `app/build/outputs/apk/debug/app-debug.apk`.

## Install and test
1. Disable the original Enable Screenshot module and reboot to prevent interaction. Keep a working recovery method before changing root modules.
2. Install the new APK and enable Reno10 Capture in LSPosed. It is a module, not a launchable app.
3. Select only one ordinary third-party app you are authorized to capture. Do NOT select Android/System Framework or system UI. This module also ignores system apps and non-Android-15 devices.
4. Force-stop and relaunch the selected app (or reboot). Test a normal window. Check LSPosed logs for `[Reno10Capture]`. The loaded line includes model and firmware build ID for compatibility diagnosis.
5. If no `loaded` line appears, check scope, Android version, app type, and legacy API support. `hook FAILED` lines identify optional hooks that could not be registered. A `first ordinary window flag change` line means the module changed an app window flag, not that screenshots necessarily work.
6. To revert, disable the module, force-stop the app or reboot, then uninstall the APK. Keep the original disabled while testing this module.

## Validation status
The pure Java flag policy test was run locally. The Android APK was not compiled here because the environment has no Android SDK/Gradle and cannot reach dependency repositories. No on-device test was possible. GitHub Actions build is provided but has not been run. The project uses public Window APIs and optional reflective WindowManagerImpl hooks; these are not verified against your particular OPPO firmware. No device-wide compatibility claim is made.
